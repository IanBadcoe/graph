package engine.brep;

import engine.Box;
import engine.OrderedPair;
import engine.XY;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class BRepUtilTest
{
   class Fake extends Curve
   {
      Fake()
      {
         super(0, 1);
      }

      @Override
      public XY computePosInner(double m_start_param)
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

      @Override
      public Double findParamForPoint(XY first, double tol)
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
      public XY tangent(Double second)
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
      public double paramCoordinateDist(double p1, double p2)
      {
         return 0;
      }
   }

   @Test
   public void testCurveCurveIntersect_Exception()
   {
      CircleCurve cc = new CircleCurve(new XY(), 10);
      LineCurve lc = new LineCurve(new XY(), new XY(1, 0), 10);

      Fake f = new Fake();

      {
         boolean thrown = false;

         try
         {
            BRepUtil.curveCurveIntersect(cc, f);
         }
         catch(UnsupportedOperationException uoe)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         boolean thrown = false;

         try
         {
            BRepUtil.curveCurveIntersect(lc, f);
         }
         catch(UnsupportedOperationException uoe)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         boolean thrown = false;

         try
         {
            BRepUtil.curveCurveIntersect(f, cc);
         }
         catch(UnsupportedOperationException uoe)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         boolean thrown = false;

         try
         {
            BRepUtil.curveCurveIntersect(f, lc);
         }
         catch(UnsupportedOperationException uoe)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }
   }

   @Test
   public void testCurveCurveIntersect_Circles()
   {
      // two circles separated by their common radius intersect at +/- 60 degrees
      CircleCurve cc1 = new CircleCurve(new XY(), 1);
      CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1);

      ArrayList<OrderedPair<Double, Double>> ret =
            BRepUtil.curveCurveIntersect(cc1, cc2);

      assertNotNull(ret);

      assertEquals(2, ret.size());

      checkParamsUnknownOrder("testCurveCurveIntersect_Circles : 1",
            cc1,
            ret.get(0).First, ret.get(1).First,
            Math.PI * 2 * 5 / 12, Math.PI * 2 * 1 / 12);

      checkParamsUnknownOrder("testCurveCurveIntersect_Circles : 2",
            cc2,
            ret.get(0).Second, ret.get(1).Second,
            Math.PI * 2 * 11 / 12, Math.PI * 2 * 7 / 12);
   }

   @Test
   public void testCircleCircleIntersect_None()
   {
      // same object
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);

         assertNull(BRepUtil.curveCurveIntersect(cc1, cc1));
      }

      // same circle
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(), 1);

         assertNull(BRepUtil.curveCurveIntersect(cc1, cc2));
      }

      // concentric
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(), 2);

         assertNull(BRepUtil.curveCurveIntersect(cc1, cc2));
         assertNull(BRepUtil.curveCurveIntersect(cc2, cc1));
      }

      // non-concentric, still inside
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(0, 0.5), 2);

         assertNull(BRepUtil.curveCurveIntersect(cc1, cc2));
         assertNull(BRepUtil.curveCurveIntersect(cc2, cc1));
      }

      // non-concentric, still inside
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(0, -0.5), 2);

         assertNull(BRepUtil.curveCurveIntersect(cc1, cc2));
         assertNull(BRepUtil.curveCurveIntersect(cc2, cc1));
      }

      // non-concentric, still inside
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(0.5, 0), 2);

         assertNull(BRepUtil.curveCurveIntersect(cc1, cc2));
         assertNull(BRepUtil.curveCurveIntersect(cc2, cc1));
      }

      // non-concentric, still inside
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(-0.5, 0), 2);

         assertNull(BRepUtil.curveCurveIntersect(cc1, cc2));
         assertNull(BRepUtil.curveCurveIntersect(cc2, cc1));
      }

      // outside
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(5, 0), 2);

         assertNull(BRepUtil.curveCurveIntersect(cc1, cc2));
         assertNull(BRepUtil.curveCurveIntersect(cc2, cc1));
      }

      // outside
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(-50, 0), 2);

         assertNull(BRepUtil.curveCurveIntersect(cc1, cc2));
         assertNull(BRepUtil.curveCurveIntersect(cc2, cc1));
      }

      // outside
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(0, 500), 2);

         assertNull(BRepUtil.curveCurveIntersect(cc1, cc2));
         assertNull(BRepUtil.curveCurveIntersect(cc2, cc1));
      }

      // outside
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(0, -5000), 2);

         assertNull(BRepUtil.curveCurveIntersect(cc1, cc2));
         assertNull(BRepUtil.curveCurveIntersect(cc2, cc1));
      }
   }

   @Test
   public void testCircleCircleIntersect_One()
   {
      // one point of contact
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(0, 3), 2);

         ArrayList<OrderedPair<Double, Double>> ret =
               BRepUtil.curveCurveIntersect(cc1, cc2);

         //noinspection ConstantConditions
         assertEquals(1, ret.size());
         assertEquals(0, ret.get(0).First, 0);
         assertEquals(Math.PI, ret.get(0).Second, 0);
      }

      // one point of contact
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(0, -4), 3);

         ArrayList<OrderedPair<Double, Double>> ret =
               BRepUtil.curveCurveIntersect(cc1, cc2);

         //noinspection ConstantConditions
         assertEquals(1, ret.size());
         assertEquals(Math.PI, ret.get(0).First, 0);
         assertEquals(0, ret.get(0).Second, 0);
      }

      // one point of contact
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(1.01, 0), 0.01);

         ArrayList<OrderedPair<Double, Double>> ret =
               BRepUtil.curveCurveIntersect(cc1, cc2);

         //noinspection ConstantConditions
         assertEquals(1, ret.size());
         assertEquals(Math.PI / 2, ret.get(0).First, 0);
         assertEquals(Math.PI * 3 / 2, ret.get(0).Second, 0);
      }

      // one point of contact
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(-3001, 0), 3000);

         ArrayList<OrderedPair<Double, Double>> ret =
               BRepUtil.curveCurveIntersect(cc1, cc2);

         //noinspection ConstantConditions
         assertEquals(1, ret.size());
         assertEquals(Math.PI * 3 / 2, ret.get(0).First, 0);
         assertEquals(Math.PI / 2, ret.get(0).Second, 0);
      }
   }

   @Test
   public void testCircleCircleIntersect_One_SelectedReverse()
   {
      // one point of contact
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, CircleCurve.RotationDirection.Reverse);
         CircleCurve cc2 = new CircleCurve(new XY(0, 3), 2);

         ArrayList<OrderedPair<Double, Double>> ret =
               BRepUtil.curveCurveIntersect(cc1, cc2);

         //noinspection ConstantConditions
         assertEquals(1, ret.size());
         assertEquals(0, ret.get(0).First, 0);
         assertEquals(Math.PI, ret.get(0).Second, 0);
      }

      // one point of contact
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(0, 3), 2, CircleCurve.RotationDirection.Reverse);

         ArrayList<OrderedPair<Double, Double>> ret =
               BRepUtil.curveCurveIntersect(cc1, cc2);

         //noinspection ConstantConditions
         assertEquals(1, ret.size());
         assertEquals(0, ret.get(0).First, 0);
         assertEquals(Math.PI, ret.get(0).Second, 0);
      }

      // one point of contact
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, CircleCurve.RotationDirection.Reverse);
         CircleCurve cc2 = new CircleCurve(new XY(0, 3), 2, CircleCurve.RotationDirection.Reverse);

         ArrayList<OrderedPair<Double, Double>> ret =
               BRepUtil.curveCurveIntersect(cc1, cc2);

         //noinspection ConstantConditions
         assertEquals(1, ret.size());
         assertEquals(0, ret.get(0).First, 0);
         assertEquals(Math.PI, ret.get(0).Second, 0);
      }
   }

   @Test
   public void testCircleCircleIntersect_Two()
   {
      cci_two(CircleCurve.RotationDirection.Forwards, CircleCurve.RotationDirection.Forwards);
      cci_two(CircleCurve.RotationDirection.Forwards, CircleCurve.RotationDirection.Reverse);
      cci_two(CircleCurve.RotationDirection.Reverse, CircleCurve.RotationDirection.Forwards);
      cci_two(CircleCurve.RotationDirection.Reverse, CircleCurve.RotationDirection.Reverse);
   }

   private void cci_two(CircleCurve.RotationDirection r1, CircleCurve.RotationDirection r2)
   {
      for(double ang = 0.0; ang < Math.PI * 2; ang += 0.01)
      {
         // two circles separated by their common radius intersect at +/- 60 degrees
         CircleCurve cc1 = new CircleCurve(new XY(), 1, r1);
         CircleCurve cc2 = new CircleCurve(new XY(Math.sin(ang), Math.cos(ang)), 1, r2);

         ArrayList<OrderedPair<Double, Double>> ret =
               BRepUtil.curveCurveIntersect(cc1, cc2);

         assertNotNull(ret);

         assertEquals(2, ret.size());

         double ang1 = r1 == CircleCurve.RotationDirection.Forwards ? ang : -ang;
         double ang2 = r2 == CircleCurve.RotationDirection.Forwards ? ang : -ang;

         double exp_ang = fixAngle(ang1 - Math.PI / 3);
         double other_exp_ang = fixAngle(ang1 + Math.PI / 3);

         checkParamsUnknownOrder("ang: " + ang,
               cc1,
               ret.get(0).First, ret.get(1).First,
               exp_ang, other_exp_ang);

         exp_ang = fixAngle(ang2 - Math.PI * 4 / 3);
         other_exp_ang = fixAngle(ang2 + Math.PI * 4 / 3);

         checkParamsUnknownOrder("ang: " + ang,
               cc2,
               ret.get(0).Second, ret.get(1).Second,
               exp_ang, other_exp_ang);
      }
   }

   @Test
   public void testCircleCircleIntersect_RespectParams()
   {
      // full circle on left, 3/4 on right with upper-left missing
      // should hit once at 150 degrees, 270 degrees resp.
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI * 2);
         CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1, 0, Math.PI * 3 / 2);

         ArrayList<OrderedPair<Double, Double>> ret =
               BRepUtil.curveCurveIntersect(cc1, cc2);

         //noinspection ConstantConditions
         assertEquals(1, ret.size());
         assertEquals(Math.PI * 2 * 5 / 12, ret.get(0).First, 0);
         assertEquals(Math.PI * 2 * 7 / 12, ret.get(0).Second, 0);
      }

      // full circle on left, 3/4 on right with lower-left missing
      // should hit once at 30 degrees, 330 degrees resp.
      {

         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI * 2);
         CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1, Math.PI * 3 / 2, Math.PI);

         ArrayList<OrderedPair<Double, Double>> ret =
               BRepUtil.curveCurveIntersect(cc1, cc2);

         //noinspection ConstantConditions
         assertEquals(1, ret.size());
         assertEquals(Math.PI * 2 * 1 / 12, ret.get(0).First, 1e-6);
         assertEquals(Math.PI * 2 * 11 / 12, ret.get(0).Second, 1e-6);
      }

      // full circle on right, 3/4 on left with upper-right missing
      // should hit once at 150 degrees, 270 degrees resp.
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, Math.PI / 2, Math.PI * 2);
         CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1, 0, Math.PI * 2);

         ArrayList<OrderedPair<Double, Double>> ret =
               BRepUtil.curveCurveIntersect(cc1, cc2);

         //noinspection ConstantConditions
         assertEquals(1, ret.size());
         assertEquals(Math.PI * 2 * 5 / 12, ret.get(0).First, 0);
         assertEquals(Math.PI * 2 * 7 / 12, ret.get(0).Second, 0);
      }

      // full circle on right, 3/4 on left with lower-right missing
      // should hit once at 30 degrees, 330 degrees resp.
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, Math.PI, Math.PI / 2);
         CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1, 0, Math.PI * 2);

         ArrayList<OrderedPair<Double, Double>> ret =
               BRepUtil.curveCurveIntersect(cc1, cc2);

         //noinspection ConstantConditions
         assertEquals(1, ret.size());
         assertEquals(Math.PI * 2 * 13 / 12, ret.get(0).First, 1e-6);
         assertEquals(Math.PI * 2 * 11 / 12, ret.get(0).Second, 1e-6);
      }

      // full circle on right, 3/4 on left with lower-left missing
      // should hit twice at both points mentioned above
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, Math.PI * 3 / 2, Math.PI);
         CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1, 0, Math.PI * 2);

         ArrayList<OrderedPair<Double, Double>> ret =
               BRepUtil.curveCurveIntersect(cc1, cc2);

         //noinspection ConstantConditions
         assertEquals(2, ret.size());

         checkParamsUnknownOrder("RespectParams 1.1",
               cc1,
               ret.get(0).First, ret.get(1).First,
               Math.PI * 2 * 13 / 12, Math.PI * 2 * 17 / 12);

         checkParamsUnknownOrder("RespectParams 1.2",
               cc2,
               ret.get(0).Second, ret.get(1).Second,
               Math.PI * 2 * 7 / 12, Math.PI * 2 * 11 / 12);
      }

      // full circle on left, 3/4 on right with lower-right missing
      // should hit twice at both points mentioned above
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI * 2);
         CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1, Math.PI, Math.PI / 2);

         ArrayList<OrderedPair<Double, Double>> ret =
               BRepUtil.curveCurveIntersect(cc1, cc2);

         //noinspection ConstantConditions
         assertEquals(2, ret.size());

         checkParamsUnknownOrder("RespectParams 2.1",
               cc1,
               ret.get(0).First, ret.get(1).First,
               Math.PI * 2 * 1 / 12, Math.PI * 2 * 5 / 12);

         checkParamsUnknownOrder("RespectParams 2.2",
               cc2,
               ret.get(0).Second, ret.get(1).Second,
               Math.PI * 2 * 7 / 12, Math.PI * 2 * 11 / 12);
      }

      // 3/4 circle on left with upper right missing,
      // 3/4 on right with lower-left missing
      // should miss
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, Math.PI / 2, Math.PI * 2);
         CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1, Math.PI * 3 / 2, Math.PI);

         ArrayList<OrderedPair<Double, Double>> ret =
               BRepUtil.curveCurveIntersect(cc1, cc2);

         assertNull(ret);
      }
   }

   @Test
   public void testCircleCircleIntersect_RespectParams_SelectedReverse()
   {
      // full circle on right, 3/4 on left with upper-right missing
      // should hit once at 150 degrees, 270 degrees resp.
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI * 3 / 2, CircleCurve.RotationDirection.Reverse);
         CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1, 0, Math.PI * 2, CircleCurve.RotationDirection.Reverse);

         ArrayList<OrderedPair<Double, Double>> ret =
               BRepUtil.curveCurveIntersect(cc1, cc2);

         //noinspection ConstantConditions
         assertEquals(1, ret.size());
         assertEquals(Math.PI * 2 * 7 / 12, ret.get(0).First, 0);
         assertEquals(Math.PI * 2 * 5 / 12, ret.get(0).Second, 0);
      }

      // 3/4 circle on left with upper right missing,
      // 3/4 on right with lower-left missing
      // should miss
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI * 3 / 2,
               CircleCurve.RotationDirection.Reverse);
         CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1, Math.PI, Math.PI / 2,
               CircleCurve.RotationDirection.Reverse);

         ArrayList<OrderedPair<Double, Double>> ret =
               BRepUtil.curveCurveIntersect(cc1, cc2);

         assertNull(ret);
      }

      // full circle on left, 3/4 on right with lower-right missing
      // should hit twice at both points mentioned above
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI * 2,
               CircleCurve.RotationDirection.Reverse);
         CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1, Math.PI * 3 / 2, Math.PI,
               CircleCurve.RotationDirection.Reverse);

         ArrayList<OrderedPair<Double, Double>> ret =
               BRepUtil.curveCurveIntersect(cc1, cc2);

         //noinspection ConstantConditions
         assertEquals(2, ret.size());

         checkParamsUnknownOrder("RespectParams 2.1",
               cc1,
               ret.get(0).First, ret.get(1).First,
               Math.PI * 2 * 11 / 12, Math.PI * 2 * 7 / 12);

         checkParamsUnknownOrder("RespectParams 2.2",
               cc2,
               ret.get(0).Second, ret.get(1).Second,
               Math.PI * 2 * 17 / 12, Math.PI * 2 * 13 / 12);
      }
   }

   @Test
   public void testLineLineIntersect()
   {
      // only need to sanity check as is using same internal routine as edge intersection
      // which is tested thoroughly above

      LineCurve lc1 = new LineCurve(new XY(), new XY(1, 0), 4);
      LineCurve lc2 = new LineCurve(new XY(2, -2), new XY(0, 1), 4);
      LineCurve lc3a = new LineCurve(new XY(0, -2), new XY(1, 0), 1, 3);
      LineCurve lc3b = new LineCurve(new XY(-1, -2), new XY(1, 0), 1, 3);
      LineCurve lc3c = new LineCurve(new XY(-2, -2), new XY(1, 0), 1, 3);

      // crossing
      {
         ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(lc1, lc2);

         assertNotNull(ret);
         assertEquals(1, ret.size());
         assertEquals(2, ret.get(0).First, 1e-6);
         assertEquals(2, ret.get(0).Second, 1e-6);
      }

      // parallel
      {
         ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(lc1, lc3a);

         assertNull(ret);
      }

      // crossing at one end
      {
         ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(lc3a, lc2);

         assertNotNull(ret);
         assertEquals(1, ret.size());
         assertEquals(2, ret.get(0).First, 1e-6);
         assertEquals(0, ret.get(0).Second, 1e-6);
      }

      // crossing end to end
      {
         ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(lc3b, lc2);

         assertNotNull(ret);
         assertEquals(1, ret.size());
         assertEquals(3, ret.get(0).First, 1e-6);
         assertEquals(0, ret.get(0).Second, 1e-6);
      }

      // off the end
      {
         ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(lc3c, lc2);

         assertNull(ret);
      }
   }

   @Test
   public void testLineCircleIntersect_None()
   {
      // line outside in various orientations
      {
         CircleCurve cc = new CircleCurve(new XY(), 1);
         LineCurve lc1 = new LineCurve(new XY(3, 0), new XY(1, 0), 1);
         LineCurve lc2 = new LineCurve(new XY(3, 0), new XY(-1, 0), 1);
         LineCurve lc3 = new LineCurve(new XY(3, 0), new XY(0, 1), 1);
         LineCurve lc4 = new LineCurve(new XY(3, 0), new XY(0, -1), 1);

         assertNull(BRepUtil.curveCurveIntersect(cc, lc1));
         assertNull(BRepUtil.curveCurveIntersect(cc, lc2));
         assertNull(BRepUtil.curveCurveIntersect(cc, lc3));
         assertNull(BRepUtil.curveCurveIntersect(cc, lc4));

         assertNull(BRepUtil.curveCurveIntersect(lc1, cc));
         assertNull(BRepUtil.curveCurveIntersect(lc2, cc));
         assertNull(BRepUtil.curveCurveIntersect(lc3, cc));
         assertNull(BRepUtil.curveCurveIntersect(lc4, cc));
      }

      // line inside in various orientations
      {
         CircleCurve cc = new CircleCurve(new XY(), 2);
         LineCurve lc1 = new LineCurve(new XY(0, 0), new XY(1, 0), 1);
         LineCurve lc2 = new LineCurve(new XY(0, 0), new XY(-1, 0), 1);
         LineCurve lc3 = new LineCurve(new XY(0, 0), new XY(0, 1), 1);
         LineCurve lc4 = new LineCurve(new XY(0, 0), new XY(0, -1), 1);

         assertNull(BRepUtil.curveCurveIntersect(cc, lc1));
         assertNull(BRepUtil.curveCurveIntersect(cc, lc2));
         assertNull(BRepUtil.curveCurveIntersect(cc, lc3));
         assertNull(BRepUtil.curveCurveIntersect(cc, lc4));

         assertNull(BRepUtil.curveCurveIntersect(lc1, cc));
         assertNull(BRepUtil.curveCurveIntersect(lc2, cc));
         assertNull(BRepUtil.curveCurveIntersect(lc3, cc));
         assertNull(BRepUtil.curveCurveIntersect(lc4, cc));
      }
   }

   @Test
   public void testLineCircleIntersect_One()
   {
      // line inside penetrating outwards
      {
         CircleCurve cc = new CircleCurve(new XY(), 2);
         LineCurve lc1 = new LineCurve(new XY(0, 0), new XY(1, 0), 4);
         LineCurve lc2 = new LineCurve(new XY(0, 0), new XY(-1, 0), 40);
         LineCurve lc3 = new LineCurve(new XY(0, 0), new XY(0, 1), 400);
         LineCurve lc4 = new LineCurve(new XY(0, 0), new XY(0, -1), 4000);

         {
            ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(cc, lc1);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(Math.PI / 2, ret.get(0).First, 1e-6);
            assertEquals(2, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(lc1, cc);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(2, ret.get(0).First, 1e-6);
            assertEquals(Math.PI / 2, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(cc, lc2);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(Math.PI * 3 / 2, ret.get(0).First, 1e-6);
            assertEquals(2, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(lc2, cc);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(2, ret.get(0).First, 1e-6);
            assertEquals(Math.PI * 3 / 2, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(cc, lc3);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(0, ret.get(0).First, 1e-6);
            assertEquals(2, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(lc3, cc);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(2, ret.get(0).First, 1e-6);
            assertEquals(0, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(cc, lc4);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(Math.PI, ret.get(0).First, 1e-6);
            assertEquals(2, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(lc4, cc);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(2, ret.get(0).First, 1e-6);
            assertEquals(Math.PI, ret.get(0).Second, 1e-6);
         }
      }

      // line outside penetrating outwards
      {
         CircleCurve cc = new CircleCurve(new XY(), 2);
         LineCurve lc1 = new LineCurve(new XY(100, 0), new XY(-1, 0), 100);
         LineCurve lc2 = new LineCurve(new XY(-10, 0), new XY(1, 0), 10);
         LineCurve lc3 = new LineCurve(new XY(0, 10000), new XY(0, -1), 10000);
         LineCurve lc4 = new LineCurve(new XY(0, -1000), new XY(0, 1), 1000);

         {
            ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(cc, lc1);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(Math.PI / 2, ret.get(0).First, 1e-6);
            assertEquals(98, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(lc1, cc);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(98, ret.get(0).First, 1e-6);
            assertEquals(Math.PI / 2, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(cc, lc2);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(Math.PI * 3 / 2, ret.get(0).First, 1e-6);
            assertEquals(8, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(lc2, cc);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(8, ret.get(0).First, 1e-6);
            assertEquals(Math.PI * 3 / 2, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(cc, lc3);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(0, ret.get(0).First, 1e-6);
            assertEquals(9998, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(lc3, cc);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(9998, ret.get(0).First, 1e-6);
            assertEquals(0, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(cc, lc4);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(Math.PI, ret.get(0).First, 1e-6);
            assertEquals(998, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(lc4, cc);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(998, ret.get(0).First, 1e-6);
            assertEquals(Math.PI, ret.get(0).Second, 1e-6);
         }
      }
   }

   @Test
   public void testLineCircleIntersect_Two()
   {
      CircleCurve cc = new CircleCurve(new XY(), 1);

      for(double d = -2; d < 2; d += 0.01)
      {
         {
            LineCurve lc = new LineCurve(new XY(d, -2), new XY(0, 1), 4);

            ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(lc, cc);

            if (ret == null)
            {
               assertTrue(Math.abs(d) > 1 - 1e-6);
            }
            else if (ret.size() == 1)
            {
               assertEquals(1, d, 1e-6);
               assertEquals(0, lc.computePos(ret.get(0).First).X, 1e-6);
               assertEquals(0, cc.computePos(ret.get(0).Second).X, 1e-6);
            }
            else
            {
               assertTrue(d < 1);
               XY exp_where1 = new XY(d, Math.sqrt(1 - d * d));
               XY exp_where2 = new XY(d, -Math.sqrt(1 - d * d));

               XY obs_where1 = lc.computePos(ret.get(0).First);
               XY obs_where2 = lc.computePos(ret.get(1).First);


               assertTrue((exp_where1.equals(obs_where1, 1e-6) && exp_where2.equals(obs_where2, 1e-6))
                     || (exp_where1.equals(obs_where2, 1e-6) && exp_where2.equals(obs_where1, 1e-6)));
            }
         }

         {
            LineCurve lc = new LineCurve(new XY(-2, d), new XY(1, 0), 4);

            ArrayList<OrderedPair<Double, Double>> ret = BRepUtil.curveCurveIntersect(cc, lc);

            if (ret == null)
            {
               assertTrue(Math.abs(d) > 1 - 1e-6);
            }
            else if (ret.size() == 1)
            {
               assertEquals(1, d, 1e-6);
               assertEquals(0, lc.computePos(ret.get(0).First).Y, 1e-6);
               assertEquals(0, cc.computePos(ret.get(0).Second).Y, 1e-6);
            }
            else
            {
               assertTrue(d < 1);
               XY exp_where1 = new XY(Math.sqrt(1 - d * d), d);
               XY exp_where2 = new XY(-Math.sqrt(1 - d * d), d);

               XY obs_where1 = lc.computePos(ret.get(0).Second);
               XY obs_where2 = lc.computePos(ret.get(1).Second);


               assertTrue((exp_where1.equals(obs_where1, 1e-6) && exp_where2.equals(obs_where2, 1e-6))
                     || (exp_where1.equals(obs_where2, 1e-6) && exp_where2.equals(obs_where1, 1e-6)));
            }
         }
      }
   }

   private static void checkParamsUnknownOrder(String msg,
                                               Curve c,
                                               double pa, double pb,
                                               double expa, double expb)
   {
      double higher_expected = Math.max(expa, expb);
      double lower_expected = Math.min(expa, expb);

      double higher_seen = Math.max(pa, pb);
      double lower_seen = Math.min(pa, pb);
      assertEquals(msg, higher_expected, higher_seen, 1e-6);
      assertEquals(msg, lower_expected, lower_seen, 1e-6);

      assertTrue(c.withinParams(pa, 1e-6));
      assertTrue(c.withinParams(pb, 1e-6));
   }

   private static double fixAngle(double w)
   {
      while(w < 0)
         w += Math.PI * 2;

      while(w > Math.PI * 2)
         w -= Math.PI * 2;

      return w;
   }
}
