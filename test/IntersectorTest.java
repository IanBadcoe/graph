import org.junit.Test;

import java.util.ArrayList;

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

      Intersector.begin();

      Intersector.buildAnnotationChains(curves);

      for(Curve c : curves)
      {
         assertNotNull(Intersector.findForwardAnnotation(c));
         assertNotNull(Intersector.findForwardAnnotation(c));
      }

      assertEquals(cb, Intersector.findForwardAnnotation(ca).Next.Curve);
      assertEquals(cc, Intersector.findForwardAnnotation(cb).Next.Curve);
      assertEquals(cd, Intersector.findForwardAnnotation(cc).Next.Curve);
      assertEquals(ce, Intersector.findForwardAnnotation(cd).Next.Curve);
      assertEquals(ca, Intersector.findForwardAnnotation(ce).Next.Curve);

      assertEquals(ce, Intersector.findReverseAnnotation(ca).Next.Curve);
      assertEquals(ca, Intersector.findReverseAnnotation(cb).Next.Curve);
      assertEquals(cb, Intersector.findReverseAnnotation(cc).Next.Curve);
      assertEquals(cc, Intersector.findReverseAnnotation(cd).Next.Curve);
      assertEquals(cd, Intersector.findReverseAnnotation(ce).Next.Curve);
   }

   @Test
   public void testSplitCurvesAtIntersections()
   {
      // circles meet at two points
      {
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

      // circles meet at one point
      {
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

      // same circle twice
      {
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

      // one circle hits existing break in other
      {
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
   }
}