package engine;

import org.junit.Test;

import static org.junit.Assert.*;

public class XYZTest
{
   @Test
   public void testCtor()
   {
      {
         XYZ xyz = new XYZ();
         assertEquals(0, xyz.X, 0);
         assertEquals(0, xyz.Y, 0);
         assertEquals(0, xyz.Z, 0);
      }

      {
         XYZ xyz = new XYZ(1, 2, 3);
         assertEquals(0, xyz.X, 1);
         assertEquals(0, xyz.Y, 2);
         assertEquals(0, xyz.Z, 3);
      }

      {
         XYZ xyz = new XYZ(new XY(3, 2), 1);
         assertEquals(0, xyz.X, 3);
         assertEquals(0, xyz.Y, 2);
         assertEquals(0, xyz.Z, 1);
      }
   }

   @Test
   public void testEquals() throws Exception
   {
      // absolute equals
      {
         XYZ xy1 = new XYZ(0, 0, 0);
         XYZ xy2 = new XYZ(0, 0, 0);
         XYZ xy3 = new XYZ(1, 0, 0);
         XYZ xy4 = new XYZ(0, 1, 0);
         XYZ xy5 = new XYZ(1, 1, 0);
         XYZ xy6 = new XYZ(0, 0, 1);

         assertTrue(xy1.equals(xy2));
         assertFalse(xy1.equals(xy3));
         assertFalse(xy1.equals(xy4));
         assertFalse(xy1.equals(xy5));
         assertFalse(xy1.equals(xy6));

         //noinspection EqualsBetweenInconvertibleTypes
         assertFalse(xy1.equals("frog"));
      }

      // equals with tolerance
      {
         XYZ xy1 = new XYZ(0, 0, 0);
         XYZ xy2 = new XYZ(0.1, 0, 0);
         XYZ xy3 = new XYZ(0, 0.1, 0);
         XYZ xy4 = new XYZ(0.1, 0.1, 0);
         XYZ xy5 = new XYZ(0, 0, 0.1);
         XYZ xy6 = new XYZ(0.1, 0, 0.1);
         XYZ xy7 = new XYZ(0, 0.1, 0.1);
         XYZ xy8 = new XYZ(0.1, 0.1, 0.1);

         assertTrue(xy1.equals(xy1, 0));
         assertFalse(xy1.equals(xy2, 0));
         assertFalse(xy1.equals(xy3, 0));
         assertFalse(xy1.equals(xy4, 0));
         assertFalse(xy1.equals(xy5, 0));
         assertFalse(xy1.equals(xy6, 0));
         assertFalse(xy1.equals(xy7, 0));
         assertFalse(xy1.equals(xy8, 0));

         assertTrue(xy1.equals(xy2, 0.1));
         assertTrue(xy1.equals(xy3, 0.1));
         assertFalse(xy1.equals(xy4, 0.1));
         assertTrue(xy1.equals(xy5, 0.1));
         assertFalse(xy1.equals(xy6, 0.1));
         assertFalse(xy1.equals(xy7, 0.1));
         assertFalse(xy1.equals(xy8, 0.1));

         assertTrue(xy1.equals(xy4, 0.15));
         assertTrue(xy1.equals(xy6, 0.15));
         assertTrue(xy1.equals(xy7, 0.15));
         assertFalse(xy1.equals(xy8, 0.15));

         assertTrue(xy1.equals(xy8, 0.2));
      }
   }

   @Test
   public void testHashCode() throws Exception
   {
      XYZ xy1 = new XYZ(0, 0, 0);
      XYZ xy2 = new XYZ(0, 0, 0);
      XYZ xy3 = new XYZ(1, 0, 0);
      XYZ xy4 = new XYZ(0, 1, 0);
      XYZ xy5 = new XYZ(1, 1, 0);
      XYZ xy6 = new XYZ(1, 0, 0);

      assertEquals(xy1.hashCode(), xy2.hashCode());
      assertNotEquals(xy1.hashCode(), xy3.hashCode());
      assertNotEquals(xy1.hashCode(), xy4.hashCode());
      assertNotEquals(xy1.hashCode(), xy5.hashCode());
      assertNotEquals(xy1.hashCode(), xy6.hashCode());
   }

   @Test
   public void testPlus() throws Exception
   {
      XYZ xy1 = new XYZ(0, 0, 0);
      XYZ xy2 = new XYZ(1, 0, 0);
      XYZ xy3 = new XYZ(0, 1, 0);
      XYZ xy4 = new XYZ(1, 1, 0);
      XYZ xy5 = new XYZ(0, 0, 1);

      assertEquals(xy1.plus(xy1), xy1);
      assertEquals(xy1.plus(xy2), xy2);
      assertEquals(xy1.plus(xy3), xy3);
      assertEquals(xy1.plus(xy4), xy4);
      assertEquals(xy1.plus(xy5), xy5);

      assertEquals(xy2.plus(xy3), xy4);
      assertEquals(xy2.plus(xy4), new XYZ(2, 1, 0));
      assertEquals(xy4.plus(xy5), new XYZ(1, 1, 1));
   }

   @Test
   public void testMinus() throws Exception
   {
      XYZ xy1 = new XYZ(0, 0, 0);
      XYZ xy2 = new XYZ(1, 0, 0);
      XYZ xy3 = new XYZ(0, 1, 0);
      XYZ xy4 = new XYZ(1, 1, 0);
      XYZ xy5 = new XYZ(0, 0, 1);

      assertEquals(xy1, xy1.minus(xy1));
      assertEquals(new XYZ(-1, 0, 0) , xy1.minus(xy2));
      assertEquals(new XYZ(0, -1, 0) , xy1.minus(xy3));
      assertEquals(new XYZ(-1, -1, 0), xy1.minus(xy4));
      assertEquals(new XYZ(1, 1, -1), xy4.minus(xy5));

      assertEquals(new XYZ(1, -1, 0) , xy2.minus(xy3));
      assertEquals(new XYZ(0, -1, 0) , xy2.minus(xy4));
   }

   @Test
   public void testNegate() throws Exception
   {
      XYZ xy1 = new XYZ(-7, -6, -5);
      XYZ xy2 = new XYZ(7, 6, 5);
      XYZ xy3 = new XYZ(23, 11, 9);
      XYZ xy4 = new XYZ(-23, -11, -9);

      assertEquals(xy1, xy2.negate());
      assertEquals(xy2, xy1.negate());
      assertEquals(xy3, xy4.negate());
      assertEquals(xy4, xy3.negate());
   }

   @Test
   public void testDivide() throws Exception
   {
      XYZ xy1 = new XYZ(12, 24, 48);

      assertEquals(new XYZ(1, 2, 4), xy1.divide(12));
      assertEquals(new XYZ(2, 4, 8), xy1.divide(6));
      assertEquals(new XYZ(3, 6, 12), xy1.divide(4));
      assertEquals(new XYZ(4, 8, 16), xy1.divide(3));
      assertEquals(new XYZ(6, 12, 24), xy1.divide(2));
      assertEquals(new XYZ(12, 24, 48), xy1.divide(1));
   }

   @Test
   public void testMultiply() throws Exception
   {
      XYZ xy1 = new XYZ(1, 2, 3);

      assertEquals(new XYZ(1, 2, 3), xy1.multiply(1));
      assertEquals(new XYZ(2, 4, 6), xy1.multiply(2));
      assertEquals(new XYZ(3, 6, 9), xy1.multiply(3));
      assertEquals(new XYZ(4, 8, 12), xy1.multiply(4));
      assertEquals(new XYZ(6, 12, 18), xy1.multiply(6));
      assertEquals(new XYZ(12, 24, 36), xy1.multiply(12));

   }

   @Test
   public void testLength() throws Exception
   {
      XYZ xy1 = new XYZ(1, 2, 0);
      XYZ xy2 = new XYZ(-1, 3, 1);
      XYZ xy3 = new XYZ(4, -1, 2);
      XYZ xy4 = new XYZ(-5, -1, 3);

      assertEquals(Math.sqrt(5), xy1.length(), 1e-6);
      assertEquals(Math.sqrt(11), xy2.length(), 1e-6);
      assertEquals(Math.sqrt(21), xy3.length(), 1e-6);
      assertEquals(Math.sqrt(35), xy4.length(), 1e-6);
   }

   @Test
   public void testMin() throws Exception
   {
      XYZ xy1 = new XYZ(1, 2, 3);
      XYZ xy2 = new XYZ(-1, 3, 1);
      XYZ xy3 = new XYZ(4, -1, -2);
      XYZ xy4 = new XYZ(-5, -1, 4);

      assertEquals(new XYZ(-1, 2, 1), xy1.min(xy2));
      assertEquals(new XYZ(1, -1, -2), xy1.min(xy3));
      assertEquals(new XYZ(-5, -1, 3), xy1.min(xy4));
   }

   @Test
   public void testMax() throws Exception
   {
      XYZ xy1 = new XYZ(1, 2, 3);
      XYZ xy2 = new XYZ(-1, 3, 1);
      XYZ xy3 = new XYZ(4, -1, -2);
      XYZ xy4 = new XYZ(-5, -1, 4);

      assertEquals(new XYZ(1, 3, 3), xy1.max(xy2));
      assertEquals(new XYZ(4, 2, 3), xy1.max(xy3));
      assertEquals(new XYZ(1, 2, 4), xy1.max(xy4));
   }

   @Test
   public void testIsZero() throws Exception
   {
      XYZ xy1 = new XYZ(0, 0, 0);
      XYZ xy2 = new XYZ(-1, 3, 0);
      XYZ xy3 = new XYZ(4, 0, 1);
      XYZ xy4 = new XYZ(0, -1, 3);

      assertTrue(xy1.isZero());
      assertFalse(xy2.isZero());
      assertFalse(xy3.isZero());
      assertFalse(xy4.isZero());
   }

   @Test
   public void testDot() throws Exception
   {
      XYZ xy1 = new XYZ(0, 1, 0);
      XYZ xy2 = new XYZ(1, 0, 0);
      XYZ xy3 = new XYZ(0, 0.5, 0);
      XYZ xy4 = new XYZ(-0.5, 0, 0);

      assertEquals(0, xy1.dot(xy2), 1e-6);
      assertEquals(1, xy1.dot(xy1), 1e-6);
      assertEquals(0.5, xy1.dot(xy3), 1e-6);
      assertEquals(0, xy1.dot(xy4), 1e-6);

      assertEquals(1, xy2.dot(xy2), 1e-6);
      assertEquals(0, xy2.dot(xy1), 1e-6);
      assertEquals(0, xy2.dot(xy3), 1e-6);
      assertEquals(-0.5, xy2.dot(xy4), 1e-6);

      XYZ xy5 = new XYZ(0, 0, 0.7);
      XYZ xy6 = new XYZ(0, 0, 1);

      assertEquals(0, xy1.dot(xy5), 1e-6);
      assertEquals(0, xy2.dot(xy5), 1e-6);
      assertEquals(0.7, xy6.dot(xy5), 1e-6);
      assertEquals(0.7, xy5.dot(xy6), 1e-6);
   }

   @Test
   public void testIsUnit()
   {
      XYZ xyz1 = new XYZ(1, 0, 0);
      XYZ xyz2 = new XYZ(1, 1, 0);
      XYZ xyz3 = new XYZ(1 / Math.sqrt(3), 1 / Math.sqrt(3), 1 / Math.sqrt(3));

      assertTrue(xyz1.isUnit());
      assertFalse(xyz2.isUnit());
      assertTrue(xyz3.isUnit());
   }

   @Test
   public void testAsUnit()
   {
      XYZ xyz1 = new XYZ(1, 0, 0);
      XYZ xyz2 = new XYZ(1, 1, 0);
      XYZ xyz3 = new XYZ(1, 1, 1);

      XYZ xyzu1 = xyz1.asUnit();
      XYZ xyzu2 = xyz2.asUnit();
      XYZ xyzu3 = xyz3.asUnit();

      assert(xyz1.equals(xyzu1));
      assert(new XYZ(1 / Math.sqrt(2), 1 / Math.sqrt(2), 0).equals(xyzu2));
      assert(new XYZ(1 / Math.sqrt(3), 1 / Math.sqrt(3), 1 / Math.sqrt(3)).equals(xyzu3));
   }

   @Test
   public void testInterpolate()
   {
      XYZ xyz1 = new XYZ();
      XYZ xyz2 = new XYZ(1, 2, 3);

      assertTrue(xyz1.equals(XYZ.interpolate(xyz1, xyz2, 0)));
      assertTrue(xyz2.equals(XYZ.interpolate(xyz1, xyz2, 1)));
      assertTrue(new XYZ(0.5, 1, 1.5).equals(XYZ.interpolate(xyz1, xyz2, 0.5)));
   }
}
