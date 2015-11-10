import java.util.*;

class Util
{
   public static HashSet<DirectedEdgePair> FindCrossingEdges(Collection<DirectedEdge> edges)
   {
      HashSet<DirectedEdgePair> ret = new HashSet<>();

      for (DirectedEdge e1 : edges)
      {
         for (DirectedEdge e2 : edges)
         {
            if (e1 == e2)
               break;

            DirectedEdgePair dep = EdgeIntersect(e1, e2);

            if (dep != null)
            {
               ret.add(dep);
            }
         }
      }

      return ret;
   }

   public static DirectedEdgePair EdgeIntersect(DirectedEdge edge1,
                                                DirectedEdge edge2)
   {
      assert edge1 != null;
      assert edge2 != null;

      OrderedPair<Double, Double> params = EdgeIntersect(edge1.Start, edge1.End, edge2.Start, edge2.End);

      if (params == null)
         return null;

      return new DirectedEdgePair(edge1, edge2, params.First, params.Second);
   }

   public static OrderedPair<Double, Double> EdgeIntersect(INode edge1Start, INode edge1End,
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

      return EdgeIntersect(edge1Start.getPos(), edge1End.getPos(),
            edge2Start.getPos(), edge2End.getPos());
   }

   private static OrderedPair<Double, Double> EdgeIntersect(double edge1StartX, double edge1StartY,
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
   static OrderedPair<Double, Double> UnitEdgeForce(double l, double dmin, double dmax)
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

   public static OrderedPair<Double, Double> EdgeIntersect(XY edge1Start, XY edge1End, XY edge2Start, XY edge2End)
   {
      return EdgeIntersect(
            edge1Start.X, edge1Start.Y,
            edge1End.X, edge1End.Y,
            edge2Start.X, edge2Start.Y,
            edge2End.X, edge2End.Y
      );
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
   static OrderedPair<Double, Double> UnitNodeForce(double l, double summed_radii)
   {
      double ratio = l / summed_radii;
      double force = 0;

      // no attractive forces
      if (ratio > 1)
      {
         return new OrderedPair<>(0.0, 0.0);
      }

      force = (ratio - 1);

      // at the moment the relationship between force and overlap is trivial
      // but will keep the two return values in case the force develops a squared term or something...
      return new OrderedPair<>(-force, force);
   }

   public static ArrayList<OrderedPair<Double,Double>> curveCurveIntersect(Curve c1, Curve c2)
   {
      if (c1 instanceof CurveCircle)
      {
         return circleCurveIntersect((CurveCircle)c1, c2);
      }

      throw new UnsupportedOperationException("Unknown type of curve");
   }

   private static ArrayList<OrderedPair<Double,Double>> circleCurveIntersect(CurveCircle c1, Curve c2)
   {
      if (c2 instanceof CurveCircle)
      {
         return circleCircleIntersect(c1, (CurveCircle)c2);
      }

      throw new UnsupportedOperationException("Unknown type of curve");
   }

   public static ArrayList<OrderedPair<Double,Double>> circleCircleIntersect(CurveCircle c1, CurveCircle c2)
   {
      // coincident
      if (c1.equals(c2))
         return null;

      OrderedPair<XY, XY> pts = circleCircleIntersect(c1.Position, c1.Radius, c2.Position, c2.Radius);

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

   public static OrderedPair<XY, XY> circleCircleIntersect(XY c1, double r1, XY c2, double r2)
   {
      double dist_2 = c1.Minus(c2).Length2();
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

      // should have assured this with the ifs above...
      assert delta_2 >= 0;

      double delta = 0.25 * Math.sqrt(delta_2);

      double xi1 = (a + c) / 2
            + (c - a) * (r1 * r1 - r2 * r2) / (2 * dist_2)
            + 2 * (b - d) * delta / dist_2;
      double xi2 = (a + c) / 2
            + (c - a) * (r1 * r1 - r2 * r2) / (2 * dist_2)
            - 2 * (b - d) * delta / dist_2;

      double yi1 = (b + d) / 2
            + (b - d) * (r1 * r1 - r2 * r2) / (2 * dist_2)
            + 2 * (a - c) * delta / dist_2;
      double yi2 = (b + d) / 2
            + (b - d) * (r1 * r1 - r2 * r2) / (2 * dist_2)
            - 2 * (a - c) * delta / dist_2;

      XY p1 = new XY(xi1, yi1);

      XY p2 = null;

      if (delta > 1e-6)
      {
         p2 = new XY(xi2, yi2);
      }

      return new OrderedPair<>(p1, p2);
   }

   static class NEDRet
   {
      final double Dist;
      final XY Target;  // point of closest approach of Node to Edge
      final XY Direction;  // direction from Node to (Tx, TY)

      NEDRet(double dist,
             XY target,
             XY direction)
      {
         Dist = dist;
         Target = target;
         Direction = direction;
      }
   }

   static NEDRet NodeEdgeDist(XY n,
                              XY es,
                              XY ee)
   {
      // direction and length of edge
      XY de = ee.Minus(es);

      // don't expect to see and hope other forces will pull the ends apart
      if (de.IsZero())
         return null;

      double le = de.Length();
      de = de.Divide(le);

      // line from n to edge start
      XY dnes = n.Minus(es);

      // project that line onto the edge direction
      double proj = de.Dot(dnes);

      XY t;
      if (proj < 0)
      {
         // closest approach before edge start
         t = es;
      }
      else if (proj < le)
      {
         // closest approach between edges
         t = es.Plus(de.Multiply(proj));
      }
      else
      {
         // closest approach beyond edge end
         t = ee;
      }

      XY d = t.Minus(n);

      // don't expect to see and hope other forces will pull the edge and node apart
      if (d.IsZero())
         return null;

      double l = d.Length();
      d = d.Divide(l);

      return new NEDRet(l, t, d);
   }

   static <T> T RemoveRandom(Random random, Collection<T> col)
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
}