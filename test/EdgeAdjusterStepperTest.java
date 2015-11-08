import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Nobody on 10/30/2015.
 */
public class EdgeAdjusterStepperTest
{
   @Test
   public void testStep_Fail() throws Exception
   {
      EdgeAdjusterStepper.SetChildFactory(
            (a, b, c, d) -> new TestStepper(false, null));

      Graph g = new Graph();
      INode n1 = g.AddNode("", "", "", 0);
      INode n2 = g.AddNode("", "", "", 0);
      DirectedEdge de = g.Connect(n1, n2, 0, 0, 0);

      Expander e = new Expander(g, new EdgeAdjusterStepper(g, de));

      Expander.ExpandRet ret;

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(Expander.ExpandStatus.StepOutFailure, ret.Status);
   }

   @Test
   public void testStep_Succeed() throws Exception
   {
      EdgeAdjusterStepper.SetChildFactory(
            (a, b, c, d) -> new TestStepper(true, null));

      Graph g = new Graph();
      INode n1 = g.AddNode("", "", "", 0);
      INode n2 = g.AddNode("", "", "", 0);
      DirectedEdge de = g.Connect(n1, n2, 0, 0, 0);

      Expander e = new Expander(g, new EdgeAdjusterStepper(g, de));

      Expander.ExpandRet ret;

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(Expander.ExpandStatus.StepOutSuccess, ret.Status);
      assertEquals(3, g.NumNodes());
      assertFalse(n1.Connects(n2));
      assertEquals(0, n1.GetInConnections().size());
      assertEquals(1, n1.GetOutConnections().size());
      assertEquals(1, n2.GetInConnections().size());
      assertEquals(0, n2.GetOutConnections().size());

      DirectedEdge n1_out = n1.GetOutConnections().stream().findFirst().get();
      DirectedEdge n2_in = n2.GetInConnections().stream().findFirst().get();
      assertEquals(n1_out.End, n2_in.Start);
   }

   @Test
   public void testThrowsForIteration()
   {
      // steppers only get called with iterate if they return it, and this one doesn't
      boolean thrown = false;

      try
      {
         // none of these parameters used in this case
         EdgeAdjusterStepper etss = new EdgeAdjusterStepper(null, null);

         etss.Step(Expander.ExpandStatus.Iterate);
      }
      catch(UnsupportedOperationException uoe)
      {
         thrown = true;
      }
   }
}