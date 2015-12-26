package engine;

// purpose of this is to make edges in ColRet a different class from Edge and any other derived classes
// point of that is that on long straight edges we cut them into many small edges but we keep the one long super-edge
// for physics purposes (basically so we don't see just one small bit in the middle of a long colliding edge)
class SuperEdge
{
   SuperEdge(XY m_start, XY m_end, XY m_normal)
   {
      this.m_start = m_start;
      this.m_end = m_end;
      this.m_normal = m_normal;
   }

   public SuperEdge getNext()
   {
      return m_next;
   }

   public SuperEdge getPrev()
   {
      return m_prev;
   }

   public XY getStart()
   {
      return m_start;
   }

   public XY getEnd()
   {
      return m_end;
   }

   public XY getNormal()
   {
      return m_normal;
   }

   private final XY m_start;
   private final XY m_end;
   private final XY m_normal;

   private SuperEdge m_next;
   private SuperEdge m_prev;

   public void setPrev(SuperEdge prev)
   {
      m_prev = prev;
   }

   public void setNext(SuperEdge next)
   {
      m_next = next;
   }

   public double getFractionalPosition(XY position)
   {
      return Util.calcFractionalPosition(m_start, m_end, position);
   }
}
