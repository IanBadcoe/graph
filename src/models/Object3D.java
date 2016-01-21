package models;

import engine.IDraw;
import engine.IDrawable;
import engine.XY;
import engine.XYZ;

public class Object3D implements IDrawable
{
   public Object3D(Model model)
   {
      this.Model = model;
   }

   public void draw2D(IDraw draw)
   {
      draw.circle(new XY(m_position), Model.Radius);
   }

   public void draw3D(IDraw draw, XYZ eye)
   {
      double d2 = m_position.minus(eye).length2();

      int lod = findLoD(d2);

      draw(draw, lod);
   }

   private int findLoD(double d2)
   {
      int ret = 0;

      while (d2 > m_lod_dist2s[ret] && ret < m_lod_dist2s.length)
         ret++;

      return ret;
   }

   private void draw(IDraw draw, int lod)
   {
      Model.draw(draw, m_position, m_orientation, lod);
   }

   private XYZ m_position;
   private double m_orientation;
   private final Model Model;

   private static final double[] m_lod_dist2s = new double[] { 100, 400, 1000 };
}
