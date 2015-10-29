import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by badcoei on 29/10/2015.
 */
public class ExpanderTest
{
   class StepDepthStepper implements IExpandStepper
   {
      StepDepthStepper(int depth, int iterate)
      {
         m_depth = depth;
         m_iterate = iterate;
      }

      @Override
      public Expander.ExpandRetInner Step(Expander.ExpandStatus status)
      {
         switch (status)
         {
            case StepIn:
            case Iterate:
               if (m_iter_count < m_iterate)
               {
                  return new Expander.ExpandRetInner(Expander.ExpandStatus.Iterate,
                        null, "iterate: " + m_iter_count++);
               }
               else if (m_depth > 0)
               {
                  return new Expander.ExpandRetInner(Expander.ExpandStatus.StepIn,
                        new StepDepthStepper(m_depth - 1, m_iterate), "in: " + m_depth);
               }
               else
               {
                  return new Expander.ExpandRetInner(Expander.ExpandStatus.StepOutSuccess,
                        null, "out: " + m_depth);
               }

            case StepOutSuccess:
               return new Expander.ExpandRetInner(Expander.ExpandStatus.StepOutSuccess,
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
         Expander e = new Expander(null,
               new StepDepthStepper(0, 0));

         Expander.ExpandRet ret;

         ret = e.Step();

         assertEquals(Expander.ExpandStatus.StepOutSuccess, ret.Status);
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
         Expander e = new Expander(null,
               new StepDepthStepper(1, 0));

         Expander.ExpandRet ret;

         ret = e.Step();

         assertEquals(Expander.ExpandStatus.StepIn, ret.Status);
         assertEquals("in: 1", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(Expander.ExpandStatus.StepOutSuccess, ret.Status);
         assertEquals("out: 0", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(Expander.ExpandStatus.StepOutSuccess, ret.Status);
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
         Expander e = new Expander(null,
               new StepDepthStepper(0, 3));

         Expander.ExpandRet ret;

         ret = e.Step();

         assertEquals(Expander.ExpandStatus.Iterate, ret.Status);
         assertEquals("iterate: 0", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(Expander.ExpandStatus.Iterate, ret.Status);
         assertEquals("iterate: 1", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(Expander.ExpandStatus.Iterate, ret.Status);
         assertEquals("iterate: 2", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(Expander.ExpandStatus.StepOutSuccess, ret.Status);
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
         Expander e = new Expander(null,
               new StepDepthStepper(2, 2));

         Expander.ExpandRet ret;

         ret = e.Step();

         assertEquals(Expander.ExpandStatus.Iterate, ret.Status);
         assertEquals("iterate: 0", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(Expander.ExpandStatus.Iterate, ret.Status);
         assertEquals("iterate: 1", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(Expander.ExpandStatus.StepIn, ret.Status);
         assertEquals("in: 2", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(Expander.ExpandStatus.Iterate, ret.Status);
         assertEquals("iterate: 0", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(Expander.ExpandStatus.Iterate, ret.Status);
         assertEquals("iterate: 1", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(Expander.ExpandStatus.StepIn, ret.Status);
         assertEquals("in: 1", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(Expander.ExpandStatus.Iterate, ret.Status);
         assertEquals("iterate: 0", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(Expander.ExpandStatus.Iterate, ret.Status);
         assertEquals("iterate: 1", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(Expander.ExpandStatus.StepOutSuccess, ret.Status);
         assertEquals("out: 0", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(Expander.ExpandStatus.StepOutSuccess, ret.Status);
         assertEquals("out: 1", ret.Log);
         assertFalse(ret.Complete);

         ret = e.Step();

         assertEquals(Expander.ExpandStatus.StepOutSuccess, ret.Status);
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

   class StepFailStepper implements IExpandStepper
   {
      StepFailStepper(Graph graph, int depth, int fail_depth)
      {
         m_graph = graph;
         m_depth = depth;
         m_fail_depth = fail_depth;
      }

      @Override
      public Expander.ExpandRetInner Step(Expander.ExpandStatus status)
      {
         if (status == Expander.ExpandStatus.StepIn)
         {
            m_graph.AddNode("n" + m_depth, "", "", 0);

            if (m_depth > 0)
            {
               return new Expander.ExpandRetInner(Expander.ExpandStatus.StepIn,
                     new StepFailStepper(m_graph, m_depth - 1, m_fail_depth),
                     "");
            }
         }

         if (m_depth == m_fail_depth)
         {
            return new Expander.ExpandRetInner(Expander.ExpandStatus.StepOutFailure,
                  null, "");
         }
         else
         {
            return new Expander.ExpandRetInner(Expander.ExpandStatus.StepOutSuccess,
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

         Expander e = new Expander(g,
               new StepFailStepper(g, 3, 3));

         Expander.ExpandRet ret;

         do
         {
            ret = e.Step();
         }
         while (!ret.Complete);

         assertEquals(Expander.ExpandStatus.StepOutFailure, ret.Status);
         // we failed at outer stepper-level, so whole graph should have been restored
         assertEquals(g.NumNodes(), 0);
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
         Graph g = new Graph();

         Expander e = new Expander(g,
               new StepFailStepper(g, 3, 2));

         Expander.ExpandRet ret;

         do
         {
            ret = e.Step();
         }
         while (!ret.Complete);

         // outer stepper succeeded
         assertEquals(Expander.ExpandStatus.StepOutSuccess, ret.Status);
         // we failed one stepper-level down, so one node should still he here
         assertEquals(g.NumNodes(), 1);
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
         Graph g = new Graph();

         Expander e = new Expander(g,
               new StepFailStepper(g, 3, 1));

         Expander.ExpandRet ret;

         do
         {
            ret = e.Step();
         }
         while (!ret.Complete);

         // outer stepper succeeded
         assertEquals(Expander.ExpandStatus.StepOutSuccess, ret.Status);
         // we failed two stepper-levels down, so two nodes should still he here
         assertEquals(g.NumNodes(), 2);
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
         Graph g = new Graph();

         Expander e = new Expander(g,
               new StepFailStepper(g, 3, 0));

         Expander.ExpandRet ret;

         do
         {
            ret = e.Step();
         }
         while (!ret.Complete);

         // outer stepper succeeded
         assertEquals(Expander.ExpandStatus.StepOutSuccess, ret.Status);
         // we failed three stepper-levels down, so three nodes should still he here
         assertEquals(g.NumNodes(), 3);
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
         Graph g = new Graph();

         Expander e = new Expander(g,
               new StepFailStepper(g, 3, -1));

         Expander.ExpandRet ret;

         do
         {
            ret = e.Step();
         }
         while (!ret.Complete);

         // outer stepper succeeded
         assertEquals(Expander.ExpandStatus.StepOutSuccess, ret.Status);
         // did not fail, so all four nodes should still he here
         assertEquals(g.NumNodes(), 4);
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
}