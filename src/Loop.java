import java.util.ArrayList;

class Loop
{
   Loop(Curve c)
   {
      m_curves.add(c);

      m_param_range = c.endParam() - c.startParam();

      XY s = c.startPos();
      XY e = c.endPos();

      assert s.equals(e, 1e-6);
   }

   public double paramRange()
   {
      return m_param_range;
   }

   public XY computePos(double p)
   {
      // curve param ranges can be anywhere
      // but the loop param range starts from zero
      for(Curve c : m_curves)
      {
         if (c.paramRange() < p)
         {
            p -= c.paramRange();
         }
         else
         {
            // shift the param range where the curve wants it...
            return c.computePos(p - c.startParam());
         }
      }

      return null;
   }

   public int numCurves()
   {
      return m_curves.size();
   }

   public void union(Loop l2)
   {
   }

   private final ArrayList<Curve> m_curves = new ArrayList<>();

   private final double m_param_range;
}
