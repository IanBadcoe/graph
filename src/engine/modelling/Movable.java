package engine.modelling;

import engine.ICollidable;
import engine.XY;

import engine.Util;
import engine.XYZ;

import java.util.Collection;

public abstract class Movable extends WorldObject
{
   protected Movable(LoDModel loDModel, XYZ pos, double m_radius)
   {
      super(loDModel, pos);
      this.m_radius = m_radius;
   }

   @SuppressWarnings("WeakerAccess")
   public void addVelocity(XY v, double s)
   {
      m_speed += s;
      m_velocity = m_velocity.plus(v);
   }

   @SuppressWarnings("WeakerAccess")
   public double getSpeed()
   {
      return m_speed;
   }

   @SuppressWarnings("WeakerAccess")
   public XY getVelocity()
   {
      return m_velocity;
   }

   private void dampVelocity()
   {
      m_speed *= DampingFactor;
      m_velocity = m_velocity.multiply(DampingFactor);
   }

   public void step(double timeStep, Collection<ICollidable> collisionCandidates)
   {
      double used_time = 0;

      int attempts = 0;
      while(used_time < timeStep && attempts < 3)
      {
         attempts++;
         used_time += tryStep(timeStep - used_time, collisionCandidates, 0.1);
      }

      // to simplify movement maths, do this once and indivisibly
      dampVelocity();
   }

   @SuppressWarnings("WeakerAccess")
   protected double tryStep(double timeStep, Collection<ICollidable> collisionCandidates, double resolution)
   {
      XY direction = getVelocity().asUnit();

      assert collideWith(collisionCandidates, getPos2D(), direction, getPos2D()) == null;

      double dist = m_velocity.length() * timeStep;

      // round small velocities to zero
      if (dist < resolution / 2)
         return timeStep;

      XY where = getPos2D().plus(m_velocity.multiply(timeStep));

      XY start_where = getPos2D();

      ICollidable.ColRet col = collideWith(collisionCandidates, where, direction, start_where);

      if (col == null)
      {
         setPos2D(where);
         return timeStep;
      }

      // binary search for an interval where "start" is not colliding and "end" is

      double start = 0;
      double end = 1;

      double here_res = resolution / dist;

      while (end - start > here_res)
      {
         double mid = (start + end) / 2;
         where = getPos2D().plus(m_velocity.multiply(timeStep * mid));

         ICollidable.ColRet temp = collideWith(collisionCandidates, where, direction, start_where);

         if (temp == null)
         {
            start = mid;
            start_where = where;
         }
         else
         {
            col = temp;
            end = mid;
         }
      }

      // we can move as far as start
      where = getPos2D().plus(m_velocity.multiply(timeStep * start));
      setPos2D(where);

//      Main.addAnnotation(new AnnPoint(0xffff0000, where, 1, true));
//      Main.addAnnotation(new AnnArrow(0xff00ff00, where, where.plus(m_velocity.asUnit().multiply(100)), 0.5, true));
//      Main.addAnnotation(new AnnArrow(0xff0000ff, where, where.plus(col.Normal.rot90().multiply(90)), 0.5, true));

      // we lose the part of our velocity which is into the edge we hit
      m_velocity = filterVelocity(m_velocity, col.Normal.rot90());
//      Main.addAnnotation(new AnnArrow(0xffff00ff, where, where.plus(m_velocity.asUnit().multiply(80)), 0.5, true));

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

   private ICollidable.ColRet collideWith(Collection<ICollidable> collisionCandidates, XY where, XY direction, XY wherePrevious)
   {
      for(ICollidable ic : collisionCandidates)
      {
         ICollidable.ColRet ret = ic.collide(this, where, direction, wherePrevious);

         if (ret != null)
         {
            return ret;
         }
      }

      return null;
   }

   @Override
   public ICollidable.ColRet collide(Movable m, XY where, XY direction, XY wherePrevious)
   {
      // can set this null if we're not in motion
      // which makes this a _slightly_ different test
      if (direction != null)
      {
         XY center_dir = where.minus(getPos2D()).asUnit();
         double dot = center_dir.dot(direction);

         if (dot > ICollidable.NormalTolerance)
            return null;
      }

      if (Util.circleCircleIntersect(getPos2D(), getRadius(),
            where, m.getRadius()) != null)
      {
         // we use wherePrevious for this because that is where m will be placed (previous non-colliding position)
         // if this turns out to be end-point of the collision search
         return new ICollidable.ColRet(getPos2D().minus(wherePrevious).asUnit());
      }

      return null;
   }

   @SuppressWarnings("WeakerAccess")
   public void addOrientation(double angle)
   {
      m_orientation += angle;
   }

   private XY m_velocity = new XY();
   private double m_speed = 0;

   private double m_orientation = 0;

   private final double m_radius;

   private final double DampingFactor = 0.9;
}
