package engine;

public class XYZ
{
   public final double X;
   public final double Y;
   public final double Z;

   public XYZ()
   {
      X = 0;
      Y = 0;
      Z = 0;
   }

   public XYZ(double x, double y, double z)
   {
      X = x;
      Y = y;
      Z = z;
   }

   public XYZ(XY xy, double z)
   {
      X = xy.X;
      Y = xy.Y;
      Z = z;
   }

   public boolean equals(XYZ xyo, double tol)
   {
      return this.minus(xyo).length() <= tol;
   }

   @Override
   public boolean equals(Object o)
   {
      if (!(o instanceof XYZ))
         return false;

      XYZ xyzo = (XYZ)o;

      return X == xyzo.X && Y == xyzo.Y && Z == xyzo.Z;
   }

   @Override
   public int hashCode()
   {
      return Double.hashCode(X) * 31 + Double.hashCode(Y) + Double.hashCode(Z) * 19;
   }

   public XYZ plus(XYZ rhs)
   {
      return new XYZ(rhs.X + X, rhs.Y + Y, rhs.Z + Z);
   }

   public XYZ minus(XYZ rhs)
   {
      return new XYZ(X - rhs.X, Y - rhs.Y, Z - rhs.Z);
   }

   public XYZ negate()
   {
      return new XYZ(-X, -Y, -Z);
   }

   public XYZ divide(double f)
   {
      return new XYZ(X / f, Y / f, Z / f);
   }

   public XYZ multiply(double f)
   {
      return new XYZ(X * f, Y * f, Z * f);
   }

   public XYZ min(XYZ rhs)
   {
      return new XYZ(Math.min(X, rhs.X),
            Math.min(Y, rhs.Y),
            Math.min(Z, rhs.Z));
   }

   public XYZ max(XYZ rhs)
   {
      return new XYZ(Math.max(X, rhs.X),
            Math.max(Y, rhs.Y),
            Math.max(Z, rhs.Z));
   }

   public boolean isZero()
   {
      return X == 0 && Y == 0 && Z == 0;
   }

   public double dot(XYZ rhs)
   {
      return X * rhs.X + Y * rhs.Y + Z * rhs.Z;
   }

   public double length2()
   {
      return X * X + Y * Y + Z * Z;
   }

   public double length()
   {
      return Math.sqrt(length2());
   }

   public boolean isUnit()
   {
      double d = length() - 1;
      return Math.abs(d) < 1e-6;
   }

   public XYZ asUnit()
   {
      return this.divide(this.length());
   }

   public static XYZ interpolate(XYZ start, XYZ end, double fraction)
   {
      return start.plus(end.minus(start).multiply(fraction));
   }
}
