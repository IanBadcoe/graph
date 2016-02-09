package engine.objects;

import engine.ICollidable;
import engine.IDrawable;
import engine.XY;
import engine.XYZ;

// base class for "real" things that can be inserted into levels
// e.g. they are drawable and collidable, this is going to mean:
// movables: players, enemies, maybe munitions
// scenery: doors etc (can change shape but not move)
// there's two things that might be around but which are less real:
// fx - will be drawable but not cvollidable
// triggers - might be collidable but not drawable (if that kind of collision proves similar enough
public abstract class WorldObject extends LoDDrawable implements ICollidable, IDrawable
{
   public WorldObject(LoDModel loDModel)
   {
      super(loDModel);
   }

   public abstract XY getPos2D();

   public abstract XYZ getEye();
}
