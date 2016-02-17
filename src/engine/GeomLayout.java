package engine;

import engine.brep.Loop;
import engine.brep.LoopSet;

abstract public class GeomLayout
{
   public interface IGeomLayoutCreateFromNode
   {
      GeomLayout create(INode n);
   }

   public interface IGeomLayoutCreateFromDirectedEdge
   {
      GeomLayout create(DirectedEdge de);
   }

   // one +ve loop that cuts the outer envelope of the space the node will occupy
   public abstract Loop makeBaseGeometry();

   // one of more -ve loops that put things like pillar back inside
   // the base geometry
   public abstract LoopSet makeDetailGeometry();

   public static GeomLayout makeDefaultCorridor(DirectedEdge de)
   {
      // scale the corridor rectangle's width down slightly
      // so that it doesn't precisely hit at a tangent to any adjoining junction-node's circle
      // -- that causes awkward numerical precision problems in the curve-curve intersection routines
      // which can throw out the union operation
      return new RectangularGeomLayout(de.Start.getPos(),
            de.End.getPos(), de.HalfWidth * 0.99);
   }
}
