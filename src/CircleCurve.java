class CircleCurve extends Curve
{
   final public XY Position;
   final public double Radius;

   CircleCurve(XY position, double radius)
   {
      // we'll only represent whole circles like this
      // so only this exact params will mean completely cyclic
      super(0.0, 2 * Math.PI);

      Position = position;
      Radius = radius;
   }

   @Override
   public XY computePos(double param)
   {
      return Position.Plus(new XY(Math.sin(param), Math.cos(param)).Multiply(Radius));
   }

   @Override
   public int hashCode()
   {
      return Position.hashCode() * 31 ^ Double.hashCode(Radius);
   }

   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof CircleCurve))
         return false;

      CircleCurve cc_o = (CircleCurve)o;

      return Position.equals(cc_o.Position) && Radius == cc_o.Radius;
   }

   @Override
   public Double findParamForPoint(XY pnt, double tol)
   {
      XY relative = pnt.Minus(Position);

      if (Math.abs(relative.Length() - Radius) > tol)
         return null;

      double ang = Math.atan2(relative.X, relative.Y);

      // atan2 returns between -pi and + pi
      if (isCyclic())
      {
         if (ang < 0.0) ang += 2 * Math.PI;

         return ang;
      }

      if (startParam() - ang < -tol)
         return null;

      if (ang - endParam() > tol)
         return null;

      return ang;
   }

   boolean isCyclic()
   {
      return startParam() == 0.0 && endParam() == Math.PI * 2;
   }
}
