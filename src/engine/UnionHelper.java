package engine;

import java.util.*;

@SuppressWarnings("WeakerAccess")
public class UnionHelper
{
   // exposed for testing but there could be cases where client code wants to reach-in
   // and add some special piece of geometry
   public void addBaseLoop(Loop l)
   {
      m_base_loops.add(l);
   }

   // exposed for testing but there could be cases where client code wants to reach-in
   // and add some special piece of geometry
   public void addDetailLoops(LoopSet ls)
   {
      m_detail_loop_sets.add(ls);
   }

   public Collection<Loop> getBaseLoops()
   {
      return Collections.unmodifiableCollection(m_base_loops);
   }

   public Collection<LoopSet> getDetailLoopSets()
   {
      return Collections.unmodifiableCollection(m_detail_loop_sets);
   }

   // returns true when all complete
   public boolean unionOne(Random r)
   {
      if (m_base_loops.size() > 0)
      {
         Loop l = m_base_loops.get(0);
         LoopSet ls = new LoopSet(l);

         m_merged_loops = Intersector.union(m_merged_loops, ls, 1e-6, r);

         assert m_merged_loops != null;

         m_base_loops.remove(0);

         return false;
      }

      if (m_detail_loop_sets.size() > 0)
      {
         LoopSet ls = m_detail_loop_sets.remove(0);

         m_merged_loops = Intersector.union(m_merged_loops, ls, 1e-6, r);

         assert m_merged_loops != null;

         return false;
      }

      return true;
   }

   void generateGeometry(Graph graph)
   {
      for (INode n : graph.allGraphNodes())
      {
         GeomLayout gl = n.geomLayoutCreator().create(n);

         Loop base = gl.makeBaseGeometry();

         // can have node with no geometry...  at least in unit-tests
         if (base != null)
         {
            addBaseLoop(base);
         }

         LoopSet details = gl.makeDetailGeometry();

         // can definitely have no details
         if (details != null)
         {
            // bounds of details no-bigger than base, so can ignore

            addDetailLoops(details);
         }
      }

      for (DirectedEdge de : graph.allGraphEdges())
      {
         GeomLayout gl = de.LayoutCreator.create(de);

         Loop l = gl.makeBaseGeometry();

         if (l != null)
         {
            addBaseLoop(l);
         }
      }

      Optional<INode> start = graph.allGraphNodes().stream().filter(
            x -> x.getName().equals("Start")).findFirst();

      if (start.isPresent())
      {
         m_start_pos = start.get().getPos();
      }
   }

   private void calculateBounds()
   {
      Box bounds = new Box();

      for (Loop l : m_merged_loops)
      {
         bounds = bounds.union(l.getBounds());
      }

      m_bounds = bounds;
   }

   public Level makeLevel(double cell_size, double wall_facet_length)
   {
      calculateBounds();

      Level ret = new Level(cell_size, wall_facet_length, m_bounds, m_start_pos);

      for (Loop l : m_merged_loops)
      {
         ArrayList<OrderedPair<XY, XY>> loop_pnts = l.facetWithNormals(wall_facet_length);

         OrderedPair<XY, XY> prev = loop_pnts.get(loop_pnts.size() - 1);

         WallLoop wl = new WallLoop();

         Wall prev_w = null;

         for (OrderedPair<XY, XY> curr : loop_pnts)
         {
            // normal (in "Second") is from 1/2 way along the segment that starts at "prev"
            Wall w = new Wall(prev.First, curr.First, prev.Second);

            if (prev_w != null)
            {
               w.setPrev(prev_w);
               prev_w.setNext(w);
            }

            wl.add(w);

            prev_w = w;

            prev = curr;
         }

         //noinspection ConstantConditions
         prev_w.setNext(wl.get(0));
         wl.get(0).setPrev(prev_w);

         ret.addWallLoop(wl);
      }

      return ret;
   }

   Collection<Loop> getMergedLoops()
   {
      return Collections.unmodifiableCollection(m_merged_loops);
   }

   private final ArrayList<Loop> m_base_loops = new ArrayList<>();
   private final ArrayList<LoopSet> m_detail_loop_sets = new ArrayList<>();

   private LoopSet m_merged_loops = new LoopSet();

   private Box m_bounds;
   private XY m_start_pos;
}
