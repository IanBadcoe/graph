abstract class Curve
{
   Curve(double start_param, double end_param)
   {
      m_start_param = start_param;
      m_end_param = end_param;
   }

   // exquisite abstractions

   public abstract XY computePos(double param);

   public abstract Double findParamForPoint(XY pnt, double tol);

   public abstract Curve cloneWithChangedParams(double start, double end);

   public abstract Box boundingBox();

   public abstract XY tangent(Double param);

   // overridden for cyclic curves

   public boolean withinParams(double p, double tol)
   {
      return p > m_start_param - tol
            && p < m_end_param + tol;
   }

   // overridden but overrides need to call these base implementations
   public int hashCode()
   {
      return Double.hashCode(m_start_param) + Double.hashCode(m_end_param) * 31;
   }

   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof Curve))
         return false;

      Curve co = (Curve)o;

      return co.startParam() == startParam() && co.endParam() == endParam();
   }

   // concrete methods

   public XY startPos()
   {
      return computePos(m_start_param);
   }

   public XY endPos()
   {
      return computePos(m_end_param);
   }

   public double startParam()
   {
      return m_start_param;
   }

   public double endParam()
   {
      return m_end_param;
   }

   public double paramRange()
   {
      return m_end_param - m_start_param;
   }

   public double paramCoordinateDist(double p1, double p2)
   {
      return computePos(p1).minus(computePos(p2)).length();
   }

   final private double m_start_param;
   final private double m_end_param;
}
