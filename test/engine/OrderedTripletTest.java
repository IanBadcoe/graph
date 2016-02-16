package engine;

import org.junit.Test;

import static org.junit.Assert.*;

public class OrderedTripletTest
{
   @Test
   public void testCtor()
   {
      OrderedTriplet<Integer,Integer,Integer> ot = new OrderedTriplet<>(1, 2, 3);

      assertEquals(1, (int)ot.First);
      assertEquals(2, (int)ot.Second);
      assertEquals(3, (int)ot.Third);
   }

   @Test
   public void testHashCode() throws Exception
   {
      OrderedTriplet<Integer,Integer,Integer> ot1 = new OrderedTriplet<>(1, 2, 3);
      OrderedTriplet<Integer,Integer,Integer> ot1b = new OrderedTriplet<>(1, 2, 3);
      OrderedTriplet<Integer,Integer,Integer> ot2 = new OrderedTriplet<>(4, 2, 3);
      OrderedTriplet<Integer,Integer,Integer> ot3 = new OrderedTriplet<>(1, 4, 3);
      OrderedTriplet<Integer,Integer,Integer> ot4 = new OrderedTriplet<>(1, 2, 4);
      OrderedTriplet<Integer,Integer,Integer> ot5 = new OrderedTriplet<>(2, 1, 3);

      assertEquals(ot1.hashCode(), ot1b.hashCode());
      assertNotEquals(ot1.hashCode(), ot2.hashCode());
      assertNotEquals(ot1.hashCode(), ot3.hashCode());
      assertNotEquals(ot1.hashCode(), ot4.hashCode());
      assertNotEquals(ot1.hashCode(), ot5.hashCode());
   }

   @Test
   public void testEquals() throws Exception
   {
      OrderedTriplet<Integer,Integer,Integer> ot1 = new OrderedTriplet<>(1, 2, 3);
      OrderedTriplet<Integer,Integer,Integer> ot1b = new OrderedTriplet<>(1, 2, 3);
      OrderedTriplet<Integer,Integer,Integer> ot2 = new OrderedTriplet<>(4, 2, 3);
      OrderedTriplet<Integer,Integer,Integer> ot3 = new OrderedTriplet<>(1, 4, 3);
      OrderedTriplet<Integer,Integer,Integer> ot4 = new OrderedTriplet<>(1, 2, 4);
      OrderedTriplet<Integer,Integer,Integer> ot5 = new OrderedTriplet<>(2, 1, 3);

      //noinspection EqualsWithItself
      assertTrue(ot1.equals(ot1));
      //noinspection EqualsBetweenInconvertibleTypes
      assertFalse(ot1.equals(1));
      assertTrue(ot1.equals(ot1b));
      assertFalse(ot1.equals(ot2));
      assertFalse(ot1.equals(ot3));
      assertFalse(ot1.equals(ot4));
      assertFalse(ot1.equals(ot5));
   }
}