package engine;

import java.util.Collection;

// for the moment, not separating physically simulated from movable, but if required later, could split this
// into a base class of Movable and a derived class of PhysicallyMovable, giving us scope for other derived
// classes such as NonPhysicallyMoving for unstoppable things
//
// also for the moment, not separating Movable from "ICollidable" (which non-movable things, such as walls, could
// also implement...
public abstract class Movable implements ICollidable
{
   protected Movable(double m_radius)
   {
      this.m_radius = m_radius;
   }

   public void setPosition(XY pos)
   {
      m_position = pos;
   }

   public XY getPosition()
   {
      return m_position;
   }

   @SuppressWarnings("WeakerAccess")
   public void addVelocity(XY v)
   {
      m_velocity = m_velocity.plus(v);
   }

   @SuppressWarnings("WeakerAccess")
   public XY getVelocity()
   {
      return m_velocity;
   }

   private void dampVelocity()
   {
      m_velocity = m_velocity.multiply(0.95);
   }

   public void step(double timeStep, Collection<ICollidable> collisionCandidates)
   {
      double used_time = 0;

      int attempts = 0;
      while(used_time < timeStep && attempts < 3)
      {
         attempts++;
         used_time += tryStep(timeStep - used_time, collisionCandidates, 0.01);
      }

      // to simplify movement maths, do this once and indivisibly
      dampVelocity();
   }

   @SuppressWarnings("WeakerAccess")
   protected double tryStep(double timeStep, Collection<ICollidable> collisionCandidates, double resolution)
   {
      assert collideWith(collisionCandidates) == null;

      double dist = m_velocity.length() * timeStep;

      // round small velocities to zero
      if (dist < resolution / 2)
         return timeStep;

      XY where = m_position.plus(m_velocity.multiply(timeStep));

      ColRet col = collideWith(collisionCandidates, where);

      if (col == null)
      {
         m_position = where;
         return timeStep;
      }

      // binary search for an interval where "start" is not colliding and "end" is

      double start = 0;
      double end = 1;

      double here_res = resolution / dist;

      while (end - start > here_res)
      {
         double mid = (start + end) / 2;
         where = m_position.plus(m_velocity.multiply(timeStep * mid));

         ColRet temp = collideWith(collisionCandidates, where);

         if (temp == null)
         {
            start = mid;
         }
         else
         {
            col = temp;
            end = mid;
         }
      }

      // we can move as far as start
      where = m_position.plus(m_velocity.multiply(timeStep * start));
      setPosition(where);

      // we lose the part of our velocity which is into the edge we hit
      m_velocity = filterVelocity(m_velocity, col.Normal.rot90());

      return timeStep * start;
   }

   private XY filterVelocity(XY velocity, XY keepComponent)
   {
      double project = keepComponent.dot(velocity);
      return keepComponent.multiply(project);
   }

   public void setOrientation(double ori)
   {
      m_orientation = ori;
   }

   public double getOrientation()
   {
      return m_orientation;
   }

   public double getRadius()
   {
      return m_radius;
   }

   private ColRet collideWith(Collection<ICollidable> collisionCandidates)
   {
      return collideWith(collisionCandidates, getPosition());
   }

   private ColRet collideWith(Collection<ICollidable> collisionCandidates, XY where)
   {
      for(ICollidable ic : collisionCandidates)
      {
         ColRet ret = ic.collide(this, where);

         if (ret != null)
         {
            return ret;
         }
      }

      return null;
   }

   @Override
   public ColRet collide(Movable m)
   {
      return collide(m, m.getPosition());
   }

   @Override
   public ColRet collide(Movable m, XY where)
   {
      if (Util.circleCircleIntersect(getPosition(), getRadius(),
            where, m.getRadius()) != null)
      {
         return new ColRet(m.getPosition().minus(where).asUnit());
      }

      return null;
   }

   public void addOrientation(double angle)
   {
      m_orientation += angle;
   }

   private XY m_position = new XY();
   private XY m_velocity = new XY();

   private double m_orientation = 0;

   private final double m_radius;
}
