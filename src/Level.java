import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

class Level
{
   Level(Graph graph)
   {
      m_graph = graph;
   }

   void generateGeometry()
   {
      for(INode n : m_graph.AllGraphNodes())
      {
         GeomLayout gl = n.geomLayoutCreator().create(this, n);
         m_layouts.add(gl);
         gl.makeBaseGeometry();
      }
   }

   void addEdge(GeomEdge ge)
   {
      m_edges.add(ge);
   }

   void removeEdge(GeomEdge ge)
   {
      m_edges.remove(ge);
   }

   Collection<GeomEdge> getEdges()
   {
      return m_edges.stream().collect(Collectors.toCollection(ArrayList::new));
   }

   private final ArrayList<GeomEdge> m_edges = new ArrayList<>();

   Graph m_graph;

   HashSet<GeomLayout> m_layouts = new HashSet<>();
}
