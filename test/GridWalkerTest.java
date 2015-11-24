import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

public class GridWalkerTest
{
   @Test
   public void testNextCell() throws Exception
   {
      {
         XY begin = new XY(0.01, 0);
         XY end = new XY(9.99, 0);

         GridWalker gw = new GridWalker(10, begin, end, 1);

         HashSet<CC> cells = new HashSet<>();

         int count = 0;

         CC cell;

         XY dir = end.minus(begin).makeUnit();

         while((cell = gw.nextCell()) != null && count < 10)
         {
            cells.add(cell);

            count++;
         }

         assertEquals(6, count);
         assertTrue(cells.contains(new CC(-1, -1)));
         assertTrue(cells.contains(new CC(-1, 0)));
         assertTrue(cells.contains(new CC(0, -1)));
         assertTrue(cells.contains(new CC(0, 0)));
         assertTrue(cells.contains(new CC(1, -1)));
         assertTrue(cells.contains(new CC(1, 0)));
      }

      {
         XY begin = new XY(9.99, 0);
         XY end = new XY(0.01, 0);

         GridWalker gw = new GridWalker(10, begin, end, 1);

         HashSet<CC> cells = new HashSet<>();

         int count = 0;

         CC cell;

         XY dir = end.minus(begin).makeUnit();

         while((cell = gw.nextCell()) != null && count < 10)
         {
            cells.add(cell);

            count++;
         }

         assertEquals(6, count);
         assertTrue(cells.contains(new CC(-1, -1)));
         assertTrue(cells.contains(new CC(-1, 0)));
         assertTrue(cells.contains(new CC(0, -1)));
         assertTrue(cells.contains(new CC(0, 0)));
         assertTrue(cells.contains(new CC(1, -1)));
         assertTrue(cells.contains(new CC(1, 0)));
      }

      {
         XY begin = new XY(0, 0.01);
         XY end = new XY(0, 9.99);

         GridWalker gw = new GridWalker(10, begin, end, 1);

         HashSet<CC> cells = new HashSet<>();

         int count = 0;

         CC cell;

         XY dir = end.minus(begin).makeUnit();

         while((cell = gw.nextCell()) != null && count < 10)
         {
            cells.add(cell);

            count++;
         }

         assertEquals(6, count);
         assertTrue(cells.contains(new CC(-1, -1)));
         assertTrue(cells.contains(new CC(0, -1)));
         assertTrue(cells.contains(new CC(-1, 0)));
         assertTrue(cells.contains(new CC(0, 0)));
         assertTrue(cells.contains(new CC(-1, 1)));
         assertTrue(cells.contains(new CC(0, 1)));
      }

      {
         XY begin = new XY(0, 9.99);
         XY end = new XY(0, 0.01);

         GridWalker gw = new GridWalker(10, begin, end, 1);

         HashSet<CC> cells = new HashSet<>();

         int count = 0;

         CC cell;

         XY dir = end.minus(begin).makeUnit();

         while((cell = gw.nextCell()) != null && count < 10)
         {
            cells.add(cell);

            count++;
         }

         assertEquals(6, count);
         assertTrue(cells.contains(new CC(-1, -1)));
         assertTrue(cells.contains(new CC(0, -1)));
         assertTrue(cells.contains(new CC(-1, 0)));
         assertTrue(cells.contains(new CC(0, 0)));
         assertTrue(cells.contains(new CC(-1, 1)));
         assertTrue(cells.contains(new CC(0, 1)));
      }

      {
         XY begin = new XY(9.99, 9.99);
         XY end = new XY(0.01, 0.01);

         GridWalker gw = new GridWalker(10, begin, end, 1);

         HashSet<CC> cells = new HashSet<>();

         int count = 0;

         CC cell;

         XY dir = end.minus(begin).makeUnit();

         while((cell = gw.nextCell()) != null && count < 10)
         {
            cells.add(cell);

            count++;
         }

         assertEquals(7, count);
         assertTrue(cells.contains(new CC(-1, -1)));
         assertTrue(cells.contains(new CC(0, -1)));
         assertTrue(cells.contains(new CC(-1, 0)));
         assertTrue(cells.contains(new CC(0, 0)));
         assertTrue(cells.contains(new CC(1, 0)));
         assertTrue(cells.contains(new CC(0, 1)));
         assertTrue(cells.contains(new CC(1, 1)));
      }

      {
         XY begin = new XY(0.01, 0.01);
         XY end = new XY(9.99, 9.99);

         GridWalker gw = new GridWalker(10, begin, end, 1);

         HashSet<CC> cells = new HashSet<>();

         int count = 0;

         CC cell;

         XY dir = end.minus(begin).makeUnit();

         while((cell = gw.nextCell()) != null && count < 10)
         {
            cells.add(cell);

            count++;
         }

         assertEquals(7, count);
         assertTrue(cells.contains(new CC(-1, -1)));
         assertTrue(cells.contains(new CC(0, -1)));
         assertTrue(cells.contains(new CC(-1, 0)));
         assertTrue(cells.contains(new CC(0, 0)));
         assertTrue(cells.contains(new CC(1, 0)));
         assertTrue(cells.contains(new CC(0, 1)));
         assertTrue(cells.contains(new CC(1, 1)));
      }

      {
         XY begin = new XY(0.01, 9.99);
         XY end = new XY(9.99, 0.01);

         GridWalker gw = new GridWalker(10, begin, end, 1);

         HashSet<CC> cells = new HashSet<>();

         int count = 0;

         CC cell;

         XY dir = end.minus(begin).makeUnit();

         while((cell = gw.nextCell()) != null && count < 10)
         {
            cells.add(cell);

            count++;
         }

         assertEquals(7, count);
         assertTrue(cells.contains(new CC(-1, 1)));
         assertTrue(cells.contains(new CC(0, 1)));
         assertTrue(cells.contains(new CC(-1, 0)));
         assertTrue(cells.contains(new CC(0, 0)));
         assertTrue(cells.contains(new CC(1, 0)));
         assertTrue(cells.contains(new CC(0, -1)));
         assertTrue(cells.contains(new CC(1, -1)));
      }

      {
         XY begin = new XY(9.99, 0.01);
         XY end = new XY(0.01, 9.99);

         GridWalker gw = new GridWalker(10, begin, end, 1);

         HashSet<CC> cells = new HashSet<>();

         int count = 0;

         CC cell;

         XY dir = end.minus(begin).makeUnit();

         while((cell = gw.nextCell()) != null && count < 10)
         {
            cells.add(cell);

            count++;
         }

         assertEquals(7, count);
         assertTrue(cells.contains(new CC(-1, 1)));
         assertTrue(cells.contains(new CC(0, 1)));
         assertTrue(cells.contains(new CC(-1, 0)));
         assertTrue(cells.contains(new CC(0, 0)));
         assertTrue(cells.contains(new CC(1, 0)));
         assertTrue(cells.contains(new CC(0, -1)));
         assertTrue(cells.contains(new CC(1, -1)));
      }
   }

   @Test
   public void testOrdinateToCell() throws Exception
   {

   }

   @Test
   public void testPosToGridCell() throws Exception
   {

   }

   @Test
   public void testResetRayEnd() throws Exception
   {

   }
}
