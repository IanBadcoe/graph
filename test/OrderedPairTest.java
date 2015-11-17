import org.junit.Test;

import static org.junit.Assert.*;

public class OrderedPairTest
{

   @Test
   public void testHashCode() throws Exception
   {
      Integer one = 1;
      Integer two = 2;

      OrderedPair<Integer, Integer> op1 = new OrderedPair<>(one, two);
      OrderedPair<Integer, Integer> op2 = new OrderedPair<>(one, two);
      OrderedPair<Integer, Integer> op3 = new OrderedPair<>(two, one);

      assertEquals(op1.hashCode(), op2.hashCode());
      assertNotEquals(op1.hashCode(), op3.hashCode());
   }

   @Test
   public void testEquals() throws Exception
   {
      Integer one = 1;
      Integer two = 2;
      Integer one_a = 1;
      Integer two_a = 2;

      assertEquals(one, one_a);

      OrderedPair<Integer, Integer> op1 = new OrderedPair<>(one, two);
      OrderedPair<Integer, Integer> op2 = new OrderedPair<>(one_a, two_a);
      OrderedPair<Integer, Integer> op3 = new OrderedPair<>(two, one);

      assertEquals(op1, op2);
      assertNotEquals(op1, op3);

      //noinspection EqualsBetweenInconvertibleTypes
      assertFalse(op1.equals(1));
   }
}