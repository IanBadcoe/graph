import java.util.Collection;
import java.util.Random;

class TryAllTemplatesOnOneNodeStepper implements IExpandStepper
{
   public interface IChildFactory
   {
      IExpandStepper MakeChild(Graph g, INode n, Template y, Random r);
   }

   TryAllTemplatesOnOneNodeStepper(Graph graph, INode node, Collection<Template> templates,
                                   Random random)
   {
      m_graph = graph;
      m_node = node;
      m_templates = templates;
      m_random = random;
   }

   @Override
   public Expander.ExpandRetInner Step(Expander.ExpandStatus status)
   {
      // if our child succeeds, we succeed
      if (status == Expander.ExpandStatus.StepOutSuccess)
      {
         return new Expander.ExpandRetInner(Expander.ExpandStatus.StepOutSuccess,
               null, "Graph Expand Step Succeeded");
      }

      // no matter what other previous status, if we run out of templates we're a fail
      if (m_templates.size() == 0)
      {
         return new Expander.ExpandRetInner(Expander.ExpandStatus.StepOutFailure,
               null, "Node: " + m_node.GetName() + " failed to expand");
      }

      Template t = Util.RemoveRandom(m_random, m_templates);

      IExpandStepper child = s_child_factory.MakeChild(
            m_graph, m_node, t, m_random);

      return new Expander.ExpandRetInner(Expander.ExpandStatus.StepIn,
            child, "Trying to expand node: " + m_node.GetName() + " with template: " + t.GetName());
   }

   public static void SetChildFactory(IChildFactory factory)
   {
      s_child_factory = factory;
   }

   private Graph m_graph;
   private INode m_node;
   private Collection<Template> m_templates;
   private Random m_random;

   private static IChildFactory s_child_factory;
}
