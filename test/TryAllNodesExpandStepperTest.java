import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by badcoei on 29/10/2015.
 */
public class TryAllNodesExpandStepperTest
{
   static ArrayList<INode> m_nodes = new ArrayList<>();

   class FailAllAndCountStepper implements IExpandStepper
   {
      FailAllAndCountStepper(Graph graph, INode n, Collection<Template> templates,
                             Random r)
      {
         m_nodes.add(n);
      }

      @Override
      public Expander.ExpandRetInner Step(Expander.ExpandStatus status)
      {
         return new Expander.ExpandRetInner(Expander.ExpandStatus.StepOutFailure,
               null, "");
      }
   }

   @Test
   public void testTryAllNodes() throws Exception
   {
      TryAllNodesExpandStepper.SetChildFactory(
            (a, b, c, d) -> new FailAllAndCountStepper(a, b, c, d));

      Graph g = new Graph();

      INode n1 = g.AddNode("", "", "", 0);
      INode n2 = g.AddNode("", "", "", 0);
      INode n3 = g.AddNode("", "", "", 0);

      Expander e = new Expander(g,
            new TryAllNodesExpandStepper(g, new TemplateStore(), new Random(1)));

      Expander.ExpandRet ret;

      m_nodes.clear();

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(3, m_nodes.size());
      assertTrue(m_nodes.contains(n1));
      assertTrue(m_nodes.contains(n2));
      assertTrue(m_nodes.contains(n3));
   }
}