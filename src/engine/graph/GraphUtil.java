package engine.graph;

import engine.OrderedPair;
import engine.Util;

import java.util.Collection;
import java.util.HashSet;

public class GraphUtil
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

      return Util.edgeIntersect(edge1Start.getPos(), edge1End.getPos(),
            edge2Start.getPos(), edge2End.getPos());
   }
}
