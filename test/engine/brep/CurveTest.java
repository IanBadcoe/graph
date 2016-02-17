package engine.brep;

import engine.Box;
import engine.XY;
import org.junit.Test;

import static org.junit.Assert.*;

public class CurveTest
{
   static class TestCurve extends Curve
   {
      TestCurve(double start_param, double end_param)
      {
         super(start_param, end_param);
      }

      @Override
      protected XY computePosInner(double param)
      {
         return null;
      }

      @Override
      public Double findParamForPoint(XY pnt, @SuppressWarnings("SameParameterValue") double tol)
      {
         return null;
      }

      @Override
      public Curve cloneWithChangedParams(double start, double end)
      {
         return null;
      }

      @Override
      public Box boundingBox()
      {
         return null;
      }

      @Override
      public XY tangent(Double param)
      {
         return null;
      }

      @Override
      public Curve merge(Curve c_after)
      {
         return null;
      }

      @Override
      public double length()
      {
         return 0;
      }

      @Override
      public XY computeNormal(double p)
      {
         return null;
      }

      @Override
      public int hashCode()
      {
         return 0;
      }

      @Override
      public boolean equals(Object o)
      {
         return false;
      }
   }

   @Test
   public void testCtor()
   {
      boolean thrown = false;

      try
      {
         new TestCurve(0, -1);
      }
      catch (UnsupportedOperationException e)
      {
         thrown = true;
      }

      assertTrue(thrown);
   }
}
