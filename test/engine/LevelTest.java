package engine;

import engine.*;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;

import static org.junit.Assert.*;

public class LevelTest
{
   private final HashSet<Object> m_seen_things = new HashSet<>();

   private class ThingNoticer extends GeomLayout
   {
      ThingNoticer(INode n)
      {
         m_seen_things.add(n);
      }

      ThingNoticer(DirectedEdge de)
      {
         m_seen_things.add(de);
      }

      @Override
      Loop makeBaseGeometry()
      {
         return null;
      }

      @Override
      LoopSet makeDetailGeometry()
      {
         return null;
      }
   }

   @Test
   public void testGenerateGeometry() throws Exception
   {
      m_seen_things.clear();

      Graph g = new Graph();

      INode n1 = g.addNode("", "", "", 0, ThingNoticer::new);
      INode n2 = g.addNode("", "", "", 0, ThingNoticer::new);

      DirectedEdge de = g.connect(n1, n2, 0, 0, 0, ThingNoticer::new);

      Level l = new Level(g, 10, 5);

      l.generateGeometry();

      assertEquals(3, m_seen_things.size());
      assertTrue(m_seen_things.contains(n1));
      assertTrue(m_seen_things.contains(n2));
      assertTrue(m_seen_things.contains(de));
   }

   @Test
   public void testUnionOne() throws Exception
   {
      {
         // if we don't call generateGeometry, a null graph is fine
         Level l = new Level(null, 0, 0);

         Loop l1 = new Loop(new CircleCurve(new XY(0, 0), 10));
         Loop l2 = new Loop(new CircleCurve(new XY(30, 0), 10));
         Loop l3 = new Loop(new CircleCurve(new XY(60, 0), 10));

         l.addBaseLoop(l1);
         l.addBaseLoop(l2);
         l.addBaseLoop(l3);

         assertEquals(3, l.getBaseLoops().size());
         assertEquals(0, l.getMergedLoops().size());

         // unionOne pumps loops one-by-one out of base-loops and into wall-loops
         // (unioning them together as it goes...)
         assertFalse(l.unionOne(new Random(1)));

         assertEquals(2, l.getBaseLoops().size());
         assertEquals(1, l.getMergedLoops().size());

         // unionOne pumps loops one-by-one out of base-loops and into wall-loops
         // (unioning them together as it goes...)
         assertFalse(l.unionOne(new Random(1)));

         assertEquals(1, l.getBaseLoops().size());
         assertEquals(2, l.getMergedLoops().size());

         // unionOne pumps loops one-by-one out of base-loops and into wall-loops
         // (unioning them together as it goes...)
         assertFalse(l.unionOne(new Random(1)));

         assertEquals(0, l.getBaseLoops().size());
         assertEquals(3, l.getMergedLoops().size());

         // unionOne pumps loops one-by-one out of base-loops and into wall-loops
         // (unioning them together as it goes...)
         assertTrue(l.unionOne(new Random(1)));
      }

      {
         // if we don't call generateGeometry, a null graph is fine
         Level l = new Level(null, 0, 0);

         Loop l1 = new Loop(new CircleCurve(new XY(0, 0), 10));
         Loop l2 = new Loop(new CircleCurve(new XY(30, 0), 10));
         Loop l3 = new Loop(new CircleCurve(new XY(0, 30), 10));
         Loop l4 = new Loop(new CircleCurve(new XY(30, 30), 10));
         Loop l5 = new Loop(new CircleCurve(new XY(15, 15), 20));

         l.addBaseLoop(l1);
         l.addBaseLoop(l2);
         l.addBaseLoop(l3);
         l.addBaseLoop(l4);
         l.addBaseLoop(l5);

         // four merges should leave us with four non-intersecting loops
         assertFalse(l.unionOne(new Random(1)));
         assertFalse(l.unionOne(new Random(1)));
         assertFalse(l.unionOne(new Random(1)));
         assertFalse(l.unionOne(new Random(1)));

         assertEquals(1, l.getBaseLoops().size());
         assertEquals(4, l.getMergedLoops().size());

         // one more merge should join all those together as the 5th loop touches all the others
         assertFalse(l.unionOne(new Random(1)));

         assertEquals(0, l.getBaseLoops().size());
         assertEquals(1, l.getMergedLoops().size());

         // and we should be done
         assertTrue(l.unionOne(new Random(1)));
      }

      {
         // if we don't call generateGeometry, a null graph is fine
         Level l = new Level(null, 0, 0);

         Loop l1 = new Loop(new CircleCurve(new XY(0, 0), 10));

         Loop l2 = new Loop(new CircleCurve(new XY(10, 0), 5, CircleCurve.RotationDirection.Reverse));
         Loop l3 = new Loop(new CircleCurve(new XY(0, 10), 5, CircleCurve.RotationDirection.Reverse));
         Loop l4 = new Loop(new CircleCurve(new XY(-10, 0), 5, CircleCurve.RotationDirection.Reverse));
         Loop l5 = new Loop(new CircleCurve(new XY(0, -10), 5, CircleCurve.RotationDirection.Reverse));

         LoopSet ls = new LoopSet();
         ls.add(l2);
         ls.add(l3);
         ls.add(l4);
         ls.add(l5);

         l.addBaseLoop(l1);
         l.addDetailLoops(ls);

         assertEquals(1, l.getBaseLoops().size());
         assertEquals(1, l.getDetailLoopSets().size());
         assertEquals(0, l.getMergedLoops().size());

         // one positive loop
         assertFalse(l.unionOne(new Random(1)));

         assertEquals(0, l.getBaseLoops().size());
         assertEquals(1, l.getDetailLoopSets().size());
         assertEquals(1, l.getMergedLoops().size());

         // one more merge should merge the four negative loops
         // leaving one rather crenalated positive one
         assertFalse(l.unionOne(new Random(1)));

         assertEquals(0, l.getBaseLoops().size());
         assertEquals(0, l.getDetailLoopSets().size());
         assertEquals(1, l.getMergedLoops().size());

         // final loop takes 8 curves to describe
         assertEquals(8, l.getMergedLoops().stream().findFirst().get().numCurves());

         // and we should be done
         assertTrue(l.unionOne(new Random(1)));
      }
   }

   private static void hitTest(Level l, XY dir)
   {
      Wall w = l.nearestWall(new XY(), dir, 110);
      assertNotNull(w);
      assertNotNull(Util.edgeIntersect(new XY(), dir.multiply(110), w.Start, w.End));
   }

   @Test
   public void testNearestWall() throws Exception
   {
      for(double rad = 90; rad < 200; rad *= 1.1)
      {
         Level l = new Level(null, 20, 10);

         Loop l1 = new Loop(new CircleCurve(new XY(), rad));
         l.addBaseLoop(l1);

         l.unionOne(new Random(1));

         l.finalise();

         // probe various angles from origin
         for(double d = 0; d < 360; d++)
         {
            XY dir = new XY(Math.sin(d * Math.PI / 180), Math.cos(d * Math.PI / 180));

            Wall w = l.nearestWall(new XY(0, 0), dir, rad * 1.1);

            assertNotNull(w);

            double dist = Math.min(w.Start.length(), w.End.length());
            // circle is facetted, but for this size we expect to find a point within
            // 1% of real radius
            assertEquals(1, dist/rad, 0.01);
         }
      }

      {
         Level l = new Level(null, 20, 10);

         Loop l1 = new Loop(new CircleCurve(new XY(), 100));
         l.addBaseLoop(l1);
         LoopSet ls = new LoopSet();
         Loop l2 = new Loop(new CircleCurve(new XY(50, 0), 10, CircleCurve.RotationDirection.Reverse));
         ls.add(l2);
         l.addDetailLoops(ls);

         l.unionOne(new Random(1));
         l.unionOne(new Random(1));

         l.finalise();

         // probe various angles from origin
         // circle subtends a touch more than +/- 11.5 degrees, so if we scan by whole degrees
         // we ought to see a sudden step out to a distance of 100 when we pass that

         for(double d = 0; d < 360; d++)
         {
            XY dir = new XY(Math.sin(d * Math.PI / 180), Math.cos(d * Math.PI / 180));

            Wall w = l.nearestWall(new XY(0, 0), dir, 110);

            assertNotNull(w);

            double dist = Math.min(w.Start.length(), w.End.length());

            // hitting small circle
            if (d >= 79 && d <= 101)
            {
               // cannot hit beyond small circle half-way line
               assertTrue(dist < 50);
            }
            else
            {
               assertTrue(dist > 99);
            }
         }
      }
   }
}
