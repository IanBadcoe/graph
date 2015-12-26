package engine;

import java.util.*;
import java.util.stream.Collectors;

public class PhysicalSimulator
{
   public PhysicalSimulator(Level level)
   {
      m_level = level;
   }

   // constraints...
   // I am thinking that the maximum speed for a rotatable object (such as a vehicle) should be about 200 units/second
   // and the maximum speed of rotation would be 1 rps
   // if we assume a frame-rate of 20 fps, this gives us a max translation per step of 10 and a max rotation of
   // 2PI / 20 = ~20 degrees
   @SuppressWarnings("SameParameterValue")
   public void step(double timeStep)
   {
      Movable m = m_movable_objects.pollFirst();

      if (m == null)
         return;

      partStep(m, timeStep);

      m_movable_objects.addLast(m);
   }

   // steps until first collision, returns time consumed (<= timeStep)
   @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
   public double partStep(Movable m, double timeStep)
   {
      Movable.DynamicsState new_state = m.step(timeStep);

      MovableCollision mc = collide(m, m.getState(), new_state, 0.1);

      if (mc == null)
      {
         m.setState(new_state);

         return timeStep;
      }

      // time before collision
      double time_taken = timeStep * mc.FractionTravelled;
      new_state = m.step(time_taken);
      m.setState(new_state);

      resolveCollision(mc);

//      // maybe a bit of a hack, movement cannot complete the time step because it hits something
//      // and if the impact is at t == 0, velocity stops evolving, so here give velocity the rest of its step
//      // even if movement cannot do it
//      //
//      // if we start doing multiple sub-steps per step, we'll need to only do this for the remaining unused
//      // time after the last one...
//      new_state = m.step(0, timeStep - time_taken);
//      m.setState(new_state);

      return timeStep * mc.FractionTravelled;
   }

   public static class MovableCollision
   {
      MovableCollision(XY worldPoint, double fractionTravelled, XY normal,
            Movable activeMovable, Movable inactiveMovable)
      {
         WorldPoint = worldPoint;
         FractionTravelled = fractionTravelled;
         Normal = normal;

         ActiveMovable = activeMovable;
         InactiveMovable = inactiveMovable;
      }

      final XY WorldPoint;
      final double FractionTravelled;
      final XY Normal;

      final Movable ActiveMovable;
      final Movable InactiveMovable;
   }

   // just allows easy communication with sub-routine
   private static class EdgeEdgeData
   {
      public double overlap = -1;
      public double p_where = 0.5;

      public SuperEdge active_edge = null;
      public SuperEdge inactive_edge = null;
   }

   @SuppressWarnings("SameParameterValue")
   private MovableCollision collide(Movable m, Movable.DynamicsPosition start, Movable.DynamicsPosition end,
         double resolution)
   {
      ArrayList<ICollidable> collideWith =  new ArrayList<>();

      collideWith.add(m_level);

      collideWith.addAll(m_movable_objects.stream().filter(c -> c != m).collect(Collectors.toList()));

      // we're looking for the first interval within the overall movement
      // that is of length at most "resolution" and where the start has no
      // collision and the end does have a collision
      //
      // we'll stop the movable at the start of this interval and calculate the
      // collision params for the collision that would be about to occur

      assert collide(m, start, collideWith) == null;

      ICollidable.ColRet col = collide(m, end, collideWith);

      if (col == null)
         return null;

      double iLength2 = end.Position.minus(start.Position).length2();

      resolution *= resolution;

      // we only need *_frac for tracking s_frac which is eventually the fraction we succeeded in moving
      double s_frac = 0;
      double e_frac = 1;

      while(iLength2 > resolution)
      {
         double m_frac = (s_frac + e_frac) / 2;
         Movable.DynamicsPosition mid = start.interpolate(end, 0.5);

         ICollidable.ColRet m_col = collide(m, mid, collideWith);

         if (m_col != null)
         {
            e_frac = m_frac;
            col = m_col;
            end = mid;
         }
         else
         {
            s_frac = m_frac;
            start = mid;
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
      // additionally, case 2 happens two ways (active-edge vs. inactive-corner and vice versa)
      // and case 3 happens 4 ways (start - start, start - end, end - start and end - end) but these become
      // identical once we have resolved them to corner-corner instead of edge-edge

      boolean active_corner = false;
      boolean inactive_corner = false;

      // any corner is expressed as between a Wall (or Edge) and the following Wall (or Edge)
      SuperEdge active_edge = col.ActiveEdge;
      SuperEdge active_edge_next = null;
      SuperEdge inactive_edge = col.InactiveEdge;
      SuperEdge inactive_edge_next = null;

      double active_edge_frac = active_edge.getFractionalPosition(col.Position);
      assert active_edge_frac > -1e-6 && active_edge_frac < 1 + 1e-6;
      double inactive_edge_frac = inactive_edge.getFractionalPosition(col.Position);
      assert inactive_edge_frac > -1e-6 && inactive_edge_frac < 1 + 1e-6;

      if (active_edge_frac < CornerTolerance)
      {
         // corner at start of reported wall
         active_edge_next = active_edge;
         active_edge = active_edge_next.getPrev();
         active_corner = true;
      }
      else if (active_edge_frac > 1 - CornerTolerance)
      {
         // corner at end of reported wall
         active_edge_next = active_edge.getNext();
         active_corner = true;
      }

      if (inactive_edge_frac < CornerTolerance)
      {
         // corner at start of reported edge
         inactive_edge_next = inactive_edge;
         inactive_edge = inactive_edge_next.getPrev();
         inactive_corner = true;
      }
      else if (inactive_edge_frac > 1 - CornerTolerance)
      {
         // corner at end of reported edge
         inactive_edge_next = inactive_edge.getNext();
         inactive_corner = true;
      }

      // distance along active edge as a fraction that is nominal centre of an edge-edge collision
      double edge_edge_parameter = 0.5;

      {
         // collision detects only edge-edge crossings, which means we can detect something like:
         //  |
         //  |    x-----------------
         //  |    |
         //  y----+----------
         //       |
         //
         // when what has happened is that y has moved in from above and what we're like to have detected
         // was the edge-edge collision of the two horizontal edges
         // this block detects that by examining the edges adjoining any corner collision, and if they
         // are sufficiently parallel and have significant axial overlap, switches the collision type to
         // an edge-edge case

         EdgeEdgeData eed = new EdgeEdgeData();

         // we always have the these two edges
         detectCrypticEdgeEdgeCollision(eed, active_edge, inactive_edge);

         if (active_corner)
         {
            detectCrypticEdgeEdgeCollision(eed, active_edge_next, inactive_edge);
         }

         if (inactive_corner)
         {
            detectCrypticEdgeEdgeCollision(eed, active_edge, inactive_edge_next);
         }

         if (active_corner && inactive_corner)
         {
            detectCrypticEdgeEdgeCollision(eed, active_edge_next, inactive_edge_next);
         }

         // we
         if (eed.active_edge != null)
         {
            assert eed.inactive_edge != null;

            active_corner = false;
            inactive_corner = false;

            active_edge = eed.active_edge;
            inactive_edge = eed.inactive_edge;

            active_edge_next = null;
            inactive_edge_next = null;

            edge_edge_parameter = eed.p_where;
         }
      }

      // normal will be "out of" inactive object and into the active object
      XY normal;
      XY collision_point;

      if (active_corner && inactive_corner)
      {
         // corner - corner
         normal = inactive_edge.getNormal()
               .plus(inactive_edge_next.getNormal())
               .minus(active_edge.getNormal())
               .minus(active_edge_next.getNormal()).asUnit();
         // arbitrary, we have two points, each slightly clear of the other body
         // but again hope "resolution" is small enough not to make any difference
         collision_point = inactive_edge.getEnd();
      }
      else if (active_corner)
      {
         // active-corner - inactive-edge
         normal = inactive_edge.getNormal();
         collision_point = active_edge.getEnd();
      }
      else if (inactive_corner)
      {
         // inactive-corner - active-edge
         normal = active_edge.getNormal();
         collision_point = inactive_edge.getEnd();
      }
      else
      {
         // edge - edge use average normal
         normal = inactive_edge.getNormal()
               .minus(active_edge.getNormal())
               .asUnit();

         // either edge or wall should give same answer
         // this is post-collision, could back-step to point on pre-collision movable position
         // but hopefully "resolution" can be set small enough for that not to matter
         collision_point = XY.interpolate(active_edge.getStart(), active_edge.getEnd(), edge_edge_parameter);
      }

      return new MovableCollision(collision_point, s_frac, normal,
            col.ActiveMovable, col.InactiveMovable);
   }

   private void detectCrypticEdgeEdgeCollision(EdgeEdgeData eed, SuperEdge active_edge,
                                               SuperEdge inactive_edge)
   {
      if (Math.abs(active_edge.getNormal().dot(inactive_edge.getNormal().rot90())) < ParallelTolerance)
      {
         Util.EPORet ret = Util.edgeParameterOverlap(
               active_edge.getStart(), active_edge.getEnd(),
               inactive_edge.getStart(), inactive_edge.getEnd(),
               CornerTolerance);

         if (ret.Overlaps)
         {
            double here_over = Math.abs(ret.PEnd - ret.PStart);
            if (here_over > eed.overlap)
            {
               eed.overlap = here_over;
               eed.active_edge = active_edge;
               eed.inactive_edge = inactive_edge;
               eed.p_where = (ret.PStart + ret.PEnd) / 2;
            }
         }
      }
   }

   // non-private for unit-testing only
   @SuppressWarnings("WeakerAccess")
   ICollidable.ColRet collide(Movable m, Movable.DynamicsPosition pos, Collection<ICollidable> collideWith)
   {
      ArrayList<IEdge> edges = m.makeEdges(pos);

      for(ICollidable c : collideWith)
      {
         ICollidable.ColRet cr = c.collide(edges, m.getRadius(), m.getPosition(), m);

         if (cr != null)
         {
            return cr;
         }
      }

      return null;
   }

   private void resolveCollision(MovableCollision mc)
   {
      // so far we have only collisions with immovable objects, we signal this to the collision routine with
      // a null second object
      //
      // normal is towards the active object and away from the inactive

      // mc.ActiveMovable is the one we are stepping when the collision occurs
      // mc.InactiveMovable is the one it hit, it is not necessarily inactive, it just isn't being
      // stepped at the moment
      //
      // if we hit something imobile then InactiveMobile is null and disappears form the maths
      // (acting as an infinite mass with zero velocity...)

      // AFAIK if one object, no need to ever make it m2
      assert mc.ActiveMovable != null;

      XY col_point_on_active = mc.WorldPoint.minus(mc.ActiveMovable.getPosition());

      XY rel_v = mc.ActiveMovable.pointVelocity(col_point_on_active);

      double restitution = mc.ActiveMovable.CoefficientOfRestitution;

      double den = 1 / mc.ActiveMovable.Mass;

      {
         double active_rot_factor = col_point_on_active.rot270().dot(mc.Normal);
         active_rot_factor *= active_rot_factor;
         active_rot_factor /= mc.ActiveMovable.MomentOfInertia;

         den += active_rot_factor;
      }

      XY col_point_on_inactive = null;

      if (mc.InactiveMovable != null)
      {
         col_point_on_inactive = mc.WorldPoint.minus(mc.InactiveMovable.getPosition());
         XY inactive_rel_v = mc.InactiveMovable.pointVelocity(col_point_on_inactive);

         rel_v = rel_v.minus(inactive_rel_v);
         restitution *= mc.InactiveMovable.CoefficientOfRestitution;

         den += 1 / mc.InactiveMovable.Mass;

         {
            double inactive_rot_factor = col_point_on_inactive.rot270().dot(mc.Normal);
            inactive_rot_factor *= inactive_rot_factor;
            inactive_rot_factor /= mc.InactiveMovable.MomentOfInertia;

            den += inactive_rot_factor;
         }
      }
      else
      {
         // all walls the same for the moment
         // proper approach is probably to make Wall and Movable both implement some "ICollidable" interface
         // but not quite sure how to split that off from Movable yet...
         restitution *= WallRestitution;
      }

      double num = rel_v.multiply(-(1 + restitution)).dot(mc.Normal);

      XY impulse = mc.Normal.multiply(num / den);

      mc.ActiveMovable.applyImpulseRelative(impulse, col_point_on_active);

      if (mc.InactiveMovable != null)
      {
         mc.InactiveMovable.applyImpulseRelative(impulse.negate(), col_point_on_inactive);
      }
   }

   public void addMovable(Movable m)
   {
      m_movable_objects.addLast(m);
   }

   public Collection<Movable> getMovables()
   {
      return Collections.unmodifiableCollection(m_movable_objects);
   }

   @SuppressWarnings("FieldCanBeLocal")
   private final double WallRestitution = 0.3;

   private final LinkedList<Movable> m_movable_objects = new LinkedList<>();

   private final Level m_level;

   // collision considered a corner when within this fraction of an edge end
   private static final double CornerTolerance = 0.05;
   // however a collision is face-to-face when two faces are within this
   private static final double ParallelTolerance = 0.05;
}
