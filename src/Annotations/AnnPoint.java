package Annotations;

import engine.IDraw;
import engine.XY;

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
   public void draw(IDraw draw)
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

   private final XY m_where;
   private final double m_radius;
   private final boolean m_scaling;
}
