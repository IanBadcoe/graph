import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class IntersectorTest
{

   class Fake extends Curve
   {
      Fake(String name)
      {
         super(0, 1);

         Name = name;
      }

      public final String Name;

      @Override
      public XY computePos(double m_start_param)
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
         // we're only going to want to compare unique objects for the test cases
         return this == o;
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
   public void testBuildAnnotationChains() throws Exception
   {
      ArrayList<Curve> curves = new ArrayList<>();

      Curve ca = new Fake("a");
      Curve cb = new Fake("b");
      Curve cc = new Fake("c");
      Curve cd = new Fake("d");
      Curve ce = new Fake("e");
      curves.add(ca);
      curves.add(cb);
      curves.add(cc);
      curves.add(cd);
      curves.add(ce);

      HashMap<Curve, Intersector.AnnotatedCurve> forward_annotations_map = new HashMap<>();

      Intersector.buildAnnotationChains(curves, 1,
            forward_annotations_map);

      for(Curve c : curves)
      {
         assertNotNull(forward_annotations_map.get(c));
         assertNotNull(forward_annotations_map.get(c));
         assertEquals(1, forward_annotations_map.get(c).LoopNumber);
      }

      assertEquals(cb, forward_annotations_map.get(ca).Next.Curve);
      assertEquals(cc, forward_annotations_map.get(cb).Next.Curve);
      assertEquals(cd, forward_annotations_map.get(cc).Next.Curve);
      assertEquals(ce, forward_annotations_map.get(cd).Next.Curve);
      assertEquals(ca, forward_annotations_map.get(ce).Next.Curve);
   }

   @Test
   public void testSplitCurvesAtIntersections_TwoCirclesTwoPoints()
   {
      // circles meet at two points
      Curve cc1 = new CircleCurve(new XY(), 1);
      Curve cc2 = new CircleCurve(new XY(1, 0), 1);

      ArrayList<Curve> curves1 = new ArrayList<>();
      curves1.add(cc1);

      ArrayList<Curve> curves2 = new ArrayList<>();
      curves2.add(cc2);

      Intersector.splitCurvesAtIntersections(curves1, curves2, 1e-6);

      // we cut each curve twice, technically we could anneal the original curve across its
      // join at 2PI -> 0.0 but we don't currently try anything clever like that
      assertEquals(3, curves1.size());
      assertEquals(3, curves2.size());

      assertTrue(curves1.get(0).endPos().equals(curves1.get(1).startPos(), 1e-6));
      assertTrue(curves1.get(1).endPos().equals(curves1.get(2).startPos(), 1e-6));
      assertTrue(curves1.get(2).endPos().equals(curves1.get(0).startPos(), 1e-6));
      assertTrue(curves2.get(0).endPos().equals(curves2.get(1).startPos(), 1e-6));
      assertTrue(curves2.get(1).endPos().equals(curves2.get(2).startPos(), 1e-6));
      assertTrue(curves2.get(2).endPos().equals(curves2.get(0).startPos(), 1e-6));

      assertTrue(Util.clockAwareAngleCompare(curves1.get(0).EndParam, curves1.get(1).StartParam, 1e-6));
      assertTrue(Util.clockAwareAngleCompare(curves1.get(1).EndParam, curves1.get(2).StartParam, 1e-6));
      assertTrue(Util.clockAwareAngleCompare(curves1.get(2).EndParam, curves1.get(0).StartParam, 1e-6));
      assertTrue(Util.clockAwareAngleCompare(curves2.get(0).EndParam, curves2.get(1).StartParam, 1e-6));
      assertTrue(Util.clockAwareAngleCompare(curves2.get(1).EndParam, curves2.get(2).StartParam, 1e-6));
      assertTrue(Util.clockAwareAngleCompare(curves2.get(2).EndParam, curves2.get(0).StartParam, 1e-6));
   }

   @Test
   public void testSplitCurvesAtIntersections_TwoCirclesOnePoint()
   {
      // circles meet at one point
      Curve cc1 = new CircleCurve(new XY(), 1);
      Curve cc2 = new CircleCurve(new XY(2, 0), 1);

      ArrayList<Curve> curves1 = new ArrayList<>();
      curves1.add(cc1);

      ArrayList<Curve> curves2 = new ArrayList<>();
      curves2.add(cc2);

      Intersector.splitCurvesAtIntersections(curves1, curves2, 1e-6);

      assertEquals(2, curves1.size());
      assertEquals(2, curves2.size());

      assertTrue(curves1.get(0).endPos().equals(curves1.get(1).startPos(), 1e-6));
      assertTrue(curves1.get(1).endPos().equals(curves1.get(0).startPos(), 1e-6));
      assertTrue(curves2.get(0).endPos().equals(curves2.get(1).startPos(), 1e-6));
      assertTrue(curves2.get(1).endPos().equals(curves2.get(0).startPos(), 1e-6));

      assertTrue(Util.clockAwareAngleCompare(curves1.get(0).EndParam, curves1.get(1).StartParam, 1e-6));
      assertTrue(Util.clockAwareAngleCompare(curves1.get(1).EndParam, curves1.get(0).StartParam, 1e-6));
      assertTrue(Util.clockAwareAngleCompare(curves2.get(0).EndParam, curves2.get(1).StartParam, 1e-6));
      assertTrue(Util.clockAwareAngleCompare(curves2.get(1).EndParam, curves2.get(0).StartParam, 1e-6));
   }

   @Test
   public void testSplitCurvesAtIntersections_SameCircleTwice()
   {
      // same circle twice
      Curve cc1 = new CircleCurve(new XY(), 1);
      Curve cc2 = new CircleCurve(new XY(), 1);

      ArrayList<Curve> curves1 = new ArrayList<>();
      curves1.add(cc1);

      ArrayList<Curve> curves2 = new ArrayList<>();
      curves2.add(cc2);

      Intersector.splitCurvesAtIntersections(curves1, curves2, 1e-6);

      assertEquals(1, curves1.size());
      assertEquals(1, curves2.size());

      assertTrue(curves1.get(0).endPos().equals(curves1.get(0).startPos(), 1e-6));
      assertTrue(curves2.get(0).endPos().equals(curves2.get(0).startPos(), 1e-6));

      assertTrue(Util.clockAwareAngleCompare(curves1.get(0).EndParam, curves1.get(0).StartParam, 1e-6));
      assertTrue(Util.clockAwareAngleCompare(curves2.get(0).EndParam, curves2.get(0).StartParam, 1e-6));
   }

   @Test
   public void testSplitCurvesAtIntersections_OneCircleHitsBreakInOther()
   {
      // one circle hits existing break in other
      Curve cc1 = new CircleCurve(new XY(), 1);

      ArrayList<Curve> curves1 = new ArrayList<>();
      curves1.add(cc1);

      ArrayList<Curve> curves2 = new ArrayList<>();

      for(double a = 0; a < Math.PI * 2; a += 0.1)
      {
         Curve cc2 = new CircleCurve(new XY(Math.sin(a), Math.cos(a)), 1);

         curves2.add(cc2);
      }

      Intersector.splitCurvesAtIntersections(curves1, curves2, 1e-6);

      for(int i = 0; i < curves1.size(); i++)
      {
         int next_i = (i + 1) % curves1.size();
         assertTrue(Util.clockAwareAngleCompare(curves1.get(i).EndParam, curves1.get(next_i).StartParam, 1e-6));
         assertTrue(curves1.get(i).endPos().equals(curves1.get(next_i).startPos(), 1e-6));
      }
   }

   @Test
   public void testFindSplices()
   {
      Curve cc1 = new CircleCurve(new XY(), 1);
      Curve cc2 = new CircleCurve(new XY(1, 0), 1);

      ArrayList<Curve> curves1 = new ArrayList<>();
      curves1.add(cc1);

      ArrayList<Curve> curves2 = new ArrayList<>();
      curves2.add(cc2);

      Intersector.splitCurvesAtIntersections(curves1, curves2, 1e-6);

      HashMap<Curve, Intersector.AnnotatedCurve> forward_annotations_map = new HashMap<>();

      Intersector.buildAnnotationChains(curves1, 1,
            forward_annotations_map);

      Intersector.buildAnnotationChains(curves2, 2,
            forward_annotations_map);

      HashMap<Curve, Intersector.Splice> endSpliceMap = new HashMap<>();

      Intersector.findSplices(curves1, curves2,
            forward_annotations_map,
            endSpliceMap,
            1e-6);

      // two splices, with two in and two out curves each
      assertEquals(4, endSpliceMap.size());

      HashSet<Intersector.Splice> unique = new HashSet<>();
      unique.addAll(endSpliceMap.values());

      assertEquals(2, unique.size());

      for(Intersector.Splice s : unique)
      {
         HashSet<Intersector.AnnotatedCurve> l1fset = new HashSet<>();
         HashSet<Intersector.AnnotatedCurve> l2fset = new HashSet<>();

         Intersector.AnnotatedCurve acl1f = s.Loop1Out;
         Intersector.AnnotatedCurve acl2f = s.Loop2Out;

         for(int i = 0; i < 4; i++)
         {
            l1fset.add(acl1f);
            l2fset.add(acl2f);

            acl1f = acl1f.Next;
            acl2f = acl2f.Next;
         }

         // although we stepped four times, the loops are of length 3 and we
         // shouldn't have found any more AnnotationCurves
         assertEquals(3, l1fset.size());
         assertEquals(3, l2fset.size());

         // loops of AnnotationCurves should be unique
         assertTrue(Collections.disjoint(l1fset, l2fset));

         HashSet<Curve> l1fcset = l1fset.stream().map(x -> x.Curve).collect(Collectors.toCollection(HashSet::new));
         HashSet<Curve> l2fcset = l2fset.stream().map(x -> x.Curve).collect(Collectors.toCollection(HashSet::new));

         // and l1 and l2 don't contain any of the same curves
         assertTrue(Collections.disjoint(l1fcset, l2fcset));
      }
   }

   @Test
   public void testTryFindIntersections()
   {
      // one circle, expect [0,] 1, 0
      {
         CircleCurve cc = new CircleCurve(new XY(), 5);

         HashSet<Curve> all_curves = new HashSet<>();
         all_curves.add(cc);

         HashSet<XY> curve_joints = new HashSet<>();
         curve_joints.add(cc.startPos());

         ArrayList<OrderedPair<Curve, Integer>> ret =
               Intersector.tryFindIntersections(
                     new XY(0, -5),
                     all_curves,
                     curve_joints,
                     10, 1e-6,
                     new Random(1),
                     false);

         assertNotNull(ret);
         assertEquals(2, ret.size());
         assertEquals(cc, ret.get(0).First);
         assertEquals(1, (int)ret.get(0).Second);
         assertEquals(cc, ret.get(1).First);
         assertEquals(0, (int)ret.get(1).Second);
      }

      // one circle, built from two half-circles, should still work
      // expect [0,] 1, 0
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 5, 0, Math.PI);
         CircleCurve cc2 = new CircleCurve(new XY(), 5, Math.PI, 2 * Math.PI);

         HashSet<Curve> all_curves = new HashSet<>();
         all_curves.add(cc1);
         all_curves.add(cc2);

         LineCurve lc = new LineCurve(new XY(-10, 0), new XY(1, 0), 20);

         ArrayList<OrderedPair<Curve, Integer>> ret =
               Intersector.tryFindCurveIntersections(
                     lc,
                     all_curves);

         assertNotNull(ret);
         assertEquals(2, ret.size());
         assertEquals(cc2, ret.get(0).First);
         assertEquals(1, (int)ret.get(0).Second);
         assertEquals(cc1, ret.get(1).First);
         assertEquals(0, (int)ret.get(1).Second);
      }

      // two concentric circles, expect [0,] 1, 2, 1, 0
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 5);
         CircleCurve cc2 = new CircleCurve(new XY(), 3);

         HashSet<Curve> all_curves = new HashSet<>();
         all_curves.add(cc1);
         all_curves.add(cc2);

         HashSet<XY> curve_joints = new HashSet<>();
         curve_joints.add(cc1.startPos());
         curve_joints.add(cc2.startPos());

         ArrayList<OrderedPair<Curve, Integer>> ret =
               Intersector.tryFindIntersections(
                     new XY(0, 0),  // use centre to force hitting both circles
                     all_curves,
                     curve_joints,
                     10, 1e-6,
                     new Random(1),
                     false);

         assertNotNull(ret);
         assertEquals(4, ret.size());
         assertEquals(cc1, ret.get(0).First);
         assertEquals(1, (int)ret.get(0).Second);
         assertEquals(cc2, ret.get(1).First);
         assertEquals(2, (int)ret.get(1).Second);
         assertEquals(cc2, ret.get(2).First);
         assertEquals(1, (int)ret.get(2).Second);
         assertEquals(cc1, ret.get(3).First);
         assertEquals(0, (int)ret.get(3).Second);
      }

      // two concentric circles, inner one -ve, expect [0,] 1, 0, 1, 0
      {
         CircleCurve cc1 = new CircleCurve(new XY(), 5);
         CircleCurve cc2 = new CircleCurve(new XY(), 3, CircleCurve.RotationDirection.Reverse);

         HashSet<Curve> all_curves = new HashSet<>();
         all_curves.add(cc1);
         all_curves.add(cc2);

         HashSet<XY> curve_joints = new HashSet<>();
         curve_joints.add(cc1.startPos());
         curve_joints.add(cc2.startPos());

         ArrayList<OrderedPair<Curve, Integer>> ret =
               Intersector.tryFindIntersections(
                     new XY(0, 0),  // use centre to force hitting both circles
                     all_curves,
                     curve_joints,
                     10, 1e-6,
                     new Random(1),
                     false);

         assertNotNull(ret);
         assertEquals(4, ret.size());
         assertEquals(cc1, ret.get(0).First);
         assertEquals(1, (int)ret.get(0).Second);
         assertEquals(cc2, ret.get(1).First);
         assertEquals(0, (int)ret.get(1).Second);
         assertEquals(cc2, ret.get(2).First);
         assertEquals(1, (int)ret.get(2).Second);
         assertEquals(cc1, ret.get(3).First);
         assertEquals(0, (int)ret.get(3).Second);
      }
   }

   private static void checkLoop(Loop l, @SuppressWarnings("SameParameterValue") int exp_size)
   {
      assertEquals(exp_size, l.numCurves());

      XY prev_end = l.getCurves().get(l.numCurves() - 1).endPos();

      for(Curve c : l.getCurves())
      {
         assertTrue(prev_end.equals(c.startPos(), 1e-6));
         prev_end = c.endPos();

         assertTrue(c instanceof CircleCurve);
      }
   }

   @Test
   public void testUnion() throws Exception
   {
      // nothing union nothing should equal nothing
      {
         LoopSet ls1 = new LoopSet();
         LoopSet ls2 = new LoopSet();

         LoopSet ret = Intersector.union(ls1, ls2, 1e-6, new Random(1), false);

         assertNull(ret);
      }

      // something union nothing should equal something
      {
         LoopSet ls1 = new LoopSet();
         LoopSet ls2 = new LoopSet();

         Loop l1 = new Loop(new CircleCurve(new XY(), 1));
         ls1.add(l1);

         LoopSet ret = Intersector.union(ls1, ls2, 1e-6, new Random(1), false);

         assertNotNull(ret);
         assertEquals(1, ret.size());
         assertEquals(ls1, ret);
      }

      // nothing union something should equal something
      {
         LoopSet ls1 = new LoopSet();
         LoopSet ls2 = new LoopSet();

         Loop l2 = new Loop(new CircleCurve(new XY(), 1));
         ls2.add(l2);

         LoopSet ret = Intersector.union(ls1, ls2, 1e-6, new Random(1), false);

         assertNotNull(ret);
         assertEquals(1, ret.size());
         assertEquals(ls2, ret);
      }

      // union of two identical things should equal either one of them
      {
         LoopSet ls1 = new LoopSet();
         LoopSet ls2 = new LoopSet();

         Loop l1 = new Loop(new CircleCurve(new XY(), 1));
         ls1.add(l1);

         Loop l2 = new Loop(new CircleCurve(new XY(), 1));
         ls2.add(l2);

         // paranoia
         assertEquals(ls1, ls2);

         LoopSet ret = Intersector.union(ls1, ls2, 1e-6, new Random(1), false);

         assertNotNull(ret);
         assertEquals(1, ret.size());
         assertEquals(ls1, ret);
      }

      // union of two overlapping circles should be one two-part curve
      {
         LoopSet ls1 = new LoopSet();
         LoopSet ls2 = new LoopSet();

         Loop l1 = new Loop(new CircleCurve(new XY(), 1));
         ls1.add(l1);

         Loop l2 = new Loop(new CircleCurve(new XY(1, 0), 1));
         ls2.add(l2);

         LoopSet ret = Intersector.union(ls1, ls2, 1e-6, new Random(1), false);

         assertNotNull(ret);
         assertEquals(1, ret.size());
         assertEquals(2, ret.get(0).numCurves());
         Curve c1 = ret.get(0).getCurves().get(0);
         Curve c2 = ret.get(0).getCurves().get(1);
         assertTrue(c1 instanceof CircleCurve);
         assertTrue(c2 instanceof CircleCurve);

         CircleCurve cc1 = (CircleCurve)c1;
         CircleCurve cc2 = (CircleCurve)c2;

         // same radii
         assertEquals(1, cc1.Radius, 1e-6);
         assertEquals(1, cc2.Radius, 1e-6);

         // same direction
         assertEquals(CircleCurve.RotationDirection.Forwards, cc1.Rotation);
         assertEquals(CircleCurve.RotationDirection.Forwards, cc2.Rotation);

         // joined end-to-end
         assertTrue(cc1.startPos().equals(cc2.endPos(), 1e-6));
         assertTrue(cc2.startPos().equals(cc1.endPos(), 1e-6));

         CircleCurve left = cc1.Position.X < cc2.Position.X ? cc1 : cc2;
         CircleCurve right = cc1.Position.X > cc2.Position.X ? cc1 : cc2;

         assertEquals(new XY(0, 0), left.Position);
         assertEquals(new XY(1, 0), right.Position);

         assertEquals(Math.PI * 2 * 5 / 12, left.StartParam, 1e-6);
         assertEquals(Math.PI * 2 * 13 / 12, left.EndParam, 1e-6);

         assertEquals(Math.PI * 2 * 11 / 12, right.StartParam, 1e-6);
         assertEquals(Math.PI * 2 * 19 / 12, right.EndParam, 1e-6);
      }

      // union of two overlapping circles with holes in
      // should be one two-part curve around outside and two two-part curves in the interior
      {
         LoopSet ls1 = new LoopSet();
         LoopSet ls2 = new LoopSet();

         Loop l1a = new Loop(new CircleCurve(new XY(), 1));
         Loop l1b = new Loop(new CircleCurve(new XY(), 0.3, CircleCurve.RotationDirection.Reverse));
         ls1.add(l1a);
         ls1.add(l1b);

         Loop l2a = new Loop(new CircleCurve(new XY(1, 0), 1));
         Loop l2b = new Loop(new CircleCurve(new XY(1, 0), 0.3, CircleCurve.RotationDirection.Reverse));
         ls2.add(l2a);
         ls2.add(l2b);

         LoopSet ret = Intersector.union(ls1, ls2, 1e-6, new Random(1), false);

         assertNotNull(ret);
         assertEquals(3, ret.size());

         checkLoop(ret.get(0), 2);
         checkLoop(ret.get(1), 2);
         checkLoop(ret.get(2), 2);
      }
   }
}
