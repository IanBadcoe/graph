import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by badcoei on 23/10/2015.
 */
public class OrderedPairTest
{

   @Test
   public void testHashCode() throws Exception
   {
      Integer one = new Integer(1);
      Integer two = new Integer(2);

      OrderedPair<Integer, Integer> op1 = new OrderedPair<>(one, two);
      OrderedPair<Integer, Integer> op2 = new OrderedPair<>(one, two);
      OrderedPair<Integer, Integer> op3 = new OrderedPair<>(two, one);

      assertEquals(op1.hashCode(), op2.hashCode());
      assertNotEquals(op1.hashCode(), op3.hashCode());
   }

   @Test
   public void testEquals() throws Exception
   {
      Integer one = new Integer(1);
      Integer two = new Integer(2);
      Integer one_a = new Integer(1);
      Integer two_a = new Integer(2);

      assertEquals(one, one_a);

      OrderedPair<Integer, Integer> op1 = new OrderedPair<>(one, two);
      OrderedPair<Integer, Integer> op2 = new OrderedPair<>(one_a, two_a);
      OrderedPair<Integer, Integer> op3 = new OrderedPair<>(two, one);

      assertEquals(op1, op2);
      assertNotEquals(op1, op3);

      assertFalse(op1.equals(new Integer(1)));
   }
}