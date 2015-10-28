import org.junit.Test;

import static org.junit.Assert.*;

public class XYTest
{
   @Test
   public void testEquals() throws Exception
   {
      XY xy1 = new XY(0, 0);
      XY xy2 = new XY(0, 0);
      XY xy3 = new XY(1, 0);
      XY xy4 = new XY(0, 1);
      XY xy5 = new XY(1, 1);

      assertTrue(xy1.equals(xy2));
      assertFalse(xy1.equals(xy3));
      assertFalse(xy1.equals(xy4));
      assertFalse(xy1.equals(xy5));

      assertFalse(xy1.equals("frog"));
   }

   @Test
   public void testHashCode() throws Exception
   {
      XY xy1 = new XY(0, 0);
      XY xy2 = new XY(0, 0);
      XY xy3 = new XY(1, 0);
      XY xy4 = new XY(0, 1);
      XY xy5 = new XY(1, 1);

      assertEquals(xy1.hashCode(), xy2.hashCode());
      assertNotEquals(xy1.hashCode(), xy3.hashCode());
      assertNotEquals(xy1.hashCode(), xy4.hashCode());
      assertNotEquals(xy1.hashCode(), xy5.hashCode());
   }

   @Test
   public void testPlus() throws Exception
   {
      XY xy1 = new XY(0, 0);
      XY xy2 = new XY(1, 0);
      XY xy3 = new XY(0, 1);
      XY xy4 = new XY(1, 1);

      assertEquals(xy1.Plus(xy1), xy1);
      assertEquals(xy1.Plus(xy2), xy2);
      assertEquals(xy1.Plus(xy3), xy3);
      assertEquals(xy1.Plus(xy4), xy4);

      assertEquals(xy2.Plus(xy3), xy4);
      assertEquals(xy2.Plus(xy4), new XY(2, 1));
   }

   @Test
   public void testMinus() throws Exception
   {
      XY xy1 = new XY(0, 0);
      XY xy2 = new XY(1, 0);
      XY xy3 = new XY(0, 1);
      XY xy4 = new XY(1, 1);

      assertEquals(xy1, xy1.Minus(xy1));
      assertEquals(new XY(-1, 0) , xy1.Minus(xy2));
      assertEquals(new XY(0, -1) , xy1.Minus(xy3));
      assertEquals(new XY(-1, -1), xy1.Minus(xy4));

      assertEquals(new XY(1, -1) , xy2.Minus(xy3));
      assertEquals(new XY(0, -1) , xy2.Minus(xy4));
   }

   @Test
   public void testNegate() throws Exception
   {
      XY xy1 = new XY(-7, -6);
      XY xy2 = new XY(7, 6);
      XY xy3 = new XY(23, 11);
      XY xy4 = new XY(-23, -11);

      assertEquals(xy1, xy2.Negate());
      assertEquals(xy2, xy1.Negate());
      assertEquals(xy3, xy4.Negate());
      assertEquals(xy4, xy3.Negate());
   }

   @Test
   public void testDivide() throws Exception
   {
      XY xy1 = new XY(12, 24);

      assertEquals(new XY(1, 2), xy1.Divide(12));
      assertEquals(new XY(2, 4), xy1.Divide(6));
      assertEquals(new XY(3, 6), xy1.Divide(4));
      assertEquals(new XY(4, 8), xy1.Divide(3));
      assertEquals(new XY(6, 12), xy1.Divide(2));
      assertEquals(new XY(12, 24), xy1.Divide(1));
   }

   @Test
   public void testMultiply() throws Exception
   {
      XY xy1 = new XY(1, 2);

      assertEquals(new XY(1, 2), xy1.Multiply(1));
      assertEquals(new XY(2, 4), xy1.Multiply(2));
      assertEquals(new XY(3, 6), xy1.Multiply(3));
      assertEquals(new XY(4, 8), xy1.Multiply(4));
      assertEquals(new XY(6, 12), xy1.Multiply(6));
      assertEquals(new XY(12, 24), xy1.Multiply(12));

   }

   @Test
   public void testLength() throws Exception
   {
      XY xy1 = new XY(1, 2);
      XY xy2 = new XY(-1, 3);
      XY xy3 = new XY(4, -1);
      XY xy4 = new XY(-5, -1);

      assertEquals(Math.sqrt(5), xy1.Length(), 1e-6);
      assertEquals(Math.sqrt(10), xy2.Length(), 1e-6);
      assertEquals(Math.sqrt(17), xy3.Length(), 1e-6);
      assertEquals(Math.sqrt(26), xy4.Length(), 1e-6);
   }

   @Test
   public void testMin() throws Exception
   {
      XY xy1 = new XY(1, 2);
      XY xy2 = new XY(-1, 3);
      XY xy3 = new XY(4, -1);
      XY xy4 = new XY(-5, -1);

      assertEquals(new XY(-1, 2), xy1.Min(xy2));
      assertEquals(new XY(1, -1), xy1.Min(xy3));
      assertEquals(new XY(-5, -1), xy1.Min(xy4));
   }

   @Test
   public void testMax() throws Exception
   {
      XY xy1 = new XY(1, 2);
      XY xy2 = new XY(-1, 3);
      XY xy3 = new XY(4, -1);
      XY xy4 = new XY(-5, -1);

      assertEquals(new XY(1, 3), xy1.Max(xy2));
      assertEquals(new XY(4, 2), xy1.Max(xy3));
      assertEquals(new XY(1, 2), xy1.Max(xy4));
   }

   @Test
   public void testIsZero() throws Exception
   {
      XY xy1 = new XY(0, 0);
      XY xy2 = new XY(-1, 3);
      XY xy3 = new XY(4, -1);
      XY xy4 = new XY(-5, -1);

      assertTrue(xy1.IsZero());
      assertFalse(xy2.IsZero());
      assertFalse(xy3.IsZero());
      assertFalse(xy4.IsZero());
   }

   @Test
   public void testDot() throws Exception
   {
      XY xy1 = new XY(0, 1);
      XY xy2 = new XY(1, 0);
      XY xy3 = new XY(0, 0.5);
      XY xy4 = new XY(-0.5, 0);

      assertEquals(0, xy1.Dot(xy2), 1e-6);
      assertEquals(1, xy1.Dot(xy1), 1e-6);
      assertEquals(0.5, xy1.Dot(xy3), 1e-6);
      assertEquals(0, xy1.Dot(xy4), 1e-6);

      assertEquals(1, xy2.Dot(xy2), 1e-6);
      assertEquals(0, xy2.Dot(xy1), 1e-6);
      assertEquals(0, xy2.Dot(xy3), 1e-6);
      assertEquals(-0.5, xy2.Dot(xy4), 1e-6);
   }
}