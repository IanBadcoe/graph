package engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

// for the moment, not separating physically simulated from movable, but if required later, could split this
// into a base class of Movable and a derived class of PhysicallyMovable, giving us scope for other derived
// classes such as NonPhysicallyMoving for unstoppable things
//
// also for the moment, not separating Movable from "ICollidable" (which non-movable things, such as walls, could
// also implement...
public abstract class Movable
{
   protected Movable(double mass, double momentOfIntertia, double coefficientOfRestitution)
   {
      Mass = mass;
      MomentOfInertia = momentOfIntertia;
      CoefficientOfRestitution = coefficientOfRestitution;
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

   @SuppressWarnings("WeakerAccess")
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
      ret.Velocity = m_state.Velocity.plus(m_force.multiply(timeStep / Mass));
      ret.Spin = m_state.Spin + m_torque * timeStep / MomentOfInertia;

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

   public void applyImpulseRelative(XY impulse, XY relPoint)
   {
      m_state.Velocity = m_state.Velocity.plus(impulse.divide(Mass));
      m_state.Spin += relPoint.rot90().dot(impulse) / MomentOfInertia;
   }

   public ArrayList<XY> getTransformedCorners()
   {
      return getTransformedCorners(m_state);
   }

   public ArrayList<XY> getTransformedCorners(DynamicsPosition pos)
   {
      Transform t = new Transform(pos);

      return getCorners().stream().map(t::transform).collect(Collectors.toCollection(ArrayList::new));
   }

   public static class DynamicsPosition
   {
      public XY Position = new XY();
      public double Orientation = 0;

      DynamicsPosition interpolate(DynamicsPosition towards, double amount)
      {
         DynamicsPosition ret = new DynamicsPosition();

         ret.Position = Position.plus(towards.Position.minus(Position).multiply(amount));

         return ret;
      }
   }

   public static class DynamicsState extends DynamicsPosition
   {
      public XY Velocity = new XY();
      public double Spin = 0;
   }

   // sum the combined effewct of translation and rotation for a point on the body
   // relativePoint is the offset from the mass centre
   public XY pointVelocity(XY relativePoint)
   {
      return m_state.Velocity.plus(relativePoint.rot90().multiply(m_state.Spin));
   }

   @SuppressWarnings("WeakerAccess")
   public abstract Collection<XY> getCorners();

   public abstract double getRadius();

   // these final more as a way of letting the compiler
   // check they got assigned, no absolute reason why they can't be changed later
   final public double Mass;
   final public double MomentOfInertia;
   final public double CoefficientOfRestitution;

   private DynamicsState m_state = new DynamicsState();

   private XY m_force = new XY();
   private double m_torque = 0;
}
