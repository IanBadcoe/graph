import java.util.ArrayList;

abstract class Curve
{
   Curve(double start_param, double end_param)
   {
      m_start_param = start_param;
      m_end_param = end_param;
   }

   public abstract XY computePos(double m_start_param);

   public abstract int hashCode();

   public abstract boolean equals(Object o);

   public abstract Double findParamForPoint(XY first, double tol);

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

   ArrayList<OrderedPair<Double, Double>> intersect(Curve other)
   {
      // efficient implementation of this requires knowledge of sub-classes
      // so hide in utility class to make me feel more comfortable
      return Util.curveCurveIntersect(this, other);
   }

   final private double m_start_param;
   final private double m_end_param;
}
