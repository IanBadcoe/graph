class CurveCircle extends Curve
{
   CurveCircle(XY position, double radius)
   {
      // we'll only represent whole circles like this
      // so only this exact params will mean completely cyclic
      super(0.0, 2 * Math.PI);

      m_position = position;
      m_radius = radius;
   }

   final private XY m_position;
   final private double m_radius;

   @Override
   public XY computePos(double param)
   {
      return m_position.Plus(new XY(Math.sin(param), Math.cos(param)).Multiply(m_radius));
   }
}
