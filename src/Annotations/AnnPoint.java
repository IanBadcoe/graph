package annotations;

import engine.IDraw;
import engine.XY;
import engine.XYZ;

public class AnnPoint extends Annotation
{
   public AnnPoint(int colour, XY where, double radius, boolean scaling)
   {
      super(colour);

      this.m_where = where;
      this.m_radius = radius;
      this.m_scaling = scaling;
   }

   @Override
   public void draw2D(IDraw draw)
   {
      draw.fill(m_colour);
      draw.noStroke();

      if (m_scaling)
      {
         draw.circle(m_where, m_radius / draw.getScale());
      }
      else
      {
         draw.circle(m_where, m_radius);
      }
   }

   @Override
   public void draw3D(IDraw draw, XYZ eye)
   {
      // nothing
   }

   private final XY m_where;
   private final double m_radius;
   private final boolean m_scaling;
}
