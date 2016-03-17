package engine.modelling;

import engine.XY;
import engine.XYZ;
import engine.controllers.IController;

public class Static extends WorldObject
{
   public Static(LoDModel loDModel, XYZ pos, IController controller, double radius)
   {
      super(loDModel, pos, controller, radius);
   }

   // can easily define this meaningfully later if we want, for e.g. security camera views
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

   @Override
   public void setOrientation(double v)
   {
      // statics do not re-orient, as far as we thus far know
      throw new UnsupportedOperationException();
   }
}
