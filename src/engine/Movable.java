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

   public void addVelocity(XY v)
   {
      m_velocity = m_velocity.plus(v);
   }

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

      while(used_time < timeStep)
      {
         used_time += tryStep(timeStep - used_time, collisionCandidates, 0.01);
      }

      // to simplify movement maths, do this once and indivisibly
      dampVelocity();
   }

   protected double tryStep(double timeStep, Collection<ICollidable> collisionCandidates, double resolution)
   {
      assert collideWith(collisionCandidates) == null;

      XY where = m_position.plus(m_velocity.multiply(timeStep));

      ColRet col = collideWith(collisionCandidates);

      if (col == null)
      {
         m_position = where;
         return timeStep;
      }

      // binary search for an interval where "start" is not colliding and "end" is

      double start = 0;
      double end = 1;

      while (end - start > resolution)
      {
         double mid = (start + end) / 2;
         ColRet temp = collideWith(collisionCandidates);

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

      // we lose the part of our velocity which is into the edge we hit
      m_velocity = filterVelocity(m_velocity, col.Normal.rot90());

      return timeStep * start;
   }

   private XY filterVelocity(XY velocity, XY keepComponent)
   {
      return keepComponent.multiply(keepComponent.dot(velocity));
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

   public ColRet collideWith(Collection<ICollidable> collisionCandidates)
   {
      for(ICollidable ic : collisionCandidates)
      {
         ColRet ret = ic.collide(this);

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
      if (Util.circleCircleIntersect(getPosition(), getRadius(),
            m.getPosition(), getRadius()) != null)
      {
         return new ColRet(m.getPosition().minus(m.getPosition()).asUnit());
      }

      return null;
   }

   private XY m_position = new XY();
   private XY m_velocity = new XY();

   public double m_orientation = 0;

   private final double m_radius;

}
