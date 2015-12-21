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

      // maybe a bit of a hack, movement cannot complete the time step because it hits something
      // and if the impact is at t == 0, velocity stops evolving, so instead here give velocity full step
      // even if movement cannot do it
      //
      // if we start doing multiple sub-steps per step, we'll need to only do this for the remaining unused
      // time after the last one...
      new_state = m.step(timeStep * mc.FractionTravelled, timeStep);
      m.setState(new_state);

      resolveCollision(mc);

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
      // additionally, case 2 happens two ways (moving-edge vs. stationary-corner and vice versa)
      // and case 3 happens 4 ways (start - start, start - end, end - start and end - end) but these become
      // identical once we have resolved them to corner-corner instead of edge-edge
      //
      // stationary edges are class Wall, moving edges are class Edge

      boolean moving_corner = false;
      boolean stationary_corner = false;

      // any corner is expressed as between a Wall (or Edge) and the following Wall (or Edge)
      ICollidable.Edge moving_edge = col.MovingEdge;
      ICollidable.Edge moving_edge_next = null;
      ICollidable.Edge stationary_edge = col.StationaryEdge;
      ICollidable.Edge stationary_edge_next = null;

      if (col.MovingEdgeFrac < CornerTolerance)
      {
         // corner at start of reported wall
         moving_edge_next = moving_edge;
         moving_edge = moving_edge_next.getPrev();
         moving_corner = true;
      }
      else if (col.MovingEdgeFrac > 1 - CornerTolerance)
      {
         // corner at end of reported wall
         moving_edge_next = moving_edge.getNext();
         moving_corner = true;
      }

      if (col.StationaryEdgeFrac < CornerTolerance)
      {
         // corner at start of reported edge
         stationary_edge_next = stationary_edge;
         stationary_edge = stationary_edge_next.getPrev();
         stationary_corner = true;
      }
      else if (col.StationaryEdgeFrac > 1 - CornerTolerance)
      {
         // corner at end of reported edge
         stationary_edge_next = stationary_edge.getNext();
         stationary_corner = true;
      }

      // normal will be "out of" statianary object as that's the way round we need it for the moving object
      XY normal;
      XY collision_point;

      if (moving_corner && stationary_corner)
      {
         // corner - corner
         normal = moving_edge.Normal
               .plus(moving_edge_next.Normal)
               .minus(stationary_edge.Normal)
               .minus(stationary_edge_next.Normal).asUnit();
         // arbitrary, we have two points, each slightly penetrating the other body
         // but again hope "resolution" is small enough not to make any difference
         collision_point = stationary_edge.End;
      }
      else if (moving_corner)
      {
         // moving-corner - stationary-edge
         normal = moving_edge.Normal;
         collision_point = stationary_edge.End;
      }
      else if (stationary_corner)
      {
         // stationary-corner - moving-edge
         normal = stationary_edge.Normal;
         collision_point = moving_edge.End;
      }
      else
      {
         // edge - edge use average normal
         normal = moving_edge.Normal
               .minus(stationary_edge.Normal)
               .asUnit();

         // either edge or wall should give same answer
         // this is post-collision, could back-step to point on pre-collision movable position
         // but hopefully "resolution" can be set small enough for that not to matter
         collision_point = XY.interpolate(stationary_edge.Start, stationary_edge.End, col.StationaryEdgeFrac);
      }

      return new MovableCollision(collision_point, s_frac, normal,
            col.ActiveMovable, col.InactiveMovable);
   }

   // non-private for unit-testing only
   @SuppressWarnings("WeakerAccess")
   ICollidable.ColRet collide(Movable m, Movable.DynamicsPosition pos, Collection<ICollidable> collideWith)
   {
      ArrayList<ICollidable.Edge> edges = m.makeEdges(pos);

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
      // normal is towards the moving object and away from the stationary

      // mc.ActiveMovable is the one we are stepping when the collision occurs
      // mc.InactiveMovable is the one it hit, it is not necessarily stationary, it just isn't being
      // stepped at the moment
      //
      // if we hit something imobile then StationaryMobile is null and disappears form the maths
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

   // constant
   private static final double CornerTolerance = 0.05;
}
