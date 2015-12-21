package engine;

// although identical to ICollidable.Edge, _feels_ like it plays a different role
// and may need more differences later
public class Wall extends ICollidable.Edge
{
   public Wall(XY start, XY end, XY normal)
   {
      super(start, end, normal);
   }
}
