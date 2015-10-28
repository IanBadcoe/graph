import java.util.Collection;
import java.util.Random;

class TryAllTemplatesOnOneNodeStepper implements IExpandStepper
{
   TryAllTemplatesOnOneNodeStepper(Graph graph, INode node, Collection<Template> templates,
                                   Random random)
   {
      m_graph = graph;
      m_node = node;
      m_templates = templates;
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

      // no matter what other previous status, if we run out of templates we're a fail
      if (m_templates.size() == 0)
      {
         return new Expander.ExpandRet(Expander.ExpandStatus.StepOutFailure,
               null, "Node: " + m_node.GetName() + " failed to expand");
      }

      Template t = Util.RemoveRandom(m_random, m_templates);

      IExpandStepper child = new TryTemplateExpandStepper(
            m_graph, m_node, t, m_random);

      return new Expander.ExpandRet(Expander.ExpandStatus.StepIn,
            child, "Trying to expand node: " + m_node.GetName() + " with template: " + t.GetName());
   }

   private Graph m_graph;
   private INode m_node;
   private Collection<Template> m_templates;
   private Random m_random;
}
