package engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Loop
{
   Loop(Curve c)
   {
      m_curves.add(c);

      m_param_range = c.EndParam - c.StartParam;

      XY s = c.startPos();
      XY e = c.endPos();

      if (!s.equals(e, 1e-6))
         throw new IllegalArgumentException("Curves do not form a closed loop");
   }

   public Loop(ArrayList<Curve> curves)
   {
      m_curves.addAll(curves);

      double range = 0.0;

      Curve prev = m_curves.get(m_curves.size() - 1);

      for(Curve curr : m_curves)
      {
         range += curr.EndParam - curr.StartParam;

         XY c_start = curr.startPos();
         XY p_end = prev.endPos();

         if (!c_start.equals(p_end, 1e-6))
            throw new IllegalArgumentException("Curves do not form a closed loop");

         prev = curr;
      }

      m_param_range = range;
   }

   public double paramRange()
   {
      return m_param_range;
   }

   public XY computePos(double p)
   {
      // because we don't use the curves eithinParams call
      // this routine should give the same behaviour for
      // multi-part curves and circles, even though the latter
      // just go round and round for any level of param
      if (p < 0)
         return null;

      // curve param ranges can be anywhere
      // but the loop param range starts from zero
      for(Curve c : m_curves)
      {
         if (c.paramRange() < p)
         {
            p -= c.paramRange();
         }
         else
         {
            // shift the param range where the curve wants it...
            return c.computePos(p + c.StartParam);
         }
      }

      return null;
   }

   public int numCurves()
   {
      return m_curves.size();
   }

   public List<Curve> getCurves()
   {
      return Collections.unmodifiableList(m_curves);
   }

   @Override
   public int hashCode()
   {
      int h = 0;

      for(Curve c : m_curves)
      {
         h ^= c.hashCode();
         h *= 3;
      }

      // m_param_range is derivative from the curves
      // so not required in hash

      return h;
   }

   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof Loop))
         return false;

      Loop loop_o = (Loop) o;

      if (numCurves() != loop_o.numCurves())
         return false;

      for (int i = 0; i < numCurves(); i++)
      {
         if (!m_curves.get(i).equals(loop_o.m_curves.get(i)))
            return false;
      }

      return true;
   }

   ArrayList<XY> facet(@SuppressWarnings("SameParameterValue") double max_length)
   {
      ArrayList<XY> ret = new ArrayList<>();

      for(Curve c : m_curves)
      {
         double param_step = c.paramRange()
               * (max_length / c.length());

         double p = 0;

         double start_p = c.StartParam;

         while(p < c.paramRange())
         {
            ret.add(c.computePos(start_p + p));

            p += param_step;
         }
      }

      return ret;
   }

   // each normal (ret.Second) is from the middle on the interval that ret.First begins
   public ArrayList<OrderedPair<XY,XY>> facetWithNormals(double max_length)
   {
      ArrayList<OrderedPair<XY,XY>> ret = new ArrayList<>();

      for(Curve c : m_curves)
      {
         int num_steps = (int)(c.length() / max_length) + 1;
         double param_step = c.paramRange() / num_steps;

         double p = 0;

         double start_p = c.StartParam;

         // we run for num_steps, which takes us to a point one-step before the end of the curve
         // the end of the last segment in the curve is the start of the next curve, but the normal
         // of that segment is the last normal we calculate here (in this curve)
         for(int i = 0; i < num_steps; i++)
         {
            ret.add(new OrderedPair<>(
                  c.computePos(start_p + p),
                  c.computeNormal(start_p + p + param_step / 2)));

            p += param_step;
         }
      }

      return ret;
   }

   Box getBounds()
   {
      return m_curves.stream().map(Curve::boundingBox).reduce(new Box(), (b, a) -> a.union(b));
   }

   private final ArrayList<Curve> m_curves = new ArrayList<>();

   private final double m_param_range;
}
