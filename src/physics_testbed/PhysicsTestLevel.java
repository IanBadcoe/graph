package physics_testbed;

import engine.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PhysicsTestLevel implements engine.IPhysicalLevel
{
   Collection<Wall> getWalls()
   {
      return Collections.unmodifiableCollection(m_walls);
   }

   void addWall(Wall wall)
   {
      m_walls.add(wall);
   }

   ArrayList<Wall> m_walls = new ArrayList<>();
}
