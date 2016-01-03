package Annotations;

import engine.IDraw;
import engine.XY;

public class AnnArrow extends Annotation
{
   public AnnArrow(int colour, XY from, XY to, double thickness, boolean scaling)
   {
      super(colour);

      this.m_from = from;
      this.m_to = to;
      this.m_thickness = thickness;
      this.m_scaling = scaling;
   }

   @Override
   public void draw(IDraw draw)
   {
      draw.stroke(m_colour);
      draw.strokeWidth(m_thickness, m_scaling);
      draw.line(m_from, m_to);

      XY offset = m_to.minus(m_from).divide(10);
      draw.line(m_to, m_to.minus(offset).plus(offset.rot90()));
      draw.line(m_to, m_to.minus(offset).plus(offset.rot270()));
   }

   private final XY m_from;
   private final XY m_to;
   private final double m_thickness;
   private final boolean m_scaling;
}
