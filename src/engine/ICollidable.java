package engine;

import java.util.Collection;

public interface ICollidable
{
   // implicitly all edges are between sequential corners
   Collection<XY> getCorners();
}
