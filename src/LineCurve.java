class LineCurve extends Curve
{
   LineCurve(XY position, XY directionCosines, double length)
   {
      super(0, length);

      Position = position;
      DirectionCosines = directionCosines;
   }

   LineCurve(XY position, XY directionCosines, double start, double end)
   {
      super(start, end);

      Position = position;
      DirectionCosines = directionCosines;
   }

   @Override
   public XY computePos(double param)
   {
      return Position.plus(DirectionCosines.multiply(param));
   }

   @Override
   public Double findParamForPoint(XY pnt, double tol)
   {
      XY relative = pnt.minus(Position);

      if (Math.abs(relative.dot(DirectionCosines.rot90())) > tol)
         return null;

      double par = relative.dot(DirectionCosines);

      if (!withinParams(par, tol))
         return null;

      return par;
   }

   @Override
   public Curve cloneWithChangedParams(double start, double end)
   {
      return new LineCurve(Position, DirectionCosines, start, end);
   }

   @Override
   public int hashCode()
   {
      return super.hashCode() * 17 + Position.hashCode() * 31 ^ DirectionCosines.hashCode();
   }

   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof LineCurve))
         return false;

      if (!super.equals(o))
         return false;

      LineCurve lc_o = (LineCurve)o;

      return Position.equals(lc_o.Position) && DirectionCosines == lc_o.DirectionCosines;
   }

   public final XY Position;
   public final XY DirectionCosines;
}
