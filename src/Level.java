import java.util.*;

class Level
{
   Level(Graph graph, double cell_size, double wall_facet_length)
   {
      m_graph = graph;

      m_cell_size = cell_size;
      m_wall_facet_length = wall_facet_length;
   }

   void generateGeometry()
   {
      Box bounds = new Box();

      for(INode n : m_graph.allGraphNodes())
      {
         GeomLayout gl = n.geomLayoutCreator().create(n);

         Loop base = gl.makeBaseGeometry();

         // could have node with no geometry?
         if (base != null)
         {
            bounds = bounds.union(base.getBounds());

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

      for(DirectedEdge de : m_graph.allGraphEdges())
      {
         GeomLayout gl = de.LayoutCreator.create(de);

         Loop l = gl.makeBaseGeometry();

         if (l != null)
         {
            bounds = bounds.union(l.getBounds());

            addBaseLoop(l);
         }
      }

      m_bounds = bounds;

      Optional<INode> start = m_graph.allGraphNodes().stream().filter(
            x -> x.getName().equals("Start")).findFirst();

      if (start.isPresent())
      {
         m_start_pos = start.get().getPos();
      }
   }

   // exposed for testing but there could be cases where client code wants to reach-in
   // and add some special piece of geometry
   public void addBaseLoop(Loop l)
   {
      m_base_loops.add(l);
   }

   // exposed for testing but there could be cases where client code wants to reach-in
   // and add some special piece of geometry
   void addDetailLoops(LoopSet ls)
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
   boolean unionOne(Random r)
   {
      if (m_base_loops.size() > 0)
      {
         Loop l = m_base_loops.get(0);
         LoopSet ls = new LoopSet(l);

         m_merged_loops = Intersector.union(m_merged_loops, ls, 1e-6, r, false);

         assert m_merged_loops != null;

         m_base_loops.remove(0);

         return false;
      }

      if (m_detail_loop_sets.size() > 0)
      {
         LoopSet ls = m_detail_loop_sets.remove(0);

         m_merged_loops = Intersector.union(m_merged_loops, ls, 1e-6, r, false);

         assert m_merged_loops != null;

         return false;
      }

      return true;
   }

   public void visualise(Random m_union_random)
   {
      Loop temp =
            m_base_loops.stream().findFirst().get();

      Intersector.union(m_merged_loops, new LoopSet(temp), 1e-6, m_union_random, true);
   }

   Collection<WallLoop> getWallLoops()
   {
      return Collections.unmodifiableCollection(m_wall_loops);
   }

   public void finalise()
   {
      for(Loop l : m_merged_loops)
      {
         ArrayList<OrderedPair<XY, XY>> loop_pnts = l.facetWithNormals(m_wall_facet_length);

         OrderedPair<XY, XY> prev = loop_pnts.get(loop_pnts.size() - 1);

         WallLoop wl = new WallLoop();

         Wall prev_w = null;

         for(OrderedPair<XY, XY> curr : loop_pnts)
         {
            Wall w = new Wall(prev.First, curr.First,
                  prev.Second.plus(curr.Second).makeUnit());

            if (prev_w != null)
            {
               w.setPrev(prev_w);
               prev_w.setNext(w);
            }

            addWallToMap(w);
            wl.add(w);

            prev_w = w;

            prev = curr;
         }

         //noinspection ConstantConditions
         prev_w.setNext(wl.get(0));
         wl.get(0).setPrev(prev_w);

         m_wall_loops.add(wl);
      }

   }

   private void addWallToMap(Wall w)
   {
      // using centre point halves the effective length of the facet,
      // making our cell-search distances smaller
      CC cell = GridWalker.positionToCell(w.Start.plus(w.End).divide(2), m_cell_size);

      ArrayList<Wall> walls = m_wall_map.get(cell);

      if (walls == null)
      {
         walls = new ArrayList<>();

         m_wall_map.put(cell, walls);
      }

      walls.add(w);
   }

   public Box getBounds()
   {
      return m_bounds;
   }

   // place probe_to beyond edge of level to definitely find something
   // however far
   public Wall nearestWall(XY nearest_to, XY dir, double length)
   {
      XY end = nearest_to.plus(dir.multiply(length));

      GridWalker ge = new GridWalker(m_cell_size, nearest_to, end, m_wall_facet_length);

      CC cell;

      Wall ret = null;

      while((cell = ge.nextCell()) != null)
      {
         ArrayList<Wall> walls = m_wall_map.get(cell);

         if (walls != null)
         {
            for(Wall w : walls)
            {
               OrderedPair<Double, Double> intersect = Util.edgeIntersect(nearest_to, end,
                     w.Start, w.End);

               if (intersect != null)
               {
                  ret = w;

                  // shorten length by the proportional position of the intersection
                  length *= intersect.First;
                  end = nearest_to.plus(dir.multiply(length));

                  ge.resetRayEnd(end);
               }
            }
         }
      }

      return ret;
   }

   Collection<Loop> getMergedLoops()
   {
      return Collections.unmodifiableCollection(m_merged_loops);
   }

   public XY startPos()
   {
      return m_start_pos;
   }

   public Collection<Wall> getVisibleWalls(XY visibility_pos)
   {
      HashSet<Wall> ret = new HashSet<>();

      for(WallLoop wl : m_wall_loops)
      {
         //noinspection Convert2streamapi
         for(Wall w : wl)
         {
            if (!ret.contains(w))
            {
               XY rel = w.midPoint().minus(visibility_pos);
               double l = rel.length();
               XY dir = rel.divide(l);

               Wall c = nearestWall(visibility_pos,
                     dir, l + 1);

               ret.add(c);
            }
         }
      }

      return ret;
   }

   private final Graph m_graph;

   private final ArrayList<Loop> m_base_loops = new ArrayList<>();
   private final ArrayList<LoopSet> m_detail_loop_sets = new ArrayList<>();

   private final HashMap<CC, ArrayList<Wall>> m_wall_map
         = new HashMap<>();

   private LoopSet m_merged_loops = new LoopSet();

   private Box m_bounds;

   @SuppressWarnings("FieldCanBeLocal")
   private final double m_cell_size;
   private final double m_wall_facet_length;

   private final WallLoopSet m_wall_loops = new WallLoopSet();

   private XY m_start_pos;
}
