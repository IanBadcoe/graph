import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

abstract class GeomLayout
{
   interface GeomLayoutCreateFromNode
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
