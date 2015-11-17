@SuppressWarnings("WeakerAccess")
class CircularGeomLayout extends GeomLayout
{
   CircularGeomLayout(XY position, double radius)
   {
      m_position = position;
      m_rad = radius;
   }

   static CircularGeomLayout createFromNode(INode n)
   {
      return new CircularGeomLayout(n.getPos(), n.getRad() * 0.95);
   }

   @Override
   Loop makeBaseGeometry()
   {
      return new Loop(new CircleCurve(m_position, m_rad));
   }

   private final XY m_position;
   private final double m_rad;
}
