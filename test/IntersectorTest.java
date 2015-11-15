import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by badcoei on 12/11/2015.
 */
public class IntersectorTest
{

   @Test
   public void testUnion() throws Exception
   {

   }

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

      Intersector.buildAnnotationChains(curves,
            forward_annotations_map);

      for(Curve c : curves)
      {
         assertNotNull(forward_annotations_map.get(c));
         assertNotNull(forward_annotations_map.get(c));
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

      assertTrue(Util.clockAwareAngleCompare(curves1.get(0).endParam(), curves1.get(1).startParam(), 1e-6));
      assertTrue(Util.clockAwareAngleCompare(curves1.get(1).endParam(), curves1.get(2).startParam(), 1e-6));
      assertTrue(Util.clockAwareAngleCompare(curves1.get(2).endParam(), curves1.get(0).startParam(), 1e-6));
      assertTrue(Util.clockAwareAngleCompare(curves2.get(0).endParam(), curves2.get(1).startParam(), 1e-6));
      assertTrue(Util.clockAwareAngleCompare(curves2.get(1).endParam(), curves2.get(2).startParam(), 1e-6));
      assertTrue(Util.clockAwareAngleCompare(curves2.get(2).endParam(), curves2.get(0).startParam(), 1e-6));
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

      assertTrue(Util.clockAwareAngleCompare(curves1.get(0).endParam(), curves1.get(1).startParam(), 1e-6));
      assertTrue(Util.clockAwareAngleCompare(curves1.get(1).endParam(), curves1.get(0).startParam(), 1e-6));
      assertTrue(Util.clockAwareAngleCompare(curves2.get(0).endParam(), curves2.get(1).startParam(), 1e-6));
      assertTrue(Util.clockAwareAngleCompare(curves2.get(1).endParam(), curves2.get(0).startParam(), 1e-6));
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

      assertTrue(Util.clockAwareAngleCompare(curves1.get(0).endParam(), curves1.get(0).startParam(), 1e-6));
      assertTrue(Util.clockAwareAngleCompare(curves2.get(0).endParam(), curves2.get(0).startParam(), 1e-6));
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
         assertTrue(Util.clockAwareAngleCompare(curves1.get(i).endParam(), curves1.get(next_i).startParam(), 1e-6));
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
      HashMap<Curve, Intersector.AnnotatedCurve> reverse_annotations_map = new HashMap<>();

      Intersector.buildAnnotationChains(curves1,
            forward_annotations_map);

      Intersector.buildAnnotationChains(curves2,
            forward_annotations_map);

      HashMap<Curve, Intersector.Splice> startSpliceMap = new HashMap<>();
      HashMap<Curve, Intersector.Splice> endSpliceMap = new HashMap<>();

      Intersector.findSplices(curves1, curves2,
            forward_annotations_map,
            endSpliceMap,
            1e-6);

      // two splices, with two in and two out curves each
      assertEquals(4, startSpliceMap.size());
      assertEquals(4, endSpliceMap.size());

      HashSet<Intersector.Splice> unique = new HashSet<>();
      unique.addAll(startSpliceMap.values());
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
                     new Random(1));

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
                     new Random(1));

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
                     new Random(1));

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
}