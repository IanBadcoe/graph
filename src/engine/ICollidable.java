package engine;

public interface ICollidable
{
   ColRet collide(Movable m);
   ColRet collide(Movable m, XY where);

   class ColRet
   {
      public final XY Normal;

      public ColRet(XY normal)
      {
         Normal = normal;
      }
   }
}
