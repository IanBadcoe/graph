package engine;

import java.util.Collection;

public interface ICollidable
{
   ColRet collide(Collection<IEdge> edges, double radius, XY centre, Movable activeMovable);

   class ColRet
   {
      final Movable ActiveMovable;
      final Movable InactiveMovable;

      final SuperEdge ActiveEdge;
      final SuperEdge InactiveEdge;
      final XY Position;

      ColRet(Movable activeMovable, Movable inactiveMovable,
             SuperEdge activeEdge, SuperEdge inactiveEdge, XY position)
      {
         ActiveMovable = activeMovable;
         InactiveMovable = inactiveMovable;

         ActiveEdge = activeEdge;
         InactiveEdge = inactiveEdge;
         Position = position;
      }
   }

}
