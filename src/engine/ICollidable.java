package engine;

import java.util.Collection;

public interface ICollidable
{
   // implicitly all edges are between sequential corners
   Collection<XY> getCorners();

   XY transformedCorner(int idx, Movable.DynamicsState where);

   // a containing radius for the whole thing
   double getRadius();
}
