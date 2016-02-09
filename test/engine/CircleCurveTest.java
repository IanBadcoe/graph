package engine;

import org.junit.Test;

import static org.junit.Assert.*;

public class CircleCurveTest
{
   @Test
   public void testCtor()
   {
      {
         boolean thrown = false;

         try
         {
            new CircleCurve(null, 1);
         }
         catch(NullPointerException e)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         boolean thrown = false;

         try
         {
            new CircleCurve(new XY(), -1);
         }
         catch(IllegalArgumentException e)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         CircleCurve cc = new CircleCurve(new XY(), 1, 0, Math.PI * 3);

         // automatically converted down to equivalent angle with only one turn
         assertEquals(Math.PI, cc.EndParam, 1e-6);
      }

      {
         CircleCurve cc = new CircleCurve(new XY(), 1, Math.PI * 3, Math.PI * 4);

         // automatically converted down to equivalent angle with only one turn
         assertEquals(Math.PI, cc.StartParam, 1e-6);
         assertEquals(2 * Math.PI, cc.EndParam, 1e-6);
      }
   }

   @Test
   public void testComputePos() throws Exception
   {
      CircleCurve cc = new CircleCurve(new XY(), 1);

      {
         XY p = cc.computePos(0);

         assertEquals(0, p.X, 1e-6);
         assertEquals(1, p.Y, 1e-6);
      }

      {
         XY p = cc.computePos(Math.PI / 2);

         assertEquals(1, p.X, 1e-6);
         assertEquals(0, p.Y, 1e-6);
      }
   }

   @Test
   public void testHashCode() throws Exception
   {
      CircleCurve cc1 = new CircleCurve(new XY(), 1);
      CircleCurve cc1b = new CircleCurve(new XY(), 1);
      CircleCurve cc2 = new CircleCurve(new XY(), 2);
      CircleCurve cc3 = new CircleCurve(new XY(1, 0), 1);

      assertNotEquals(cc1.hashCode(), cc2.hashCode());
      assertNotEquals(cc1.hashCode(), cc3.hashCode());
      assertNotEquals(cc2.hashCode(), cc3.hashCode());

      assertEquals(cc1b.hashCode(), cc1.hashCode());
   }

   @Test
   public void testEquals() throws Exception
   {
      CircleCurve cc1 = new CircleCurve(new XY(), 1);
      CircleCurve cc1b = new CircleCurve(new XY(), 1);
      CircleCurve cc2 = new CircleCurve(new XY(), 2);
      CircleCurve cc3 = new CircleCurve(new XY(1, 0), 1);

      assertTrue(cc1.equals(cc1b));
      assertFalse(cc1.equals(cc2));
      assertFalse(cc1.equals(cc3));
      assertFalse(cc2.equals(cc3));
      //noinspection EqualsBetweenInconvertibleTypes
      assertFalse(cc1.equals(1));
   }

   @Test
   public void testFindParamForPoint() throws Exception
   {
      CircleCurve cc = new CircleCurve(new XY(), 1);
      CircleCurve ccr = new CircleCurve(new XY(), 1, CircleCurve.RotationDirection.Reverse);

      {
         double p = cc.findParamForPoint(new XY(0, 1), 1e-6);

         assertEquals(0, p, 1e-6);
      }

      {
         double p = cc.findParamForPoint(new XY(1, 0), 1e-6);

         assertEquals(Math.PI / 2, p, 1e-6);
      }

      {
         double p = ccr.findParamForPoint(new XY(0, 1), 1e-6);

         assertEquals(0, p, 1e-6);
      }

      {
         double p = ccr.findParamForPoint(new XY(1, 0), 1e-6);

         assertEquals(3 * Math.PI / 2, p, 1e-6);
      }

      {
         assertNull(cc.findParamForPoint(new XY(2, 0), 1e-6));
      }
   }

   @Test
   public void testCloneWithChangedParams() throws Exception
   {
      {
         CircleCurve cc = new CircleCurve(new XY(5, 6), 7);

         CircleCurve ccb = (CircleCurve) cc.cloneWithChangedParams(Math.PI / 2, 3 * Math.PI / 2);
         assertEquals(5, ccb.Position.X, 1e-6);
         assertEquals(6, ccb.Position.Y, 1e-6);
         assertEquals(7, ccb.Radius, 1e-6);
         assertEquals(CircleCurve.RotationDirection.Forwards, ccb.Rotation);
         assertEquals(Math.PI / 2, ccb.StartParam, 1e-6);
         assertEquals(3 * Math.PI / 2, ccb.EndParam, 1e-6);
      }

      {
         CircleCurve cc = new CircleCurve(new XY(5, 6), 7, CircleCurve.RotationDirection.Reverse);

         CircleCurve ccb = (CircleCurve) cc.cloneWithChangedParams(Math.PI / 2, 3 * Math.PI / 2);
         assertEquals(5, ccb.Position.X, 1e-6);
         assertEquals(6, ccb.Position.Y, 1e-6);
         assertEquals(7, ccb.Radius, 1e-6);
         assertEquals(CircleCurve.RotationDirection.Reverse, ccb.Rotation);
         assertEquals(Math.PI / 2, ccb.StartParam, 1e-6);
         assertEquals(3 * Math.PI / 2, ccb.EndParam, 1e-6);
      }
   }

   @Test
   public void testBoundingBox() throws Exception
   {
      // for the moment we do not account for partial circles, so don't test that...
      CircleCurve cc = new CircleCurve(new XY(5, 6), 7);

      Box b = cc.boundingBox();

      assertTrue(new Box(new XY(-2, -1), new XY(12, 13)).equals(b));
   }

   @Test
   public void testTangent() throws Exception
   {
      CircleCurve cc = new CircleCurve(new XY(), 1);

      assertTrue(new XY(1, 0).equals(cc.tangent(0.0), 1e-6));
      assertTrue(new XY(0, -1).equals(cc.tangent(Math.PI / 2), 1e-6));
      assertTrue(new XY(-1, 0).equals(cc.tangent(Math.PI), 1e-6));
      assertTrue(new XY(0, 1).equals(cc.tangent(3 * Math.PI / 2), 1e-6));
   }

   @Test
   public void testMerge() throws Exception
   {
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI);
         CircleCurve cc2 = new CircleCurve(new XY(), 1, Math.PI, 2 * Math.PI);

         CircleCurve cm = (CircleCurve)cc1.merge(cc2);

         assertNotNull(cm);
         assertTrue(new XY().equals(cm.Position));
         assertEquals(1, cm.Radius, 0);
         assertEquals(CircleCurve.RotationDirection.Forwards, cm.Rotation);
         assertEquals(0, cm.StartParam, 0);
         assertEquals(2 * Math.PI, cm.EndParam, 0);
      }

      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI, CircleCurve.RotationDirection.Reverse);
         CircleCurve cc2 = new CircleCurve(new XY(), 1, Math.PI, 2 * Math.PI, CircleCurve.RotationDirection.Reverse);

         CircleCurve cm = (CircleCurve)cc1.merge(cc2);

         assertNotNull(cm);
         assertTrue(new XY().equals(cm.Position));
         assertEquals(1, cm.Radius, 0);
         assertEquals(CircleCurve.RotationDirection.Reverse, cm.Rotation);
         assertEquals(0, cm.StartParam, 0);
         assertEquals(2 * Math.PI, cm.EndParam, 0);
      }

      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI, CircleCurve.RotationDirection.Forwards);
         CircleCurve cc2 = new CircleCurve(new XY(), 1, Math.PI, 2 * Math.PI, CircleCurve.RotationDirection.Reverse);

         CircleCurve cm = (CircleCurve) cc1.merge(cc2);

         assertNull(cm);
      }

      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI);
         CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1, Math.PI, 2 * Math.PI);

         CircleCurve cm = (CircleCurve) cc1.merge(cc2);

         assertNull(cm);
      }

      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI);
         CircleCurve cc2 = new CircleCurve(new XY(), 2, Math.PI, 2 * Math.PI);

         CircleCurve cm = (CircleCurve) cc1.merge(cc2);

         assertNull(cm);
      }

      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI);
         CircleCurve cc2 = new CircleCurve(new XY(), 1, 3 * Math.PI / 2, 2 * Math.PI);

         CircleCurve cm = (CircleCurve) cc1.merge(cc2);

         assertNull(cm);
      }

      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI);

         CircleCurve cm = (CircleCurve) cc1.merge(cc1);

         assertNull(cm);
      }

      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI);
         LineCurve cc2 = new LineCurve(new XY(), new XY(1, 0), 10);

         CircleCurve cm = (CircleCurve) cc1.merge(cc2);

         assertNull(cm);
      }
   }

   @Test
   public void testLength() throws Exception
   {
      CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI);
      CircleCurve cc2 = new CircleCurve(new XY(), 3, 0, 2 * Math.PI);

      assertEquals(Math.PI, cc1.length(), 1e-6);
      assertEquals(2 * Math.PI * 3, cc2.length(), 1e-6);
   }

   @Test
   public void testComputeNormal() throws Exception
   {
      CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI);

      assert(new XY(0, 1).equals(cc1.computeNormal(0), 1e-6));
      assert(new XY(1, 0).equals(cc1.computeNormal(Math.PI / 2), 1e-6));
      assert(new XY(0, -1).equals(cc1.computeNormal(Math.PI), 1e-6));
      assert(new XY(-1, 0).equals(cc1.computeNormal(3 * Math.PI / 2), 1e-6));
   }

   @Test
   public void testWithinParams() throws Exception
   {
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI / 2);
         assertTrue(cc1.withinParams(0, 1e-6));
         assertTrue(cc1.withinParams(Math.PI / 2, 1e-6));
         assertFalse(cc1.withinParams(Math.PI, 1e-6));
         assertFalse(cc1.withinParams(-1, 1e-6));
      }

      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, 2 * Math.PI);
         assertTrue(cc1.withinParams(0, 1e-6));
         assertTrue(cc1.withinParams(Math.PI / 2, 1e-6));
         assertTrue(cc1.withinParams(Math.PI, 1e-6));
         assertTrue(cc1.withinParams(-1, 1e-6));
      }

      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 3 * Math.PI / 2, 5 * Math.PI / 2);
         assertTrue(cc1.withinParams(0, 1e-6));
         assertTrue(cc1.withinParams(Math.PI / 2, 1e-6));
         assertFalse(cc1.withinParams(Math.PI, 1e-6));
         assertTrue(cc1.withinParams(-1, 1e-6));
      }

      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI / 2, CircleCurve.RotationDirection.Reverse);
         assertTrue(cc1.withinParams(0, 1e-6));
         assertTrue(cc1.withinParams(Math.PI / 2, 1e-6));
         assertFalse(cc1.withinParams(Math.PI, 1e-6));
         assertFalse(cc1.withinParams(-1, 1e-6));
      }

      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, 2 * Math.PI, CircleCurve.RotationDirection.Reverse);
         assertTrue(cc1.withinParams(0, 1e-6));
         assertTrue(cc1.withinParams(Math.PI / 2, 1e-6));
         assertTrue(cc1.withinParams(Math.PI, 1e-6));
         assertTrue(cc1.withinParams(-1, 1e-6));
      }

      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 3 * Math.PI / 2, 5 * Math.PI / 2,
               CircleCurve.RotationDirection.Reverse);
         assertTrue(cc1.withinParams(0, 1e-6));
         assertTrue(cc1.withinParams(Math.PI / 2, 1e-6));
         assertFalse(cc1.withinParams(Math.PI, 1e-6));
         assertTrue(cc1.withinParams(-1, 1e-6));
      }
   }

   @Test
   public void testIsCyclic() throws Exception
   {
      CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, 2 * Math.PI);
      CircleCurve cc2 = new CircleCurve(new XY(), 1, 0, 2 * Math.PI, CircleCurve.RotationDirection.Reverse);
      CircleCurve cc3 = new CircleCurve(new XY(), 1, Math.PI, 3 * Math.PI);
      CircleCurve cc4 = new CircleCurve(new XY(), 1, 0, 3 * Math.PI / 2);

      assertTrue(cc1.isCyclic());
      assertTrue(cc2.isCyclic());
      assertTrue(cc3.isCyclic());
      assertFalse(cc4.isCyclic());
   }
}
