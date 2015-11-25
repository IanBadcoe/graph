import org.junit.Test;

import static org.junit.Assert.*;

public class ExpanderTest
{
   class StepDepthStepper implements IStepper
   {
      StepDepthStepper(int depth, int iterate)
      {
         m_depth = depth;
         m_iterate = iterate;
      }

      @Override
      public StepperController.StatusReportInner step(StepperController.Status status)
      {
         switch (status)
         {
            case StepIn:
            case Iterate:
               if (m_iter_count < m_iterate)
               {
                  return new StepperController.StatusReportInner(StepperController.Status.Iterate,
                        null, "iterate: " + m_iter_count++);
               }
               else if (m_depth > 0)
               {
                  return new StepperController.StatusReportInner(StepperController.Status.StepIn,
                        new StepDepthStepper(m_depth - 1, m_iterate), "in: " + m_depth);
               }
               else
               {
                  return new StepperController.StatusReportInner(StepperController.Status.StepOutSuccess,
                        null, "out: " + m_depth);
               }

            case StepOutSuccess:
               return new StepperController.StatusReportInner(StepperController.Status.StepOutSuccess,
                     null, "out: " + m_depth);
         }

         assertTrue(false);

         return null;
      }

      final int m_depth;
      final int m_iterate;
      int m_iter_count = 0;
   }

   @Test
   public void testSubStepLogic() throws Exception
   {
      {
         StepperController e = new StepperController(null,
               new StepDepthStepper(0, 0));

         StepperController.StatusReport ret;

         ret = e.Step();

         assertEquals(StepperController.Status.StepOutSuccess, ret.Status);
         assertEquals("out: 0", ret.Log);
         assertTrue(ret.Complete);

         boolean thrown = false;
         try
         {
            e.Step();
         }
         catch(NullPointerException npe)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         StepperController e = new StepperController(null,
               new StepDepthStepper(1, 0));

         StepperController.StatusReport ret;

         ret = e.Step();

         assertEquals(StepperController.Status.StepIn, ret.Status);
         assertEquals("in: 1", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(StepperController.Status.StepOutSuccess, ret.Status);
         assertEquals("out: 0", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(StepperController.Status.StepOutSuccess, ret.Status);
         assertEquals("out: 1", ret.Log);
         assertTrue(ret.Complete);

         boolean thrown = false;
         try
         {
            e.Step();
         }
         catch(NullPointerException npe)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         StepperController e = new StepperController(null,
               new StepDepthStepper(0, 3));

         StepperController.StatusReport ret;

         ret = e.Step();

         assertEquals(StepperController.Status.Iterate, ret.Status);
         assertEquals("iterate: 0", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(StepperController.Status.Iterate, ret.Status);
         assertEquals("iterate: 1", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(StepperController.Status.Iterate, ret.Status);
         assertEquals("iterate: 2", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(StepperController.Status.StepOutSuccess, ret.Status);
         assertEquals("out: 0", ret.Log);
         assertTrue(ret.Complete);

         boolean thrown = false;
         try
         {
            e.Step();
         }
         catch(NullPointerException npe)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         StepperController e = new StepperController(null,
               new StepDepthStepper(2, 2));

         StepperController.StatusReport ret;

         ret = e.Step();

         assertEquals(StepperController.Status.Iterate, ret.Status);
         assertEquals("iterate: 0", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(StepperController.Status.Iterate, ret.Status);
         assertEquals("iterate: 1", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(StepperController.Status.StepIn, ret.Status);
         assertEquals("in: 2", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(StepperController.Status.Iterate, ret.Status);
         assertEquals("iterate: 0", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(StepperController.Status.Iterate, ret.Status);
         assertEquals("iterate: 1", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(StepperController.Status.StepIn, ret.Status);
         assertEquals("in: 1", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(StepperController.Status.Iterate, ret.Status);
         assertEquals("iterate: 0", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(StepperController.Status.Iterate, ret.Status);
         assertEquals("iterate: 1", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(StepperController.Status.StepOutSuccess, ret.Status);
         assertEquals("out: 0", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(StepperController.Status.StepOutSuccess, ret.Status);
         assertEquals("out: 1", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(StepperController.Status.StepOutSuccess, ret.Status);
         assertEquals("out: 2", ret.Log);
         assertTrue(ret.Complete);

         boolean thrown = false;
         try
         {
            e.Step();
         }
         catch(NullPointerException npe)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }
   }

   class StepFailStepper implements IStepper
   {
      StepFailStepper(Graph graph, int depth, int fail_depth)
      {
         m_graph = graph;
         m_depth = depth;
         m_fail_depth = fail_depth;
      }

      @Override
      public StepperController.StatusReportInner step(StepperController.Status status)
      {
         if (status == StepperController.Status.StepIn)
         {
            m_graph.addNode("n" + m_depth, "", "", 0);

            if (m_depth > 0)
            {
               return new StepperController.StatusReportInner(StepperController.Status.StepIn,
                     new StepFailStepper(m_graph, m_depth - 1, m_fail_depth),
                     "");
            }
         }

         if (m_depth == m_fail_depth)
         {
            return new StepperController.StatusReportInner(StepperController.Status.StepOutFailure,
                  null, "");
         }
         else
         {
            return new StepperController.StatusReportInner(StepperController.Status.StepOutSuccess,
                  null, "");
         }
      }

      final int m_depth;
      final int m_fail_depth;
      final Graph m_graph;
   }

   @Test
   public void testFailureGraphRestore() throws Exception
   {
      {
         Graph g = new Graph();

         StepperController e = new StepperController(g,
               new StepFailStepper(g, 3, 3));

         StepperController.StatusReport ret;

         do
         {
            ret = e.Step();
         }
         while (!ret.Complete);

         assertEquals(StepperController.Status.StepOutFailure, ret.Status);
         // we failed at outer stepper-level, so whole graph should have been restored
         assertEquals(g.numNodes(), 0);

         boolean thrown = false;
         try
         {
            e.Step();
         }
         catch(NullPointerException npe)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         Graph g = new Graph();

         StepperController e = new StepperController(g,
               new StepFailStepper(g, 3, 2));

         StepperController.StatusReport ret;

         do
         {
            ret = e.Step();
         }
         while (!ret.Complete);

         // outer stepper succeeded
         assertEquals(StepperController.Status.StepOutSuccess, ret.Status);
         // we failed one stepper-level down, so one node should still he here
         assertEquals(g.numNodes(), 1);

         boolean thrown = false;
         try
         {
            e.Step();
         }
         catch(NullPointerException npe)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         Graph g = new Graph();

         StepperController e = new StepperController(g,
               new StepFailStepper(g, 3, 1));

         StepperController.StatusReport ret;

         do
         {
            ret = e.Step();
         }
         while (!ret.Complete);

         // outer stepper succeeded
         assertEquals(StepperController.Status.StepOutSuccess, ret.Status);
         // we failed two stepper-levels down, so two nodes should still he here
         assertEquals(g.numNodes(), 2);

         boolean thrown = false;
         try
         {
            e.Step();
         }
         catch(NullPointerException npe)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         Graph g = new Graph();

         StepperController e = new StepperController(g,
               new StepFailStepper(g, 3, 0));

         StepperController.StatusReport ret;

         do
         {
            ret = e.Step();
         }
         while (!ret.Complete);

         // outer stepper succeeded
         assertEquals(StepperController.Status.StepOutSuccess, ret.Status);
         // we failed three stepper-levels down, so three nodes should still he here
         assertEquals(g.numNodes(), 3);

         boolean thrown = false;
         try
         {
            e.Step();
         }
         catch(NullPointerException npe)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         Graph g = new Graph();

         StepperController e = new StepperController(g,
               new StepFailStepper(g, 3, -1));

         StepperController.StatusReport ret;

         do
         {
            ret = e.Step();
         }
         while (!ret.Complete);

         // outer stepper succeeded
         assertEquals(StepperController.Status.StepOutSuccess, ret.Status);
         // did not fail, so all four nodes should still he here
         assertEquals(g.numNodes(), 4);

         boolean thrown = false;
         try
         {
            e.Step();
         }
         catch(NullPointerException npe)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }
   }
}