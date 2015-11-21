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

         Loop base = gl.makeBaseGeometry();

         // could have node with no geometry?
         if (base != null)
         {
            m_base_loops.add(base);
         }

         LoopSet details = gl.makeDetailGeometry();

         // can definitely have no details
         if (details != null)
         {
            m_detail_loop_sets.add(details);
         }
      }

      for(DirectedEdge de : m_graph.AllGraphEdges())
      {
         GeomLayout gl = new RectangularGeomLayout(de.Start.getPos(), de.End.getPos(), de.HalfWidth);
         m_base_loops.add(gl.makeBaseGeometry());
      }
   }

   boolean unionOne(Random r)
   {
      if (m_base_loops.size() > 0)
      {
         Loop l = m_base_loops.remove(0);

         m_level = Intersector.union(m_level, new LoopSet(l), 1e-6, r, false);

         assert m_level != null;

         return true;
      }

      if (m_detail_loop_sets.size() > 0)
      {
         LoopSet ls = m_detail_loop_sets.remove(0);

         m_level = Intersector.union(m_level, ls, 1e-6, r, false);

         assert m_level != null;

         return true;
      }

      return false;
   }

   public void visualise(Random m_union_random)
   {
      Loop temp =
            m_base_loops.stream().findFirst().get();

      Intersector.union(m_level, new LoopSet(temp), 1e-6, m_union_random, true);
   }

   Collection<Loop> getLoops()
   {
      return Collections.unmodifiableCollection(m_base_loops);
   }


   public Collection<LoopSet> getDetailLoopSets()
   {
      return Collections.unmodifiableCollection(m_detail_loop_sets);
   }

   public LoopSet getLevel()
   {
      return m_level;
   }

   private final Graph m_graph;

   @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
   private final HashMap<INode, GeomLayout> m_layouts = new HashMap<>();
   private final ArrayList<Loop> m_base_loops = new ArrayList<>();
   private final ArrayList<LoopSet> m_detail_loop_sets = new ArrayList<>();

   private LoopSet m_level = new LoopSet();

}
