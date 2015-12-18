package engine;

import java.util.*;
import java.util.stream.Collectors;

public class Level
{
   Level(double cell_size, double wall_facet_length, Box bounds, XY start_pos)
   {
      m_cell_size = cell_size;
      m_wall_facet_length = wall_facet_length;
      m_bounds = bounds;
      m_start_pos = start_pos;
   }

   public Collection<WallLoop> getWallLoops()
   {
      return Collections.unmodifiableCollection(m_wall_loops);
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

   public void addWallLoop(WallLoop wl)
   {
      wl.forEach(this::addWallToMap);

      m_wall_loops.add(wl);
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

   @SuppressWarnings("SameParameterValue")
   public MovableCollision collide(Movable m,
                                   Movable.DynamicsPosition start, Movable.DynamicsPosition end,
                                   double resolution)
   {
      // we're looking for the first interval within the overall movement
      // that is of length at most "resolution" and where the start has no
      // collision and the end does have a collision
      //
      // we'll stop the movable at the start of this interval and calculate the
      // collision params for the collision that would be about to occur

      assert collide(m, start) == null;

      double s_frac = 0;
      double e_frac = 1;

      ColRet col = collide(m, end);

      if (col == null)
         return null;

      double iLength2 = end.Position.minus(start.Position).length2();

      resolution *= resolution;

      while(iLength2 > resolution)
      {
         double m_frac = (s_frac + e_frac) / 2;

         Movable.DynamicsPosition mid = start.interpolate(end, m_frac);

         ColRet m_col = collide(m, mid);

         if (m_col != null)
         {
            e_frac = m_frac;
            col = m_col;
         }
         else
         {
            s_frac = m_frac;
         }

         iLength2 = end.Position.minus(start.Position).length2();
      }

      // we've got three kinds of collision:
      // 1) corner - corner
      // 2) corner - edge
      // 3) edge - edge
      //
      // Since we detect collision by actual penetration (that we then back off from) these manifest as
      // two lines crossing:
      // 1) both intersecting close to an end
      // 2) midway on one line crossing close to the end of the other
      // 3) two lines crossing midway
      //
      // (arbitrarily define "midway" as more than some fraction from both ends, maybe 5%?)
      //
      // additionally, case 2 happens two ways (moving-edge vs. stationary-corner and vice versa)
      // and case 3 happens 4 ways (start - start, start - end, end - start and end - end) but these become
      // identical once we have resolved them to corner-corner instead of edge-edge
      //
      // stationary edges are class Wall, moving edges are class Edge

      boolean moving_corner = false;
      boolean stationary_corner = false;

      // any corner is expressed as between a Wall (or Edge) and the following Wall (or Edge)
      Wall wall = col.Wall;
      Wall next_wall = null;
      Edge edge = col.Edge;
      Edge next_edge = null;

      if (col.WallFrac < CornerTolerance)
      {
         // corner at start of reported wall
         next_wall = wall;
         wall = next_wall.getPrev();
         stationary_corner = true;
      }
      else if (col.WallFrac > 1 - CornerTolerance)
      {
         // corner at end of reported wall
         next_wall = wall.getNext();
         stationary_corner = true;
      }

      if (col.EdgeFrac < CornerTolerance)
      {
         // corner at start of reported edge
         next_edge = edge;
         edge = next_edge.getPrev();
         moving_corner = true;
      }
      else if (col.EdgeFrac > 1 - CornerTolerance)
      {
         // corner at end of reported edge
         next_edge = edge.getNext();
         moving_corner = true;
      }

      // normal will be "out of" statianary object as that's the way round we need it for the moving object
      XY normal;
      XY collision_point;

      if (!moving_corner && !stationary_corner)
      {
         // edge - edge use average normal
         normal = wall.Normal.minus(edge.normal()).asUnit();
         // either edge or wall should give same answer
         // this is post-collision, could back-step to point on pre-collision movable position
         // but hopefully "resolution" can be set small enough for that not to matter
         collision_point = XY.interpolate(edge.Start, edge.End, col.EdgeFrac);
      }
      else if (!moving_corner)
      {
         // stationary-corner - moving-edge
         normal = edge.normal();
         collision_point = edge.End;
      }
      else if (!stationary_corner)
      {
         // moving-corner - stationary-edge
         normal = wall.Normal;
         collision_point = wall.End;
      }
      else
      {
         // corner - corner
         normal = wall.Normal.plus(next_wall.Normal).minus(edge.normal()).minus(next_edge.normal()).asUnit();
         // arbitrary, we have two points, each slightly penetrating the other body
         // but again hope "resolution" is small enough not to make any difference
         collision_point = edge.End;
      }

      return new MovableCollision(collision_point, s_frac, normal);
   }

   private static class Edge
   {
      private Edge m_next;
      private Edge m_prev;

      Edge(XY start, XY end)
      {
         Start = start;
         End = end;
      }

      final XY Start;
      final XY End;

      public void setNext(Edge next)
      {
         this.m_next = next;
      }

      public Edge getNext()
      {
         return m_next;
      }

      public void setPrev(Edge prev)
      {
         this.m_prev = prev;
      }

      public Edge getPrev()
      {
         return m_prev;
      }

      // we build edges rotating clockwise, so their normals are 90 degrees anticlockwise from their directions
      public XY normal()
      {
         return End.minus(Start).rot270().asUnit();
      }
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

   private ArrayList<Edge> makeEdges(Movable m, Movable.DynamicsPosition pos)
   {
      Transformer t = new Transformer(pos);

      ArrayList<XY> transformed_corners
            = m.getCorners().stream().map(t::transform).collect(Collectors.toCollection(ArrayList::new));

      XY prev = transformed_corners.get(transformed_corners.size() - 1);

      ArrayList<Edge> ret = new ArrayList<>();

      Edge prev_e = null;

      for (XY curr : transformed_corners)
      {
         Edge curr_e = new Edge(prev, curr);
         ret.add(curr_e);

         if (prev_e != null)
         {
            prev_e.setNext(curr_e);
            curr_e.setPrev(prev_e);
         }

         prev = curr;
         prev_e = curr_e;
      }

      assert prev_e != null;

      ret.get(0).setPrev(prev_e);
      prev_e.setNext(ret.get(0));

      return ret;
   }

   private static class ColRet
   {
      final Edge Edge;
      final Wall Wall;
      final double EdgeFrac;
      final double WallFrac;

      ColRet(Edge edge, Wall wall, double edgeFrac, double wallFrac)
      {
         Edge = edge;
         Wall = wall;
         EdgeFrac = edgeFrac;
         WallFrac = wallFrac;
      }
   }

   private ColRet collide(Movable m, Movable.DynamicsPosition pos)
   {
      GridWalker gw = new GridWalker(m_cell_size, pos.Position, pos.Position, m.getRadius());

      ArrayList<Edge> edges = null;

      CC cell;

      while((cell = gw.nextCell()) != null)
      {
         ArrayList<Wall> walls = m_wall_map.get(cell);

         if (walls != null)
         {
            if (edges == null)
            {
               edges = makeEdges(m, pos);
            }

            for(Wall w : walls)
            {
               for(Edge e : edges)
               {
                  OrderedPair<Double, Double> intr = Util.edgeIntersect(w.Start, w.End, e.Start, e.End);

                  if (intr != null)
                  {
                     return new ColRet(e, w, intr.Second, intr.First);
                  }
               }
            }
         }
      }

      return null;
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

   private final HashMap<CC, ArrayList<Wall>> m_wall_map
         = new HashMap<>();

   private final Box m_bounds;

   private final double m_cell_size;
   private final double m_wall_facet_length;

   private final WallLoopSet m_wall_loops = new WallLoopSet();

   private final XY m_start_pos;

   // constant
   private static final double CornerTolerance = 0.05;
}
