import java.util.Collection;
import java.util.Random;

class TryAllTemplatesOnOneNodeStepper implements IStepper
{
   public interface IChildFactory
   {
      IStepper MakeChild(Graph g, INode n, Template y, Random r);
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
   public StepperController.ExpandRetInner Step(StepperController.ExpandStatus status)
   {
      // if our child succeeds, we succeed
      if (status == StepperController.ExpandStatus.StepOutSuccess)
      {
         return new StepperController.ExpandRetInner(StepperController.ExpandStatus.StepOutSuccess,
               null, "Graph Expand step Succeeded");
      }

      // no matter what other previous status, if we run out of templates we're a fail
      if (m_templates.size() == 0)
      {
         return new StepperController.ExpandRetInner(StepperController.ExpandStatus.StepOutFailure,
               null, "Node: " + m_node.getName() + " failed to expand");
      }

      Template t = Util.removeRandom(m_random, m_templates);

      IStepper child = s_child_factory.MakeChild(
            m_graph, m_node, t, m_random);

      //noinspection ConstantConditions
      return new StepperController.ExpandRetInner(StepperController.ExpandStatus.StepIn,
            child, "Trying to expand node: " + m_node.getName() + " with template: " + t.GetName());
   }

   public static void SetChildFactory(IChildFactory factory)
   {
      s_child_factory = factory;
   }

   private final Graph m_graph;
   private final INode m_node;
   private final Collection<Template> m_templates;
   private final Random m_random;

   private static IChildFactory s_child_factory;

}
