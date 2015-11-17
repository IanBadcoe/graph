abstract class GeomLayout
{
   interface IGeomLayoutCreateFromNode
   {
      GeomLayout create(INode n);
   }

   GeomLayout()
   {
   }

   abstract Loop makeBaseGeometry();

   // we cut all edges up into fragments shorter than this
   // is this a good idea?
   static double MaxEdgeLength = 10;
}
