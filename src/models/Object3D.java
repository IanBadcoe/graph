package models;

import engine.IDraw;
import engine.IDrawable;
import engine.XY;
import engine.XYZ;

@SuppressWarnings("WeakerAccess")
public class Object3D implements IDrawable
{
   public Object3D(Model model)
   {
      assert model != null;

      this.Model = model;
   }

   public void draw2D(IDraw draw)
   {
      draw.circle(new XY(Position), Model.Radius);
   }

   public void draw3D(IDraw draw, XYZ eye)
   {
      double d2 = Position.minus(eye).length2();

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
      Model.draw(draw, Position, Orientation, Elevation, lod);
   }

   @SuppressWarnings("CanBeFinal")
   public XYZ Position = new XYZ();
   @SuppressWarnings("CanBeFinal")
   public double Orientation = 0;
   public double Elevation = 0;

   private final Model Model;

   public static final double[] LoDDistances = new double[] { 400, 900 };
   public static final double[] FacetingFactors = new double[] { 0.1, 0.5, 4 };

   static
   {
      assert LoDDistances.length + 1 == FacetingFactors.length;
   }
}
