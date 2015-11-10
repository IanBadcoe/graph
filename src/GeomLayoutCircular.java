class GeomLayoutCircular extends GeomLayout
{
   GeomLayoutCircular(XY position, double radius)
   {
      m_position = position;
      m_rad = radius;
   }

   static GeomLayoutCircular createFromNode(INode n)
   {
      return new GeomLayoutCircular(n.getPos(), n.getRad() * 0.95);
   }

   @Override
   Loop makeBaseGeometry()
   {
      return new Loop(new CurveCircle(m_position, m_rad));
   }

   private final XY m_position;
   private final double m_rad;
}
