package engine;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

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

   /**
    * Calculate the force and distortion of an edge constrained to be between dmin and dmax in length.
    *
    * @param l    the current length of the edge
    * @param dmin the minimum permitted length of the edge
    * @param dmax the maximum permitted length of the edge
    * @return a pair of floats, the first is the fractional distortion of the edge.  If between dmin and dmax this
    * is 1.0 (no distortion) if shorter than dmin this is l as a fraction of dmin (< 1.0) and if
    * if longer than dmax then this is l as a fraction of dmax )e.g. > 1.0)
    * <p>
    * The second double is the force.  The sign of the force is that -ve is repulsive (happens when too close)
    * and vice versa.
    */
   static OrderedPair<Double, Double> unitEdgeForce(double l, double dmin, double dmax)
   {
      double ratio;

      // between min and max there is no force and we always return 1.0
      if (l < dmin)
      {
         ratio = l / dmin;
      } else if (l > dmax)
      {
         ratio = l / dmax;
      } else
      {
         ratio = 1.0;
      }

      double force = (ratio - 1);

      return new OrderedPair<>(ratio, force);
   }

   /**
    * Calculate force and distance ratio of two circular nodes
    * @param l node separation
    * @param summed_radii idea minimum separation
    * @return a pair of floats, the first is a fractional measure of how much too close the nodes are,
    * zero if they are more than their summed_radii apart.
    * <p>
    * The second double is the force.  The sign of the force is that -ve is repulsive (happens when too close)
    * the are no attractive forces for nodes so the force is never > 0.
    */
   static OrderedPair<Double, Double> unitNodeForce(double l, double summed_radii)
   {
      double ratio = l / summed_radii;

      // no attractive forces
      if (ratio > 1)
      {
         return new OrderedPair<>(0.0, 0.0);
      }

      double force = (ratio - 1);

      // at the moment the relationship between force and overlap is trivial
      // but will keep the two return values in case the force develops a squared term or something...
      return new OrderedPair<>(-force, force);
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

   static class NEDRet
   {
      final double Dist;
      final XY Target;  // point of closest approach of Node to Edge
      final XY Direction;  // direction from Node to Target

      NEDRet(double dist,
             XY target,
             XY direction)
      {
         Dist = dist;
         Target = target;
         Direction = direction;
      }
   }

   // specialised version returning extra data for use in force calculations
   // force calculation cannot  handle zero distances so returns null for that
   static NEDRet nodeEdgeDistDetailed(XY n,
                                      XY es,
                                      XY ee)
   {
      // direction and length of edge
      XY de = ee.minus(es);

      // don't expect to see and hope other forces will pull the ends apart
      if (de.isZero())
         return null;

      double le = de.length();
      de = de.divide(le);

      // line from n to edge start
      XY dnes = n.minus(es);

      // project that line onto the edge direction
      double proj = de.dot(dnes);

      XY t;
      if (proj < 0)
      {
         // closest approach before edge start
         t = es;
      }
      else if (proj < le)
      {
         // closest approach between edges
         t = es.plus(de.multiply(proj));
      }
      else
      {
         // closest approach beyond edge end
         t = ee;
      }

      XY d = t.minus(n);

      // don't expect to see and hope other forces will pull the edge and node apart
      if (d.isZero())
         return null;

      double l = d.length();
      d = d.divide(l);

      return new NEDRet(l, t, d);
   }

   static double nodeEdgeDist(XY n,
                              XY es,
                              XY ee)
   {
      // direction and length of edge
      XY de = ee.minus(es);

      // don't expect to see and hope other forces will pull the ends apart
      if (de.isZero())
         throw new UnsupportedOperationException("zero length edge");

      double le = de.length();
      de = de.divide(le);

      // line from n to edge start
      XY dnes = n.minus(es);

      // project that line onto the edge direction
      double proj = de.dot(dnes);

      XY t;
      if (proj < 0)
      {
         // closest approach before edge start
         t = es;
      }
      else if (proj < le)
      {
         // closest approach between edges
         t = es.plus(de.multiply(proj));
      }
      else
      {
         // closest approach beyond edge end
         t = ee;
      }

      XY d = t.minus(n);

      return d.length();
   }

   static <T> T removeRandom(Random random, Collection<T> col)
   {
      int which = (int)(random.nextDouble() * col.size());

      Iterator<T> it = col.iterator();

      while(it.hasNext())
      {
         T n = it.next();

         if (which == 0)
         {
            it.remove();

            return n;
         }

         which--;
      }

      // shouldn't happen
      return null;
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
