package engine;

public class Wall
{
   public final XY Start;
   public final XY End;
   public final XY Normal;

   private Wall m_prev;
   private Wall m_next;

   public Wall(XY start, XY end, XY normal)
   {
      Start = start;
      End = end;
      Normal = normal;
   }

   public XY midPoint()
   {
      return End.plus(Start).divide(2);
   }

   public void setPrev(Wall prev)
   {
      this.m_prev = prev;
   }

   public Wall getPrev()
   {
      return m_prev;
   }


   public void setNext(Wall next)
   {
      this.m_next = next;
   }

   public Wall getNext()
   {
      return m_next;
   }
}
