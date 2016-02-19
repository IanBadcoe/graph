package engine;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

@SuppressWarnings("ConstantConditions")
public class UtilTest
{
   @Test
   public void testEdgeIntersectSimple() throws Exception
   {
      assertNotNull(Util.edgeIntersect(new XY(1, 0), new XY(-1, 0), new XY(0, 1), new XY(0, -1)));
      assertNull(Util.edgeIntersect(new XY(1, 0), new XY(-1, 0), new XY(1, 0), new XY(-1, 0)));

      assertNotNull(Util.edgeIntersect(new XY(-1, 0), new XY(1, 0), new XY(0, -1), new XY(0, 1)));
      assertNull(Util.edgeIntersect(new XY(-1, 0), new XY(1, 0), new XY(-1, 0), new XY(1, 0)));

      assertNotNull(Util.edgeIntersect(new XY(0, 1), new XY(0, -1), new XY(1, 0), new XY(-1, 0)));
      assertNull(Util.edgeIntersect(new XY(0, 1), new XY(0, -1), new XY(0, 1), new XY(0, -1)));

      assertNotNull(Util.edgeIntersect(new XY(0, -1), new XY(0, 1), new XY(-1, 0), new XY(1, 0)));
      assertNull(Util.edgeIntersect(new XY(0, -1), new XY(0, 1), new XY(0, -1), new XY(0, 1)));

      assertNull(Util.edgeIntersect(new XY(0, -1), new XY(0, 1), new XY(-1, 2), new XY(1, 2)));
      assertNull(Util.edgeIntersect(new XY(0, -1), new XY(0, 1), new XY(-1, -2), new XY(1, -2)));

      assertNull(Util.edgeIntersect(new XY(2, -1), new XY(2, 1), new XY(-1, 0), new XY(1, 0)));
      assertNull(Util.edgeIntersect(new XY(-2, -1), new XY(-2, 1), new XY(-1, 0), new XY(1, 0)));
   }

   @Test
   public void testEdgeIntersectT1Scan() throws Exception
   {
      double[] values = {1e-6, 2e-6, 5e-6, 1e-5, 2e-5, 5e-5, 1e-4, 2e-4, 5e-4, 1e-3, 2e-3, 5e-3, 1e-2, 2e-2, 5e-2, 1e-1, 2e-1, 5e-1};

      for (double f : values)
      {
         assertEquals(f, Util.edgeIntersect(new XY(0, 0), new XY(1, 0),
               new XY(f, 0.5), new XY(f, -0.5)).First, 1e-8);
      }
   }

   @Test
   public void testEdgeIntersectT2Scan() throws Exception
   {
      double[] values = {1e-6, 2e-6, 5e-6, 1e-5, 2e-5, 5e-5, 1e-4, 2e-4, 5e-4, 1e-3, 2e-3, 5e-3, 1e-2, 2e-2, 5e-2, 1e-1, 2e-1, 5e-1};

      for (double f : values)
      {
         assertEquals(f, Util.edgeIntersect(new XY(f, 0.5), new XY(f, -0.5),
               new XY(0, 0), new XY(1, 0)).Second, 1e-8);
      }
   }

   @Test
   public void testEdgeIntersectSlantScan() throws Exception
   {
      double[] values = {1e-6, 2e-6, 5e-6, 1e-5, 2e-5, 5e-5, 1e-4, 2e-4, 5e-4, 1e-3, 2e-3, 5e-3, 1e-2, 2e-2, 5e-2, 1e-1, 2e-1, 5e-1, 1, 2, 5};

      for (double f : values)
      {
         // increasing slant on x
         OrderedPair<Double, Double> ret = Util.edgeIntersect(new XY(f, 0), new XY(-f, 1),
               new XY(0, 0), new XY(0, 1));

         assertEquals(0.5,  ret.First, 1e-8);
         assertEquals(0.5,  ret.Second, 1e-8);

         // same on y
         ret = Util.edgeIntersect(new XY(0, f), new XY(1, -f),
               new XY(0, 0), new XY(1, 0));

         assertEquals(0.5,  ret.First, 1e-8);
         assertEquals(0.5,  ret.Second, 1e-8);
      }
   }

   @Test
   public void testEdgeIntersectMagnitudeScan() throws Exception
   {
      double[] values = {-1000000, -100000, -10000, -1000, -100, -10, -1, 0, 1, 10, 100, 1000, 10000, 100000, 1000000};

      for (double f : values)
      {
         for (double g : values)
         {
            OrderedPair<Double, Double> ret = Util.edgeIntersect(new XY(1 + f, 0 + g), new XY(-1 + f, 0 + g),
                  new XY(0 + f, 1 + g), new XY(0 + f, -1 + g));

            assertEquals(0.5, ret.First, 1e-8);
            assertEquals(0.5, ret.Second, 1e-8);
         }
      }
   }

   @Test
   public void testRelativeAngle()
   {
                              // absolute angle:
      XY l1 = new XY(1, 0);   // Pi/2
      XY l2 = new XY(0, 1);   // 0
      XY l3 = new XY(-1, 0);  // 3Pi/2
      XY l4 = new XY(0, -1);  // Pi

      assertEquals(0, Util.relativeAngle(l1, l1), 1e-6);
      assertEquals(0, Util.relativeAngle(l2, l2), 1e-6);
      assertEquals(0, Util.relativeAngle(l3, l3), 1e-6);
      assertEquals(0, Util.relativeAngle(l4, l4), 1e-6);

      assertEquals(Math.PI / 2, Util.relativeAngle(l2, l1), 1e-6);
      assertEquals(Math.PI / 2, Util.relativeAngle(l3, l2), 1e-6);
      assertEquals(Math.PI / 2, Util.relativeAngle(l4, l3), 1e-6);
      assertEquals(Math.PI / 2, Util.relativeAngle(l1, l4), 1e-6);

      assertEquals(Math.PI, Util.relativeAngle(l1, l3), 1e-6);
      assertEquals(Math.PI, Util.relativeAngle(l2, l4), 1e-6);
      assertEquals(Math.PI, Util.relativeAngle(l3, l1), 1e-6);
      assertEquals(Math.PI, Util.relativeAngle(l4, l2), 1e-6);

      assertEquals(Math.PI * 3 / 2, Util.relativeAngle(l1, l2), 1e-6);
      assertEquals(Math.PI * 3 / 2, Util.relativeAngle(l2, l3), 1e-6);
      assertEquals(Math.PI * 3 / 2, Util.relativeAngle(l3, l4), 1e-6);
      assertEquals(Math.PI * 3 / 2, Util.relativeAngle(l4, l1), 1e-6);

      XY l5 = new XY(Math.sin(1), Math.cos(1));

      assertEquals(1, Util.relativeAngle(l2, l5), 1e-6);
      assertEquals(1 + Math.PI / 2, Util.relativeAngle(l3, l5), 1e-6);
      assertEquals(1 + Math.PI, Util.relativeAngle(l4, l5), 1e-6);
      assertEquals(1 + Math.PI * 3 / 2, Util.relativeAngle(l1, l5), 1e-6);

      // any old angle
      XY any = new XY(1.23, 3.21);
      assertEquals(Math.PI / 2, Util.relativeAngle(any, any.rot90()), 1e-6);
      assertEquals(Math.PI, Util.relativeAngle(any, any.rot90().rot90()), 1e-6);
      assertEquals(Math.PI *3 / 2, Util.relativeAngle(any, any.rot270()), 1e-6);
   }
}
