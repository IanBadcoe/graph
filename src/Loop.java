import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class Loop
{
   Loop(Curve c)
   {
      m_curves.add(c);

      m_param_range = c.endParam() - c.startParam();

      XY s = c.startPos();
      XY e = c.endPos();

      assert s.equals(e, 1e-6);
   }

   public Loop(ArrayList<Curve> curves)
   {
      m_curves.addAll(curves);

      double range = 0.0;

      for(int i = 0; i < m_curves.size(); i++)
      {
         range += m_curves.get(i).endParam() - m_curves.get(i).startParam();
      }

      m_param_range = range;

      XY s = m_curves.get(0).startPos();
      XY e = m_curves.get(m_curves.size() - 1).endPos();

      assert s.equals(e, 1e-6);
   }

   public double paramRange()
   {
      return m_param_range;
   }

   public XY computePos(double p)
   {
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
            return c.computePos(p - c.startParam());
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

   // loop -> loop intersection data
   // C1 and C2 are two curves within each loop resp.
   // Param1 and Param2 are parameters around the _loops_ not the individual curves
   public static class LLIntersect
   {
      LLIntersect(Curve c1, Curve c2,
                  double param1, double param2)
      {
         C1 = c1;
         C2 = c2;
         Param1 = param1;
         Param2 = param2;
      }

      public final Curve C1;
      public final Curve C2;
      public final double Param1;
      public final double Param2;
   }

//   public Loop union(Loop other)
//   {
//      // the union of any two identical objects as the same as either object
//      if (this.equals(other))
//         return this;
//
//      // find all curve-curve intersections
//      ArrayList<LLIntersect> intersections = intersect(other);
//   }

   ArrayList<LLIntersect> intersect(Loop other)
   {
//      ArrayList<LLIntersect> ret = new ArrayList<>();
//
//      for(Curve c : m_curves)
//      {
//         for(Curve c_other : other.m_curves)
//         {
//            CCIntersect
//         }
//      }

      return null;
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

   private final ArrayList<Curve> m_curves = new ArrayList<>();

   private final double m_param_range;
}
