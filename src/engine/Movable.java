package engine;

import javafx.geometry.Pos;

// for the moment, not separating physically simulated from movable, but if required later, could split this
// into a base class of Movable and a derived class of PhysicallyMovable, giving us scope for other derived
// classes such as NonPhysicallyMoving for unstoppable things
public abstract class Movable implements ICollidable
{
   public Movable(double mass, double momentOfIntertia, double coefficientOfRestitution)
   {
      m_mass = mass;
      m_momentOfInertia = momentOfIntertia;
      m_coefficientOfRestitution = coefficientOfRestitution;
   }

   public void setPosition(XY pos)
   {
      m_state.Position = pos;
   }

   public XY getPosition()
   {
      return m_state.Position;
   }

   public void applyForceAbsolute(XY position, XY force)
   {
      applyForceRelative(position.minus(m_state.Position), force);
   }

   public void applyForceRelative(XY relativePosition, XY force)
   {
      m_force = m_force.plus(force);

      double torque = relativePosition.rot90().dot(force);
      m_torque += torque;
   }

   public DynamicsState step(double timeStep)
   {
      DynamicsState ret = new DynamicsState();

      ret.Position = m_state.Position.plus(m_state.Velocity.multiply(timeStep));
      ret.Orientation = m_state.Spin + m_state.Spin * timeStep;
      ret.Velocity = m_state.Velocity.plus(m_force.multiply(timeStep / m_mass));
      ret.Spin = m_state.Spin + m_torque * timeStep / m_momentOfInertia;

      return ret;
   }

   public DynamicsState getState()
   {
      return m_state;
   }

   public void setState(DynamicsState state)
   {
      m_state = state;
   }

   public static class DynamicsPosition
   {
      public XY Position;
      public double Orientation;

      DynamicsPosition interpolate(DynamicsPosition towards, double amount)
      {
         DynamicsPosition ret = new DynamicsPosition();

         ret.Position = Position.plus(towards.Position.minus(Position).multiply(amount));

         return ret;
      }
   }

   public static class DynamicsState extends DynamicsPosition
   {
      public XY Velocity;
      public double Spin;
   }

   // these final more as a way of letting the compiler
   // check they got assigned, no absolute reason why they can't be changed later
   final double m_mass;
   final double m_momentOfInertia;
   final double m_coefficientOfRestitution;

   DynamicsState m_state = new DynamicsState();

   private XY m_force;
   private double m_torque;
}
