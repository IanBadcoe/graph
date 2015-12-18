package physics_testbed;

import engine.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PhysicsTestObject extends Movable
{
   @SuppressWarnings("SameParameterValue")
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

      m_radius = Math.sqrt(width * width + height * height);

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
   public double getRadius()
   {
      return m_radius;
   }

   private final ArrayList<XY> m_corners = new ArrayList<>();
   private final double m_radius;
}
