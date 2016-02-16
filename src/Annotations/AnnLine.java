package annotations;

import engine.IDraw;
import engine.XY;
import engine.XYZ;

public class AnnLine extends Annotation
{
   public AnnLine(int colour, XY from, XY to, double thickness, boolean scaling)
   {
      super(colour);

      this.m_from = from;
      this.m_to = to;
      this.m_thickness = thickness;
      this.m_scaling = scaling;
   }

   @Override
   public void draw2D(IDraw draw)
   {
      draw.stroke(m_colour);
      draw.strokeWidth(m_thickness, m_scaling);
      draw.line(m_from, m_to);
   }

   @Override
   public void draw3D(IDraw draw, XYZ eye)
   {
      // nothing
   }

   private final XY m_from;
   private final XY m_to;
   private final double m_thickness;
   private final boolean m_scaling;
}
