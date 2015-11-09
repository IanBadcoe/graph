class XY
{
   final double X;
   final double Y;

   XY()
   {
      X = 0;
      Y = 0;
   }

   XY(double x, double y)
   {
      X = x;
      Y = y;
   }

   public boolean equals(Object o, double tol)
   {
      if (!(o instanceof XY))
         return false;

      XY xyo = (XY)o;

      return Math.abs(X - xyo.X) <= tol
            && Math.abs(Y - xyo.Y) <= tol;
   }

   @Override
   public boolean equals(Object o)
   {
      if (!(o instanceof XY))
         return false;

      XY xyo = (XY)o;

      return X == xyo.X && Y == xyo.Y;
   }

   @Override
   public int hashCode()
   {
      return ((Double)X).hashCode() * 31 + ((Double)Y).hashCode();
   }

   XY Plus(XY rhs)
   {
      return new XY(rhs.X + X, rhs.Y + Y);
   }

   XY Minus(XY rhs)
   {
      return new XY(X - rhs.X, Y - rhs.Y);
   }

   XY Negate()
   {
      return new XY(-X, -Y);
   }

   XY Divide(double f)
   {
      return new XY(X / f, Y / f);
   }

   XY Multiply(double f)
   {
      return new XY(X * f, Y * f);
   }

   double Length()
   {
      return Math.sqrt(X * X + Y * Y);
   }

   XY Min(XY rhs)
   {
      return new XY(Math.min(X, rhs.X),
            Math.min(Y, rhs.Y));
   }

   XY Max(XY rhs)
   {
      return new XY(Math.max(X, rhs.X),
            Math.max(Y, rhs.Y));
   }

   boolean IsZero()
   {
      return X == 0 && Y == 0;
   }

   double Dot(XY rhs)
   {
      return X * rhs.X + Y * rhs.Y;
   }
}
