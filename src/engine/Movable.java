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
      ret.Velocity = m_state.Velocity.plus(m_force.multiply(velocityTimeStep / Mass));
      ret.Spin = m_state.Spin + m_torque * velocityTimeStep / MomentOfInertia;

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
   private ArrayList<IEdge> makeEdges()
   {
      return makeEdges(m_state);
   }

   // make edges for given hypothetical position/orientation
   public ArrayList<IEdge> makeEdges(DynamicsPosition pos)
   {
      ArrayList<XY> transformed_corners = getTransformedCorners(pos);

      XY prev = transformed_corners.get(transformed_corners.size() - 1);

      ArrayList<IEdge> ret = new ArrayList<>();

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

      ((Edge)ret.get(0)).setPrev(prev_e);
      prev_e.setNext(ret.get(0));

      return ret;
   }

   @SuppressWarnings("WeakerAccess")
   public abstract Collection<XY> getCorners();

   public abstract double getRadius();

   @Override
   public ColRet collide(Collection<IEdge> active_edges, double radius, XY centre, Movable activeMovable)
   {
      double combined_r2 = radius + getRadius();
      combined_r2 *= combined_r2;

      if (centre.minus(getPosition()).length2() > combined_r2)
         return null;

      ArrayList<IEdge> inactive_edges = makeEdges();

      for(IEdge inactive_edge : inactive_edges)
      {
         for(IEdge active_edge : active_edges)
         {
            OrderedPair<Double, Double> intr = Util.edgeIntersect(
                  active_edge.getStart(), active_edge.getEnd(),
                  inactive_edge.getStart(), inactive_edge.getEnd());

            if (intr != null)
            {
               XY position = active_edge.getStart()
                     .plus(active_edge.getEnd()
                           .minus(active_edge.getStart())
                           .multiply(intr.First));

               return new ColRet(activeMovable, this,
                     active_edge.getSuperEdge(), inactive_edge.getSuperEdge(),
                     position);
            }
         }
      }

      return null;
   }

   // just for testing
   static IEdge makeEdge(XY start, XY end, XY normal)
   {
      return new Edge(start, end, normal);
   }

   static private class Edge implements IEdge
   {
      Edge(XY start, XY end, XY normal)
      {
         m_start = start;
         m_end = end;
         m_normal = normal;

         m_super_edge = new SuperEdge(start, end, normal);
      }

      public void setNext(IEdge next)
      {
         this.m_next = next;
         assert next.getSuperEdge() != null;
         m_super_edge.setNext(next.getSuperEdge());
      }

      @Override
      public IEdge getNext()
      {
         return m_next;
      }

      public void setPrev(IEdge prev)
      {
         this.m_prev = prev;
         assert prev.getSuperEdge() != null;
         m_super_edge.setPrev(prev.getSuperEdge());
      }

      @Override
      public IEdge getPrev()
      {
         return m_prev;
      }

      @Override
      public XY getStart()
      {
         return m_start;
      }

      @Override
      public XY getEnd()
      {
         return m_end;
      }

      @Override
      public XY getNormal()
      {
         return m_normal;
      }

      @Override
      public SuperEdge getSuperEdge()
      {
         return m_super_edge;
      }

      private final XY m_start;
      private final XY m_end;
      private final XY m_normal;
      private IEdge m_next;
      private IEdge m_prev;
      private final SuperEdge m_super_edge;
   }

   // these final more as a way of letting the compiler
   // check they got assigned, no absolute reason why they can't be changed later
   final public double Mass;
   final public double MomentOfInertia;
   final public double CoefficientOfRestitution;

   private DynamicsState m_state = new DynamicsState();

   private XY m_force = new XY();
   private double m_torque = 0;
}
