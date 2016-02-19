package engine.modelling;

import engine.IDraw;
import engine.IDrawable;
import engine.XY;
import engine.XYZ;

@SuppressWarnings("WeakerAccess")
public abstract class LoDDrawable implements IDrawable
{
   public LoDDrawable(LoDModel loDModel)
   {
      this.LoDModel = loDModel;
   }

   public void draw2D(IDraw draw)
   {
      // cover the case where something could have a model but doesn't in this instance
      if (LoDModel == null)
         return;

      draw.circle(getPos2D(), LoDModel.Radius);
   }

   public void draw3D(IDraw draw, XYZ eye)
   {
      // cover the case where something could have a model but doesn't in this instance
      if (LoDModel == null)
         return;

      double d2 = getPos3D().minus(eye).length2();

      int lod = findLoD(d2);

      draw(draw, lod);
   }

   private int findLoD(double d2)
   {
      int ret = 0;

      while (d2 > LoDDistances[ret] && ret < LoDDistances.length - 1)
         ret++;

      return ret;
   }

   private void draw(IDraw draw, int lod)
   {
      LoDModel.draw(draw, getPos3D(), getOrientation(), getRotation(), getElevation(), lod);
   }

   public abstract XYZ getPos3D();
   public abstract XY getPos2D();

   // orientation is the facing one the whole object in the world
   // rotation and elevation are the tracking of some "turret-like" subcomponent within the
   // object
   //
   // if we get objects that want more internal posing than that,
   // could at some point generalise this to an array of doubles
   // whose length would have to match the expectations of the controller and the LoDModel
   public abstract double getOrientation();
   public abstract void setOrientation(double v);
   public abstract double getElevation();
   public abstract void setElevation(double v);
   public abstract double getRotation();
   public abstract void setRotation(double v);

   public abstract XYZ getEye();


   private final LoDModel LoDModel;

   public static final double[] LoDDistances = new double[] { 400, 900 };
   public static final double[] FacetingFactors = new double[] { 0.1, 0.5, 4 };

   static
   {
      assert LoDDistances.length + 1 == FacetingFactors.length;
   }
}
