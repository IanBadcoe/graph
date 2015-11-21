import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;

public class TryAllNodesExpandStepper implements IStepper
{
   public interface IChildFactory
   {
      IStepper MakeChild(Graph g, INode n, Collection<Template> templates,
                               Random r);
   }

   TryAllNodesExpandStepper(Graph graph, TemplateStore templates,
                            Random random)
   {
      m_graph = graph;
      m_templates = templates;
      m_all_nodes = graph.AllGraphNodes().stream().filter(n -> n.getCodes().contains("e"))
            .collect(Collectors.toCollection(ArrayList::new));
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

      // no matter what other previous status, if we run out of nodes we're a fail
      if (m_all_nodes.size() == 0)
      {
         return new StepperController.ExpandRetInner(StepperController.ExpandStatus.StepOutFailure,
               null, "All nodes failed to expand");
      }

      INode node = Util.removeRandom(m_random, m_all_nodes);

      Collection<Template> templates = m_templates.GetTemplatesCopy();

      // if this was our last chance at a node, take only templates that expand further
      // (could also allow those that expand enough, but that would involve copying the
      // required size down here...
      if (m_all_nodes.size() == 0)
         templates = templates.stream().filter(t -> t.GetCodes().contains("e"))
               .collect(Collectors.toCollection(ArrayList::new));

      IStepper child = s_child_factory.MakeChild(
            m_graph, node, templates, m_random);

      //noinspection ConstantConditions
      return new StepperController.ExpandRetInner(StepperController.ExpandStatus.StepIn,
            child, "Trying to expand node: " + node.getName());
   }

   public static void SetChildFactory(IChildFactory factory)
   {
      s_child_factory = factory;
   }

   private final Graph m_graph;
   private final TemplateStore m_templates;
   private final Collection<INode> m_all_nodes;
   private final Random m_random;

   private static IChildFactory s_child_factory;
}
