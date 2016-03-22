package engine.level;

import engine.graph.Graph;
import engine.graph.TemplateStore;
import game.TemplateStore1;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExpandToSizeStepperTest
{
   @Test
   public void testAllTemplatesFail() throws Exception
   {
      IoCContainer ioc_container = new IoCContainer(
            null,
            (x, a, b, c) -> new TestStepper(false, null),
            null,
            null,
            null);

      Graph g = new Graph();

      TemplateStore ts = new TemplateStore1();

      StepperController e = new StepperController(g, new ExpandToSizeStepper(ioc_container, g, 1, ts, new LevelGeneratorConfiguration(1)));

      StepperController.StatusReport ret;

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(StepperController.Status.StepOutFailure, ret.Status);
   }

   class SimpleAddNodeStepper implements IStepper
   {
      SimpleAddNodeStepper(int m_max, Graph g)
      {
         this.m_max = m_max;
         m_graph = g;
      }

      @Override
      public StepperController.StatusReportInner step(StepperController.Status status)
      {
         // lets us send a fail to the expander, after some initial successes
         if (m_graph.numNodes() >= m_max)
            return new StepperController.StatusReportInner(StepperController.Status.StepOutFailure,
                  null, "");

         m_graph.addNode("", "", "", 0);

         return new StepperController.StatusReportInner(StepperController.Status.StepOutSuccess,
               null, "");
      }

      final int m_max;
      final Graph m_graph;
   }

   @Test
   public void testGrowToSize() throws Exception
   {
      {
         IoCContainer ioc_container = new IoCContainer(
               null,
               (x, a, b, c) -> new SimpleAddNodeStepper(1000, a),
               null,
               null,
               null);

         Graph g = new Graph();

         StepperController e = new StepperController(g,
               new ExpandToSizeStepper(ioc_container, g, 10, new TemplateStore1(), new LevelGeneratorConfiguration(1)));

         StepperController.StatusReport ret;

         do
         {
            ret = e.Step();
         }
         while (!ret.Complete);

         assertEquals(StepperController.Status.StepOutSuccess, ret.Status);
         assertEquals(10, g.numNodes());
      }

      // partial success, when we can add some nodes but not enough, is counted
      // as a success
      {
         IoCContainer ioc_container = new IoCContainer(
               null,
               (x, a, b, c) -> new SimpleAddNodeStepper(5, a),
               null,
               null,
               null);

         Graph g = new Graph();

         StepperController e = new StepperController(g,
               new ExpandToSizeStepper(ioc_container, g, 10, new TemplateStore1(), new LevelGeneratorConfiguration(1)));

         StepperController.StatusReport ret;

         do
         {
            ret = e.Step();
         }
         while (!ret.Complete);

         assertEquals(StepperController.Status.StepOutSuccess, ret.Status);
         assertEquals(5, g.numNodes());
      }
   }

   @Test
   public void testThrowsForIteration()
   {
      // steppers only get called with iterate if they return it, and this one doesn't
      boolean thrown = false;

      try
      {
         // none of these parameters used in this case
         ExpandToSizeStepper etss = new ExpandToSizeStepper(null, null, 0, null, null);

         etss.step(StepperController.Status.Iterate);
      }
      catch(UnsupportedOperationException uoe)
      {
         thrown = true;
      }

      assertTrue(thrown);
   }
}
