package engine;

import java.util.Collection;

public interface ICollidable
{
   // implicitly all edges are between sequential corners
   Collection<XY> getCorners();

   Level.Collision findFirstCollision(Movable.DynamicsPosition oldState,
                                      Movable.DynamicsPosition newState,
                                      IPhysicalLevel level, double resolution);

   XY transformedCorner(int idx, Movable.DynamicsState where);
}
