package engine.brep;

import engine.Box;
import engine.XY;
import org.junit.Test;

import static org.junit.Assert.*;

public class LineCurveTest
{
   @Test
   public void testCtor() throws Exception
   {
      {
         boolean thrown = false;

         try
         {
            new LineCurve(null, new XY(1, 0), 1);
         }
         catch (NullPointerException e)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         boolean thrown = false;

         try
         {
            new LineCurve(new XY(), null, 1);
         }
         catch (NullPointerException e)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         boolean thrown = false;

         try
         {
            new LineCurve(new XY(), new XY(1, 1), 1);
         }
         catch (IllegalArgumentException e)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }
   }

   @Test
   public void testFindParamForPoint() throws Exception
   {
      LineCurve lc = new LineCurve(new XY(), new XY(1, 0), 5);

      assertEquals(0, lc.findParamForPoint(new XY(), 1e-6), 1e-6);
      assertEquals(1, lc.findParamForPoint(new XY(1, 0), 1e-6), 1e-6);
      assertNull(lc.findParamForPoint(new XY(-0.1, 0), 1e-6));
      assertNull(lc.findParamForPoint(new XY(5.1, 0), 1e-6));
      assertNull(lc.findParamForPoint(new XY(1, 1), 1e-6));
   }

   @Test
   public void testCloneWithChangedParams() throws Exception
   {
      LineCurve lc = new LineCurve(new XY(), new XY(1, 0), 5);

      LineCurve lc2 = (LineCurve)lc.cloneWithChangedParams(2, 4);

      assertNotNull(lc2);
      assertEquals(2, lc2.StartParam, 0);
      assertEquals(4, lc2.EndParam, 0);
      assertTrue(new XY().equals(lc2.Position));
      assertTrue(new XY(1, 0).equals(lc2.Direction));
   }

   @Test
   public void testBoundingBox() throws Exception
   {
      LineCurve lc = new LineCurve(new XY(-1, -2), new XY(1, 0), 5);
      LineCurve lc2 = new LineCurve(new XY(), new XY(1 / Math.sqrt(2), 1 / Math.sqrt(2)), 5);
      LineCurve lc3 = new LineCurve(new XY(10, 11), new XY(0, 1), 5);

      assertTrue(new Box(new XY(-1, -2), new XY(4, -2)).equals(lc.boundingBox()));
      assertTrue(new Box(new XY(), new XY(5 / Math.sqrt(2), 5 / Math.sqrt(2))).equals(lc2.boundingBox()));
      assertTrue(new Box(new XY(10, 11), new XY(10, 16)).equals(lc3.boundingBox()));
   }

   @Test
   public void testTangent() throws Exception
   {
      LineCurve lc = new LineCurve(new XY(), new XY(1, 0), 5);
      LineCurve lc2 = new LineCurve(new XY(-1, -2), new XY(1 / Math.sqrt(2), 1 / Math.sqrt(2)), 5);

      assertTrue(new XY(1, 0).equals(lc.tangent(0.0)));
      assertTrue(new XY(1, 0).equals(lc.tangent(1.0)));

      assertTrue(new XY(1 / Math.sqrt(2), 1 / Math.sqrt(2)).equals(lc2.tangent(0.0)));
      assertTrue(new XY(1 / Math.sqrt(2), 1 / Math.sqrt(2)).equals(lc2.tangent(1.0)));
   }

   @Test
   public void testMerge() throws Exception
   {
      LineCurve lc = new LineCurve(new XY(), new XY(1, 0), 5);

      {
         Curve c = new LineCurve(new XY(0, 0), new XY(1, 0), 5, 10);
         LineCurve lc3 = (LineCurve)lc.merge(c);

         assertNotNull(lc3);
         assertTrue(new XY().equals(lc3.Position));
         assertTrue(new XY(1, 0).equals(lc3.Direction));
         assertEquals(10, lc3.length(), 0);
         assertEquals(0, lc3.StartParam, 0);
         assertEquals(10, lc3.EndParam, 0);
      }

      // not with self
      {
         LineCurve lc3 = (LineCurve)lc.merge(lc);

         assertNull(lc3);
      }

      // not with different curve type
      {
         Curve c = new CircleCurve(new XY(0, 0), 5, Math.PI / 2, 3 * Math.PI / 2);
         LineCurve lc3 = (LineCurve)lc.merge(c);

         assertNull(lc3);
      }

      // not if position doesn't match
      // (we could here, detect if a different position nonetheless lies on the same line
      //  but so far we never need that case as we're only ever re-merging things we just split...)
      {
         Curve c = new LineCurve(new XY(0, 1), new XY(1, 0), 5, 10);
         LineCurve lc3 = (LineCurve)lc.merge(c);

         assertNull(lc3);
      }

      // not if end and start params don't coincide
      {
         Curve c = new LineCurve(new XY(0, 0), new XY(1, 0), 6, 10);
         LineCurve lc3 = (LineCurve)lc.merge(c);

         assertNull(lc3);
      }

      // not if different direction
      {
         Curve c = new LineCurve(new XY(0, 0), new XY(0, 1), 5, 10);
         LineCurve lc3 = (LineCurve) lc.merge(c);

         assertNull(lc3);
      }
   }

   @Test
   public void testLength() throws Exception
   {
      LineCurve lc = new LineCurve(new XY(), new XY(1, 0), 5);
      LineCurve lc2 = new LineCurve(new XY(), new XY(1, 0), 6);
      LineCurve lc3 = new LineCurve(new XY(), new XY(1, 0), 5, 10);

      assertEquals(5, lc.length(), 0);
      assertEquals(6, lc2.length(), 0);
      assertEquals(5, lc3.length(), 0);
   }

   @Test
   public void testComputeNormal() throws Exception
   {
      LineCurve lc = new LineCurve(new XY(), new XY(1, 0), 5);
      LineCurve lc2 = new LineCurve(new XY(-1, -2), new XY(1 / Math.sqrt(2), 1 / Math.sqrt(2)), 5);

      assertTrue(new XY(0, 1).equals(lc.computeNormal(0.0)));
      assertTrue(new XY(0, 1).equals(lc.computeNormal(1.0)));

      assertTrue(new XY(-1 / Math.sqrt(2), 1 / Math.sqrt(2)).equals(lc2.computeNormal(0.0)));
      assertTrue(new XY(-1 / Math.sqrt(2), 1 / Math.sqrt(2)).equals(lc2.computeNormal(1.0)));
   }

   @Test
   public void testHashCode() throws Exception
   {
      LineCurve lc = new LineCurve(new XY(), new XY(1, 0), 5);
      LineCurve lcb = new LineCurve(new XY(), new XY(1, 0), 5);
      LineCurve lc2 = new LineCurve(new XY(1, 0), new XY(1, 0), 5);
      LineCurve lc3 = new LineCurve(new XY(), new XY(0, 1), 5);
      LineCurve lc4 = new LineCurve(new XY(), new XY(1, 0), 6);
      LineCurve lc5 = new LineCurve(new XY(), new XY(1, 0), 1, 5);

      assertEquals(lc.hashCode(), lcb.hashCode());
      assertNotEquals(lc.hashCode(), lc2.hashCode());
      assertNotEquals(lc.hashCode(), lc3.hashCode());
      assertNotEquals(lc.hashCode(), lc4.hashCode());
      assertNotEquals(lc.hashCode(), lc5.hashCode());
   }

   @Test
   public void testEquals() throws Exception
   {
      LineCurve lc = new LineCurve(new XY(), new XY(1, 0), 5);
      LineCurve lcb = new LineCurve(new XY(), new XY(1, 0), 5);
      LineCurve lc2 = new LineCurve(new XY(1, 0), new XY(1, 0), 5);
      LineCurve lc3 = new LineCurve(new XY(), new XY(0, 1), 5);
      LineCurve lc4 = new LineCurve(new XY(), new XY(1, 0), 6);
      LineCurve lc5 = new LineCurve(new XY(), new XY(1, 0), 1, 5);

      //noinspection EqualsWithItself
      assertTrue(lc.equals(lc));
      //noinspection EqualsBetweenInconvertibleTypes
      assertFalse(lc.equals(1));

      assertTrue(lc.equals(lcb));
      assertFalse(lc.equals(lc2));
      assertFalse(lc.equals(lc3));
      assertFalse(lc.equals(lc4));
      assertFalse(lc.equals(lc5));
   }
}
