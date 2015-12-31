package engine;

import java.util.*;
import java.util.stream.Collectors;

public class Level implements ICollidable
{
   Level(double cell_size, double wall_facet_length, Box bounds, XY start_pos)
   {
      m_cell_size = cell_size;
      m_cell_radius = Math.sqrt(cell_size * cell_size) / 2;
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

   public Collection<IDrawable> getDrawables()
   {
      return m_movable_objects.stream()
            .filter(x -> x instanceof IDrawable)
            .map(x -> (IDrawable)x)
            .collect(Collectors.toCollection(ArrayList::new));
   }

   public void step(double stepSize)
   {
      for(Movable m : m_movable_objects)
      {
         stepMovable(m, stepSize);
      }
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

   @Override
   public ColRet collide(Movable m)
   {
      ArrayList<Wall> walls = wallsInRangeOfPoint(m.getPosition(), m.getRadius());

      for(Wall wall : walls)
      {
         if (Util.circleLineIntersect(m.getPosition(), m.getRadius(),
               wall.Start, wall.End) != null)
         {
            return new ColRet(wall.Normal);
         }
      }

      return null;
   }

   private ArrayList<Wall> wallsInRangeOfPoint(XY position, double radius)
   {
      Collection<CC> cells = GridWalker.pointSample(m_cell_size, m_cell_radius,
         position, radius + m_wall_facet_length / 2);

      ArrayList<Wall> ret = new ArrayList<>();

      for(CC cell : cells)
      {
         ArrayList<Wall> walls = m_wall_map.get(cell);

         if (walls != null)
         {
            ret.addAll(walls);
         }
      }

      return ret;
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

//   public void stepNextMovable(double timeStep)
//   {
//      Movable m = m_movable_objects.pollFirst();
//
//      if (m == null)
//         return;
//
//      stepMovable(m, timeStep);
//
//      m_movable_objects.addLast(m);
//   }

   private void stepMovable(Movable m, double timeStep)
   {
      ArrayList<ICollidable> collideWith = new ArrayList<>();
      collideWith.add(this);
      collideWith.addAll(m_movable_objects.stream().filter(ic -> ic != m).collect(Collectors.toList()));

      m.step(timeStep, collideWith);
   }

   public void addMovable(Movable m)
   {
      m_movable_objects.addLast(m);
   }

   public Collection<Movable> getMovables()
   {
      return Collections.unmodifiableCollection(m_movable_objects);
   }

   private final HashMap<CC, ArrayList<Wall>> m_wall_map
         = new HashMap<>();

   private final Box m_bounds;

   private final double m_cell_size;
   private final double m_cell_radius;
   private final double m_wall_facet_length;

   private final WallLoopSet m_wall_loops = new WallLoopSet();

   private final XY m_start_pos;

   private final LinkedList<Movable> m_movable_objects = new LinkedList<>();}
