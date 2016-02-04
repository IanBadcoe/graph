package engine;

public class LineCurve extends Curve
{
   public LineCurve(XY position, XY directionCosines, double length)
   {
      super(0, length);

      if (position == null)
         throw new NullPointerException("'position' cannot be null");

      if (directionCosines == null)
         throw new NullPointerException("'directionCosines' cannot be null");

      Position = position;
      Direction = directionCosines;

      if (!Direction.isUnit())
         throw new IllegalArgumentException();
   }

   LineCurve(XY position, XY directionCosines, double start, double end)
   {
      super(start, end);

      Position = position;
      Direction = directionCosines;
   }

   @Override
   protected XY computePosInner(double param)
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
   public Curve merge(Curve c_after)
   {
      if (c_after == this)
         return null;

      if (!(c_after instanceof LineCurve))
         return null;

      LineCurve c_lc = (LineCurve)c_after;
      // could loop for coaxial line swith different origins here
      // but current use is more to re-merge stuff we temporarily split
      // and that all leaves Position the same in both halves
      if (!Position.equals(c_lc.Position))
         return null;

      if (!Direction.equals(c_lc.Direction))
         return null;

      if (EndParam != c_lc.StartParam)
         return null;

      return new LineCurve(Position, Direction, StartParam, c_lc.EndParam);
   }

   @Override
   public double length()
   {
      return EndParam - StartParam;
   }

   @Override
   public XY computeNormal(double v)
   {
      return Direction.rot270();
   }

   @Override
   public int hashCode()
   {
      return super.hashCode_inner() * 17 + Position.hashCode() * 31 ^ Direction.hashCode();
   }

   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof LineCurve))
         return false;

      if (!super.equals_inner(o))
         return false;

      LineCurve lc_o = (LineCurve)o;

      return Position.equals(lc_o.Position) && Direction.equals(lc_o.Direction);
   }

   public final XY Position;
   public final XY Direction;
}
