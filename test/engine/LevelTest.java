package engine;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class LevelTest
{
   @Test
   public void testNearestWall() throws Exception
   {
      for(double rad = 90; rad < 200; rad *= 1.1)
      {
         UnionHelper uh = new UnionHelper();

         Loop l1 = new Loop(new CircleCurve(new XY(), rad));
         uh.addBaseLoop(l1);

         uh.unionOne(new Random(1));

         Level l = uh.makeLevel(20, 10);

         // probe various angles from origin
         for(double d = 0; d < 360; d++)
         {
            XY dir = new XY(Math.sin(d * Math.PI / 180), Math.cos(d * Math.PI / 180));

            Level.RayCollision wcr = l.nearestWall(new XY(0, 0), dir, rad * 1.1);

            assertNotNull(wcr);

            double dist = Math.min(wcr.WallHit.Start.length(), wcr.WallHit.End.length());
            // circle is facetted, but for this size we expect to find a point within
            // 1% of real radius
            assertEquals(1, dist/rad, 0.01);

            // expect dist reported similar
            assertEquals(1, dist/wcr.DistanceTo, 0.02);
         }
      }

      {
         UnionHelper uh = new UnionHelper();

         Loop l1 = new Loop(new CircleCurve(new XY(), 100));
         uh.addBaseLoop(l1);
         LoopSet ls = new LoopSet();
         Loop l2 = new Loop(new CircleCurve(new XY(50, 0), 10, CircleCurve.RotationDirection.Reverse));
         ls.add(l2);
         uh.addDetailLoops(ls);

         uh.unionOne(new Random(1));
         uh.unionOne(new Random(1));

         Level l = uh.makeLevel(20, 10);

         // probe various angles from origin
         // circle subtends a touch more than +/- 11.5 degrees, so if we scan by whole degrees
         // we ought to see a sudden step out to a distance of 100 when we pass that

         for(double d = 0; d < 360; d++)
         {
            XY dir = new XY(Math.sin(d * Math.PI / 180), Math.cos(d * Math.PI / 180));

            Level.RayCollision wcr = l.nearestWall(new XY(0, 0), dir, 110);

            assertNotNull(wcr);

            double len1 = wcr.WallHit.Start.length();
            double len2 = wcr.WallHit.End.length();
            double dist = Math.min(len1, len2);

            // hitting small circle
            if (d >= 79 && d <= 101)
            {
               // cannot hit beyond small circle half-way line
               assertTrue(dist < 50);
               // OK, can hit slightly beyond due to finite facets
               // but previous OK a nearer end of facet must always still be closer
               assertTrue(wcr.DistanceTo < 51);
            }
            else
            {
               assertTrue(dist > 99);
               assertTrue(wcr.DistanceTo > 99);
            }
         }
      }
   }
}
