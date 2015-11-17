import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.stream.Collectors;

public class Intersector
{
   // only non-private for unit-testing
   static class AnnotatedCurve
   {
      final Curve Curve;
      AnnotatedCurve Next;

      AnnotatedCurve(Curve curve)
      {
         Curve = curve;
      }

      @Override
      public boolean equals(Object o)
      {
         if (!(o instanceof AnnotatedCurve))
            return false;

         AnnotatedCurve aco = (AnnotatedCurve)o;

         return Curve == aco.Curve;
      }

      @Override
      public int hashCode()
      {
         return Curve.hashCode();
      }
   }

   // only non-private for unit-testing
   static class Splice
   {
      Splice(AnnotatedCurve l1out, AnnotatedCurve l2out)
      {
         Loop1Out = l1out;
         Loop2Out = l2out;
      }

      final AnnotatedCurve Loop1Out;
      final AnnotatedCurve Loop2Out;
   }

   public static LoopSet union(LoopSet ls1, LoopSet ls2, @SuppressWarnings("SameParameterValue") double tol, Random random)
   {
      if (ls1.size() == 0 && ls2.size() == 0)
         return null;

      // simple case, also covers us being handed the same instance twice
      if (ls1.equals(ls2))
         return ls1;

      ArrayList<ArrayList<Curve>> working_loops1 =
            ls1.stream()
                  .map(l -> new ArrayList<>(l.getCurves()))
                  .collect(Collectors.toCollection(ArrayList::new));

      ArrayList<ArrayList<Curve>> working_loops2 =
            ls2.stream()
                  .map(l -> new ArrayList<>(l.getCurves()))
                  .collect(Collectors.toCollection(ArrayList::new));

      // split all curves that intersect
      for(ArrayList<Curve> alc1 : working_loops1)
      {
         for (ArrayList<Curve> alc2 : working_loops2)
         {
            splitCurvesAtIntersections(alc1, alc2, tol);
         }
      }

      HashMap<Curve, AnnotatedCurve> forward_annotations_map = new HashMap<>();

      // build forward and reverse chains of annotation-curves around both loops
      for(ArrayList<Curve> alc1 : working_loops1)
      {
         buildAnnotationChains(alc1, forward_annotations_map);
      }

      for (ArrayList<Curve> alc2 : working_loops2)
      {
         buildAnnotationChains(alc2, forward_annotations_map);
      }

      // now find all the splices
      // did not do this in loops above, because of complexity of some of them crossing loop-ends and some of them
      // lying on existing curve boundaries

      HashMap<Curve, Splice> endSpliceMap = new HashMap<>();

      for(ArrayList<Curve> alc1 : working_loops1)
      {
         for (ArrayList<Curve> alc2 : working_loops2)
         {
            findSplices(alc1, alc2,
                  forward_annotations_map,
                  endSpliceMap,
                  tol);
         }
      }

      // build a set of all curves and another of all AnnotatedCurves (Open)
      // 1) pick a line that crosses at least one open AnnotationCurve
      // 2) find all curve intersections along the line (taking care to ignore tangent touchings and not duplicate
      //    an intersection if it occurs at a curve-curve joint)
      // 3) calculate a count of crossings so that when we cross a curve inwards (crosses us from the right
      //    as we look down our line) we increase the count and when we cross a curve outwards we decrease it
      // 4) label the intervals on the line between the intersections with the count in that interval
      // 5) only counts from zero -> 1 or from 1 -> zero are interesting
      // 6) for 0 -> 1 or 1 -> 0 crossings we are entering the output shape
      // 6a) if the forward AnnoationCurve is open
      // 6b) follow the curve forwards, removing AnnotationCurves from open
      // 6c) when we get to a splice, find the sharpest left turn (another forwards AnnotationCurve)
      // 6d) until we reach our start curve
      // 6e) add all these curves as a loop in the output
      // 6f (this will be a
      // 7) for +ve -> +ve or -ve -> -ve crossings we walk backwards around the loop just removing
      //    the annotation edges from open
      // 8) until there are no open AnnotationEdges

      HashSet<Curve> all_curves = new HashSet<>();

      working_loops1.forEach(all_curves::addAll);
      working_loops2.forEach(all_curves::addAll);

      HashSet<AnnotatedCurve> open = new HashSet<>();

      open.addAll(forward_annotations_map.values());

      HashSet<XY> curve_joints = all_curves.stream()
            .map(Curve::startPos)
            .collect(Collectors.toCollection(HashSet::new));

      // bounding box allows us to create cutting lines that definitely exceed all loop boundaries
      Box bounds = all_curves.stream().map(Curve::boundingBox).reduce(new Box(), (b, a) -> a.union(b));

      // but all we need from that is the max length in the box
      Double diameter = bounds.diagonal().length();

      LoopSet ret = new LoopSet();

      while(open.size() > 0)
      {
         ArrayList<OrderedPair<Curve, Integer>> intervals = null;
         for(int i = 0; i < 5; i++)
         {
            // get any curve to help pick an intersection line
            Curve c = open.stream().skip(random.nextInt(open.size())).findFirst().get().Curve;

            XY mid_point = c.computePos((c.startParam() + c.endParam()) / 2);

            intervals = tryFindIntersections(mid_point, all_curves, curve_joints, diameter, tol, random);

            if (intervals != null)
               break;
         }

         // failure, don't really expect this has have had multiple tries and it
         // shouldn't be so hard to find a good cutting line
         if (intervals == null)
            return null;

         // now use the intervals to decide what to do with the AnnotationEdges
         int prev_crossings = 0;

         for(OrderedPair<Curve, Integer> intersection : intervals)
         {
            int crossings = intersection.Second;

            AnnotatedCurve ac_current = forward_annotations_map.get(intersection.First);

            if (open.contains(ac_current))
            {
               // three cases, 0 -> 1, 1 -> 0 and anything else
               if (prev_crossings == 0 && crossings == 1
                     || prev_crossings == 1 && crossings == 0)
               {
                  // take a loop that is part of the perimeter
                  ret.add(extractLoop(
                        open,
                        ac_current,
                        endSpliceMap,
                        LoopMode.Keep));
               }
               else
               {
                  // discard a loop that isn't part of the perimeter
                  extractLoop(
                        open,
                        ac_current,
                        endSpliceMap,
                        LoopMode.Discard);
               }
            }

            prev_crossings = crossings;
         }
      }

      if (ret.size() == 0)
         return null;

      return ret;
   }

   enum LoopMode
   {
      Discard,    // turn as sharply right as possible at each splice, do not return found loop
      Keep        // turn as sharply left as possible at each splice, tidy and return found loop
   }

   @SuppressWarnings("WeakerAccess")
   static Loop extractLoop(
         HashSet<AnnotatedCurve> open,
         AnnotatedCurve start_ac,
         HashMap<Curve, Splice> endSpliceMap,
         LoopMode loop_mode)
   {
      AnnotatedCurve curr_ac = start_ac;

      ArrayList<Curve> found_curves = new ArrayList<>();

      do
      {
         Curve c = curr_ac.Curve;
         found_curves.add(c);
         open.remove(curr_ac);

         // look for a splice that ends this curve
         Splice splice = endSpliceMap.get(c);

         // if no splice we just follow the chain of ACs
         if (splice == null)
         {
            curr_ac = curr_ac.Next;
         }
         else
         {
            AnnotatedCurve ac1 = splice.Loop1Out;
            AnnotatedCurve ac2 = splice.Loop2Out;
            Curve c1 = ac1.Curve;
            Curve c2 = ac2.Curve;

            // we want the sharpest left corner we can find
            // which we do by taking the direction backwards on the incoming edge and looking for the smallest angle
            // relative to that (because we normalise angles between 0 and 2pi, any right turns will be > pi)
            XY rev_curr_dir = c.tangent(c.endParam()).negate();
            XY dir1 = c1.tangent(c1.startParam());
            XY dir2 = c2.tangent(c2.startParam());

            double ang1 = Util.relativeAngle(rev_curr_dir, dir1);
            double ang2 = Util.relativeAngle(rev_curr_dir, dir2);

            if (Math.abs(ang1 - ang2) < 1e-6)
            {
               // if we're tangent at the point of contact
               // then we need to look a small distance in to the curves to see which way they are bending
               // just going to guess that .1 is a good distance here (not a total guess, for lines makes no
               // difference and for circles is .1 rad = 6 degrees which should be enough

               dir1 = c1.tangent(c1.startParam());
               dir2 = c2.tangent(c2.startParam());

               ang1 = Util.relativeAngle(rev_curr_dir, dir1);
               ang2 = Util.relativeAngle(rev_curr_dir, dir2);
            }

            if (loop_mode == LoopMode.Keep)
            {
               if (ang1 < ang2)
               {
                  curr_ac = ac1;
               }
               else
               {
                  curr_ac = ac2;
               }
            }
            else
            {
               if (ang1 > ang2)
               {
                  curr_ac = ac1;
               }
               else
               {
                  curr_ac = ac2;
               }
            }
         }
      }
      while (curr_ac != start_ac);

      if (loop_mode == LoopMode.Discard)
         return null;

      // because input cyclic curves have a joint at 12 o'clock
      // and nothing before here removes that, we can have splits we don't need
      // between otherwise identical curves
      //
      // this merges those back together
      tidyLoop(found_curves);

      return new Loop(found_curves);
   }

   private static void tidyLoop(ArrayList<Curve> curves)
   {
      int prev = curves.size() - 1;
      Curve c_prev = curves.get(prev);

      for(int i = 0; i < curves.size();)
      {
         Curve c_here = curves.get(i);

         // if we get down to (or start with) only one curve, don't try to merge it with itself
         if (c_here == c_prev)
            break;

         Curve merged = c_prev.merge(c_here);

         if (merged != null)
         {
            curves.set(prev, merged);
            curves.remove(i);
            c_prev = merged;

            // if we've removed curves[i] then we'll look at the new
            // curves[i] next pass and prev remains the same
         }
         else
         {
            // move everything on one
            prev = i;
            i++;
            c_prev = c_here;
         }
      }
   }


   // non-private for unit-testing only
   static ArrayList<OrderedPair<Curve, Integer>>
         tryFindIntersections(
         XY mid_point,
         HashSet<Curve> all_curves,
         HashSet<XY> curve_joints,
         double diameter, double tol,
         Random random)
   {
      for(int i = 0; i < 5; i++)
      {
         double rand_ang = random.nextDouble() * Math.PI * 2;
         double dx = Math.sin(rand_ang);
         double dy = Math.cos(rand_ang);

         XY direction = new XY(dx, dy);

         XY start = mid_point.minus(direction.multiply(diameter));

         LineCurve lc = new LineCurve(start, direction, 2 * diameter);

         if (!lineClearsPoints(lc, curve_joints, tol))
            continue;

         ArrayList<OrderedPair<Curve, Integer>> ret =
               tryFindCurveIntersections(lc, all_curves);

         if (ret != null)
            return ret;
      }

      return null;
   }

   private static boolean lineClearsPoints(LineCurve lc, HashSet<XY> curve_joints, double tol)
   {
      for(XY pnt : curve_joints)
      {
         if (Math.abs(pnt.minus(lc.Position).dot(lc.Direction.rot90())) < tol)
            return false;
      }

      return true;
   }

   // returns a set of <Curve, int> pairs sorted by distance down the line
   // at which the intersection occurs
   //
   // the curve is the curve intersecting and the integer is the
   // crossing number after we have passed that intersection
   //
   // the crossing number is implicitly zero before the first intersection
   //
   // non-private only for unit-testing
   static ArrayList<OrderedPair<Curve, Integer>>
   tryFindCurveIntersections(
            LineCurve lc,
            HashSet<Curve> all_curves)
   {
      HashSet<OrderedTriplet<Curve, Double, Double>> intersecting_curves = new HashSet<>();

      for(Curve c : all_curves)
      {
         ArrayList<OrderedPair<Double, Double>> intersections =
               Util.curveCurveIntersect(lc, c);

         if (intersections == null)
            continue;

         for(OrderedPair<Double, Double> intersection : intersections)
         {
            double dot = c.tangent(intersection.Second).dot(lc.Direction.rot270());

            // chicken out and scrap any line that has a glancing contact with a curve
            // a bit more than .1 degrees
            if (Math.abs(dot) < 0.001)
               return null;

            intersecting_curves.add(new OrderedTriplet<>(c, intersection.First, dot));
         }
      }

      // sort by distance down the line
      ArrayList<OrderedTriplet<Curve, Double, Double>> ordered =
            intersecting_curves.stream().sorted((x, y) -> (int)Math.signum(x.Second - y.Second))
                  .collect(Collectors.toCollection(ArrayList::new));

      int crossings = 0;

      ArrayList<OrderedPair<Curve, Integer>> ret = new ArrayList<>();

      for(OrderedTriplet<Curve, Double, Double> entry : ordered)
      {
         if (entry.Third > 0)
         {
            crossings++;
         }
         else
         {
            crossings--;
         }

         ret.add(new OrderedPair<>(entry.First, crossings));
      }

      return ret;
   }

   static void findSplices(ArrayList<Curve> working_loop1, ArrayList<Curve> working_loop2,
                                  HashMap<Curve, AnnotatedCurve> forward_annotations_map,
                                  HashMap<Curve, Splice> endSpliceMap,
                                  double tol)
   {
      Curve l1prev = working_loop1.get(working_loop1.size() - 1);

      for(Curve l1curr : working_loop1)
      {
         XY l1_cur_start_pos = l1curr.startPos();
         assert l1prev.endPos().equals(l1_cur_start_pos, 1e-6);

         Curve l2prev = working_loop2.get(working_loop2.size() - 1);

         for(Curve l2curr : working_loop2)
         {
            XY l2_cur_start_pos = l2curr.startPos();
            assert l2prev.endPos().equals(l2_cur_start_pos, 1e-6);

            if (l1_cur_start_pos.equals(l2_cur_start_pos, tol))
            {
               Splice s = new Splice(
                     forward_annotations_map.get(l1curr),
                     forward_annotations_map.get(l2curr));

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
                                     HashMap<Curve, AnnotatedCurve> forward_annotations_map)
   {
      Curve prev = null;

      for(Curve curr : curves)
      {
         AnnotatedCurve ac_forward_curr = new AnnotatedCurve(curr);

         if (prev != null)
         {
            AnnotatedCurve ac_forward_prev = forward_annotations_map.get(prev);

            ac_forward_prev.Next = ac_forward_curr;
         }

         forward_annotations_map.put(curr, ac_forward_curr);

         prev = curr;
      }

      Curve first = curves.get(0);

      AnnotatedCurve ac_forward_first = forward_annotations_map.get(first);
      AnnotatedCurve ac_forward_last = forward_annotations_map.get(prev);

      ac_forward_last.Next = ac_forward_first;
   }
}
