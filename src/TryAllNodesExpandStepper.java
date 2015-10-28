import java.util.Collection;
import java.util.Random;

public class TryAllNodesExpandStepper implements IExpandStepper
{
   TryAllNodesExpandStepper(Graph graph, TemplateStore templates,
                            Random random)
   {
      m_graph = graph;
      m_templates = templates;
      m_all_nodes = graph.AllGraphNodes();
      m_random = random;
   }

   @Override
   public Expander.ExpandRet Step(Expander.ExpandStatus status)
   {
      // if our child succeeds, we succeed
      if (status == Expander.ExpandStatus.StepOutSuccess)
      {
         return new Expander.ExpandRet(Expander.ExpandStatus.StepOutSuccess,
               null, "Graph Expand Step Succeeded");
      }

      // no matter what other previous status, if we run out of nodes we're a fail
      if (m_all_nodes.size() == 0)
      {
         return new Expander.ExpandRet(Expander.ExpandStatus.StepOutFailure,
               null, "All nodes failed to expand");
      }

      INode node = Util.RemoveRandom(m_random, m_all_nodes);

      IExpandStepper child = new TryAllTemplatesOnOneNodeStepper(
            m_graph, node, m_templates.GetTemplatesCopy(), m_random);

      return new Expander.ExpandRet(Expander.ExpandStatus.StepIn,
            child, "Trying to expand node: " + node.GetName());
   }

   Graph m_graph;
   TemplateStore m_templates;
   Collection<INode> m_all_nodes;
   Random m_random;
}
