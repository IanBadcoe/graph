package engine;

abstract public class GeomLayout
{
   interface IGeomLayoutCreateFromNode
   {
      GeomLayout create(INode n);
   }

   interface IGeomLayoutCreateFromDirectedEdge
   {
      GeomLayout create(DirectedEdge de);
   }

   // one +ve loop that cuts the outer envelope of the space the node will occupy
   public abstract Loop makeBaseGeometry();

   // one of more -ve loops that put things like pillar back inside
   // the base geometry
   public abstract LoopSet makeDetailGeometry();
}
