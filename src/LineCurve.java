import java.security.InvalidParameterException;

class LineCurve extends Curve
{
   LineCurve(XY position, XY directionCosines, double length)
   {
      super(0, length);

      Position = position;
      Direction = directionCosines;

      if (!Direction.isUnit())
         throw new InvalidParameterException();
   }

   LineCurve(XY position, XY directionCosines, double start, double end)
   {
      super(start, end);

      Position = position;
      Direction = directionCosines;
   }

   @Override
   public XY computePos(double param)
   {
      return Position.plus(Direction.multiply(param));
   }

   @Override
   public Double findParamForPoint(XY pnt, double tol)
   {
      XY relative = pnt.minus(Position);

      if (Math.abs(relative.dot(Direction.rot90())) > tol)
         return null;

      double par = relative.dot(Direction);

      if (!withinParams(par, tol))
         return null;

      return par;
   }

   @Override
   public Curve cloneWithChangedParams(double start, double end)
   {
      return new LineCurve(Position, Direction, start, end);
   }

   @Override
   public Box boundingBox()
   {
      return new Box(startPos().min(endPos()),
            startPos().max(endPos()));
   }

   @Override
   public XY tangent(Double param)
   {
      return Direction;
   }

   @Override
   public int hashCode()
   {
      return super.hashCode() * 17 + Position.hashCode() * 31 ^ Direction.hashCode();
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

      return Position.equals(lc_o.Position) && Direction == lc_o.Direction;
   }

   public final XY Position;
   public final XY Direction;
}
