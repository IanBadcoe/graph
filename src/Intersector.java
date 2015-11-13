import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by badcoei on 11/11/2015.
 */
public class Intersector
{
   // only non-private for unit-testing
   static class AnnotatedCurve
   {
      AnnotatedCurve(Curve curve)
      {
         Curve = curve;
      }

      final Curve Curve;
      boolean UsedForwards;
      boolean UsedBackwards;

      AnnotatedCurve Next;
   }

   // only non-private for unit-testing
   static class Splice
   {
      Splice(AnnotatedCurve l1in, AnnotatedCurve l1out, AnnotatedCurve l2in, AnnotatedCurve l2out)
      {
         Loop1In = l1in;
         Loop1Out = l1out;
         Loop2In = l2in;
         Loop2Out = l2out;
      }

      AnnotatedCurve Loop1In;
      AnnotatedCurve Loop1Out;
      AnnotatedCurve Loop2In;
      AnnotatedCurve Loop2Out;
   }

   static class IntersectRet
   {
      IntersectRet(Loop splitLoop1, Loop splitLoop2,
                   HashMap<Curve, Splice> curveStartSplices,
                   HashMap<Curve, Splice> curveEndSplices)
      {
         SplitLoop1 = splitLoop1;
         SplitLoop2 = splitLoop2;

         CurveStartSpliceMap = curveStartSplices;
         CurveEndSpliceMap = curveEndSplices;
      }

      final Loop SplitLoop1;
      final Loop SplitLoop2;

      final HashMap<Curve, Splice> CurveStartSpliceMap;
      final HashMap<Curve, Splice> CurveEndSpliceMap;
   }

   static LoopSet union(LoopSet ls1, LoopSet ls2, double tol)
   {
      // simple case, also covers us being handed the same instance twice
      if (ls1.equals(ls2))
         return ls1;

      ArrayList<ArrayList<Curve>> working_loops1 = new ArrayList<>();
      for(Loop l : ls1)
      {
         working_loops1.add(new ArrayList<>(l.getCurves()));
      }

      ArrayList<ArrayList<Curve>> working_loops2 = new ArrayList<>();
      for(Loop l : ls2)
      {
         working_loops2.add(new ArrayList<>(l.getCurves()));
      }

      // split all curves that intersect
      for(ArrayList<Curve> alc1 : working_loops1)
      {
         for (ArrayList<Curve> alc2 : working_loops2)
         {
            splitCurvesAtIntersections(alc1, alc2, tol);
         }
      }

      HashMap<Curve, AnnotatedCurve> forward_annotations_map = new HashMap<>();
      HashMap<Curve, AnnotatedCurve> reverse_annotations_map = new HashMap<>();

      // build forward and reverse chains of annotation-curves around both loops
      for(ArrayList<Curve> alc1 : working_loops1)
      {
         buildAnnotationChains(alc1, forward_annotations_map, reverse_annotations_map);
      }

      for (ArrayList<Curve> alc2 : working_loops2)
      {
         buildAnnotationChains(alc2, forward_annotations_map, reverse_annotations_map);
      }

//      Loop splitl1 = new Loop(working_loop1);
//      Loop splitl2 = new Loop(working_loop2);

      // now find all the splices
      // did not do this in loops above, because of complexity of some of them crossing loop-ends and some of them
      // lying on existing curve boundaries

      HashMap<Curve, Splice> startSpliceMap = new HashMap<>();
      HashMap<Curve, Splice> endSpliceMap = new HashMap<>();

      for(ArrayList<Curve> alc1 : working_loops1)
      {
         for (ArrayList<Curve> alc2 : working_loops2)
         {
            findSplices(alc1, alc2,
                  forward_annotations_map,
                  reverse_annotations_map,
                  startSpliceMap, endSpliceMap,
                  tol);
         }
      }

      // Now follow edges between splices, turning as sharply right as possible
      // to find non-divided cells and make temporary loops of them

      HashSet<Splice> open_splices = new HashSet<>();
      open_splices.add(startSpliceMap.values().stream().findFirst().get());

      // we visit each edge twice, once forwards and once backwards
      while(open_splices.size() > 0)
      {
         Splice s = open_splices.stream().findFirst().get();

         AnnotatedCurve ac = findUnvisitedOutCurve(s);

         if (ac == null)
         {
            open_splices.remove(s);

            continue;
         }

//         Splice next_s = findOtherEnd

      }

      return null;
   }

   public static void findSplices(ArrayList<Curve> working_loop1, ArrayList<Curve> working_loop2,
                                  HashMap<Curve, AnnotatedCurve> forward_annotations_map,
                                  HashMap<Curve, AnnotatedCurve> reverse_annotations_map,
                                  HashMap<Curve, Splice> startSpliceMap,
                                  HashMap<Curve, Splice> endSpliceMap,
                                  double tol)
   {
      Curve l1prev = working_loop1.get(working_loop1.size() - 1);

      for(int i = 0; i < working_loop1.size(); i++)
      {
         Curve l1curr = working_loop1.get(i);
         XY l1_cur_start_pos = l1curr.startPos();
         assert l1prev.endPos().equals(l1_cur_start_pos, 1e-6);

         Curve l2prev = working_loop2.get(working_loop2.size() - 1);

         for(int j = 0; j < working_loop2.size(); j++)
         {
            Curve l2curr = working_loop2.get(j);
            XY l2_cur_start_pos = l2curr.startPos();
            assert l2prev.endPos().equals(l2_cur_start_pos, 1e-6);

            if (l1_cur_start_pos.equals(l2_cur_start_pos, tol))
            {
               Splice s = new Splice(
                     reverse_annotations_map.get(l1prev),
                     forward_annotations_map.get(l1curr),
                     reverse_annotations_map.get(l2prev),
                     forward_annotations_map.get(l2curr));

               startSpliceMap.put(l1curr, s);
               startSpliceMap.put(l2curr, s);
               endSpliceMap.put(l1prev, s);
               endSpliceMap.put(l2prev, s);
            }

            l2prev = l2curr;
         }

         l1prev = l1curr;
      }
   }

   // non-private only for unit-tests
   static void splitCurvesAtIntersections(ArrayList<Curve> working_loop1, ArrayList<Curve> working_loop2, double tol)
   {
      for(int i = 0; i < working_loop1.size(); i++)
      {
         Curve c1 = working_loop1.get(i);
         for(int j = 0; j < working_loop2.size(); j++)
         {
            Curve c2 = working_loop2.get(j);

            boolean any_splits;

            do
            {
               any_splits = false;

               ArrayList<OrderedPair<Double, Double>> ret = Util.curveCurveIntersect(c1, c2);

               if (ret == null)
                  break;

               // we only count up in case the earlier entries fall close to existing splits and
               // are ignored, otherwise if the first intersection causes a split
               for (int k = 0; k < ret.size() && !any_splits; k++)
               {
                  OrderedPair<Double, Double> split_points = ret.get(k);

                  // if we are far enough from existing splits
                  if (c1.paramCoordinateDist(c1.startParam(), split_points.First) > tol
                        && c1.paramCoordinateDist(c1.endParam(), split_points.First) > tol)
                  {
                     any_splits = true;

                     Curve c1split1 = c1.cloneWithChangedParams(c1.startParam(), split_points.First);
                     Curve c1split2 = c1.cloneWithChangedParams(split_points.First, c1.endParam());

                     working_loop1.set(i, c1split1);
                     working_loop1.add(i + 1, c1split2);

                     // once we've split once any second split could be in either new curve
                     // and also any further comparisons of the original c1 now need to be done separately on the two
                     // fragments
                     //
                     // so all-in-all simplest seems to be to pretend the two earlier fragments were where we were
                     // all along and re-start this (c1, c2) pair using them
                     //
                     // this will lead to a little repetition, as c1split2 will be checked against working_list2 items
                     // at indices < j, but hardly seems worth worrying about for small-ish curve numbers with few splits
                     c1 = c1split1;
                  }

                  // if we are far enough from existing splits
                  if (c2.paramCoordinateDist(c2.startParam(), split_points.Second) > tol
                        && c2.paramCoordinateDist(c2.endParam(), split_points.Second) > tol)
                  {
                     any_splits = true;

                     Curve c2split1 = c2.cloneWithChangedParams(c2.startParam(), split_points.Second);
                     Curve c2split2 = c2.cloneWithChangedParams(split_points.Second, c2.endParam());

                     working_loop2.set(j, c2split1);
                     working_loop2.add(j + 1, c2split2);

                     // see comment in previous if-block
                     c2 = c2split1;
                  }
               }
            } while (any_splits);
         }
      }
   }

   // only non-private for unit-testing
   static void buildAnnotationChains(ArrayList<Curve> curves,
                                     HashMap<Curve, AnnotatedCurve> forward_annotations_map,
                                     HashMap<Curve, AnnotatedCurve> reverse_annotations_map)
   {
      Curve prev = null;

      for(Curve curr : curves)
      {
         AnnotatedCurve ac_forward_curr = new AnnotatedCurve(curr);
         AnnotatedCurve ac_reverse_curr = new AnnotatedCurve(curr);

         if (prev != null)
         {
            AnnotatedCurve ac_forward_prev = forward_annotations_map.get(prev);
            AnnotatedCurve ac_reverse_prev = reverse_annotations_map.get(prev);

            ac_forward_prev.Next = ac_forward_curr;
            ac_reverse_curr.Next = ac_reverse_prev;
         }

         forward_annotations_map.put(curr, ac_forward_curr);
         reverse_annotations_map.put(curr, ac_reverse_curr);

         prev = curr;
      }

      Curve first = curves.get(0);

      AnnotatedCurve ac_forward_first = forward_annotations_map.get(first);
      AnnotatedCurve ac_forward_last = forward_annotations_map.get(prev);

      ac_forward_last.Next = ac_forward_first;

      AnnotatedCurve ac_reverse_first = reverse_annotations_map.get(first);
      AnnotatedCurve ac_reverse_last = reverse_annotations_map.get(prev);

      ac_reverse_first.Next = ac_reverse_last;
   }

   private static AnnotatedCurve findUnvisitedOutCurve(Splice s)
   {
      if (!s.Loop1In.UsedBackwards)
      {
         return s.Loop1In;
      }
      else if (!s.Loop1Out.UsedForwards)
      {
         return s.Loop1Out;
      }
      else if (!s.Loop2In.UsedBackwards)
      {
         return s.Loop2In;
      }
      else if (!s.Loop2Out.UsedForwards)
      {
         return s.Loop2Out;
      }

      return null;
   }
}
