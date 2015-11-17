import org.junit.Test;

import static org.junit.Assert.*;

public class BoxTest
{
   @Test
   public void testCtor_Exceptions() throws Exception
   {
      {
         boolean thrown = false;

         try
         {
            new Box(new XY(0, 1), new XY(-1, 2));
         }
         catch(IllegalArgumentException iae)
         {
            thrown = true;

            assertTrue(iae.getMessage().contains(" X "));
         }

         assertTrue(thrown);
      }

      {
         boolean thrown = false;

         try
         {
            new Box(new XY(0, 1), new XY(1, -2));
         }
         catch(IllegalArgumentException iae)
         {
            thrown = true;

            assertTrue(iae.getMessage().contains(" Y "));
         }

         assertTrue(thrown);
      }
   }

   @Test
   public void testCenter() throws Exception
   {
      {
         Box b = new Box(new XY(0, 1), new XY(1, 2));

         assertEquals(new XY(0.5, 1.5), b.Center());
      }

      {
         Box b = new Box(new XY(-7, -14), new XY(14, 7));

         assertEquals(new XY(3.5, -3.5), b.Center());
      }
   }

   @Test
   public void testDX() throws Exception
   {
      {
         Box b = new Box(new XY(0, 1), new XY(1, 3));

         assertEquals(1, b.DX(), 0);
      }

      {
         Box b = new Box(new XY(-7, -7), new XY(14, 13));

         assertEquals(21, b.DX(), 0);
      }
   }

   @Test
   public void testDY() throws Exception
   {
      {
         Box b = new Box(new XY(0, 1), new XY(1, 3));

         assertEquals(2, b.DY(), 0);
      }

      {
         Box b = new Box(new XY(-7, -7), new XY(14, 13));

         assertEquals(20, b.DY(), 0);
      }
   }

   @Test
   public void testEquals() throws Exception
   {
      {
         Box b1 = new Box(new XY(0, 1), new XY(1, 2));
         Box b2 = new Box(new XY(0, 1), new XY(1, 2));
         Box b3 = new Box(new XY(-1, -2), new XY(1, 2));
         Box b4 = new Box(new XY(0, 1), new XY(10, 20));

         assertEquals(b1, b2);
         assertNotEquals(b1, b3);
         assertNotEquals(b1, b4);
         assertNotEquals(b2, b3);
         assertNotEquals(b2, b4);
         assertNotEquals(b1, b3);
         assertNotEquals(b3, b4);

         //noinspection EqualsBetweenInconvertibleTypes
         assertFalse(b1.equals(1));
      }
   }

   @Test
   public void testHashCode() throws Exception
   {
      {
         Box b1 = new Box(new XY(0, 1), new XY(1, 2));
         Box b2 = new Box(new XY(0, 1), new XY(1, 2));
         Box b3 = new Box(new XY(-1, 1), new XY(1, 2));
         Box b4 = new Box(new XY(0, -1), new XY(1, 2));
         Box b5 = new Box(new XY(0, 1), new XY(10, 2));
         Box b6 = new Box(new XY(0, 1), new XY(1, 20));

         assertEquals(b1.hashCode(), b2.hashCode());
         assertNotEquals(b1.hashCode(), b3.hashCode());
         assertNotEquals(b1.hashCode(), b4.hashCode());
         assertNotEquals(b1.hashCode(), b5.hashCode());
         assertNotEquals(b1.hashCode(), b6.hashCode());
      }
   }
}