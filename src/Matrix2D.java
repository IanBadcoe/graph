class Matrix2D
{
   private final double[][] M = new double[2][2];

   Matrix2D(double angle)
   {
      double sa = Math.sin(angle);
      double ca = Math.cos(angle);

      M[0][0] = M[1][1] = ca;
      M[0][1] = -sa;
      M[1][0] = sa;
   }

   XY Multiply(XY rhs)
   {
      // haven't thought much about wether matrix is row major or column major
      // or wether this is pre or post multiplication
      // would need a convention for that if we got more complex matrices
      return new XY(
            rhs.X * M[0][0] + rhs.Y * M[0][1],
            rhs.X * M[1][0] + rhs.Y * M[1][1]);
   }
}
