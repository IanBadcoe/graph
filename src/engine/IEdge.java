package engine;

public interface IEdge
{
   IEdge getNext();

   IEdge getPrev();

   XY getStart();

   XY getEnd();

   XY getNormal();

   SuperEdge getSuperEdge();
}
