package physics_testbed;

import engine.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PhysicsTestObject extends Movable
{
   PhysicsTestObject(double width, double height, double mass, double coefficientOfRestitution)
   {
      super(
            mass,
            // moment of inertia for a rectangle rotating in-plane
            // also often expressed w * h * (w^2 + h^2) / 12 (second moment of area) TIMES area density: m / (w * h)
            mass * (width * width + height * height) / 12,
            coefficientOfRestitution);

      width /= 2;
      height /= 2;

      XY tl = new XY(width, height);
      XY bl = new XY(width, -height);
      XY br = new XY(-width, -height);
      XY tr = new XY(-width, height);

      m_corners.add(tl);
      m_corners.add(bl);
      m_corners.add(br);
      m_corners.add(tr);
   }

   @Override
   public Collection<XY> getCorners()
   {
      return Collections.unmodifiableCollection(m_corners);
   }

   @Override
   public Level.Collision findFirstCollision(DynamicsPosition oldState, DynamicsPosition newState,
                                             IPhysicalLevel level, double resolution)
   {
      // we're looking for the first sub-interval of this movement where
      // the interval is shorter than resolution and the end is colliding but the start is not
      //
      // when we get this we'll return start as the collision point
      double d2 = newState.Position.minus(newState.Position).length2();
   }

   @Override
   public XY transformedCorner(int idx, DynamicsState where)
   {
      Matrix2D rot = new Matrix2D(where.Orientation);

      return rot.multiply(m_corners.get(idx)).plus(where.Position);
   }

   ArrayList<XY> m_corners = new ArrayList<>();
}
