public class Box
{
   final public XY Min;
   final public XY Max;

   Box()
   {
      Min = new XY();
      Max = new XY();
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

   double DX()
   {
      return Max.X - Min.X;
   }

   double DY()
   {
      return Max.Y - Min.Y;
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