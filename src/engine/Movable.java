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
public abstract class Movable implements ICollidable
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

   public void applyForceAbsolute(XY force, XY position)
   {
      applyForceRelative(force, position.minus(m_state.Position));
   }

   @SuppressWarnings("WeakerAccess")
   public void applyForceRelative(XY force, XY relativePosition)
   {
      m_force = m_force.plus(force);

      double torque = relativePosition.rot270().dot(force);
      m_torque += torque;
   }

   public DynamicsState step(double timeStep)
   {
      return step(timeStep, timeStep);
   }

   public DynamicsState step(double positionTimeStep, double velocityTimeStep)
   {
      DynamicsState ret = new DynamicsState();

      ret.Position = m_state.Position.plus(m_state.Velocity.multiply(positionTimeStep));
      ret.Orientation = m_state.Orientation + m_state.Spin * positionTimeStep;

      ret.Velocity = m_state.Velocity
            .plus(m_force.multiply(velocityTimeStep / Mass))
            .plus(m_temporary_force.multiply(velocityTimeStep / Mass));
      ret.Spin = m_state.Spin
            + m_torque * velocityTimeStep / MomentOfInertia
            + m_temporary_torque * velocityTimeStep / MomentOfInertia;

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
      m_state.Spin += relPoint.rot270().dot(impulse) / MomentOfInertia;
   }

   public ArrayList<XY> getTransformedCorners()
   {
      return getTransformedCorners(m_state);
   }

   @SuppressWarnings("WeakerAccess")
   public ArrayList<XY> getTransformedCorners(DynamicsPosition pos)
   {
      Transform t = new Transform(pos);

      return getCorners().stream().map(t::transform).collect(Collectors.toCollection(ArrayList::new));
   }

   public void setSpin(double spin)
   {
      m_state.Spin = spin;
   }

   public void clearTemporaryForces()
   {
      m_temporary_force = new XY();
      m_temporary_torque = 0;
   }

   public void applyTemporaryForceAbsolute(XY force, XY worldPosition)
   {
      applyTemporaryForceRelative(force, worldPosition.minus(getPosition()));
   }

   private void applyTemporaryForceRelative(XY force, XY relativePosition)
   {
      m_temporary_force = m_temporary_force.plus(force);

      double torque = relativePosition.rot270().dot(force);
      m_temporary_torque += torque;
   }

   public static class DynamicsPosition
   {
      public XY Position = new XY();
      public double Orientation = 0;

      @SuppressWarnings("SameParameterValue")
      DynamicsPosition interpolate(DynamicsPosition towards, double amount)
      {
         DynamicsPosition ret = new DynamicsPosition();

         ret.Position = Position.plus(towards.Position.minus(Position).multiply(amount));
         ret.Orientation = Orientation + (towards.Orientation - Orientation) * amount;

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
      return m_state.Velocity.plus(relativePoint.rot270().multiply(m_state.Spin));
   }

   // make edges for current position/orientation
   //
   // optimisation: make this memoize the result as we can use the edges for the same position
   // over and over
   private ArrayList<Edge> makeEdges()
   {
      return makeEdges(m_state);
   }

   // make edges for given hypothetical position/orientation
   public ArrayList<Edge> makeEdges(DynamicsPosition pos)
   {
      ArrayList<XY> transformed_corners = getTransformedCorners(pos);

      XY prev = transformed_corners.get(transformed_corners.size() - 1);

      ArrayList<Edge> ret = new ArrayList<>();

      Edge prev_e = null;

      for (XY curr : transformed_corners)
      {
         Edge curr_e = new Edge(prev, curr, curr.minus(prev).rot270().asUnit());
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

   // make edges for given hypothetical position/orientation
   public ArrayList<Track> makeRadialTracks(DynamicsPosition pos)
   {
      ArrayList<XY> from_transformed_corners = getTransformedCorners(pos);

      ArrayList<Track> ret = new ArrayList<>();

      for (int i = 0; i < from_transformed_corners.size(); i++)
      {
         XY corner = from_transformed_corners.get(i);

         ret.add(new Track(pos.Position, corner));
      }

      return ret;
   }

   @SuppressWarnings("WeakerAccess")
   public abstract Collection<XY> getCorners();

   public abstract double getRadius();

   @Override
   public Collection<ColRet> collide(Collection<Track> corner_tracks, double radius, XY centre, Movable activeMovable)
   {
      double combined_r2 = radius + getRadius();
      combined_r2 *= combined_r2;

      if (centre.minus(getPosition()).length2() > combined_r2)
         return null;

      ArrayList<Edge> stationary_edges = makeEdges();

      ArrayList<ColRet> ret = new ArrayList<>();

      for(Edge stationary_edge : stationary_edges)
      {
         for(Track corner_track : corner_tracks)
         {
            OrderedPair<Double, Double> intr = Util.edgeIntersect(
                  corner_track.Start, corner_track.End,
                  stationary_edge.Start, stationary_edge.End);

            if (intr != null)
            {
               ret.add(new ColRet(activeMovable, this, corner_track, intr.First, stationary_edge, intr.Second,
                     corner_track.interp(intr.First)));
            }
         }
      }

      if (ret.size() > 0)
      {
         return ret;
      }

      return null;
   }

   // these final more as a way of letting the compiler
   // check they got assigned, no absolute reason why they can't be changed later
   final public double Mass;
   final public double MomentOfInertia;
   final public double CoefficientOfRestitution;

   private DynamicsState m_state = new DynamicsState();

   private XY m_force = new XY();
   private double m_torque = 0;

   private XY m_temporary_force = new XY();
   private double m_temporary_torque = 0;
}
