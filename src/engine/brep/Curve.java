package engine.brep;

import engine.Box;
import engine.XY;

public abstract class Curve
{
   public final double StartParam;
   public final double EndParam;

   @SuppressWarnings("WeakerAccess")
   protected Curve(double start_param, double end_param)
   {
      StartParam = start_param;
      EndParam = end_param;

      if (EndParam - StartParam < 1e-12)
         throw new UnsupportedOperationException("StartParam must be < EndParam");
   }

   // exquisite abstractions

   protected abstract XY computePosInner(double param);

   public abstract Double findParamForPoint(XY pnt, @SuppressWarnings("SameParameterValue") double tol);

   public abstract Curve cloneWithChangedParams(double start, double end);

   public abstract Box boundingBox();

   public abstract XY tangent(Double param);

   public abstract Curve merge(Curve c_after);

   public abstract double length();

   public abstract XY computeNormal(double p);

   // overridden for cyclic curves

   @SuppressWarnings("WeakerAccess")
   public boolean withinParams(double p, double tol)
   {
      return p > StartParam - tol
            && p < EndParam + tol;
   }

   // overridden but overrides need to call these base implementations
   public abstract int hashCode();
   int hashCode_inner()
   {
      return Double.hashCode(StartParam) + Double.hashCode(EndParam) * 31;
   }

   public abstract boolean equals(Object o);
   @SuppressWarnings("BooleanMethodIsAlwaysInverted")
   boolean equals_inner(Object o)
   {
      // need caller to have checked these
      assert o instanceof Curve;
      assert o != this;

      Curve co = (Curve)o;

      return co.StartParam == StartParam && co.EndParam == EndParam;
   }

   // concrete methods

   public XY startPos()
   {
      return computePos(StartParam);
   }

   public XY endPos()
   {
      return computePos(EndParam);
   }

   public double paramRange()
   {
      return EndParam - StartParam;
   }

   public double paramCoordinateDist(double p1, double p2)
   {
      return computePos(p1).minus(computePos(p2)).length();
   }

   public XY computePos(double p)
   {
      p = clampToParamRange(p);

      return computePosInner(p);
   }

   private double clampToParamRange(double p)
   {
      return Math.min(Math.max(p, StartParam), EndParam);
   }
}
