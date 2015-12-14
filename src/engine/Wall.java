package engine;

public class Wall
{
   @SuppressWarnings("WeakerAccess")
   public final XY Start;
   @SuppressWarnings("WeakerAccess")
   public final XY End;
   @SuppressWarnings("WeakerAccess")
   public final XY Normal;

   Wall(XY start, XY end, XY normal)
   {
      Start = start;
      End = end;
      Normal = normal;
   }

   void setNext(Wall w)
   {
      m_next = w;
   }
   Wall getNext()
   {
      return m_next;
   }

   void setPrev(Wall w)
   {
      m_prev = w;
   }
   Wall getPrev()
   {
      return m_prev;
   }

   public XY midPoint()
   {
      return End.plus(Start).divide(2);
   }

   private Wall m_next;
   private Wall m_prev;
}
