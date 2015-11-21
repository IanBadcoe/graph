abstract class GeomLayout
{
   interface IGeomLayoutCreateFromNode
   {
      GeomLayout create(INode n);
   }

   GeomLayout()
   {
   }

   // one +ve loop that cuts the outer envelope of the space the node will occupy
   abstract Loop makeBaseGeometry();

   // one of more -ve loops that put things like pillar back inside
   // the base geometry
   abstract LoopSet makeDetailGeometry();

   // we cut all edges up into fragments shorter than this
   // is this a good idea?
   static double MaxEdgeLength = 10;
}
