package engine;

import java.util.*;

class Util
{
   public static HashSet<DirectedEdgePair> findCrossingEdges(Collection<DirectedEdge> edges)
   {
      HashSet<DirectedEdgePair> ret = new HashSet<>();

      for (DirectedEdge e1 : edges)
      {
         for (DirectedEdge e2 : edges)
         {
            if (e1 == e2)
               break;

            DirectedEdgePair dep = edgeIntersect(e1, e2);

            if (dep != null)
            {
               ret.add(dep);
            }
         }
      }

      return ret;
   }

   public static DirectedEdgePair edgeIntersect(DirectedEdge edge1,
                                                DirectedEdge edge2)
   {
      assert edge1 != null;
      assert edge2 != null;

      OrderedPair<Double, Double> params = edgeIntersect(edge1.Start, edge1.End, edge2.Start, edge2.End);

      if (params == null)
         return null;

      return new DirectedEdgePair(edge1, edge2, params.First, params.Second);
   }

   public static OrderedPair<Double, Double> edgeIntersect(INode edge1Start, INode edge1End,
                                                           INode edge2Start, INode edge2End)
   {
      assert edge1Start != null;
      assert edge1End != null;
      assert edge2Start != null;
      assert edge2End != null;

      // connecting lines not considered crossing
      if (edge1Start == edge2Start
            || edge1Start == edge2End
            || edge1End == edge2Start
            || edge1End == edge2End)
      {
         return null;
      }

      return edgeIntersect(edge1Start.getPos(), edge1End.getPos(),
            edge2Start.getPos(), edge2End.getPos());
   }

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

   public static ArrayList<OrderedPair<Double,Double>> curveCurveIntersect(Curve c1, Curve c2)
   {
      if (c1.equals(c2))
         return null;

      OrderedPair<XY, XY> pts;

      if (c1 instanceof CircleCurve)
      {
         pts = circleCurveIntersect((CircleCurve)c1, c2);
      }
      else if (c1 instanceof LineCurve)
      {
         pts = lineCurveIntersect((LineCurve)c1, c2);
      }
      else
      {
         throw new UnsupportedOperationException("Unknown type of curve");
      }

      if (pts == null)
         return null;

      ArrayList<OrderedPair<Double,Double>> ret = new ArrayList<>();

      {
         Double pc1 = c1.findParamForPoint(pts.First, 1e-6);
         Double pc2 = c2.findParamForPoint(pts.First, 1e-6);

         if (pc1 != null && pc2 != null)
         {
            ret.add(new OrderedPair<>(pc1, pc2));
         }
      }

      if (pts.Second != null)
      {
         Double pc1 = c1.findParamForPoint(pts.Second, 1e-6);
         Double pc2 = c2.findParamForPoint(pts.Second, 1e-6);

         if (pc1 != null && pc2 != null)
         {
            ret.add(new OrderedPair<>(pc1, pc2));
         }
      }

      if (ret.size() > 0)
         return ret;

      return null;
   }

   private static OrderedPair<XY, XY> circleCurveIntersect(CircleCurve c1, Curve c2)
   {
      if (c2 instanceof CircleCurve)
      {
         return circleCircleIntersect(c1, (CircleCurve)c2);
      }
      else if (c2 instanceof LineCurve)
      {
         return circleLineIntersect(c1, (LineCurve)c2);
      }

      throw new UnsupportedOperationException("Unknown type of curve");
   }

   private static OrderedPair<XY, XY> circleCircleIntersect(CircleCurve c1, CircleCurve c2)
   {
      return circleCircleIntersect(c1.Position, c1.Radius, c2.Position, c2.Radius);
   }

   private static OrderedPair<XY, XY> lineCurveIntersect(LineCurve c1, Curve c2)
   {
      if (c2 instanceof CircleCurve)
      {
         return lineCircleIntersect(c1, (CircleCurve)c2);
      }
      else if (c2 instanceof LineCurve)
      {
         return lineLineIntersect(c1, (LineCurve)c2);
      }


      throw new UnsupportedOperationException("Unknown type of curve");
   }

   private static OrderedPair<XY, XY> lineLineIntersect(LineCurve l1, LineCurve l2)
   {
      OrderedPair<Double, Double> ret = edgeIntersect(
            l1.startPos(), l1.endPos(),
            l2.startPos(), l2.endPos());

      if (ret == null)
         return null;

      // inefficient, am going to calculate a position here, just so that I can
      // back-calculate params from it above, however line-line is the only intersection that gives
      // direct params so it would be a pain to change the approach for this one case

      return new OrderedPair<>(
            l1.computePos(l1.StartParam + (l1.EndParam - l1.StartParam) * ret.First),
            null);
   }

   private static OrderedPair<XY, XY> lineCircleIntersect(LineCurve l1, CircleCurve c2)
   {
      return circleLineIntersect(c2, l1);
   }

   // algorithm stolen with thanks from:
   // http://stackoverflow.com/questions/1073336/circle-line-segment-collision-detection-algorithm
   private static OrderedPair<XY, XY> circleLineIntersect(CircleCurve c1, LineCurve l2)
   {
      XY d = l2.endPos().minus(l2.startPos());
      XY f = l2.startPos().minus(c1.Position);

      double a = d.length2();
      double b = 2 * f.dot(d);
      double c = f.length2() - c1.Radius * c1.Radius;

      double discriminant_2 = b * b - 4 * a * c;

      if( discriminant_2 < 0 )
      {
         return null;
      }

      XY hit1 = null;
      XY hit2 = null;

      // ray didn't totally miss sphere,
      // so there is a solution to
      // the equation.

      double discriminant = Math.sqrt(discriminant_2);

      // either solution may be on or off the ray so need to test both
      // t1 is always the smaller value, because BOTH discriminant and
      // a are nonnegative.
      double t1 = (-b - discriminant) / (2 * a);
      double t2 = (-b + discriminant) / (2 * a);

      // 3x HIT cases:
      //          -o->             --|-->  |            |  --|->
      // Impale(t1 hit,t2 hit), Poke(t1 hit,t2>1), ExitWound(t1<0, t2 hit),

      // 3x MISS cases:
      //       ->  o                     o ->              | -> |
      // FallShort (t1>1,t2>1), Past (t1<0,t2<0), CompletelyInside(t1<0, t2>1)

      double tol = 1e-12;

      if( t1 >= -tol && t1 <= 1 + tol )
      {
         hit1 = l2.computePos(l2.StartParam + (l2.EndParam - l2.StartParam) * t1);
      }

      if( t2 >= -tol && t2 <= 1 + tol )
      {
         hit2 = l2.computePos(l2.StartParam + (l2.EndParam - l2.StartParam) * t2);
      }

      if (hit1 == null)
      {
         hit1 = hit2;
         hit2 = null;
      }

      if (hit1 == null)
         return null;

      return new OrderedPair<>(hit1, hit2);
   }

   private static OrderedPair<XY, XY> circleCircleIntersect(XY c1, double r1, XY c2, double r2)
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

   public static boolean loopsIntersect(Loop l1, Loop l2)
   {
      for(Curve c1 : l1.getCurves())
      {
         for(Curve c2 : l2.getCurves())
         {
            if (curveCurveIntersect(c1, c2) != null)
               return true;
         }
      }

      return false;
   }

   public static double atan2(XY vec)
   {
      //noinspection SuspiciousNameCombination
      return Math.atan2(vec.X, vec.Y);
   }

   static class NEDRet
   {
      final double Dist;
      final XY Target;  // point of closest approach of engine.Node to Edge
      final XY Direction;  // direction from engine.Node to (Tx, TY)

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
   static NEDRet nodeEdgeForceDist(XY n,
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
   static double fixupAngle(double a)
   {
      while(a < 0)
         a += Math.PI * 2;

      while(a >= Math.PI * 2)
         a -= Math.PI * 2;

      return a;
   }

   static boolean clockAwareAngleCompare(double a1, double a2, double tol)
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

   public static class EPORet
   {
      public final boolean Overlaps;
      public final double PStart;
      public final double PEnd;

      public EPORet(boolean overlaps, double pStart, double pEnd)
      {
         Overlaps = overlaps;
         PStart = pStart;
         PEnd = pEnd;
      }
   }

   // measures whether e2, projected onto e1, overlaps by more than tolerance
   // with the parameter range of e1
   public static EPORet edgeParameterOverlap(XY e1Start, XY e1End, XY e2Start, XY e2End, double tolerance)
   {
      XY e1vec = e1End.minus(e1Start);

      // vector divided by l, gives us a projected distance between 0 and 1
      e1vec = e1vec.divide(e1vec.length2());

      double p_start = e1vec.dot(e2Start.minus(e1Start));
      double p_end = e1vec.dot(e2End.minus(e1Start));

      // clamp to parameter range of 0 -> 1
      p_start = Math.max(Math.min(p_start, 1), 0);
      p_end = Math.max(Math.min(p_end, 1), 0);

      return new EPORet(Math.abs(p_start - p_end) > tolerance * 2, p_start, p_end);
   }

   public static double calcFractionalPosition(XY start, XY end, XY pos)
   {
      XY vec = end.minus(start);

      // vector divided by l, gives us a projected distance between 0 and 1
      vec = vec.divide(vec.length2());

      return vec.dot(pos.minus(start));
   }
}
