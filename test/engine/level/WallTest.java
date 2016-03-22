package engine.level;

import engine.XY;
import org.junit.Test;

import static org.junit.Assert.*;

public class WallTest
{
   @Test
   public void testCtor() throws Exception
   {
      XY start = new XY();
      XY end = new XY(1, 1);
      XY normal = new XY(1, 0);

      Wall w = new Wall(start, end, normal);

      assertTrue(start.equals(w.Start));
      assertTrue(end.equals(w.End));
      assertTrue(normal.equals(w.Normal));
   }

   @Test
   public void testNext() throws Exception
   {
      Wall w1 = new Wall(null, null, null);
      Wall w2 = new Wall(null, null, null);
      Wall w3 = new Wall(null, null, null);

      assertNull(w1.getNext());
      w1.setNext(w2);
      assertEquals(w2, w1.getNext());
      w1.setNext(w3);
      assertEquals(w3, w1.getNext());
   }

   @Test
   public void testPrev() throws Exception
   {
      Wall w1 = new Wall(null, null, null);
      Wall w2 = new Wall(null, null, null);
      Wall w3 = new Wall(null, null, null);

      assertNull(w1.getPrev());
      w1.setPrev(w2);
      assertEquals(w2, w1.getPrev());
      w1.setPrev(w3);
      assertEquals(w3, w1.getPrev());
   }

   @Test
   public void testMidPoint() throws Exception
   {
      XY start = new XY();
      XY end = new XY(1, 1);
      XY normal = new XY(1, 0);

      Wall w = new Wall(start, end, normal);

      assertTrue(new XY(0.5, 0.5).equals(w.midPoint()));
   }
}
