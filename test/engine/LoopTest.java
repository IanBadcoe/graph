package engine;

import engine.*;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class LoopTest
{
   @Test
   public void testCtors()
   {
      // single fullcircle works
      {
         Curve c0 = new CircleCurve(new XY(), 1);

         new Loop(c0);
      }

      // part cicle fails (ends don't meet)
      {
         Curve c0 = new CircleCurve(new XY(), 1, 0, Math.PI);

         boolean thrown = false;

         try
         {
            new Loop(c0);
         }
         catch(IllegalArgumentException iae)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      // several parts that form a loop work
      {
         Curve c1 = new CircleCurve(new XY(), 1, 0, Math.PI);
         Curve c2 = new LineCurve(new XY(0, -1), new XY(-1, 0), 2);
         Curve c3 = new CircleCurve(new XY(-2, 0), 1, Math.PI, 2 * Math.PI);
         Curve c4 = new LineCurve(new XY(-2, 1), new XY(1, 0), 2);

         ArrayList<Curve> list = new ArrayList<>();
         list.add(c1);
         list.add(c2);
         list.add(c3);
         list.add(c4);

         new Loop(list);
      }

      // several parts that don't form a loop throw
      {
         Curve c1 = new CircleCurve(new XY(), 1, 0, Math.PI);
         Curve c2 = new LineCurve(new XY(0, -1), new XY(-1, 0), 2);
         Curve c3 = new CircleCurve(new XY(-2, 0), 1, Math.PI, 2 * Math.PI);

         ArrayList<Curve> list = new ArrayList<>();
         list.add(c1);
         list.add(c2);
         list.add(c3);

         boolean thrown = false;

         try
         {
            new Loop(list);
         }
         catch(IllegalArgumentException iae)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }
   }

   @Test
   public void testParams()
   {
      // param range of loop is sum of ranges of curves
      {
         Curve c1 = new CircleCurve(new XY(), 1, 0, Math.PI);
         Curve c2 = new LineCurve(new XY(0, -1), new XY(-1, 0), 2);
         Curve c3 = new CircleCurve(new XY(-2, 0), 1, Math.PI, 2 * Math.PI);
         Curve c4 = new LineCurve(new XY(-2, 1), new XY(1, 0), 2);

         ArrayList<Curve> list = new ArrayList<>();

         list.add(c1);
         list.add(c2);
         list.add(c3);
         list.add(c4);

         Loop l = new Loop(list);

         assertEquals(Math.PI * 2 + 4,
               l.paramRange(), 1e-12);
      }

      // worth trying again with c3 as first curve
      // as that means the first param of the first curve is non-zero, which the
      // loop adjusts for because its param always runs from zero
      {
         Curve c1 = new CircleCurve(new XY(), 1, 0, Math.PI);
         Curve c2 = new LineCurve(new XY(0, -1), new XY(-1, 0), 2);
         Curve c3 = new CircleCurve(new XY(-2, 0), 1, Math.PI, 2 * Math.PI);
         Curve c4 = new LineCurve(new XY(-2, 1), new XY(1, 0), 2);

         ArrayList<Curve> list = new ArrayList<>();

         list.add(c3);
         list.add(c4);
         list.add(c1);
         list.add(c2);

         Loop l = new Loop(list);

         assertEquals(Math.PI * 2 + 4,
               l.paramRange(), 1e-12);
      }
   }

   @Test
   public void calculateTest()
   {
      Curve c1 = new CircleCurve(new XY(), 1, 0, Math.PI);
      Curve c2 = new LineCurve(new XY(0, -1), new XY(-1, 0), 2);
      Curve c3 = new CircleCurve(new XY(-2, 0), 1, Math.PI, 2 * Math.PI);
      Curve c4 = new LineCurve(new XY(-2, 1), new XY(1, 0), 2);

      ArrayList<Curve> list = new ArrayList<>();

      list.add(c1);
      list.add(c2);
      list.add(c3);
      list.add(c4);

      Loop l = new Loop(list);

      XY c1_start = new XY(0, 1);
      XY c1_mid = new XY(1, 0);
      XY c2_start = new XY(0, -1);
      XY c2_mid = new XY(-1, -1);
      XY c3_start = new XY(-2, -1);
      XY c3_mid = new XY(-3, 0);
      XY c4_start = new XY(-2, 1);
      XY c4_mid = new XY(-1, 1);

      assertTrue(c1_start.equals(l.computePos(0), 1e-6));
      assertTrue(c1_mid.equals(l.computePos(Math.PI / 2), 1e-6));
      assertTrue(c2_start.equals(l.computePos(Math.PI), 1e-6));
      assertTrue(c2_mid.equals(l.computePos(Math.PI + 1), 1e-6));
      assertTrue(c3_start.equals(l.computePos(Math.PI + 2), 1e-6));
      assertTrue(c3_mid.equals(l.computePos(Math.PI * 3 / 2 + 2), 1e-6));
      assertTrue(c4_start.equals(l.computePos(Math.PI * 2 + 2), 1e-6));
      assertTrue(c4_mid.equals(l.computePos(Math.PI * 2 + 3), 1e-6));
      assertTrue(c1_start.equals(l.computePos(Math.PI * 2 + 4), 1e-6));

      assertNull(l.computePos(100));
      assertNull(l.computePos(-1));
   }
}
