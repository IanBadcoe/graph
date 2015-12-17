package engine;

public class PhysicalSimulator
{
   // constraints...
   // I am thinking that the maximum speed for a rotatable object (such as a vehicle) should be about 200 units/second
   // and the maximum speed of rotation would be 1 rps
   // if we assume a frame-rate of 20 fps, this gives us a max translation per step of 10 and a max rotation of
   // 2PI / 20 = ~20 degrees
   public void step(Movable m, IPhysicalLevel level, double timeStep)
   {
      partStep(m, level, timeStep);
   }

   // steps until first collision, returns fraction of the time-step consumed
   public double partStep(Movable m, IPhysicalLevel level, double timeStep)
   {
//      Movable.DynamicsState new_state = m.step(timeStep);
//
//      Level.RayCollision c = m.findFirstCollision(m.getState(), new_state, level);
//
//      if (c == null)
//      {
//         m.setState(new_state);
//
//         return timeStep;
//      }
//
//      resolveCollision(m, c);
//
//      return timeStep * c.FractionThrough;

      return 0;
   }

   private void resolveCollision(Movable m, Level.RayCollision cr)
   {

   }
}
