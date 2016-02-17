package engine.brep;

import engine.OrderedPair;
import engine.Util;
import engine.XY;

import java.util.ArrayList;

public class BRepUtil
{
   public static ArrayList<OrderedPair<Double,Double>> curveCurveIntersect(Curve c1, Curve c2)
   {
      if (c1.equals(c2))
         return null;

      OrderedPair<XY, XY> pts;

      if (c1 instanceof CircleCurve)
      {
         pts = circleCurveIntersect((CircleCurve)c1, c2);
      }
      else if (c1 instanceof LineCurve)
      {
         pts = lineCurveIntersect((LineCurve)c1, c2);
      }
      else
      {
         throw new UnsupportedOperationException("Unknown type of curve");
      }

      if (pts == null)
         return null;

      ArrayList<OrderedPair<Double,Double>> ret = new ArrayList<>();

      {
         Double pc1 = c1.findParamForPoint(pts.First, 1e-6);
         Double pc2 = c2.findParamForPoint(pts.First, 1e-6);

         if (pc1 != null && pc2 != null)
         {
            ret.add(new OrderedPair<>(pc1, pc2));
         }
      }

      if (pts.Second != null)
      {
         Double pc1 = c1.findParamForPoint(pts.Second, 1e-6);
         Double pc2 = c2.findParamForPoint(pts.Second, 1e-6);

         if (pc1 != null && pc2 != null)
         {
            ret.add(new OrderedPair<>(pc1, pc2));
         }
      }

      if (ret.size() > 0)
         return ret;

      return null;
   }

   private static OrderedPair<XY, XY> circleCurveIntersect(CircleCurve c1, Curve c2)
   {
      if (c2 instanceof CircleCurve)
      {
         return circleCircleIntersect(c1, (CircleCurve)c2);
      }
      else if (c2 instanceof LineCurve)
      {
         return circleLineIntersect(c1, (LineCurve)c2);
      }

      throw new UnsupportedOperationException("Unknown type of curve");
   }

   private static OrderedPair<XY, XY> circleCircleIntersect(CircleCurve c1, CircleCurve c2)
   {
      return Util.circleCircleIntersect(c1.Position, c1.Radius, c2.Position, c2.Radius);
   }

   private static OrderedPair<XY, XY> lineCurveIntersect(LineCurve c1, Curve c2)
   {
      if (c2 instanceof CircleCurve)
      {
         return lineCircleIntersect(c1, (CircleCurve)c2);
      }
      else if (c2 instanceof LineCurve)
      {
         return lineLineIntersect(c1, (LineCurve)c2);
      }


      throw new UnsupportedOperationException("Unknown type of curve");
   }

   private static OrderedPair<XY, XY> lineLineIntersect(LineCurve l1, LineCurve l2)
   {
      OrderedPair<Double, Double> ret = Util.edgeIntersect(
            l1.startPos(), l1.endPos(),
            l2.startPos(), l2.endPos());

      if (ret == null)
         return null;

      // inefficient, am going to calculate a position here, just so that I can
      // back-calculate params from it above, however line-line is the only intersection that gives
      // direct params so it would be a pain to change the approach for this one case

      return new OrderedPair<>(
            l1.computePos(l1.StartParam + (l1.EndParam - l1.StartParam) * ret.First),
            null);
   }

   private static OrderedPair<XY, XY> lineCircleIntersect(LineCurve l1, CircleCurve c2)
   {
      return circleLineIntersect(c2, l1);
   }

   // algorithm stolen with thanks from:
   // http://stackoverflow.com/questions/1073336/circle-line-segment-collision-detection-algorithm
   private static OrderedPair<XY, XY> circleLineIntersect(CircleCurve c1, LineCurve l2)
   {
      OrderedPair<Double, Double> ret = circleLineIntersect(c1.Position, c1.Radius,
            l2.startPos(), l2.endPos());

      if (ret == null)
         return null;

      XY hit1 = null;
      XY hit2 = null;

      if(ret.First != null)
      {
         hit1 = l2.computePos(l2.StartParam + (l2.EndParam - l2.StartParam) * ret.First);
      }

      if(ret.Second != null)
      {
         hit2 = l2.computePos(l2.StartParam + (l2.EndParam - l2.StartParam) * ret.Second);
      }

      return new OrderedPair<>(hit1, hit2);
   }

   public static OrderedPair<Double, Double> circleLineIntersect(XY circlePos, double circleRadius,
                                                                 XY lineStart, XY lineEnd)
   {
      XY d = lineEnd.minus(lineStart);
      XY f = lineStart.minus(circlePos);

      double a = d.length2();
      double b = 2 * f.dot(d);
      double c = f.length2() - circleRadius * circleRadius;

      double discriminant_2 = b * b - 4 * a * c;

      if( discriminant_2 < 0 )
      {
         return null;
      }

      // ray didn't totally miss sphere,
      // so there is a solution to
      // the equation.

      double discriminant = Math.sqrt(discriminant_2);

      // either solution may be on or off the ray so need to test both
      // t1 is always the smaller value, because BOTH discriminant and
      // a are nonnegative.
      double t1 = (-b - discriminant) / (2 * a);
      double t2 = (-b + discriminant) / (2 * a);

      // 3x HIT cases:
      //          -o->             --|-->  |            |  --|->
      // Impale(t1 hit,t2 hit), Poke(t1 hit,t2>1), ExitWound(t1<0, t2 hit),

      // 3x MISS cases:
      //       ->  o                     o ->              | -> |
      // FallShort (t1>1,t2>1), Past (t1<0,t2<0), CompletelyInside(t1<0, t2>1)

      double tol = 1e-12;

      Double hit1 = null;
      Double hit2 = null;

      if( t1 >= -tol && t1 <= 1 + tol )
      {
         hit1 = t1;
      }

      if( t2 >= -tol && t2 <= 1 + tol )
      {
         hit2 = t2;
      }

      if (hit1 == null)
      {
         hit1 = hit2;
         hit2 = null;
      }

      if (hit1 == null)
         return null;

      return new OrderedPair<>(hit1, hit2);
   }
}
