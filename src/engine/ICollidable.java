package engine;

public interface ICollidable
{
   ColRet collide(Movable m, XY where, XY direction, XY wherePrevious);

   class ColRet
   {
      public final XY Normal;

      public ColRet(XY normal)
      {
         Normal = normal;
      }
   }

   double NormalTolerance = 1e-6;
}
