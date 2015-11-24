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

      for(INode n : m_graph.AllGraphNodes())
      {
         GeomLayout gl = n.geomLayoutCreator().create(n);
         m_layouts.put(n, gl);

         Loop base = gl.makeBaseGeometry();

         // could have node with no geometry?
         if (base != null)
         {
            bounds = bounds.union(base.getBounds());

            m_base_loops.add(base);
         }

         LoopSet details = gl.makeDetailGeometry();

         // can definitely have no details
         if (details != null)
         {
            // bounds of details no-bigger than base, so can ignore

            m_detail_loop_sets.add(details);
         }
      }

      for(DirectedEdge de : m_graph.AllGraphEdges())
      {
         // TODO : store this on the edge to allow corridor geometry options
         GeomLayout gl = new RectangularGeomLayout(de.Start.getPos(), de.End.getPos(), de.HalfWidth);
         Loop l = gl.makeBaseGeometry();

         // may one day have corridors w/o geometry, but for the moment not

         bounds = bounds.union(l.getBounds());

         m_base_loops.add(l);
      }

      m_bounds = bounds;
   }

   boolean unionOne(Random r)
   {
      if (m_base_loops.size() > 0)
      {
         Loop l = m_base_loops.remove(0);

         m_level = Intersector.union(m_level, new LoopSet(l), 1e-6, r, false);

         assert m_level != null;

         return false;
      }

      if (m_detail_loop_sets.size() > 0)
      {
         LoopSet ls = m_detail_loop_sets.remove(0);

         m_level = Intersector.union(m_level, ls, 1e-6, r, false);

         assert m_level != null;

         return false;
      }

      return true;
   }

   public void visualise(Random m_union_random)
   {
      Loop temp =
            m_base_loops.stream().findFirst().get();

      Intersector.union(m_level, new LoopSet(temp), 1e-6, m_union_random, true);
   }

   Collection<WallLoop> getWallLoops()
   {
      return Collections.unmodifiableCollection(m_wall_loops);
   }

   public void finalise()
   {
      for(Loop l : m_level)
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
      CC cell = GridWalker.posToGridCell(w.Start.plus(w.End).divide(2), m_cell_size);

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

   private XY cellCentre(CC cell)
   {
      return cellOrigin(cell).plus(new XY(m_cell_size / 2, m_cell_size / 2));
   }

   private XY cellOrigin(CC cell)
   {
      return new XY(cell.First * m_cell_size, cell.Second * m_cell_size);
   }

   private final Graph m_graph;

   @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
   private final HashMap<INode, GeomLayout> m_layouts = new HashMap<>();
   private final ArrayList<Loop> m_base_loops = new ArrayList<>();
   private final ArrayList<LoopSet> m_detail_loop_sets = new ArrayList<>();

   private final HashMap<CC, ArrayList<Wall>> m_wall_map
         = new HashMap<>();

   private LoopSet m_level = new LoopSet();

   private Box m_bounds;

   @SuppressWarnings("FieldCanBeLocal")
   private final double m_cell_size;
   private final double m_wall_facet_length;

   private final WallLoopSet m_wall_loops = new WallLoopSet();
}
