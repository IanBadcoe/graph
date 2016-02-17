package engine;

import engine.brep.CircleCurve;
import engine.brep.Loop;
import engine.brep.LoopSet;

@SuppressWarnings("WeakerAccess")
public class CircularPillarGeomLayout extends GeomLayout
{
   CircularPillarGeomLayout(XY position, double radius)
   {
      m_position = position;
      m_rad = radius;
   }

   public static GeomLayout createFromNode(INode n)
   {
      return new CircularPillarGeomLayout(n.getPos(), n.getRad());
   }

   @Override
   public Loop makeBaseGeometry()
   {
      return new Loop(new CircleCurve(m_position, m_rad));
   }

   @Override
   public LoopSet makeDetailGeometry()
   {
      LoopSet ret = new LoopSet();
      ret.add(new Loop(new CircleCurve(m_position, m_rad / 2, CircleCurve.RotationDirection.Reverse)));

      return ret;
   }

   private final XY m_position;
   private final double m_rad;
}
