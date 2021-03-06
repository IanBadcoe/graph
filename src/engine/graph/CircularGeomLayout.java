package engine.graph;

import engine.XY;
import engine.brep.CircleCurve;
import engine.brep.Loop;
import engine.brep.LoopSet;

@SuppressWarnings("WeakerAccess")
public class CircularGeomLayout extends GeomLayout
{
   public CircularGeomLayout(XY position, double radius)
   {
      Position = position;
      Radius = radius;
   }

   public static CircularGeomLayout createFromNode(INode n)
   {
      return new CircularGeomLayout(n.getPos(), n.getRad());
   }

   @Override
   public Loop makeBaseGeometry()
   {
      return new Loop(new CircleCurve(Position, Radius));
   }

   @Override
   public LoopSet makeDetailGeometry()
   {
      return null;
   }

   public final XY Position;
   public final double Radius;
}
