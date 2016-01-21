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

      while (d2 > m_lod_dist2s[ret] && ret < m_lod_dist2s.length - 1)
         ret++;

      return ret;
   }

   private void draw(IDraw draw, int lod)
   {
      Model.draw(draw, Position, Orientation, lod);
   }

   @SuppressWarnings("CanBeFinal")
   public XYZ Position = new XYZ();
   @SuppressWarnings("CanBeFinal")
   public double Orientation = 0;

   private final Model Model;

   private static final double[] m_lod_dist2s = new double[] { 100, 400 };
}
