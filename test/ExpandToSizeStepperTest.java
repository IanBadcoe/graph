import org.junit.Test;

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

      StepperController e = new StepperController(g, new ExpandToSizeStepper(g, 1, ts, new LevelGeneratorConfiguration(1)));

      StepperController.ExpandRet ret;

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(StepperController.ExpandStatus.StepOutFailure, ret.Status);
   }

   class SimpleAddNodeStepper implements IStepper
   {
      SimpleAddNodeStepper(Graph g)
      {
         m_graph = g;
      }

      @Override
      public StepperController.ExpandRetInner Step(StepperController.ExpandStatus status)
      {
         m_graph.AddNode("", "", "", 0);

         return new StepperController.ExpandRetInner(StepperController.ExpandStatus.StepOutSuccess,
               null, "");
      }

      final Graph m_graph;
   }

   @Test
   public void testGrowToSize() throws Exception
   {
      ExpandToSizeStepper.SetChildFactory(
            (a, b, c) -> new SimpleAddNodeStepper(a));

      Graph g = new Graph();

      StepperController e = new StepperController(g, new ExpandToSizeStepper(g, 10, new TemplateStore1(), new LevelGeneratorConfiguration(1)));

      StepperController.ExpandRet ret;

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(StepperController.ExpandStatus.StepOutSuccess, ret.Status);
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

         etss.Step(StepperController.ExpandStatus.Iterate);
      }
      catch(UnsupportedOperationException uoe)
      {
         thrown = true;
      }

      assertTrue(thrown);
   }
}