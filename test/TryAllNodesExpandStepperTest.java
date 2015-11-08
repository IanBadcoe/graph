import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

public class TryAllNodesExpandStepperTest
{
   static ArrayList<INode> m_nodes = new ArrayList<>();

   class TestStepperLoggingNodes extends TestStepper
   {
      TestStepperLoggingNodes(INode n)
      {
         super(false, null);

         m_nodes.add(n);
      }
   }

   @Test
   public void testTryAllNodes() throws Exception
   {
      TryAllNodesExpandStepper.SetChildFactory(
            (a, b, c, d) -> new TestStepperLoggingNodes(b));

      Graph g = new Graph();

      // all nodes must be tagged "e" to be considered expandable
      INode n1 = g.AddNode("", "e", "", 0);
      INode n2 = g.AddNode("", "e", "", 0);
      INode n3 = g.AddNode("", "e", "", 0);

      Expander e = new Expander(g,
            new TryAllNodesExpandStepper(g, new TemplateStore(), new Random(1)));

      Expander.ExpandRet ret;

      m_nodes.clear();

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(Expander.ExpandStatus.StepOutFailure, ret.Status);

      assertEquals(3, m_nodes.size());
      assertTrue(m_nodes.contains(n1));
      assertTrue(m_nodes.contains(n2));
      assertTrue(m_nodes.contains(n3));
   }

   @Test
   public void testSuccess()
   {
      TryAllNodesExpandStepper.SetChildFactory(
            (a, b, c, d) -> new TestStepper(true, null));

      Graph g = new Graph();

      // all nodes must be tagged "e" to be considered expandable
      INode n1 = g.AddNode("", "e", "", 0);
      INode n2 = g.AddNode("", "e", "", 0);
      INode n3 = g.AddNode("", "e", "", 0);

      Expander e = new Expander(g,
            new TryAllNodesExpandStepper(g, new TemplateStore(), new Random(1)));

      Expander.ExpandRet ret;

      m_nodes.clear();

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(Expander.ExpandStatus.StepOutSuccess, ret.Status);
   }
}