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
      assertEquals(-2, GridWalker.ordinateToCell(-11, 10));
      assertEquals(-1, GridWalker.ordinateToCell(-1, 10));
      assertEquals(0, GridWalker.ordinateToCell(1, 10));
      assertEquals(0, GridWalker.ordinateToCell(9.99999, 10));
      assertEquals(1, GridWalker.ordinateToCell(10.000001, 10));
      assertEquals(100, GridWalker.ordinateToCell(1001, 10));
   }

   @Test
   public void testPosToCell() throws Exception
   {
      assertEquals(new CC(1, 1), GridWalker.positionToCell(new XY(11.0, 11.0), 10));
   }

   @Test
   public void testResetRayEnd() throws Exception
   {
      {
         XY begin = new XY(0.01, 0);
         XY end = new XY(19.99, 0);

         GridWalker gw = new GridWalker(10, begin, end, 1);

         HashSet<CC> cells = new HashSet<>();

         int count = 0;

         CC cell;

         while((cell = gw.nextCell()) != null && count < 10)
         {
            cells.add(cell);

            count++;

            if (count == 4)
            {
               gw.resetRayEnd(new XY(9.99, 0));
            }
         }

         // was going to go out to 8 cells (0, -1 -> 3, 0)
         // but resetting the end makes it unnecessary to visit the end two
         // so should see same as if line were shorter in first instance

         assertEquals(6, count);
         assertTrue(cells.contains(new CC(-1, -1)));
         assertTrue(cells.contains(new CC(-1, 0)));
         assertTrue(cells.contains(new CC(0, -1)));
         assertTrue(cells.contains(new CC(0, 0)));
         assertTrue(cells.contains(new CC(1, -1)));
         assertTrue(cells.contains(new CC(1, 0)));
      }
   }

   @Test
   public void testRotation() throws Exception
   {
      double cell_size = 20;
      double feature_diameter = 10;

      // probe variously angles from origin
      for(double d = 0; d < 360; d++)
      {
         XY start = new XY(0, 0);
         XY dir = new XY(Math.sin(d * Math.PI / 180), Math.cos(d * Math.PI / 180));
         XY end = start.plus(dir.multiply(105));

         GridWalker gw = new GridWalker(cell_size, start, end, feature_diameter);

         CC cell;
         HashSet<CC> cells = new HashSet<>();

         int min_x = 1000;
         int max_x = -1000;
         int min_y = 1000;
         int max_y = -1000;

         while((cell = gw.nextCell()) != null)
         {
            min_x = Math.min(min_x, cell.First);
            max_x = Math.max(max_x, cell.First);
            min_y = Math.min(min_y, cell.Second);
            max_y = Math.max(max_y, cell.Second);

            cells.add(cell);
         }

         for(int x = min_x - 1; x <= max_x + 1; x++)
         {
            for(int y = min_y - 1; y <= max_y + 1; y++)
            {
               cell = new CC(x, y);

               // four cell corners
               XY bl = gw.cellToEdgePosition(cell);
               XY br = gw.cellToEdgePosition(new CC(x + 1, y));
               XY tl = gw.cellToEdgePosition(new CC(x, y + 1));
               XY tr = gw.cellToEdgePosition(new CC(x + 1, y + 1));

               // does line pass through cell?
               boolean hits_cell = Util.edgeIntersect(bl, br, start, end) != null
                     || Util.edgeIntersect(br, tr, start, end) != null
                     || Util.edgeIntersect(tr, tl, start, end) != null
                     || Util.edgeIntersect(tl, bl, start, end) != null;

               // four dists of those from the line
               double dist_bl = Util.nodeEdgeDist(bl, start, end);
               double dist_br = Util.nodeEdgeDist(br, start, end);
               double dist_tl = Util.nodeEdgeDist(tl, start, end);
               double dist_tr = Util.nodeEdgeDist(tr, start, end);

               double min_dist = Math.min(
                     Math.min(dist_bl, dist_br),
                     Math.min(dist_tl, dist_tr));

               // otherwise, is the lines closest approach to the cell < feature radius
               boolean close_approach = min_dist <= feature_diameter / 2;

               if (hits_cell || close_approach)
               {
                  assertTrue(cells.contains(cell));
               }

               // could assert others not in but problem is
            }
         }
      }
   }
}
