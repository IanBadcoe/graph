package engine;

public class Util
{
   public static OrderedPair<Double, Double> edgeIntersect(XY edge1Start, XY edge1End, XY edge2Start, XY edge2End)
   {
      return edgeIntersect(
            edge1Start.X, edge1Start.Y,
            edge1End.X, edge1End.Y,
            edge2Start.X, edge2Start.Y,
            edge2End.X, edge2End.Y
      );
   }

   private static OrderedPair<Double, Double> edgeIntersect(double edge1StartX, double edge1StartY,
                                                            double edge1EndX, double edge1EndY,
                                                            double edge2StartX, double edge2StartY,
                                                            double edge2EndX, double edge2EndY)
   {

      double den = (edge2EndX - edge2StartX) * (edge1StartY - edge1EndY) - (edge1StartX - edge1EndX) * (edge2EndY - edge2StartY);

      // very near to parallel
      if (Math.abs(den) < 1e-20)
         return null;

      double t1 = ((edge2StartY - edge2EndY) * (edge1StartX - edge2StartX) + (edge2EndX - edge2StartX) * (edge1StartY - edge2StartY)) / den;

      if (t1 < 0 || t1 > 1)
         return null;

      double t2 = ((edge1StartY - edge1EndY) * (edge1StartX - edge2StartX) + (edge1EndX - edge1StartX) * (edge1StartY - edge2StartY)) / den;

      if (t2 < 0 || t2 > 1)
         return null;

      return new OrderedPair<>(t1, t2);
   }

   public static OrderedPair<XY, XY> circleCircleIntersect(XY c1, double r1, XY c2, double r2)
   {
      double dist_2 = c1.minus(c2).length2();
      double dist = Math.sqrt(dist_2);

      // too far apart
      if (dist > r1 + r2)
         return null;

      // too close together
      if (dist < Math.abs(r1 - r2))
         return null;

      double a = c1.X;
      double b = c1.Y;
      double c = c2.X;
      double d = c2.Y;

      double delta_2 = (dist + r1 + r2)
            * (dist + r1 - r2)
            * (dist - r1 + r2)
            * (-dist + r1 + r2);

      // should have assured delta_2 +ve with the ifs above...
      // but rounding can give v. small negative numbers
      assert delta_2 > -1e-6;

      if (delta_2 < 0)
         delta_2 = 0;

      double delta = 0.25 * Math.sqrt(delta_2);

      double xi1 = (a + c) / 2
            + (c - a) * (r1 * r1 - r2 * r2) / (2 * dist_2)
            + 2 * (b - d) * delta / dist_2;
      double xi2 = (a + c) / 2
            + (c - a) * (r1 * r1 - r2 * r2) / (2 * dist_2)
            - 2 * (b - d) * delta / dist_2;

      double yi1 = (b + d) / 2
            + (d - b) * (r1 * r1 - r2 * r2) / (2 * dist_2)
            - 2 * (a - c) * delta / dist_2;
      double yi2 = (b + d) / 2
            + (d - b) * (r1 * r1 - r2 * r2) / (2 * dist_2)
            + 2 * (a - c) * delta / dist_2;

      XY p1 = new XY(xi1, yi1);

      XY p2 = null;

      if (delta > 1e-6)
      {
         p2 = new XY(xi2, yi2);
      }

      return new OrderedPair<>(p1, p2);
   }

   public static double atan2(XY vec)
   {
      //noinspection SuspiciousNameCombination
      return Math.atan2(vec.X, vec.Y);
   }

   // removes any positive or negative whole turns to leave a number
   // between 0.0 and 2 PI
   public static double fixupAngle(double a)
   {
      while(a < 0)
         a += Math.PI * 2;

      while(a >= Math.PI * 2)
         a -= Math.PI * 2;

      return a;
   }

   public static boolean clockAwareAngleCompare(double a1, double a2, double tol)
   {
      double diff = fixupAngle(Math.abs(a1 - a2));

      return diff <= tol || diff >= Math.PI * 2 - tol;
   }

   // d1 is the unit direction vector for the reference line
   // d2 is the unit direction vector for the line whose angle we're measuring
   static double relativeAngle(XY d1, XY d2)
   {
      double rel_y = d1.dot(d2);
      double rel_x = d1.rot90().dot(d2);

      //noinspection SuspiciousNameCombination
      return fixupAngle(Math.atan2(rel_x, rel_y));
   }
}
