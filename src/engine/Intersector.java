package engine;

import java.util.*;
import java.util.stream.Collectors;

class Intersector
{
   // only non-private for unit-testing
   static class AnnotatedCurve
   {
      final Curve Curve;
      AnnotatedCurve Next;
      public final int LoopNumber;

      AnnotatedCurve(Curve curve, int loop_number)
      {
         Curve = curve;
         LoopNumber = loop_number;
      }

      @Override
      public boolean equals(Object o)
      {
         if (!(o instanceof AnnotatedCurve))
            return false;

         AnnotatedCurve aco = (AnnotatedCurve) o;

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

   public LoopSet union(LoopSet ls1, LoopSet ls2, @SuppressWarnings("SameParameterValue") double tol,
         Random random)
   {
      if (ls1.size() == 0 && ls2.size() == 0)
         return null;

      // simple case, also covers us being handed the same instance twice
      if (ls1.equals(ls2))
         return ls1;

      // used later as an id for which loop an AnnotationCurve comes from
      int loop_count = 0;

      HashMap<Integer, ArrayList<Curve>> working_loops1 = new HashMap<>();

      for (Loop l : ls1)
      {
         working_loops1.put(loop_count, new ArrayList<>(l.getCurves()));
         loop_count++;
      }

      HashMap<Integer, ArrayList<Curve>> working_loops2 = new HashMap<>();

      for (Loop l : ls2)
      {
         working_loops2.put(loop_count, new ArrayList<>(l.getCurves()));
         loop_count++;
      }


      LoopSet ret = new LoopSet();

      // first, an easy bit, any loops from either set whos bounding boxes are disjunct from all loops in the
      // other set, they have no influence on any other loops and can be simply copied inchanged into
      // the output

      HashMap<ArrayList<Curve>, Box> bound_map1 = new HashMap<>();

      for (ArrayList<Curve> alc1 : working_loops1.values())
      {
         Box bound = alc1.stream()
               .map(Curve::boundingBox)
               .reduce(new Box(), Box::union);

         bound_map1.put(alc1, bound);
      }

      HashMap<ArrayList<Curve>, Box> bound_map2 = new HashMap<>();

      for (ArrayList<Curve> alc2 : working_loops2.values())
      {
         Box bound = alc2.stream()
               .map(Curve::boundingBox)
               .reduce(new Box(), Box::union);

         bound_map2.put(alc2, bound);
      }

      removeEasyLoops(working_loops1, ret, bound_map2.values(), bound_map1);
      removeEasyLoops(working_loops2, ret, bound_map1.values(), bound_map2);

      // split all curves that intersect
      for (ArrayList<Curve> alc1 : working_loops1.values())
      {
         for (ArrayList<Curve> alc2 : working_loops2.values())
         {
            splitCurvesAtIntersections(alc1, alc2, tol);

            // has a side effect of checking that the loops are still loops
//            new engine.Loop(alc1);
//            new engine.Loop(alc2);
         }
      }

      HashMap<Curve, AnnotatedCurve> forward_annotations_map = new HashMap<>();

      // build forward and reverse chains of annotation-curves around both loops
      for (Integer i : working_loops1.keySet())
      {
         ArrayList<Curve> alc1 = working_loops1.get(i);

         buildAnnotationChains(alc1, i, forward_annotations_map);
      }

      for (Integer i : working_loops2.keySet())
      {
         ArrayList<Curve> alc1 = working_loops2.get(i);

         buildAnnotationChains(alc1, i, forward_annotations_map);
      }

      // now find all the splices
      // did not do this in loops above, because of complexity of some of them crossing loop-ends and some of them
      // lying on existing curve boundaries

      HashMap<Curve, Splice> endSpliceMap = new HashMap<>();

      for (ArrayList<Curve> alc1 : working_loops1.values())
      {
         for (ArrayList<Curve> alc2 : working_loops2.values())
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

      working_loops1.values().forEach(all_curves::addAll);
      working_loops2.values().forEach(all_curves::addAll);

      HashSet<AnnotatedCurve> open = new HashSet<>();

      open.addAll(forward_annotations_map.values());

      HashSet<XY> curve_joints = all_curves.stream()
            .map(Curve::startPos)
            .collect(Collectors.toCollection(HashSet::new));

      // bounding box allows us to create cutting lines that definitely exceed all loop boundaries
      Box bounds = all_curves.stream().map(Curve::boundingBox).reduce(new Box(), (b, a) -> a.union(b));

      // but all we need from that is the max length in the box
      Double diameter = bounds.diagonal().length();

      if (!extractInternalCurves(tol, random, forward_annotations_map, all_curves, open, curve_joints, diameter))
         return null;

      while (open.size() > 0)
      {
         AnnotatedCurve ac_current = open.stream().findFirst().get();

         // take a loop that is part of the perimeter
         ret.add(extractLoop(
               open,
               ac_current,
               endSpliceMap));
      }

      // this would imply _everything_ was internal, which is impossible without
      // a dimension warp
      assert ret.size() > 0;

      return ret;
   }

   boolean extractInternalCurves(double tol, Random random,
         HashMap<Curve, AnnotatedCurve> forward_annotations_map, HashSet<Curve> all_curves,
         HashSet<AnnotatedCurve> open, HashSet<XY> curve_joints, Double diameter)
   {
      for (Curve c : all_curves)
      {
         AnnotatedCurve ac_c = forward_annotations_map.get(c);

         if (!open.contains(ac_c))
            continue;

         XY mid_point = c.computePos((c.StartParam + c.EndParam) / 2);

         ArrayList<OrderedPair<Curve, Integer>> intervals = tryFindIntersections(mid_point, all_curves, curve_joints,
               diameter, tol, random);

         // failure, don't really expect this has have had multiple tries and it
         // shouldn't be so hard to find a good cutting line
         if (intervals == null)
            return false;

         // now use the intervals to decide what to do with the AnnotationEdges
         int prev_crossings = 0;

         for (OrderedPair<Curve, Integer> intersection : intervals)
         {
            int crossings = intersection.Second;

            AnnotatedCurve ac_intersecting = forward_annotations_map.get(intersection.First);

            if (open.contains(ac_intersecting))
            {
               // three cases, 0 -> 1, 1 -> 0 and anything else
               if ((prev_crossings != 0 || crossings != 1)
                     && (prev_crossings != 1 || crossings != 0))
               {
                  open.remove(ac_intersecting);
               }
            }

            prev_crossings = crossings;
         }
      }

      return true;
   }

   // non-private only for testing
   @SuppressWarnings("WeakerAccess")
   void removeEasyLoops(HashMap<Integer, ArrayList<Curve>> working_loops,
         LoopSet ret,
         Collection<Box> other_bounds,
         HashMap<ArrayList<Curve>, Box> bound_map)
   {
      ArrayList<Integer> keys = new ArrayList<>(working_loops.keySet());
      for (Integer i : keys)
      {
         ArrayList<Curve> alc1 = working_loops.get(i);

         Box bound = bound_map.get(alc1);

         boolean hits = false;

         for (Box b : other_bounds)
         {
            if (!bound.disjoint(b))
            {
               hits = true;
               break;
            }
         }

         if (!hits)
         {
            ret.add(new Loop(alc1));
            working_loops.remove(i);
            // won't need the bounds of this again, either
            bound_map.remove(alc1);
         }
      }
   }

   @SuppressWarnings("WeakerAccess")
   Loop extractLoop(
         HashSet<AnnotatedCurve> open,
         AnnotatedCurve start_ac,
         HashMap<Curve, Splice> endSpliceMap)
   {
      AnnotatedCurve curr_ac = start_ac;

      ArrayList<Curve> found_curves = new ArrayList<>();

      while (true)
      {
         assert open.contains(curr_ac);

         Curve c = curr_ac.Curve;
         found_curves.add(c);
         open.remove(curr_ac);

         // look for a splice that ends this curve
         Splice splice = endSpliceMap.get(c);

         // if no splice we just follow the chain of ACs
         if (splice == null)
         {
            if (curr_ac.Next == start_ac)
               break;

            curr_ac = curr_ac.Next;
         }
         else
         {
            if (splice.Loop1Out == start_ac
                  || splice.Loop2Out == start_ac)
               break;

            // at every splice, at least one of the two possible exits should be still open
            assert open.contains(splice.Loop1Out) || open.contains(splice.Loop2Out);

            if (!open.contains(splice.Loop1Out))
            {
               curr_ac = splice.Loop2Out;
            }
            else if (!open.contains(splice.Loop2Out))
            {
               curr_ac = splice.Loop1Out;
            }

            // if both exit curves are still in open (happens with osculating circles)
            // we need to take the one that puts us on a different loop
            else if (curr_ac.LoopNumber != splice.Loop1Out.LoopNumber)
            {
               curr_ac = splice.Loop1Out;
            }
            else
            {
               curr_ac = splice.Loop2Out;
            }
         }
      }

      // because input cyclic curves have a joint at 12 o'clock
      // and nothing before here removes that, we can have splits we don't need
      // between otherwise identical curves
      //
      // this merges those back together
      tidyLoop(found_curves);

      return new Loop(found_curves);
   }

   private void tidyLoop(ArrayList<Curve> curves)
   {
      int prev = curves.size() - 1;
      Curve c_prev = curves.get(prev);

      for (int i = 0; i < curves.size(); )
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
   ArrayList<OrderedPair<Curve, Integer>>
   tryFindIntersections(
         XY mid_point,
         HashSet<Curve> all_curves,
         HashSet<XY> curve_joints,
         double diameter, double tol,
         Random random)
   {
      for (int i = 0; i < 25; i++)
      {
         double rand_ang = random.nextDouble() * Math.PI * 2;
         double dx = Math.sin(rand_ang);
         double dy = Math.cos(rand_ang);

         XY direction = new XY(dx, dy);

         XY start = mid_point.minus(direction.multiply(diameter));

         LineCurve lc = new LineCurve(start, direction, 2 * diameter);

         // must use a smaller tolerance here as our curve splitting can
         // give us curves < 2 * tol long, and if we are on the midpoint of one of those
         // we can't cleat both ends by tol...
         if (!lineClearsPoints(lc, curve_joints, tol / 10))
            continue;

         ArrayList<OrderedPair<Curve, Integer>> ret =
               tryFindCurveIntersections(lc, all_curves);

         if (ret != null)
         {
            return ret;
         }
      }

      return null;
   }

   // public for testing
   public boolean lineClearsPoints(LineCurve lc, HashSet<XY> curve_joints, double tol)
   {
      for (XY pnt : curve_joints)
      {
         if (Math.abs(pnt.minus(lc.Position).dot(lc.Direction.rot90())) < tol)
            return false;
      }

      return true;
   }

   // returns a set of <engine.Curve, int> pairs sorted by distance down the line
   // at which the intersection occurs
   //
   // the curve is the curve intersecting and the integer is the
   // crossing number after we have passed that intersection
   //
   // the crossing number is implicitly zero before the first intersection
   //
   // non-private only for unit-testing
   ArrayList<OrderedPair<Curve, Integer>>
   tryFindCurveIntersections(
         LineCurve lc,
         HashSet<Curve> all_curves)
   {
      HashSet<OrderedTriplet<Curve, Double, Double>> intersecting_curves = new HashSet<>();

      for (Curve c : all_curves)
      {
         ArrayList<OrderedPair<Double, Double>> intersections =
               Util.curveCurveIntersect(lc, c);

         if (intersections == null)
            continue;

         for (OrderedPair<Double, Double> intersection : intersections)
         {
            double dot = c.tangent(intersection.Second).dot(lc.Direction.rot270());

            // chicken out and scrap any line that has a glancing contact with a curve
            // a bit more than .1 degrees
            if (Math.abs(dot) < 0.001)
               return null;

            intersecting_curves.add(new OrderedTriplet<>(c, intersection.First, dot));
         }
      }

      if (intersecting_curves.size() == 0)
         return null;

      // sort by distance down the line
      ArrayList<OrderedTriplet<Curve, Double, Double>> ordered =
            intersecting_curves.stream().sorted((x, y) -> (int) Math.signum(x.Second - y.Second))
                  .collect(Collectors.toCollection(ArrayList::new));

      int crossings = 0;

      ArrayList<OrderedPair<Curve, Integer>> ret = new ArrayList<>();

      for (OrderedTriplet<Curve, Double, Double> entry : ordered)
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

   void findSplices(ArrayList<Curve> working_loop1, ArrayList<Curve> working_loop2,
         HashMap<Curve, AnnotatedCurve> forward_annotations_map,
         HashMap<Curve, Splice> endSpliceMap,
         double tol)
   {
      Curve l1prev = working_loop1.get(working_loop1.size() - 1);

      for (Curve l1curr : working_loop1)
      {
         XY l1_cur_start_pos = l1curr.startPos();
         assert l1prev.endPos().equals(l1_cur_start_pos, 1e-6);

         Curve l2prev = working_loop2.get(working_loop2.size() - 1);

         for (Curve l2curr : working_loop2)
         {
            XY l2_cur_start_pos = l2curr.startPos();
            assert l2prev.endPos().equals(l2_cur_start_pos, 1e-6);

            if (l1_cur_start_pos.equals(l2_cur_start_pos, tol))
            {
               Splice s = new Splice(
                     forward_annotations_map.get(l1curr),
                     forward_annotations_map.get(l2curr));

               assert !endSpliceMap.containsKey(l1prev);
               assert !endSpliceMap.containsKey(l2prev);

               endSpliceMap.put(l1prev, s);
               endSpliceMap.put(l2prev, s);
            }

            l2prev = l2curr;
         }

         l1prev = l1curr;
      }
   }

   // non-private only for unit-tests
   void splitCurvesAtIntersections(ArrayList<Curve> working_loop1, ArrayList<Curve> working_loop2, double tol)
   {
      for (int i = 0; i < working_loop1.size(); i++)
      {
         Curve c1 = working_loop1.get(i);
         for (int j = 0; j < working_loop2.size(); j++)
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
               // we exit this loop immediately and look at the first pair from the newly inserted curve(s)
               // instead
               for (int k = 0; k < ret.size() && !any_splits; k++)
               {
                  OrderedPair<Double, Double> split_points = ret.get(k);

                  double start_dist = c1.paramCoordinateDist(c1.StartParam, split_points.First);
                  double end_dist = c1.paramCoordinateDist(c1.EndParam, split_points.First);

                  // if we are far enough from existing splits
                  if (start_dist > tol && end_dist > tol)
                  {
                     any_splits = true;

                     Curve c1split1 = c1.cloneWithChangedParams(c1.StartParam, split_points.First);
                     Curve c1split2 = c1.cloneWithChangedParams(split_points.First, c1.EndParam);

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

                  start_dist = c2.paramCoordinateDist(c2.StartParam, split_points.Second);
                  end_dist = c2.paramCoordinateDist(c2.EndParam, split_points.Second);

                  // if we are far enough from existing splits
                  if (start_dist > tol && end_dist > tol)
                  {
                     any_splits = true;

                     Curve c2split1 = c2.cloneWithChangedParams(c2.StartParam, split_points.Second);
                     Curve c2split2 = c2.cloneWithChangedParams(split_points.Second, c2.EndParam);

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
   void buildAnnotationChains(ArrayList<Curve> curves, int loop_number,
         HashMap<Curve, AnnotatedCurve> forward_annotations_map)
   {
      Curve prev = null;

      for (Curve curr : curves)
      {
         AnnotatedCurve ac_forward_curr = new AnnotatedCurve(curr, loop_number);

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

//      if (visualise)
//      {
//         Random r = new Random(1);
//
//         Main.clear(255);
//         XY pnt = working_loops1.get(0).get(2).startPos();
//         Box size = new Box(pnt.minus(new XY(.001, .001)),
//               pnt.plus(new XY(.001, .001)));
////         engine.Box size = bounds;
//         Main.scaleTo(size);
//
//         Main.fill(r.nextInt(256), r.nextInt(256), 256);
//         for(Splice s : endSpliceMap.values())
//         {
//            Main.circle(s.Loop1Out.Curve.startPos().X,
//                  s.Loop1Out.Curve.startPos().Y,
//                  size.DX() * 0.004);
//         }
//
//         for(ArrayList<Curve> alc1 : working_loops1.values())
//         {
//            Main.strokeWidth(size.DX() * 0.001);
//            Main.stroke(256, r.nextInt(128), r.nextInt(128));
//            Loop l = new Loop(alc1);
//            Main.drawLoopPoints(l.facet(.3));
//
//            for(Curve c : l.getCurves())
//            {
//               XY end = c.endPos();
//               Main.circle(end.X, end.Y, size.DX() * 0.002);
//            }
//         }
//
//         for (ArrayList<Curve> alc2 : working_loops2.values())
//         {
//            Main.stroke(r.nextInt(128), 256, r.nextInt(128));
//            Loop l = new Loop(alc2);
//            Main.drawLoopPoints(l.facet(.3));
//
//            for(Curve c : l.getCurves())
//            {
//               XY end = c.endPos();
//               Main.circle(end.X, end.Y, size.DX() * 0.002);
//            }
//         }
//      }

//      // don't keep eating random numbers if we're visualising the same frame over and over
//      if (visualise && m_visualisation_line != null)
//      {
//         Main.stroke(0, 0, 0);
//         Main.line(m_visualisation_line.startPos(), m_visualisation_line.endPos());
//
//         return null;
//      }

//            if (visualise)
//            {
//               m_visualisation_line = lc;
//               Main.stroke(0, 0, 0);
//               Main.line(m_visualisation_line.startPos(), m_visualisation_line.endPos());
//               return null;
//            }

// private static LineCurve m_visualisation_line;
