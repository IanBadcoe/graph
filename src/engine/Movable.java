package engine;

// for the moment, not separating physically simulated from movable, but if required later, could split this
// into a base class of Movable and a derived class of PhysicallyMovable, giving us scope for other derived
// classes such as NonPhysicallyMoving for unstoppable things
public abstract class Movable implements ICollidable
{
   public void setPosition(XY pos)
   {
      m_position = pos;
   }

   public XY getPosition()
   {
      return m_position;
   }

   public void applyForceAbsolute(XY position, XY force)
   {
      applyForceRelative(position.minus(m_position), force);
   }

   public void applyForceRelative(XY relativePosition, XY force)
   {
      m_force = m_force.plus(force);

      double torque = relativePosition.rot90().dot(force);
      m_torque += torque;
   }

   public void step(double timeStep)
   {
      m_position = m_position.plus(m_velocity.multiply(timeStep));
      m_angle += m_spin * timeStep;


   }

   public static class DynamicsState
   {
      public XY Position;
      public double Orientation;
      public XY Velocity;
      public double Spin;
   }

   double m_mass;
   double m_momentOfInertia;

   private XY m_position;
   private double m_angle;

   private XY m_velocity;
   private double m_spin;

   private XY m_force;
   private double m_torque;
}
