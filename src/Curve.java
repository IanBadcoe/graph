abstract class Curve
{
   Curve(double start_param, double end_param)
   {
      m_start_param = start_param;
      m_end_param = end_param;
   }

   public abstract XY computePos(double m_start_param);

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

   final private double m_start_param;
   final private double m_end_param;
}
