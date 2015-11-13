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

      return this.minus(xyo).length() < tol;
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

   XY plus(XY rhs)
   {
      return new XY(rhs.X + X, rhs.Y + Y);
   }

   XY minus(XY rhs)
   {
      return new XY(X - rhs.X, Y - rhs.Y);
   }

   XY negate()
   {
      return new XY(-X, -Y);
   }

   XY divide(double f)
   {
      return new XY(X / f, Y / f);
   }

   XY multiply(double f)
   {
      return new XY(X * f, Y * f);
   }

   XY min(XY rhs)
   {
      return new XY(Math.min(X, rhs.X),
            Math.min(Y, rhs.Y));
   }

   XY max(XY rhs)
   {
      return new XY(Math.max(X, rhs.X),
            Math.max(Y, rhs.Y));
   }

   boolean isZero()
   {
      return X == 0 && Y == 0;
   }

   double dot(XY rhs)
   {
      return X * rhs.X + Y * rhs.Y;
   }

   public double length2()
   {
      return X * X + Y * Y;
   }

   double length()
   {
      return Math.sqrt(length2());
   }

   public XY rot90()
   {
      return new XY(Y, -X);
   }

   public boolean isUnit()
   {
      return Math.abs(length() - 1) < 1e-6;
   }
}
