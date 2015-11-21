import java.util.Collection;

class TryAllTemplatesOnOneNodeStepper implements IStepper
{
   public interface IChildFactory
   {
      IStepper MakeChild(Graph g, INode n, Template t, LevelGeneratorConfiguration c);
   }

   TryAllTemplatesOnOneNodeStepper(Graph graph, INode node, Collection<Template> templates,
         LevelGeneratorConfiguration c)
   {
      m_graph = graph;
      m_node = node;
      m_templates = templates;
      m_config = c;
   }

   @Override
   public StepperController.StatusReportInner step(StepperController.Status status)
   {
      // if our child succeeds, we succeed
      if (status == StepperController.Status.StepOutSuccess)
      {
         return new StepperController.StatusReportInner(StepperController.Status.StepOutSuccess,
               null, "Graph Expand step Succeeded");
      }

      // no matter what other previous status, if we run out of templates we're a fail
      if (m_templates.size() == 0)
      {
         return new StepperController.StatusReportInner(StepperController.Status.StepOutFailure,
               null, "Node: " + m_node.getName() + " failed to expand");
      }

      Template t = Util.removeRandom(m_config.Rand, m_templates);

      IStepper child = s_child_factory.MakeChild(
            m_graph, m_node, t, m_config);

      //noinspection ConstantConditions
      return new StepperController.StatusReportInner(StepperController.Status.StepIn,
            child, "Trying to expand node: " + m_node.getName() + " with template: " + t.GetName());
   }

   public static void SetChildFactory(IChildFactory factory)
   {
      s_child_factory = factory;
   }

   private final Graph m_graph;
   private final INode m_node;
   private final Collection<Template> m_templates;
   private final LevelGeneratorConfiguration m_config;

   private static IChildFactory s_child_factory;

}
