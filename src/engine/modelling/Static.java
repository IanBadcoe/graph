package engine.modelling;

import engine.XY;
import engine.XYZ;

public abstract class Static extends WorldObject
{
   public Static(LoDModel loDModel, XYZ pos)
   {
      super(loDModel, pos);
   }

   @Override
   public XYZ getEye()
   {
      return null;
   }

   @Override
   public ColRet collide(Movable m, XY where, XY direction, XY wherePrevious)
   {
      return null;
   }
}
