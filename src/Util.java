import processing.core.PApplet;

import java.util.*;

class Util
{
   // static class
   private Util()
   {
   }

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

      return EdgeIntersect(edge1Start.GetPos(), edge1End.GetPos(),
            edge2Start.GetPos(), edge2End.GetPos());
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
      assert col.size() > 0;

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

   static Collection<INode> FilterByCodes(Collection<INode> nodes, String s)
   {
      assert nodes.size() > 0;

      ArrayList<INode> ret = new ArrayList<>();

      for(INode n : nodes)
      {
         if (n.GetCodes().contains(s))
         {
            ret.add(n);
         }
      }

      return ret;
   }

   static void Line(PApplet app, XY from, XY to)
   {
      app.line((float)from.X, (float)from.Y, (float)to.X, (float)to.Y);
   }

   static void Text(PApplet app, String text, XY pos)
   {
      app.text(text, (float)pos.X, (float)pos.Y);
   }

   static void DrawGraph(Main app, Graph g, boolean show_labels, boolean show_arrows)
   {
      for (INode n : g.AllGraphNodes())
      {
         DrawNode(app, n);
      }

      for (INode n : g.AllGraphNodes())
      {
         DrawConnections(app, n, show_arrows);
      }

      if (show_labels)
      {
         for (INode n : g.AllGraphNodes())
         {
            DrawLabel(app, n);
         }
      }
   }

   private static void DrawNode(PApplet app, INode n)
   {
      app.noStroke();
      app.fill(140);
      app.ellipse((float) n.GetPos().X, (float) n.GetPos().Y,
            (float) n.GetRad(), (float) n.GetRad());
   }

   private static void DrawLabel(PApplet app, INode n)
   {
      app.fill(255, 255, 255);
      app.text(n.GetName(),
            (float) n.GetPos().X, (float) n.GetPos().Y);
   }

   private static void DrawConnections(PApplet app, INode n, boolean show_arrows)
   {
      // in connections are drawn by the other node...
      for(DirectedEdge e : n.GetOutConnections())
      {
         app.stroke(180);
         app.strokeWeight((float)(e.Width * 1.75));
         Util.Line(app, e.Start.GetPos(), e.End.GetPos());

         if (show_arrows)
         {
            XY d = e.End.GetPos().Minus(e.Start.GetPos());
            d = d.Divide(10);

            XY rot = new XY(-d.Y, d.X);

            Util.Line(app, e.End.GetPos(), e.End.GetPos().Minus(d).Minus(rot));
            Util.Line(app, e.End.GetPos(), e.End.GetPos().Minus(d).Plus(rot));
         }
      }
   }

}