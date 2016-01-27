package engine;

import org.junit.Test;

import java.util.HashSet;
import java.util.Random;

import static org.junit.Assert.*;

public class UnionHelperTest
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

      // returning objects here increases coverage

      @Override
      public Loop makeBaseGeometry()
      {
         return new Loop();
      }

      @Override
      public LoopSet makeDetailGeometry()
      {
         return new LoopSet();
      }
   }

   @Test
   public void testGenerateGeometry() throws Exception
   {
      m_seen_things.clear();

      Graph g = new Graph();

      // having a start increases coverage
      INode n1 = g.addNode("Start", "", "", 0, ThingNoticer::new);
      INode n2 = g.addNode("", "", "", 0, ThingNoticer::new);

      DirectedEdge de = g.connect(n1, n2, 0, 0, 0, ThingNoticer::new);

      UnionHelper uh = new UnionHelper();

      uh.generateGeometry(g);

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
         UnionHelper uh = new UnionHelper();

         Loop l1 = new Loop(new CircleCurve(new XY(0, 0), 10));
         Loop l2 = new Loop(new CircleCurve(new XY(30, 0), 10));
         Loop l3 = new Loop(new CircleCurve(new XY(60, 0), 10));

         uh.addBaseLoop(l1);
         uh.addBaseLoop(l2);
         uh.addBaseLoop(l3);

         assertEquals(3, uh.getBaseLoops().size());
         assertEquals(0, uh.getMergedLoops().size());

         // unionOne pumps loops one-by-one out of base-loops and into wall-loops
         // (unioning them together as it goes...)
         assertFalse(uh.unionOne(new Random(1)));

         assertEquals(2,uh.getBaseLoops().size());
         assertEquals(1,uh.getMergedLoops().size());

         // unionOne pumps loops one-by-one out of base-loops and into wall-loops
         // (unioning them together as it goes...)
         assertFalse(uh.unionOne(new Random(1)));

         assertEquals(1, uh.getBaseLoops().size());
         assertEquals(2, uh.getMergedLoops().size());

         // unionOne pumps loops one-by-one out of base-loops and into wall-loops
         // (unioning them together as it goes...)
         assertFalse(uh.unionOne(new Random(1)));

         assertEquals(0, uh.getBaseLoops().size());
         assertEquals(3, uh.getMergedLoops().size());

         // unionOne pumps loops one-by-one out of base-loops and into wall-loops
         // (unioning them together as it goes...)
         assertTrue(uh.unionOne(new Random(1)));
      }

      {
         // if we don't call generateGeometry, a null graph is fine
         UnionHelper uh = new UnionHelper();

         Loop l1 = new Loop(new CircleCurve(new XY(0, 0), 10));
         Loop l2 = new Loop(new CircleCurve(new XY(30, 0), 10));
         Loop l3 = new Loop(new CircleCurve(new XY(0, 30), 10));
         Loop l4 = new Loop(new CircleCurve(new XY(30, 30), 10));
         Loop l5 = new Loop(new CircleCurve(new XY(15, 15), 20));

         uh.addBaseLoop(l1);
         uh.addBaseLoop(l2);
         uh.addBaseLoop(l3);
         uh.addBaseLoop(l4);
         uh.addBaseLoop(l5);

         // four merges should leave us with four non-intersecting loops
         assertFalse(uh.unionOne(new Random(1)));
         assertFalse(uh.unionOne(new Random(1)));
         assertFalse(uh.unionOne(new Random(1)));
         assertFalse(uh.unionOne(new Random(1)));

         assertEquals(1, uh.getBaseLoops().size());
         assertEquals(4, uh.getMergedLoops().size());

         // one more merge should join all those together as the 5th loop touches all the others
         assertFalse(uh.unionOne(new Random(1)));

         assertEquals(0, uh.getBaseLoops().size());
         assertEquals(1, uh.getMergedLoops().size());

         // and we should be done
         assertTrue(uh.unionOne(new Random(1)));
      }

      {
         // if we don't call generateGeometry, a null graph is fine
         UnionHelper uh = new UnionHelper();

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

         uh.addBaseLoop(l1);
         uh.addDetailLoops(ls);

         assertEquals(1, uh.getBaseLoops().size());
         assertEquals(1, uh.getDetailLoopSets().size());
         assertEquals(0, uh.getMergedLoops().size());

         // one positive loop
         assertFalse(uh.unionOne(new Random(1)));

         assertEquals(0, uh.getBaseLoops().size());
         assertEquals(1, uh.getDetailLoopSets().size());
         assertEquals(1, uh.getMergedLoops().size());

         // one more merge should merge the four negative loops
         // leaving one rather crenalated positive one
         assertFalse(uh.unionOne(new Random(1)));

         assertEquals(0, uh.getBaseLoops().size());
         assertEquals(0, uh.getDetailLoopSets().size());
         assertEquals(1, uh.getMergedLoops().size());

         // final loop takes 8 curves to describe
         assertEquals(8, uh.getMergedLoops().stream().findFirst().get().numCurves());

         // and we should be done
         assertTrue(uh.unionOne(new Random(1)));
      }
   }
}
