import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class ExpandToSizeStepperTest
{
   @Test
   public void testAllTemplatesFail() throws Exception
   {
      ExpandToSizeStepper.SetChildFactory(
            (a, b, c) -> new TestStepper(false, null));

      Graph g = new Graph();

      TemplateStore ts = new TemplateStore1();

      Expander e = new Expander(g, new ExpandToSizeStepper(g, 1, ts, new Random(1)));

      Expander.ExpandRet ret;

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(Expander.ExpandStatus.StepOutFailure, ret.Status);
   }

   class SimpleAddNodeStepper implements IExpandStepper
   {
      SimpleAddNodeStepper(Graph g)
      {
         m_graph = g;
      }

      @Override
      public Expander.ExpandRetInner Step(Expander.ExpandStatus status)
      {
         m_graph.AddNode("", "", "", 0);

         return new Expander.ExpandRetInner(Expander.ExpandStatus.StepOutSuccess,
               null, "");
      }

      Graph m_graph;
   }

   @Test
   public void testGrowToSize() throws Exception
   {
      ExpandToSizeStepper.SetChildFactory(
            (a, b, c) -> new SimpleAddNodeStepper(a));

      Graph g = new Graph();

      Expander e = new Expander(g, new ExpandToSizeStepper(g, 10, new TemplateStore1(), new Random(1)));

      Expander.ExpandRet ret;

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(Expander.ExpandStatus.StepOutSuccess, ret.Status);
      assertEquals(10, g.NumNodes());
   }

   @Test
   public void testThrowsForIteration()
   {
      // steppers only get called with iterate if they return it, and this one doesn't
      boolean thrown = false;

      try
      {
         // none of these parameters used in this case
         ExpandToSizeStepper etss = new ExpandToSizeStepper(null, 0, null, null);

         etss.Step(Expander.ExpandStatus.Iterate);
      }
      catch(UnsupportedOperationException uoe)
      {
         thrown = true;
      }
   }
}