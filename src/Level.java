import java.util.*;
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
         GeomLayout gl = n.geomLayoutCreator().create(n);
         m_layouts.put(n, gl);
         Loop l = gl.makeBaseGeometry();
         m_loops.put(n, l);
      }
   }

   Collection<Loop> getLoops()
   {
      return Collections.unmodifiableCollection(m_loops.values());
   }

   Graph m_graph;

   HashMap<INode, GeomLayout> m_layouts = new HashMap<>();
   HashMap<INode, Loop> m_loops = new HashMap<>();
}
