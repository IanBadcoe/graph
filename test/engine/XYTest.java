package engine;

import engine.XY;
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

      //noinspection EqualsBetweenInconvertibleTypes
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

      assertEquals(xy1.plus(xy1), xy1);
      assertEquals(xy1.plus(xy2), xy2);
      assertEquals(xy1.plus(xy3), xy3);
      assertEquals(xy1.plus(xy4), xy4);

      assertEquals(xy2.plus(xy3), xy4);
      assertEquals(xy2.plus(xy4), new XY(2, 1));
   }

   @Test
   public void testMinus() throws Exception
   {
      XY xy1 = new XY(0, 0);
      XY xy2 = new XY(1, 0);
      XY xy3 = new XY(0, 1);
      XY xy4 = new XY(1, 1);

      assertEquals(xy1, xy1.minus(xy1));
      assertEquals(new XY(-1, 0) , xy1.minus(xy2));
      assertEquals(new XY(0, -1) , xy1.minus(xy3));
      assertEquals(new XY(-1, -1), xy1.minus(xy4));

      assertEquals(new XY(1, -1) , xy2.minus(xy3));
      assertEquals(new XY(0, -1) , xy2.minus(xy4));
   }

   @Test
   public void testNegate() throws Exception
   {
      XY xy1 = new XY(-7, -6);
      XY xy2 = new XY(7, 6);
      XY xy3 = new XY(23, 11);
      XY xy4 = new XY(-23, -11);

      assertEquals(xy1, xy2.negate());
      assertEquals(xy2, xy1.negate());
      assertEquals(xy3, xy4.negate());
      assertEquals(xy4, xy3.negate());
   }

   @Test
   public void testDivide() throws Exception
   {
      XY xy1 = new XY(12, 24);

      assertEquals(new XY(1, 2), xy1.divide(12));
      assertEquals(new XY(2, 4), xy1.divide(6));
      assertEquals(new XY(3, 6), xy1.divide(4));
      assertEquals(new XY(4, 8), xy1.divide(3));
      assertEquals(new XY(6, 12), xy1.divide(2));
      assertEquals(new XY(12, 24), xy1.divide(1));
   }

   @Test
   public void testMultiply() throws Exception
   {
      XY xy1 = new XY(1, 2);

      assertEquals(new XY(1, 2), xy1.multiply(1));
      assertEquals(new XY(2, 4), xy1.multiply(2));
      assertEquals(new XY(3, 6), xy1.multiply(3));
      assertEquals(new XY(4, 8), xy1.multiply(4));
      assertEquals(new XY(6, 12), xy1.multiply(6));
      assertEquals(new XY(12, 24), xy1.multiply(12));

   }

   @Test
   public void testLength() throws Exception
   {
      XY xy1 = new XY(1, 2);
      XY xy2 = new XY(-1, 3);
      XY xy3 = new XY(4, -1);
      XY xy4 = new XY(-5, -1);

      assertEquals(Math.sqrt(5), xy1.length(), 1e-6);
      assertEquals(Math.sqrt(10), xy2.length(), 1e-6);
      assertEquals(Math.sqrt(17), xy3.length(), 1e-6);
      assertEquals(Math.sqrt(26), xy4.length(), 1e-6);
   }

   @Test
   public void testMin() throws Exception
   {
      XY xy1 = new XY(1, 2);
      XY xy2 = new XY(-1, 3);
      XY xy3 = new XY(4, -1);
      XY xy4 = new XY(-5, -1);

      assertEquals(new XY(-1, 2), xy1.min(xy2));
      assertEquals(new XY(1, -1), xy1.min(xy3));
      assertEquals(new XY(-5, -1), xy1.min(xy4));
   }

   @Test
   public void testMax() throws Exception
   {
      XY xy1 = new XY(1, 2);
      XY xy2 = new XY(-1, 3);
      XY xy3 = new XY(4, -1);
      XY xy4 = new XY(-5, -1);

      assertEquals(new XY(1, 3), xy1.max(xy2));
      assertEquals(new XY(4, 2), xy1.max(xy3));
      assertEquals(new XY(1, 2), xy1.max(xy4));
   }

   @Test
   public void testIsZero() throws Exception
   {
      XY xy1 = new XY(0, 0);
      XY xy2 = new XY(-1, 3);
      XY xy3 = new XY(4, -1);
      XY xy4 = new XY(-5, -1);

      assertTrue(xy1.isZero());
      assertFalse(xy2.isZero());
      assertFalse(xy3.isZero());
      assertFalse(xy4.isZero());
   }

   @Test
   public void testDot() throws Exception
   {
      XY xy1 = new XY(0, 1);
      XY xy2 = new XY(1, 0);
      XY xy3 = new XY(0, 0.5);
      XY xy4 = new XY(-0.5, 0);

      assertEquals(0, xy1.dot(xy2), 1e-6);
      assertEquals(1, xy1.dot(xy1), 1e-6);
      assertEquals(0.5, xy1.dot(xy3), 1e-6);
      assertEquals(0, xy1.dot(xy4), 1e-6);

      assertEquals(1, xy2.dot(xy2), 1e-6);
      assertEquals(0, xy2.dot(xy1), 1e-6);
      assertEquals(0, xy2.dot(xy3), 1e-6);
      assertEquals(-0.5, xy2.dot(xy4), 1e-6);
   }
}
