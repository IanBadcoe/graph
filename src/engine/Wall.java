package engine;

public class Wall
{
   @SuppressWarnings("WeakerAccess")
   public final XY Start;
   @SuppressWarnings("WeakerAccess")
   public final XY End;
   @SuppressWarnings("WeakerAccess")
   public final XY Normal;

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
}
