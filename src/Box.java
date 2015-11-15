public class Box
{
   final public XY Min;
   final public XY Max;

   Box()
   {
      // these exact values indicate an empty box with no size and no position
      Min = new XY(0, 0);
      Max = new XY(-1, -1);
   }

   Box(XY min, XY max)
   {
      Min = min;
      Max = max;

      if (DX() < 0)
         throw new IllegalArgumentException("Creating box with xegative X size.");

      if (DY() < 0)
         throw new IllegalArgumentException("Creating box with xegative Y size.");
   }

   XY Center()
   {
      return Min.plus(Max).divide(2);
   }

   // empty box will return -1
   double DX()
   {
      return Max.X - Min.X;
   }

   // empty box will return -1
   double DY()
   {
      return Max.Y - Min.Y;
   }

   boolean isEmpty()
   {
      // shorthand works as at present no other way to make
      // a box with -ve sizes
      return DX() == -1;
   }

   Box union(Box b)
   {
      if (isEmpty())
         return b;

      if (b.isEmpty())
         return this;

      return new Box(Min.min(b.Min), Max.max(b.Max));
   }

   public XY diagonal()
   {
      return Max.minus(Min);
   }

   @Override
   public boolean equals(Object o)
   {
      if (!(o instanceof Box))
         return false;

      Box bo = (Box)o;

      return Max.equals(bo.Max) && Min.equals(bo.Min);
   }

   @Override
   public int hashCode()
   {
      return Min.hashCode() * 31 + Max.hashCode();
   }

}