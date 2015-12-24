package engine;

// although identical to IEdge, _feels_ like it plays a different role
// and may need more differences later
public class Wall implements IEdge
{
   public Wall(XY start, XY end, XY normal){
      m_start = start;
      m_end = end;
      m_normal = normal;
   }

   public XY midPoint()
   {
      return getEnd().plus(getStart()).divide(2);
   }

   @Override
   public Wall getNext()
   {
      return m_next;
   }

   public void setNext(Wall next)
   {
      m_next = next;
   }

   @Override
   public Wall getPrev()
   {
      return m_prev;
   }

   public void setPrev(Wall prev)
   {
      m_prev = prev;
   }

   @Override
   public XY getStart()
   {
      return m_start;
   }

   @Override
   public XY getEnd()
   {
      return m_end;
   }

   @Override
   public XY getNormal()
   {
      return m_normal;
   }

   @Override
   public SuperEdge getSuperEdge()
   {
      return m_super_edge;
   }

   public void setSuperEdge(SuperEdge super_edge)
   {
      m_super_edge = super_edge;
   }

   private final XY m_start;
   private final XY m_end;
   private final XY m_normal;
   private Wall m_next;
   private Wall m_prev;
   private SuperEdge m_super_edge;
}
