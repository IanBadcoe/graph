package engine;

public interface ICollidable
{
   ColRet collide(Movable m);

   class ColRet
   {
      public final XY Normal;

      public ColRet(XY normal)
      {
         Normal = normal;
      }
   }
}
