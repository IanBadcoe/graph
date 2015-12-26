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
      Util.NEDRet vals = Util.nodeEdgeForceDist(h_n, h_es, h_ee);

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

   class Fake extends Curve
   {
      Fake()
      {
         super(0, 1);
      }

      @Override
      public XY computePosInner(double m_start_param)
      {
         return null;
      }

      @Override
      public int hashCode()
      {
         return 0;
      }

      @Override
      public boolean equals(Object o)
      {
         return false;
      }

      @Override
      public Double findParamForPoint(XY first, double tol)
      {
         return null;
      }

      @Override
      public Curve cloneWithChangedParams(double start, double end)
      {
         return null;
      }

      @Override
      public Box boundingBox()
      {
         return null;
      }

      @Override
      public XY tangent(Double second)
      {
         return null;
      }

      @Override
      public Curve merge(Curve c_after)
      {
         return null;
      }

      @Override
      public double length()
      {
         return 0;
      }

      @Override
      public XY computeNormal(double p)
      {
         return null;
      }

      @Override
      public double paramCoordinateDist(double p1, double p2)
      {
         return 0;
      }
   }

   @Test
   public void testCurveCurveIntersect_Exception()
   {
      CircleCurve cc = new CircleCurve(new XY(), 10);

      Fake f = new Fake();

      {
         boolean thrown = false;

         try
         {
            Util.curveCurveIntersect(cc, f);
         }
         catch(UnsupportedOperationException uoe)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         boolean thrown = false;

         try
         {
            Util.curveCurveIntersect(f, cc);
         }
         catch(UnsupportedOperationException uoe)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }
   }

   @Test
   public void testCurveCurveIntersect_Circles()
   {
      // two circles separated by their common radius intersect at +/- 60 degrees
      CircleCurve cc1 = new CircleCurve(new XY(), 1);
      CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1);

      ArrayList<OrderedPair<Double, Double>> ret =
            Util.curveCurveIntersect(cc1, cc2);

      assertNotNull(ret);

      assertEquals(2, ret.size());

      checkParamsUnknownOrder("testCurveCurveIntersect_Circles : 1",
            cc1,
            ret.get(0).First, ret.get(1).First,
            Math.PI * 2 * 5 / 12, Math.PI * 2 * 1 / 12);

      checkParamsUnknownOrder("testCurveCurveIntersect_Circles : 2",
            cc2,
            ret.get(0).Second, ret.get(1).Second,
            Math.PI * 2 * 11 / 12, Math.PI * 2 * 7 / 12);
   }

   @Test
   public void testCircleCircleIntersect_None()
   {
      // same object
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);

         assertNull(Util.curveCurveIntersect(cc1, cc1));
      }

      // same circle
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(), 1);

         assertNull(Util.curveCurveIntersect(cc1, cc2));
      }

      // concentric
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(), 2);

         assertNull(Util.curveCurveIntersect(cc1, cc2));
         assertNull(Util.curveCurveIntersect(cc2, cc1));
      }

      // non-concentric, still inside
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(0, 0.5), 2);

         assertNull(Util.curveCurveIntersect(cc1, cc2));
         assertNull(Util.curveCurveIntersect(cc2, cc1));
      }

      // non-concentric, still inside
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(0, -0.5), 2);

         assertNull(Util.curveCurveIntersect(cc1, cc2));
         assertNull(Util.curveCurveIntersect(cc2, cc1));
      }

      // non-concentric, still inside
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(0.5, 0), 2);

         assertNull(Util.curveCurveIntersect(cc1, cc2));
         assertNull(Util.curveCurveIntersect(cc2, cc1));
      }

      // non-concentric, still inside
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(-0.5, 0), 2);

         assertNull(Util.curveCurveIntersect(cc1, cc2));
         assertNull(Util.curveCurveIntersect(cc2, cc1));
      }

      // outside
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(5, 0), 2);

         assertNull(Util.curveCurveIntersect(cc1, cc2));
         assertNull(Util.curveCurveIntersect(cc2, cc1));
      }

      // outside
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(-50, 0), 2);

         assertNull(Util.curveCurveIntersect(cc1, cc2));
         assertNull(Util.curveCurveIntersect(cc2, cc1));
      }

      // outside
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(0, 500), 2);

         assertNull(Util.curveCurveIntersect(cc1, cc2));
         assertNull(Util.curveCurveIntersect(cc2, cc1));
      }

      // outside
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(0, -5000), 2);

         assertNull(Util.curveCurveIntersect(cc1, cc2));
         assertNull(Util.curveCurveIntersect(cc2, cc1));
      }
   }

   @Test
   public void testCircleCircleIntersect_One()
   {
      // one point of contact
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(0, 3), 2);

         ArrayList<OrderedPair<Double, Double>> ret =
               Util.curveCurveIntersect(cc1, cc2);

         assertEquals(1, ret.size());
         assertEquals(0, ret.get(0).First, 0);
         assertEquals(Math.PI, ret.get(0).Second, 0);
      }

      // one point of contact
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(0, -4), 3);

         ArrayList<OrderedPair<Double, Double>> ret =
               Util.curveCurveIntersect(cc1, cc2);

         assertEquals(1, ret.size());
         assertEquals(Math.PI, ret.get(0).First, 0);
         assertEquals(0, ret.get(0).Second, 0);
      }

      // one point of contact
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(1.01, 0), 0.01);

         ArrayList<OrderedPair<Double, Double>> ret =
               Util.curveCurveIntersect(cc1, cc2);

         assertEquals(1, ret.size());
         assertEquals(Math.PI / 2, ret.get(0).First, 0);
         assertEquals(Math.PI * 3 / 2, ret.get(0).Second, 0);
      }

      // one point of contact
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(-3001, 0), 3000);

         ArrayList<OrderedPair<Double, Double>> ret =
               Util.curveCurveIntersect(cc1, cc2);

         assertEquals(1, ret.size());
         assertEquals(Math.PI * 3 / 2, ret.get(0).First, 0);
         assertEquals(Math.PI / 2, ret.get(0).Second, 0);
      }
   }

   @Test
   public void testCircleCircleIntersect_One_SelectedReverse()
   {
      // one point of contact
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, CircleCurve.RotationDirection.Reverse);
         CircleCurve cc2 = new CircleCurve(new XY(0, 3), 2);

         ArrayList<OrderedPair<Double, Double>> ret =
               Util.curveCurveIntersect(cc1, cc2);

         assertEquals(1, ret.size());
         assertEquals(0, ret.get(0).First, 0);
         assertEquals(Math.PI, ret.get(0).Second, 0);
      }

      // one point of contact
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1);
         CircleCurve cc2 = new CircleCurve(new XY(0, 3), 2, CircleCurve.RotationDirection.Reverse);

         ArrayList<OrderedPair<Double, Double>> ret =
               Util.curveCurveIntersect(cc1, cc2);

         assertEquals(1, ret.size());
         assertEquals(0, ret.get(0).First, 0);
         assertEquals(Math.PI, ret.get(0).Second, 0);
      }

      // one point of contact
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, CircleCurve.RotationDirection.Reverse);
         CircleCurve cc2 = new CircleCurve(new XY(0, 3), 2, CircleCurve.RotationDirection.Reverse);

         ArrayList<OrderedPair<Double, Double>> ret =
               Util.curveCurveIntersect(cc1, cc2);

         assertEquals(1, ret.size());
         assertEquals(0, ret.get(0).First, 0);
         assertEquals(Math.PI, ret.get(0).Second, 0);
      }
   }

   @Test
   public void testCircleCircleIntersect_Two()
   {
      cci_two(CircleCurve.RotationDirection.Forwards, CircleCurve.RotationDirection.Forwards);
      cci_two(CircleCurve.RotationDirection.Forwards, CircleCurve.RotationDirection.Reverse);
      cci_two(CircleCurve.RotationDirection.Reverse, CircleCurve.RotationDirection.Forwards);
      cci_two(CircleCurve.RotationDirection.Reverse, CircleCurve.RotationDirection.Reverse);
   }

   private void cci_two(CircleCurve.RotationDirection r1, CircleCurve.RotationDirection r2)
   {
      for(double ang = 0.0; ang < Math.PI * 2; ang += 0.01)
      {
         // two circles separated by their common radius intersect at +/- 60 degrees
         CircleCurve cc1 = new CircleCurve(new XY(), 1, r1);
         CircleCurve cc2 = new CircleCurve(new XY(Math.sin(ang), Math.cos(ang)), 1, r2);

         ArrayList<OrderedPair<Double, Double>> ret =
               Util.curveCurveIntersect(cc1, cc2);

         assertNotNull(ret);

         assertEquals(2, ret.size());

         double ang1 = r1 == CircleCurve.RotationDirection.Forwards ? ang : -ang;
         double ang2 = r2 == CircleCurve.RotationDirection.Forwards ? ang : -ang;

         double exp_ang = fixAngle(ang1 - Math.PI / 3);
         double other_exp_ang = fixAngle(ang1 + Math.PI / 3);

         checkParamsUnknownOrder("ang: " + ang,
               cc1,
               ret.get(0).First, ret.get(1).First,
               exp_ang, other_exp_ang);

         exp_ang = fixAngle(ang2 - Math.PI * 4 / 3);
         other_exp_ang = fixAngle(ang2 + Math.PI * 4 / 3);

         checkParamsUnknownOrder("ang: " + ang,
               cc2,
               ret.get(0).Second, ret.get(1).Second,
               exp_ang, other_exp_ang);
      }
   }

   @Test
   public void testCircleCircleIntersect_RespectParams()
   {
      // full circle on left, 3/4 on right with upper-left missing
      // should hit once at 150 degrees, 270 degrees resp.
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI * 2);
         CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1, 0, Math.PI * 3 / 2);

         ArrayList<OrderedPair<Double, Double>> ret =
               Util.curveCurveIntersect(cc1, cc2);

         assertEquals(1, ret.size());
         assertEquals(Math.PI * 2 * 5 / 12, ret.get(0).First, 0);
         assertEquals(Math.PI * 2 * 7 / 12, ret.get(0).Second, 0);
      }

      // full circle on left, 3/4 on right with lower-left missing
      // should hit once at 30 degrees, 330 degrees resp.
      {

         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI * 2);
         CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1, Math.PI * 3 / 2, Math.PI);

         ArrayList<OrderedPair<Double, Double>> ret =
               Util.curveCurveIntersect(cc1, cc2);

         assertEquals(1, ret.size());
         assertEquals(Math.PI * 2 * 1 / 12, ret.get(0).First, 1e-6);
         assertEquals(Math.PI * 2 * 11 / 12, ret.get(0).Second, 1e-6);
      }

      // full circle on right, 3/4 on left with upper-right missing
      // should hit once at 150 degrees, 270 degrees resp.
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, Math.PI / 2, Math.PI * 2);
         CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1, 0, Math.PI * 2);

         ArrayList<OrderedPair<Double, Double>> ret =
               Util.curveCurveIntersect(cc1, cc2);

         assertEquals(1, ret.size());
         assertEquals(Math.PI * 2 * 5 / 12, ret.get(0).First, 0);
         assertEquals(Math.PI * 2 * 7 / 12, ret.get(0).Second, 0);
      }

      // full circle on right, 3/4 on left with lower-right missing
      // should hit once at 30 degrees, 330 degrees resp.
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, Math.PI, Math.PI / 2);
         CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1, 0, Math.PI * 2);

         ArrayList<OrderedPair<Double, Double>> ret =
               Util.curveCurveIntersect(cc1, cc2);

         assertEquals(1, ret.size());
         assertEquals(Math.PI * 2 * 13 / 12, ret.get(0).First, 1e-6);
         assertEquals(Math.PI * 2 * 11 / 12, ret.get(0).Second, 1e-6);
      }

      // full circle on right, 3/4 on left with lower-left missing
      // should hit twice at both points mentioned above
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, Math.PI * 3 / 2, Math.PI);
         CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1, 0, Math.PI * 2);

         ArrayList<OrderedPair<Double, Double>> ret =
               Util.curveCurveIntersect(cc1, cc2);

         assertEquals(2, ret.size());

         checkParamsUnknownOrder("RespectParams 1.1",
               cc1,
               ret.get(0).First, ret.get(1).First,
               Math.PI * 2 * 13 / 12, Math.PI * 2 * 17 / 12);

         checkParamsUnknownOrder("RespectParams 1.2",
               cc2,
               ret.get(0).Second, ret.get(1).Second,
               Math.PI * 2 * 7 / 12, Math.PI * 2 * 11 / 12);
      }

      // full circle on left, 3/4 on right with lower-right missing
      // should hit twice at both points mentioned above
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI * 2);
         CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1, Math.PI, Math.PI / 2);

         ArrayList<OrderedPair<Double, Double>> ret =
               Util.curveCurveIntersect(cc1, cc2);

         assertEquals(2, ret.size());

         checkParamsUnknownOrder("RespectParams 2.1",
               cc1,
               ret.get(0).First, ret.get(1).First,
               Math.PI * 2 * 1 / 12, Math.PI * 2 * 5 / 12);

         checkParamsUnknownOrder("RespectParams 2.2",
               cc2,
               ret.get(0).Second, ret.get(1).Second,
               Math.PI * 2 * 7 / 12, Math.PI * 2 * 11 / 12);
      }

      // 3/4 circle on left with upper right missing,
      // 3/4 on right with lower-left missing
      // should miss
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, Math.PI / 2, Math.PI * 2);
         CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1, Math.PI * 3 / 2, Math.PI);

         ArrayList<OrderedPair<Double, Double>> ret =
               Util.curveCurveIntersect(cc1, cc2);

         assertNull(ret);
      }
   }

   @Test
   public void testCircleCircleIntersect_RespectParams_SelectedReverse()
   {
      // full circle on right, 3/4 on left with upper-right missing
      // should hit once at 150 degrees, 270 degrees resp.
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI * 3 / 2, CircleCurve.RotationDirection.Reverse);
         CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1, 0, Math.PI * 2, CircleCurve.RotationDirection.Reverse);

         ArrayList<OrderedPair<Double, Double>> ret =
               Util.curveCurveIntersect(cc1, cc2);

         assertEquals(1, ret.size());
         assertEquals(Math.PI * 2 * 7 / 12, ret.get(0).First, 0);
         assertEquals(Math.PI * 2 * 5 / 12, ret.get(0).Second, 0);
      }

      // 3/4 circle on left with upper right missing,
      // 3/4 on right with lower-left missing
      // should miss
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI * 3 / 2,
               CircleCurve.RotationDirection.Reverse);
         CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1, Math.PI, Math.PI / 2,
               CircleCurve.RotationDirection.Reverse);

         ArrayList<OrderedPair<Double, Double>> ret =
               Util.curveCurveIntersect(cc1, cc2);

         assertNull(ret);
      }

      // full circle on left, 3/4 on right with lower-right missing
      // should hit twice at both points mentioned above
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 1, 0, Math.PI * 2,
               CircleCurve.RotationDirection.Reverse);
         CircleCurve cc2 = new CircleCurve(new XY(1, 0), 1, Math.PI * 3 / 2, Math.PI,
               CircleCurve.RotationDirection.Reverse);

         ArrayList<OrderedPair<Double, Double>> ret =
               Util.curveCurveIntersect(cc1, cc2);

         assertEquals(2, ret.size());

         checkParamsUnknownOrder("RespectParams 2.1",
               cc1,
               ret.get(0).First, ret.get(1).First,
               Math.PI * 2 * 11 / 12, Math.PI * 2 * 7 / 12);

         checkParamsUnknownOrder("RespectParams 2.2",
               cc2,
               ret.get(0).Second, ret.get(1).Second,
               Math.PI * 2 * 17 / 12, Math.PI * 2 * 13 / 12);
      }
   }

   @Test
   public void testLineLineIntersect()
   {
      // only need to sanity check as is using same internal routine as edge intersection
      // which is tested thoroughly above

      LineCurve lc1 = new LineCurve(new XY(), new XY(1, 0), 4);
      LineCurve lc2 = new LineCurve(new XY(2, -2), new XY(0, 1), 4);
      LineCurve lc3a = new LineCurve(new XY(0, -2), new XY(1, 0), 1, 3);
      LineCurve lc3b = new LineCurve(new XY(-1, -2), new XY(1, 0), 1, 3);
      LineCurve lc3c = new LineCurve(new XY(-2, -2), new XY(1, 0), 1, 3);

      // crossing
      {
         ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(lc1, lc2);

         assertNotNull(ret);
         assertEquals(1, ret.size());
         assertEquals(2, ret.get(0).First, 1e-6);
         assertEquals(2, ret.get(0).Second, 1e-6);
      }

      // parallel
      {
         ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(lc1, lc3a);

         assertNull(ret);
      }

      // crossing at one end
      {
         ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(lc3a, lc2);

         assertNotNull(ret);
         assertEquals(1, ret.size());
         assertEquals(2, ret.get(0).First, 1e-6);
         assertEquals(0, ret.get(0).Second, 1e-6);
      }

      // crossing end to end
      {
         ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(lc3b, lc2);

         assertNotNull(ret);
         assertEquals(1, ret.size());
         assertEquals(3, ret.get(0).First, 1e-6);
         assertEquals(0, ret.get(0).Second, 1e-6);
      }

      // off the end
      {
         ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(lc3c, lc2);

         assertNull(ret);
      }
   }

   @Test
   public void testLineCircleIntersect_None()
   {
      // line outside in various orientations
      {
         CircleCurve cc = new CircleCurve(new XY(), 1);
         LineCurve lc1 = new LineCurve(new XY(3, 0), new XY(1, 0), 1);
         LineCurve lc2 = new LineCurve(new XY(3, 0), new XY(-1, 0), 1);
         LineCurve lc3 = new LineCurve(new XY(3, 0), new XY(0, 1), 1);
         LineCurve lc4 = new LineCurve(new XY(3, 0), new XY(0, -1), 1);

         assertNull(Util.curveCurveIntersect(cc, lc1));
         assertNull(Util.curveCurveIntersect(cc, lc2));
         assertNull(Util.curveCurveIntersect(cc, lc3));
         assertNull(Util.curveCurveIntersect(cc, lc4));

         assertNull(Util.curveCurveIntersect(lc1, cc));
         assertNull(Util.curveCurveIntersect(lc2, cc));
         assertNull(Util.curveCurveIntersect(lc3, cc));
         assertNull(Util.curveCurveIntersect(lc4, cc));
      }

      // line inside in various orientations
      {
         CircleCurve cc = new CircleCurve(new XY(), 2);
         LineCurve lc1 = new LineCurve(new XY(0, 0), new XY(1, 0), 1);
         LineCurve lc2 = new LineCurve(new XY(0, 0), new XY(-1, 0), 1);
         LineCurve lc3 = new LineCurve(new XY(0, 0), new XY(0, 1), 1);
         LineCurve lc4 = new LineCurve(new XY(0, 0), new XY(0, -1), 1);

         assertNull(Util.curveCurveIntersect(cc, lc1));
         assertNull(Util.curveCurveIntersect(cc, lc2));
         assertNull(Util.curveCurveIntersect(cc, lc3));
         assertNull(Util.curveCurveIntersect(cc, lc4));

         assertNull(Util.curveCurveIntersect(lc1, cc));
         assertNull(Util.curveCurveIntersect(lc2, cc));
         assertNull(Util.curveCurveIntersect(lc3, cc));
         assertNull(Util.curveCurveIntersect(lc4, cc));
      }
   }

   @Test
   public void testLineCircleIntersect_One()
   {
      // line inside penetrating outwards
      {
         CircleCurve cc = new CircleCurve(new XY(), 2);
         LineCurve lc1 = new LineCurve(new XY(0, 0), new XY(1, 0), 4);
         LineCurve lc2 = new LineCurve(new XY(0, 0), new XY(-1, 0), 40);
         LineCurve lc3 = new LineCurve(new XY(0, 0), new XY(0, 1), 400);
         LineCurve lc4 = new LineCurve(new XY(0, 0), new XY(0, -1), 4000);

         {
            ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(cc, lc1);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(Math.PI / 2, ret.get(0).First, 1e-6);
            assertEquals(2, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(lc1, cc);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(2, ret.get(0).First, 1e-6);
            assertEquals(Math.PI / 2, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(cc, lc2);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(Math.PI * 3 / 2, ret.get(0).First, 1e-6);
            assertEquals(2, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(lc2, cc);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(2, ret.get(0).First, 1e-6);
            assertEquals(Math.PI * 3 / 2, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(cc, lc3);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(0, ret.get(0).First, 1e-6);
            assertEquals(2, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(lc3, cc);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(2, ret.get(0).First, 1e-6);
            assertEquals(0, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(cc, lc4);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(Math.PI, ret.get(0).First, 1e-6);
            assertEquals(2, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(lc4, cc);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(2, ret.get(0).First, 1e-6);
            assertEquals(Math.PI, ret.get(0).Second, 1e-6);
         }
      }

      // line outside penetrating outwards
      {
         CircleCurve cc = new CircleCurve(new XY(), 2);
         LineCurve lc1 = new LineCurve(new XY(100, 0), new XY(-1, 0), 100);
         LineCurve lc2 = new LineCurve(new XY(-10, 0), new XY(1, 0), 10);
         LineCurve lc3 = new LineCurve(new XY(0, 10000), new XY(0, -1), 10000);
         LineCurve lc4 = new LineCurve(new XY(0, -1000), new XY(0, 1), 1000);

         {
            ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(cc, lc1);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(Math.PI / 2, ret.get(0).First, 1e-6);
            assertEquals(98, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(lc1, cc);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(98, ret.get(0).First, 1e-6);
            assertEquals(Math.PI / 2, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(cc, lc2);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(Math.PI * 3 / 2, ret.get(0).First, 1e-6);
            assertEquals(8, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(lc2, cc);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(8, ret.get(0).First, 1e-6);
            assertEquals(Math.PI * 3 / 2, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(cc, lc3);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(0, ret.get(0).First, 1e-6);
            assertEquals(9998, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(lc3, cc);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(9998, ret.get(0).First, 1e-6);
            assertEquals(0, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(cc, lc4);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(Math.PI, ret.get(0).First, 1e-6);
            assertEquals(998, ret.get(0).Second, 1e-6);
         }

         {
            ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(lc4, cc);

            assertNotNull(ret);
            assertEquals(1, ret.size());
            assertEquals(998, ret.get(0).First, 1e-6);
            assertEquals(Math.PI, ret.get(0).Second, 1e-6);
         }
      }
   }

   @Test
   public void testLineCircleIntersect_Two()
   {
      CircleCurve cc = new CircleCurve(new XY(), 1);

      for(double d = -2; d < 2; d += 0.01)
      {
         {
            LineCurve lc = new LineCurve(new XY(d, -2), new XY(0, 1), 4);

            ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(lc, cc);

            if (ret == null)
            {
               assertTrue(Math.abs(d) > 1 - 1e-6);
            }
            else if (ret.size() == 1)
            {
               assertEquals(1, d, 1e-6);
               assertEquals(0, lc.computePos(ret.get(0).First).X, 1e-6);
               assertEquals(0, cc.computePos(ret.get(0).Second).X, 1e-6);
            }
            else
            {
               assertTrue(d < 1);
               XY exp_where1 = new XY(d, Math.sqrt(1 - d * d));
               XY exp_where2 = new XY(d, -Math.sqrt(1 - d * d));

               XY obs_where1 = lc.computePos(ret.get(0).First);
               XY obs_where2 = lc.computePos(ret.get(1).First);


               assertTrue((exp_where1.equals(obs_where1, 1e-6) && exp_where2.equals(obs_where2, 1e-6))
                     || (exp_where1.equals(obs_where2, 1e-6) && exp_where2.equals(obs_where1, 1e-6)));
            }
         }

         {
            LineCurve lc = new LineCurve(new XY(-2, d), new XY(1, 0), 4);

            ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(cc, lc);

            if (ret == null)
            {
               assertTrue(Math.abs(d) > 1 - 1e-6);
            }
            else if (ret.size() == 1)
            {
               assertEquals(1, d, 1e-6);
               assertEquals(0, lc.computePos(ret.get(0).First).Y, 1e-6);
               assertEquals(0, cc.computePos(ret.get(0).Second).Y, 1e-6);
            }
            else
            {
               assertTrue(d < 1);
               XY exp_where1 = new XY(Math.sqrt(1 - d * d), d);
               XY exp_where2 = new XY(-Math.sqrt(1 - d * d), d);

               XY obs_where1 = lc.computePos(ret.get(0).Second);
               XY obs_where2 = lc.computePos(ret.get(1).Second);


               assertTrue((exp_where1.equals(obs_where1, 1e-6) && exp_where2.equals(obs_where2, 1e-6))
                     || (exp_where1.equals(obs_where2, 1e-6) && exp_where2.equals(obs_where1, 1e-6)));
            }
         }
      }
   }

   @Test
   public void testEdgeParameterOverlap() throws Exception
   {
      {
         IEdge e1 = Movable.makeEdge(new XY(0, 0), new XY(10, 0), null);
         IEdge e2 = Movable.makeEdge(new XY(0, 0), new XY(10, 0), null);

         Util.EPORet ret = Util.edgeParameterOverlap(e1.getStart(), e1.getEnd(), e2.getStart(), e2.getEnd(), 0);

         assertEquals(true, ret.Overlaps);
         assertEquals(0, ret.PStart, 1e-6);
         assertEquals(1, ret.PEnd, 1e-6);
      }

      {
         IEdge e1 = Movable.makeEdge(new XY(0, 0), new XY(10, 0), null);
         IEdge e2 = Movable.makeEdge(new XY(1, 1), new XY(11, 11), null);

         Util.EPORet ret = Util.edgeParameterOverlap(e1.getStart(), e1.getEnd(), e2.getStart(), e2.getEnd(), 0.1);

         assertEquals(true, ret.Overlaps);
         assertEquals(.1, ret.PStart, 1e-6);
         assertEquals(1, ret.PEnd, 1e-6);
      }

      {
         IEdge e1 = Movable.makeEdge(new XY(0, 0), new XY(0, 10), null);
         IEdge e2 = Movable.makeEdge(new XY(1, 1), new XY(11, 11), null);

         Util.EPORet ret = Util.edgeParameterOverlap(e1.getStart(), e1.getEnd(), e2.getStart(), e2.getEnd(), 0.01);

         assertEquals(true, ret.Overlaps);
         assertEquals(.1, ret.PStart, 1e-6);
         assertEquals(1, ret.PEnd, 1e-6);
      }

      {
         IEdge e1 = Movable.makeEdge(new XY(0, 0), new XY(0, 10), null);
         IEdge e2 = Movable.makeEdge(new XY(5, 5), new XY(6, 6), null);

         Util.EPORet ret = Util.edgeParameterOverlap(e1.getStart(), e1.getEnd(), e2.getStart(), e2.getEnd(), 0.1);

         assertEquals(true, ret.Overlaps);
         assertEquals(.5, ret.PStart, 1e-6);
         assertEquals(.6, ret.PEnd, 1e-6);
      }

      {
         IEdge e1 = Movable.makeEdge(new XY(0, 0), new XY(0, 10), null);
         IEdge e2 = Movable.makeEdge(new XY(15, 15), new XY(16, 16), null);

         Util.EPORet ret = Util.edgeParameterOverlap(e1.getStart(), e1.getEnd(), e2.getStart(), e2.getEnd(), 0.1);

         assertEquals(false, ret.Overlaps);
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

   private static double fixAngle(double w)
   {
      while(w < 0)
         w += Math.PI * 2;

      while(w > Math.PI * 2)
         w -= Math.PI * 2;

      return w;
   }

   private static void checkParamsUnknownOrder(String msg,
                                               Curve c,
                                               double pa, double pb,
                                               double expa, double expb)
   {
      double higher_expected = Math.max(expa, expb);
      double lower_expected = Math.min(expa, expb);

      double higher_seen = Math.max(pa, pb);
      double lower_seen = Math.min(pa, pb);
      assertEquals(msg, higher_expected, higher_seen, 1e-6);
      assertEquals(msg, lower_expected, lower_seen, 1e-6);

      assertTrue(c.withinParams(pa, 1e-6));
      assertTrue(c.withinParams(pb, 1e-6));
   }
}
