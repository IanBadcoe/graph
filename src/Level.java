import java.util.*;

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
         m_base_loops.add(l);
      }

      for(DirectedEdge de : m_graph.AllGraphEdges())
      {
         GeomLayout gl = new RectangularGeomLayout(de.Start.getPos(), de.End.getPos(), de.HalfWidth);
         m_base_loops.add(gl.makeBaseGeometry());
      }
   }

   boolean unionOne(Random r)
   {
      if (m_base_loops.size() == 0)
         return false;

      Loop l = m_base_loops.remove(0);

      LoopSet temp = new LoopSet();
      temp.add(l);

      m_level = Intersector.union(m_level, temp, 1e-6, r, false);

      assert m_level != null;

      return true;
   }

   public void visualise(Random m_union_random)
   {
      LoopSet temp = new LoopSet();
      temp.add(m_base_loops.stream().findFirst().get());

      Intersector.union(m_level, temp, 1e-6, m_union_random, true);
   }

   Collection<Loop> getLoops()
   {
      return Collections.unmodifiableCollection(m_base_loops);
   }

   public LoopSet getLevel()
   {
      return m_level;
   }

   private final Graph m_graph;

   @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
   private final HashMap<INode, GeomLayout> m_layouts = new HashMap<>();
   private final LoopSet m_base_loops = new LoopSet();

   private LoopSet m_level = new LoopSet();
}
