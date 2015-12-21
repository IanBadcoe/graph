package engine;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.TreeSet;

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

      Level.MovableCollision c = m_level.collide(m, m.getState(), new_state, 0.1);

      if (c == null)
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
      new_state = m.step(timeStep * c.FractionTravelled, timeStep);
      m.setState(new_state);

      resolveCollision(m, c);

      return timeStep * c.FractionTravelled;
   }

   private void resolveCollision(Movable m, Level.MovableCollision mc)
   {
      // so far we have only collisions with immovable objects, we signal this to the collision routine with
      // a null second object
      //
      // normal is towards the first object and away from the second

      resolveCollision(m, null, mc);
   }

   @SuppressWarnings("SameParameterValue")
   private void resolveCollision(Movable m1, Movable m2, Level.MovableCollision mc)
   {
      // AFAIK if one object, no need to ever make it m2
      assert m1 != null;

      XY col_point_on_m1 = mc.WorldPoint.minus(m1.getPosition());

      XY rel_v = m1.pointVelocity(col_point_on_m1);

      XY col_point_on_m2 = null;

      double restitution = m1.CoefficientOfRestitution;

      double den = 1 / m1.Mass;

      {
         double m1_rot_factor = col_point_on_m1.rot270().dot(mc.Normal);
         m1_rot_factor *= m1_rot_factor;
         m1_rot_factor /= m1.MomentOfInertia;

         den += m1_rot_factor;
      }

      if (m2 != null)
      {
         col_point_on_m2 = mc.WorldPoint.minus(m2.getPosition());
         XY m2_rel_v = m2.pointVelocity(col_point_on_m2);

         rel_v = rel_v.minus(m2_rel_v);
         restitution *= m2.CoefficientOfRestitution;

         den += 1 / m2.Mass;

         {
            double m2_rot_factor = col_point_on_m2.rot270().dot(mc.Normal);
            m2_rot_factor *= m2_rot_factor;
            m2_rot_factor /= m2.MomentOfInertia;

            den += m2_rot_factor;
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

      m1.applyImpulseRelative(impulse, col_point_on_m1);

      if (m2 != null)
      {
         m2.applyImpulseRelative(impulse.negate(), col_point_on_m2);
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

   private LinkedList<Movable> m_movable_objects = new LinkedList<>();

   private final Level m_level;
}
