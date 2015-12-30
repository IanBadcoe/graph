package engine;

import java.awt.color.ICC_ColorSpace;
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

      step(m, timeStep);

      m_movable_objects.addLast(m);
   }

   // steps until first collision, returns time consumed (<= timeStep)
   @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
   public void step(Movable m, double timeStep)
   {
      Movable.DynamicsState new_state = m.step(timeStep);

      Collection<ICollidable.ColRet> collisions = collide(m, new_state);
      m.setState(new_state);

      if (collisions == null)
         return;

      m.clearTemporaryForces();

      applyCollisionForces(m, collisions);
   }

   public Collection<ICollidable.ColRet> peekCollisions(double timeStep)
   {
      Movable m = m_movable_objects.getFirst();

      if (m == null)
         return null;

      Movable.DynamicsState new_state = m.step(timeStep);

      return collide(m, new_state);
   }

   private void applyCollisionForces(Movable m, Collection<ICollidable.ColRet> collisions)
   {
      for(ICollidable.ColRet cr : collisions)
      {
         double scale = RestitutionForceScale * (1 - cr.ActiveTrackFrac);
         scale *= scale;
//         scale *= scale;
         m.applyTemporaryForceAbsolute(cr.InactiveEdge.Normal.multiply(scale),
               cr.WorldPosition);
      }
   }

   @SuppressWarnings("SameParameterValue")
   private Collection<ICollidable.ColRet> collide(Movable m, Movable.DynamicsPosition to)
   {
      ArrayList<ICollidable> collideWith =  new ArrayList<>();

      collideWith.add(m_level);

      collideWith.addAll(m_movable_objects.stream().filter(c -> c != m).collect(Collectors.toList()));

      ArrayList<ICollidable.Track> corner_tracks = m.makeRadialTracks(to);

      ArrayList<ICollidable.ColRet> ret = new ArrayList<>();

      for(ICollidable c : collideWith)
      {
         Collection<ICollidable.ColRet> collisions = c.collide(corner_tracks, m.getRadius(), m.getPosition(), m);

         if (collisions != null)
         {
            ret.addAll(collisions);
         }
      }

      if (ret.size() > 0)
      {
         return ret;
      }

      return null;
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

   private final double RestitutionForceScale = 100.0;
}
