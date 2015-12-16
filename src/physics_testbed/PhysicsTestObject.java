package physics_testbed;

import engine.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PhysicsTestObject extends Movable
{
   PhysicsTestObject(double width, double height)
   {
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

   ArrayList<XY> m_corners = new ArrayList<>();
}
