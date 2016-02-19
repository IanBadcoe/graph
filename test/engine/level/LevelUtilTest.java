package engine.level;

import engine.Matrix2D;
import engine.OrderedPair;
import engine.XY;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

public class LevelUtilTest
{
   @Test
   public void testUnitEdgeForce()
   {
      // between min and max should be zero and no distortion
      for(int i = 0; i <= 10; i++)
      {
         double l = 10.0 + i;

         OrderedPair<Double, Double> ret = LevelUtil.unitEdgeForce(l, 10.0, 20.0);

         assertEquals(0.0, ret.Second, 0.0);
         assertEquals(1.0, ret.First, 0.0);
      }

      {
         // at 10% compression, force should be -0.1 and distortion 0.9
         OrderedPair<Double, Double> ret = LevelUtil.unitEdgeForce(9.0, 10.0, 20.0);

         assertEquals(-0.1, ret.Second, 1e-6);
         assertEquals(0.9, ret.First, 0.0);
      }

      {
         // at 10% stretch, force should be 0.1 and distortion 1.1
         OrderedPair<Double, Double> ret = LevelUtil.unitEdgeForce(22.0, 10.0, 20.0);

         assertEquals(0.1, ret.Second, 1e-6);
         assertEquals(1.1, ret.First, 0.0);
      }
   }

   @Test
   public void testUnitNodeForce()
   {
      {
         OrderedPair<Double, Double> ret = LevelUtil.unitNodeForce(1.0, 2.0);

         // 50% compression and 50% repulsion force
         assertEquals(0.5, ret.First, 0);
         assertEquals(-0.5, ret.Second, 0);
      }

      {
         OrderedPair<Double, Double> ret = LevelUtil.unitNodeForce(2.0, 2.0);

         // 0% compression and no force
         assertEquals(0, ret.First, 0);
         assertEquals(0, ret.Second, 0);
      }

      {
         OrderedPair<Double, Double> ret = LevelUtil.unitNodeForce(4.0, 2.0);

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
         LevelUtil.nodeEdgeDist(xy2, xy1, xy1);
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
      LevelUtil.NEDRet vals = LevelUtil.nodeEdgeDistDetailed(h_n, h_es, h_ee);

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

         int i = LevelUtil.removeRandom(r1, c);

         assertTrue(i < 4 && i > 0);
         assertEquals(2, c.size());
         assertFalse(c.contains(i));

         int j = LevelUtil.removeRandom(r2, d);

         assertTrue(i == j);


         i = LevelUtil.removeRandom(r1, c);

         assertTrue(i < 4 && i > 0);
         assertEquals(1, c.size());
         assertFalse(c.contains(i));

         j = LevelUtil.removeRandom(r2, d);

         assertTrue(i == j);


         i = LevelUtil.removeRandom(r1, c);

         assertTrue(i < 4 && i > 0);
         assertEquals(0, c.size());
         assertFalse(c.contains(i));

         j = LevelUtil.removeRandom(r2, d);

         assertTrue(i == j);

         assertNull(LevelUtil.removeRandom(r1, c));
      }
   }
}
