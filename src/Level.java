import java.util.*;

class Level
{
   // cell-coordinate
   static private class CC extends OrderedPair<Integer, Integer>
   {
      CC(int x, int y)
      {
         super(x, y);
      }

      CC minus(CC rhs)
      {
         return new CC(First - rhs.First, Second - rhs.Second);
      }

      CC plus(CC rhs)
      {
         return new CC(First + rhs.First, Second + rhs.Second);
      }
   }

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
      CC cell = posToGridCell(w.Start.plus(w.End).divide(2));

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

   // place prob_to beyond edge of level to definitely find something
   // however far
   public Wall nearestWall(XY nearest_to, XY probe_to)
   {
      XY diff = probe_to.minus(nearest_to);

      // this will step us along search line in whichever axis we travers fastest
      XY major_step;

      if (Math.abs(diff.X) > Math.abs(diff.Y))
      {
         major_step = new XY(Math.signum(diff.X), 0);
      }
      else
      {
         major_step = new XY(0, Math.signum(diff.Y));
      }

      CC near_point_cell = posToGridCell(nearest_to);

      double max_hit_dist = (m_cell_size + m_wall_facet_length) / 2;

      assert cellCentre(near_point_cell).minus(nearest_to).length() < max_hit_dist;

      XY step_back = nearest_to.minus(major_step);

      XY curr = nearest_to;

      // if we are close enough to the trailing edge of the cell we're in
      if (cellCentre(posToGridCell((step_back))).minus(nearest_to).length() < max_hit_dist)
      {
         curr = step_back;
      }
   }

   private XY cellCentre(CC cell)
   {
      return cellOrigin(cell).plus(new XY(m_cell_size / 2, m_cell_size / 2));
   }

   private XY cellOrigin(CC cell)
   {
      return new XY(cell.First * m_cell_size, cell.Second * m_cell_size);
   }

   private CC posToGridCell(XY pos)
   {
      XY rel_pos = pos.minus(m_bounds.Min);

      XY cell_pos = rel_pos.divide(20);

      return new CC((int)cell_pos.X, (int)cell_pos.Y);
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
