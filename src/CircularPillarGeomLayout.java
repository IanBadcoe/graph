@SuppressWarnings("WeakerAccess")
class CircularPillarGeomLayout extends GeomLayout
{
   CircularPillarGeomLayout(XY position, double radius)
   {
      m_position = position;
      m_rad = radius;
   }

   static GeomLayout createFromNode(INode n)
   {
      return new CircularPillarGeomLayout(n.getPos(), n.getRad() * 0.95);
   }

   @Override
   Loop makeBaseGeometry()
   {
      return new Loop(new CircleCurve(m_position, m_rad));
   }

   @Override
   LoopSet makeDetailGeometry()
   {
      LoopSet ret = new LoopSet();
      ret.add(new Loop(new CircleCurve(m_position, m_rad / 2, CircleCurve.RotationDirection.Reverse)));

      return ret;
   }

   private final XY m_position;
   private final double m_rad;
}
