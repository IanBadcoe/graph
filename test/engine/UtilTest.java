package engine;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

@SuppressWarnings("ConstantConditions")
public class UtilTest
{
   @Test
   public void testEdgeIntersectSimple() throws Exception
   {
      assertNotNull(Util.edgeIntersect(new XY(1, 0), new XY(-1, 0), new XY(0, 1), new XY(0, -1)));
      assertNull(Util.edgeIntersect(new XY(1, 0), new XY(-1, 0), new XY(1, 0), new XY(-1, 0)));

      assertNotNull(Util.edgeIntersect(new XY(-1, 0), new XY(1, 0), new XY(0, -1), new XY(0, 1)));
      assertNull(Util.edgeIntersect(new XY(-1, 0), new XY(1, 0), new XY(-1, 0), new XY(1, 0)));

      assertNotNull(Util.edgeIntersect(new XY(0, 1), new XY(0, -1), new XY(1, 0), new XY(-1, 0)));
      assertNull(Util.edgeIntersect(new XY(0, 1), new XY(0, -1), new XY(0, 1), new XY(0, -1)));

      assertNotNull(Util.edgeIntersect(new XY(0, -1), new XY(0, 1), new XY(-1, 0), new XY(1, 0)));
      assertNull(Util.edgeIntersect(new XY(0, -1), new XY(0, 1), new XY(0, -1), new XY(0, 1)));

      assertNull(Util.edgeIntersect(new XY(0, -1), new XY(0, 1), new XY(-1, 2), new XY(1, 2)));
      assertNull(Util.edgeIntersect(new XY(0, -1), new XY(0, 1), new XY(-1, -2), new XY(1, -2)));

      assertNull(Util.edgeIntersect(new XY(2, -1), new XY(2, 1), new XY(-1, 0), new XY(1, 0)));
      assertNull(Util.edgeIntersect(new XY(-2, -1), new XY(-2, 1), new XY(-1, 0), new XY(1, 0)));
   }

   @Test
   public void testEdgeIntersectT1Scan() throws Exception
   {
      double[] values = {1e-6, 2e-6, 5e-6, 1e-5, 2e-5, 5e-5, 1e-4, 2e-4, 5e-4, 1e-3, 2e-3, 5e-3, 1e-2, 2e-2, 5e-2, 1e-1, 2e-1, 5e-1};

      for (double f : values)
      {
         assertEquals(f, Util.edgeIntersect(new XY(0, 0), new XY(1, 0),
               new XY(f, 0.5), new XY(f, -0.5)).First, 1e-8);
      }
   }

   @Test
   public void testEdgeIntersectT2Scan() throws Exception
   {
      double[] values = {1e-6, 2e-6, 5e-6, 1e-5, 2e-5, 5e-5, 1e-4, 2e-4, 5e-4, 1e-3, 2e-3, 5e-3, 1e-2, 2e-2, 5e-2, 1e-1, 2e-1, 5e-1};

      for (double f : values)
      {
         assertEquals(f, Util.edgeIntersect(new XY(f, 0.5), new XY(f, -0.5),
               new XY(0, 0), new XY(1, 0)).Second, 1e-8);
      }
   }

   @Test
   public void testEdgeIntersectSlantScan() throws Exception
   {
      double[] values = {1e-6, 2e-6, 5e-6, 1e-5, 2e-5, 5e-5, 1e-4, 2e-4, 5e-4, 1e-3, 2e-3, 5e-3, 1e-2, 2e-2, 5e-2, 1e-1, 2e-1, 5e-1, 1, 2, 5};

      for (double f : values)
      {
         // increasing slant on x
         OrderedPair<Double, Double> ret = Util.edgeIntersect(new XY(f, 0), new XY(-f, 1),
               new XY(0, 0), new XY(0, 1));

         assertEquals(0.5,  ret.First, 1e-8);
         assertEquals(0.5,  ret.Second, 1e-8);

         // same on y
         ret = Util.edgeIntersect(new XY(0, f), new XY(1, -f),
               new XY(0, 0), new XY(1, 0));

         assertEquals(0.5,  ret.First, 1e-8);
         assertEquals(0.5,  ret.Second, 1e-8);
      }
   }

   @Test
   public void testEdgeIntersectMagnitudeScan() throws Exception
   {
      double[] values = {-1000000, -100000, -10000, -1000, -100, -10, -1, 0, 1, 10, 100, 1000, 10000, 100000, 1000000};

      for (double f : values)
      {
         for (double g : values)
         {
            OrderedPair<Double, Double> ret = Util.edgeIntersect(new XY(1 + f, 0 + g), new XY(-1 + f, 0 + g),
                  new XY(0 + f, 1 + g), new XY(0 + f, -1 + g));

            assertEquals(0.5, ret.First, 1e-8);
            assertEquals(0.5, ret.Second, 1e-8);
         }
      }
   }

   @Test
   public void testEdgeIntersectSimple_Node() throws Exception
   {
      assertNotNull(Util.edgeIntersect(makeNodeAt(1, 0), makeNodeAt(-1, 0), makeNodeAt(0, 1), makeNodeAt(0, -1)));
      assertNull(Util.edgeIntersect(makeNodeAt(1, 0), makeNodeAt(-1, 0), makeNodeAt(1, 0), makeNodeAt(-1, 0)));

      assertNotNull(Util.edgeIntersect(makeNodeAt(-1, 0), makeNodeAt(1, 0), makeNodeAt(0, -1), makeNodeAt(0, 1)));
      assertNull(Util.edgeIntersect(makeNodeAt(-1, 0), makeNodeAt(1, 0), makeNodeAt(-1, 0), makeNodeAt(1, 0)));

      assertNotNull(Util.edgeIntersect(makeNodeAt(0, 1), makeNodeAt(0, -1), makeNodeAt(1, 0), makeNodeAt(-1, 0)));
      assertNull(Util.edgeIntersect(makeNodeAt(0, 1), makeNodeAt(0, -1), makeNodeAt(0, 1), makeNodeAt(0, -1)));

      assertNotNull(Util.edgeIntersect(makeNodeAt(0, -1), makeNodeAt(0, 1), makeNodeAt(-1, 0), makeNodeAt(1, 0)));
      assertNull(Util.edgeIntersect(makeNodeAt(0, -1), makeNodeAt(0, 1), makeNodeAt(0, -1), makeNodeAt(0, 1)));
   }

   @Test
   public void testEdgeIntersectAdjoining_Node() throws Exception
   {
      // we can detect end-collisions of edges
      assertNotNull(Util.edgeIntersect(new XY(1, 0), new XY(-1, 0), new XY(-1, 0), new XY(0, 1)));
      assertNotNull(Util.edgeIntersect(new XY(-1, 0), new XY(1, 0), new XY(-1, 0), new XY(0, 1)));
      assertNotNull(Util.edgeIntersect(new XY(1, 0), new XY(-1, 0), new XY(0, 1), new XY(-1, 0)));
      assertNotNull(Util.edgeIntersect(new XY(-1, 0), new XY(1, 0), new XY(0, 1), new XY(-1, 0)));

      Node n1 = makeNodeAt(1, 0);
      Node n2 = makeNodeAt(-1, 0);
      Node n3 = makeNodeAt(0, 1);

      // but we don't if we know it is deliberate edge concatenation
      // e.g. if the node is shared
      assertNull(Util.edgeIntersect(n1, n2, n2, n3));
      assertNull(Util.edgeIntersect(n2, n1, n2, n3));
      assertNull(Util.edgeIntersect(n1, n2, n3, n2));
      assertNull(Util.edgeIntersect(n2, n1, n3, n2));
   }

   @Test
   public void testEdgeIntersect_Edges()
   {
      {
         Node n1 = makeNodeAt(1, 0);
         Node n2 = makeNodeAt(-1, 0);
         Node n3 = makeNodeAt(0, 1);

         // check same adjoining-edge behaviour as we checked with nodes
         assertNull(Util.edgeIntersect(makeEdge(n1, n2), makeEdge(n2, n3)));
         assertNull(Util.edgeIntersect(makeEdge(n2, n1), makeEdge(n2, n3)));
         assertNull(Util.edgeIntersect(makeEdge(n1, n2), makeEdge(n3, n2)));
         assertNull(Util.edgeIntersect(makeEdge(n2, n1), makeEdge(n3, n2)));
      }

      // just repeat a couple of the above tests and check we get the same t values
      double[] values = {1e-6, 2e-6, 5e-6, 1e-5, 2e-5, 5e-5, 1e-4, 2e-4, 5e-4, 1e-3, 2e-3, 5e-3, 1e-2, 2e-2, 5e-2, 1e-1, 2e-1, 5e-1};

      Node n1 = makeNodeAt(0, 0);
      Node n2 = makeNodeAt(1, 0);
      DirectedEdge e1 = makeEdge(n1, n2);

      for (double f : values)
      {
         Node n3 = makeNodeAt(f, 0.5);
         Node n4 = makeNodeAt(f, -0.5);
         DirectedEdge e2 = makeEdge(n3, n4);

         {
            DirectedEdgePair ret = Util.edgeIntersect(e1, e2);

            assertEquals(e1, ret.m_e1);
            assertEquals(e2, ret.m_e2);
            assertEquals(f, ret.m_t1, 1e-8);
            assertEquals(0.5, ret.m_t2, 1e-8);
         }

         {
            DirectedEdgePair ret = Util.edgeIntersect(e2, e1);

            assertEquals(e2, ret.m_e1);
            assertEquals(e1, ret.m_e2);
            assertEquals(0.5, ret.m_t1, 1e-8);
            assertEquals(f, ret.m_t2, 1e-8);
         }
      }
   }

   @Test
   public void testUnitEdgeForce()
   {
      // between min and max should be zero and no distortion
      for(int i = 0; i <= 10; i++)
      {
         double l = 10.0 + i;

         OrderedPair<Double, Double> ret = Util.unitEdgeForce(l, 10.0, 20.0);

         assertEquals(0.0, ret.Second, 0.0);
         assertEquals(1.0, ret.First, 0.0);
      }

      {
         // at 10% compression, force should be -0.1 and distortion 0.9
         OrderedPair<Double, Double> ret = Util.unitEdgeForce(9.0, 10.0, 20.0);

         assertEquals(-0.1, ret.Second, 1e-6);
         assertEquals(0.9, ret.First, 0.0);
      }

      {
         // at 10% stretch, force should be 0.1 and distortion 1.1
         OrderedPair<Double, Double> ret = Util.unitEdgeForce(22.0, 10.0, 20.0);

         assertEquals(0.1, ret.Second, 1e-6);
         assertEquals(1.1, ret.First, 0.0);
      }
   }

   @Test
   public void testUnitNodeForce()
   {
      {
         OrderedPair<Double, Double> ret = Util.unitNodeForce(1.0, 2.0);

         // 50% compression and 50% repulsion force
         assertEquals(0.5, ret.First, 0);
         assertEquals(-0.5, ret.Second, 0);
      }

      {
         OrderedPair<Double, Double> ret = Util.unitNodeForce(2.0, 2.0);

         // 0% compression and no force
         assertEquals(0, ret.First, 0);
         assertEquals(0, ret.Second, 0);
      }

      {
         OrderedPair<Double, Double> ret = Util.unitNodeForce(4.0, 2.0);

         // 200% of minimum size and no force
         assertEquals(0, ret.First, 0);
         assertEquals(0, ret.Second, 0);
      }
   }


   @Test
   public void testNodeEdgeDist_Exceptions()
   {
      XY xy1 = new XY();
      XY xy2 = new XY(1, 0);

      boolean thrown = false;

      try
      {
         Util.nodeEdgeDist(xy2, xy1, xy1);
      }
      catch(UnsupportedOperationException e)
      {
         thrown = true;
      }

      assertTrue(thrown);
   }

   @Test
   public void testNodeEdgeDist()
   {
      double rots[] = { 0.0, 0.001, 0.1, Math.PI / 4, Math.PI / 2, Math.PI,
            Math.PI * 1.5};
      XY offsets[] = { new XY(0, 0), new XY(-10, 0), new XY(10, 20), new XY(0, -3) };

      for(double rot : rots)
      {
         for(XY offset : offsets)
         {
            NodeEdgeDist_Test(rot, offset);
         }
      }
   }

   private void OneNodeEdgeDist_Test(XY n, XY es, XY ee,
                                     Matrix2D mat, XY offset,
                                     double exp_dist, XY exp_targ, XY exp_dir)
   {
      XY h_n = mat.multiply(n.plus(offset));
      XY h_es = mat.multiply(es.plus(offset));
      XY h_ee = mat.multiply(ee.plus(offset));
      XY h_exp_targ = mat.multiply(exp_targ.plus(offset));
      XY h_exp_dir = mat.multiply(exp_dir);
      Util.NEDRet vals = Util.nodeEdgeDistDetailed(h_n, h_es, h_ee);

      assertEquals(exp_dist, vals.Dist, 1e-5);
      assertEquals(h_exp_targ.X, vals.Target.X, 1e-6);
      assertEquals(h_exp_targ.Y, vals.Target.Y, 1e-6);
      assertEquals(h_exp_dir.X, vals.Direction.X, 1e-6);
      assertEquals(h_exp_dir.Y, vals.Direction.Y, 1e-6);
   }

   private void NodeEdgeDist_Test(double angle, XY offset)
   {
      Matrix2D mat = new Matrix2D(angle);

      final double ROOT2 = Math.sqrt(2);

      // one unit before start of line
      OneNodeEdgeDist_Test(new XY(0, -1), new XY(0, 0), new XY(0, 1),
            mat, offset,
            1, new XY(0, 0), new XY(0, 1));
      // one unit before start of line and one to the side
      OneNodeEdgeDist_Test(new XY(1, -1), new XY(0, 0), new XY(0, 1),
            mat, offset,
            ROOT2, new XY(0, 0), new XY(-1 / ROOT2, 1 / ROOT2));
      // one unit before start of line and one to the other side
      OneNodeEdgeDist_Test(new XY(-1, -1), new XY(0, 0), new XY(0, 1),
            mat, offset,
            ROOT2, new XY(0, 0), new XY(1 / ROOT2, 1 / ROOT2));
      // level with start of line and one to the side
      OneNodeEdgeDist_Test(new XY(1, 0), new XY(0, 0), new XY(0, 1),
            mat, offset,
            1, new XY(0, 0), new XY(-1, 0));
      // level with start of line and one to the other side
      OneNodeEdgeDist_Test(new XY(-1, 0), new XY(0, 0), new XY(0, 1),
            mat, offset,
            1, new XY(0, 0), new XY(1, 0));
      // short dist inside start and 1 to the side
      OneNodeEdgeDist_Test(new XY(-1, 0.1), new XY(0, 0), new XY(0, 1),
            mat, offset,
            1, new XY(0, 0.1), new XY(1, 0));
      // short dist inside start and 1 to the other side
      OneNodeEdgeDist_Test(new XY(1, 0.1), new XY(0, 0), new XY(0, 1),
            mat, offset,
            1, new XY(0, 0.1), new XY(-1, 0));
      // same, but further into edge
      OneNodeEdgeDist_Test(new XY(1, 0.3), new XY(0, 0), new XY(0, 1),
            mat, offset,
            1, new XY(0, 0.3), new XY(-1, 0));
      // just before end
      OneNodeEdgeDist_Test(new XY(1, 0.9), new XY(0, 0), new XY(0, 1),
            mat, offset,
            1, new XY(0, 0.9), new XY(-1, 0));
      // level with end and to the side
      OneNodeEdgeDist_Test(new XY(1, 1), new XY(0, 0), new XY(0, 1),
            mat, offset,
            1, new XY(0, 1), new XY(-1, 0));
      // level with end and to the other side
      OneNodeEdgeDist_Test(new XY(-1, 1), new XY(0, 0), new XY(0, 1),
            mat, offset,
            1, new XY(0, 1), new XY(1, 0));
      // 1 after end and to the side
      OneNodeEdgeDist_Test(new XY(1, 2), new XY(0, 0), new XY(0, 1),
            mat, offset,
            ROOT2, new XY(0, 1), new XY(-1 / ROOT2, -1 / ROOT2));
      // 1 after end and to the other side
      OneNodeEdgeDist_Test(new XY(-1, 2), new XY(0, 0), new XY(0, 1),
            mat, offset,
            ROOT2, new XY(0, 1), new XY(1 / ROOT2, -1 / ROOT2));
      // 1 after end
      OneNodeEdgeDist_Test(new XY(0, 2), new XY(0, 0), new XY(0, 1),
            mat, offset,
            1, new XY(0, 1), new XY(0, -1));
   }

   @Test
   public void testRemoveRandom()
   {
      {
         ArrayList<Integer> c = new ArrayList<>();

         c.add(1);
         c.add(2);
         c.add(3);

         assertTrue(c.contains(1));

         ArrayList<Integer> d = new ArrayList<>();

         d.add(1);
         d.add(2);
         d.add(3);

         Random r1 = new Random(1);
         Random r2 = new Random(1);

         int i = Util.removeRandom(r1, c);

         assertTrue(i < 4 && i > 0);
         assertEquals(2, c.size());
         assertFalse(c.contains(i));

         int j = Util.removeRandom(r2, d);

         assertTrue(i == j);


         i = Util.removeRandom(r1, c);

         assertTrue(i < 4 && i > 0);
         assertEquals(1, c.size());
         assertFalse(c.contains(i));

         j = Util.removeRandom(r2, d);

         assertTrue(i == j);


         i = Util.removeRandom(r1, c);

         assertTrue(i < 4 && i > 0);
         assertEquals(0, c.size());
         assertFalse(c.contains(i));

         j = Util.removeRandom(r2, d);

         assertTrue(i == j);

         assertNull(Util.removeRandom(r1, c));
      }
   }

   @Test
   public void testRelativeAngle()
   {
                              // absolute angle:
      XY l1 = new XY(1, 0);   // Pi/2
      XY l2 = new XY(0, 1);   // 0
      XY l3 = new XY(-1, 0);  // 3Pi/2
      XY l4 = new XY(0, -1);  // Pi

      assertEquals(0, Util.relativeAngle(l1, l1), 1e-6);
      assertEquals(0, Util.relativeAngle(l2, l2), 1e-6);
      assertEquals(0, Util.relativeAngle(l3, l3), 1e-6);
      assertEquals(0, Util.relativeAngle(l4, l4), 1e-6);

      assertEquals(Math.PI / 2, Util.relativeAngle(l2, l1), 1e-6);
      assertEquals(Math.PI / 2, Util.relativeAngle(l3, l2), 1e-6);
      assertEquals(Math.PI / 2, Util.relativeAngle(l4, l3), 1e-6);
      assertEquals(Math.PI / 2, Util.relativeAngle(l1, l4), 1e-6);

      assertEquals(Math.PI, Util.relativeAngle(l1, l3), 1e-6);
      assertEquals(Math.PI, Util.relativeAngle(l2, l4), 1e-6);
      assertEquals(Math.PI, Util.relativeAngle(l3, l1), 1e-6);
      assertEquals(Math.PI, Util.relativeAngle(l4, l2), 1e-6);

      assertEquals(Math.PI * 3 / 2, Util.relativeAngle(l1, l2), 1e-6);
      assertEquals(Math.PI * 3 / 2, Util.relativeAngle(l2, l3), 1e-6);
      assertEquals(Math.PI * 3 / 2, Util.relativeAngle(l3, l4), 1e-6);
      assertEquals(Math.PI * 3 / 2, Util.relativeAngle(l4, l1), 1e-6);

      XY l5 = new XY(Math.sin(1), Math.cos(1));

      assertEquals(1, Util.relativeAngle(l2, l5), 1e-6);
      assertEquals(1 + Math.PI / 2, Util.relativeAngle(l3, l5), 1e-6);
      assertEquals(1 + Math.PI, Util.relativeAngle(l4, l5), 1e-6);
      assertEquals(1 + Math.PI * 3 / 2, Util.relativeAngle(l1, l5), 1e-6);

      // any old angle
      XY any = new XY(1.23, 3.21);
      assertEquals(Math.PI / 2, Util.relativeAngle(any, any.rot90()), 1e-6);
      assertEquals(Math.PI, Util.relativeAngle(any, any.rot90().rot90()), 1e-6);
      assertEquals(Math.PI *3 / 2, Util.relativeAngle(any, any.rot270()), 1e-6);
   }

   // ------------------

   private Node makeNodeAt(double x, double y)
   {
      return makeRadiusNodeAt(x, y, 0.0);
   }

   private Node makeRadiusNodeAt(double x, double y, @SuppressWarnings("SameParameterValue") double radius)
   {
      Node ret = new Node("", "", "", radius);

      ret.setPos(new XY(x, y));

      return ret;
   }

   private DirectedEdge makeEdge(Node n1, Node n2)
   {
      return new DirectedEdge(n1, n2, 0, 0, 0);
   }
}
