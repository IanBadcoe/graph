package engine;

@SuppressWarnings("WeakerAccess")
public class CircularGeomLayout extends GeomLayout
{
   public CircularGeomLayout(XY position, double radius)
   {
      m_position = position;
      m_rad = radius;
   }

   static CircularGeomLayout createFromNode(INode n)
   {
      return new CircularGeomLayout(n.getPos(), n.getRad());
   }

   @Override
   public Loop makeBaseGeometry()
   {
      return new Loop(new CircleCurve(m_position, m_rad));
   }

   @Override
   public LoopSet makeDetailGeometry()
   {
      return null;
   }

   private final XY m_position;
   private final double m_rad;
}
