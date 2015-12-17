package engine;

import java.util.*;
import java.util.stream.Collectors;

public class Level implements IPhysicalLevel
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

   public Collection<WallLoop> getWallLoops()
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

//            if (prev_w != null)
//            {
//               w.setPrev(prev_w);
//               prev_w.setNext(w);
//            }

            addWallToMap(w);
            wl.add(w);

            prev_w = w;

            prev = curr;
         }

//         //noinspection ConstantConditions
//         prev_w.setNext(wl.get(0));
//         wl.get(0).setPrev(prev_w);

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

   public static class RayCollision
   {
      public RayCollision(Wall w, double dist, XY point)
      {
         WallHit = w;
         DistanceTo = dist;
         ImpactPoint = point;
      }

      public final Wall WallHit;
      public final double DistanceTo;
      public final XY ImpactPoint;
   }

   public RayCollision nearestWall(XY nearest_to, XY step)
   {
      double len = step.length();
      XY dir = step.divide(len);
      return nearestWall(nearest_to, dir, len);
   }

   // place probe_to beyond edge of level to definitely find something
   // however far
   public RayCollision nearestWall(XY nearest_to, XY dir, double length)
   {
      assert dir.isUnit();

      XY end = nearest_to.plus(dir.multiply(length));

      GridWalker ge = new GridWalker(m_cell_size, nearest_to, end, m_wall_facet_length);

      CC cell;

      Wall hit = null;

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
                  hit = w;

                  // shorten length by the proportional position of the intersection
                  length *= intersect.First;
                  end = nearest_to.plus(dir.multiply(length));

                  ge.resetRayEnd(end);
               }
            }
         }
      }

      return new RayCollision(hit, length, nearest_to.plus(dir.multiply(length)));
   }

   public static class MovableCollision
   {
      MovableCollision(XY worldPoint, double fractionTravelled, XY normal)
      {
         WorldPoint = worldPoint;
         FractionTravelled = fractionTravelled;
         Normal = normal;
      }

      final XY WorldPoint;
      final double FractionTravelled;
      final XY Normal;
   }

   MovableCollision collide(Movable m,
         Movable.DynamicsPosition start, Movable.DynamicsPosition end,
         double resolution)
   {
      // we're looking for the first interval within the overall movement
      // that is of length at most "resolution" and where the start has no
      // collision and the end does have a collision
      //
      // we'll stop the movable at the start of this interval and calculate the
      // collision params for the collision that would be about to occur

      assert colliding(m, start) == null;

      double s_frac = 0;
      double e_frac = 1;

      MovableCollision ret = colliding(m, end);

      if (ret == null)
         return null;

      double iLength2 = end.Position.minus(start.Position).length2();

      resolution *= resolution;

      while(iLength2 > resolution)
      {
         double m_frac = (s_frac + e_frac) / 2;

         Movable.DynamicsPosition mid = start.interpolate(end, m_frac);

         MovableCollision m_col = colliding(m, mid);

         if (m_col != null)
         {
            e_frac = m_frac;
            ret = m_col;
         }
         else
         {
            s_frac = m_frac;
         }

         iLength2 = end.Position.minus(start.Position).length2();
      }

      return ret;
   }

   private static class Line
   {
      Line(XY start, XY end)
      {
         Start = start;
         End = end;
      }

      final XY Start;
      final XY End;
   }

   private class Transformer
   {
      Transformer(Movable.DynamicsPosition pos)
      {
         m_translate = pos.Position;
         m_rotate = new Matrix2D(pos.Orientation);
      }

      XY transform(XY in)
      {
         return m_rotate.multiply(in).plus(m_translate);
      }

      final XY m_translate;
      final Matrix2D m_rotate;
   }

   ArrayList<Line> makeEdges(Movable m, Movable.DynamicsPosition pos)
   {
      Transformer t = new Transformer(pos);

      ArrayList<XY> transformed_corners
            = m.getCorners().stream().map(x -> t.transform(x)).collect(Collectors.toCollection(ArrayList::new));

      XY prev = transformed_corners.get(transformed_corners.size() - 1);

      ArrayList<Line> ret = new ArrayList<>();

      for(XY curr : transformed_corners)
      {
         ret.add(new Line(prev, curr));
         prev = curr;
      }

      return ret;
   }

   private MovableCollision colliding(Movable m, Movable.DynamicsPosition pos)
   {
      GridWalker gw = new GridWalker(m_cell_size, pos.Position, pos.Position, m.getRadius());

      ArrayList<Line> edges = makeEdges(m, pos);

      return null;
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

               RayCollision wcr = nearestWall(visibility_pos,
                     dir, l + 1);

               ret.add(wcr.WallHit);
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
