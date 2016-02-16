package engine;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class LoopSetTest
{
   @Test
   public void testCtor() throws Exception
   {
      {
         @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") LoopSet ls = new LoopSet();
         assertEquals(0, ls.size());
      }

      {
         Loop l = new Loop(new CircleCurve(new XY(), 1));
         @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") LoopSet ls = new LoopSet(l);
         assertEquals(1, ls.size());
         assertEquals(l, ls.get(0));
      }
   }

   @Test
   public void testHashCode() throws Exception
   {
      @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") LoopSet ls1 = new LoopSet();
      @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") LoopSet ls1b = new LoopSet();
      LoopSet ls2 = new LoopSet();
      LoopSet ls2b = new LoopSet();
      LoopSet ls4 = new LoopSet();

      {
         Loop l = new Loop(new CircleCurve(new XY(), 1));
         ls2.add(l);
         ls2b.add(l);

         ls4.add(l);
         ls4.add(l);
      }

      LoopSet ls3;

      {
         Curve c1 = new CircleCurve(new XY(), 1, 0, Math.PI);
         Curve c2 = new LineCurve(new XY(0, -1), new XY(-1, 0), Math.PI);
         Curve c3 = new CircleCurve(new XY(-Math.PI, 0), 1, Math.PI, 2 * Math.PI);
         Curve c4 = new LineCurve(new XY(-Math.PI, 1), new XY(1, 0), Math.PI);

         ArrayList<Curve> list = new ArrayList<>();

         list.add(c1);
         list.add(c2);
         list.add(c3);
         list.add(c4);

         Loop l = new Loop(list);

         ls3 = new LoopSet(l);
      }

      assertEquals(ls1.hashCode(), ls1b.hashCode());
      assertEquals(ls2.hashCode(), ls2b.hashCode());

      assertNotEquals(ls1.hashCode(), ls2.hashCode());
      assertNotEquals(ls1.hashCode(), ls3.hashCode());
      assertNotEquals(ls1.hashCode(), ls4.hashCode());

      assertNotEquals(ls2.hashCode(), ls3.hashCode());
      assertNotEquals(ls2.hashCode(), ls4.hashCode());
   }

   @Test
   public void testEquals() throws Exception
   {
      LoopSet ls1 = new LoopSet();
      LoopSet ls1b = new LoopSet();
      LoopSet ls2 = new LoopSet();
      LoopSet ls2b = new LoopSet();
      LoopSet ls4 = new LoopSet();

      {
         Loop l = new Loop(new CircleCurve(new XY(), 1));
         ls2.add(l);
         ls2b.add(l);

         ls4.add(l);
         ls4.add(l);
      }

      LoopSet ls3;

      {
         Curve c1 = new CircleCurve(new XY(), 1, 0, Math.PI);
         Curve c2 = new LineCurve(new XY(0, -1), new XY(-1, 0), Math.PI);
         Curve c3 = new CircleCurve(new XY(-Math.PI, 0), 1, Math.PI, 2 * Math.PI);
         Curve c4 = new LineCurve(new XY(-Math.PI, 1), new XY(1, 0), Math.PI);

         ArrayList<Curve> list = new ArrayList<>();

         list.add(c1);
         list.add(c2);
         list.add(c3);
         list.add(c4);

         Loop l = new Loop(list);

         ls3 = new LoopSet(l);
      }

      //noinspection EqualsWithItself
      assertTrue(ls1.equals(ls1));
      //noinspection EqualsBetweenInconvertibleTypes
      assertFalse(ls1.equals(1));
      assertTrue(ls1.equals(ls1b));
      assertTrue(ls2.equals(ls2b));

      assertFalse(ls1.equals(ls2));
      assertFalse(ls1.equals(ls3));
      assertFalse(ls1.equals(ls4));
      assertFalse(ls2.equals(ls3));
      assertFalse(ls2.equals(ls4));
   }
}