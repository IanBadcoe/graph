import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

abstract class GeomLayout
{
   interface GeomLayoutCreateFromNode
   {
      GeomLayout create(Level l, INode n);
   }

   GeomLayout(Level level)
   {
      m_level = level;
   }

   abstract void makeBaseGeometry();

   void IntersectNeighbour(GeomLayout neighb)
   {
   }

   Collection<GeomEdge> getEdges()
   {
      return m_edges.stream().collect(Collectors.toCollection(ArrayList::new));
   }

   enum EdgeCutSide
   {
      RemoveStart,
      RemoveEnd
   }

   void cutEdge(GeomEdge ge, XY position, EdgeCutSide side)
   {
   }

   void addEdge(GeomEdge ge)
   {
      m_edges.add(ge);
      m_level.addEdge(ge);
   }

   void removeEdge(GeomEdge ge)
   {
      m_edges.remove(ge);
      m_level.removeEdge(ge);
   }


   // we cut all edges up into fragments shorter than this
   // is this a good idea?
   static double MaxEdgeLength = 10;

   private final HashSet<GeomEdge> m_edges = new HashSet<>();
   private final Level m_level;
}
