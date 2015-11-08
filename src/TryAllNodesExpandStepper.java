import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;

public class TryAllNodesExpandStepper implements IExpandStepper
{
   public interface IChildFactory
   {
      IExpandStepper MakeChild(Graph g, INode n, Collection<Template> templates,
                               Random r);
   }

   TryAllNodesExpandStepper(Graph graph, TemplateStore templates,
                            Random random)
   {
      m_graph = graph;
      m_templates = templates;
      m_all_nodes = graph.AllGraphNodes().stream().filter(n -> n.GetCodes().contains("e"))
            .collect(Collectors.toCollection(ArrayList::new));
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

      // no matter what other previous status, if we run out of nodes we're a fail
      if (m_all_nodes.size() == 0)
      {
         return new Expander.ExpandRetInner(Expander.ExpandStatus.StepOutFailure,
               null, "All nodes failed to expand");
      }

      INode node = Util.RemoveRandom(m_random, m_all_nodes);

      Collection<Template> templates = m_templates.GetTemplatesCopy();

      // if this was our last chance at a node, take only templates that expand further
      // (could also allow those that expand enough, but that would involve copying the
      // required size down here...
      if (m_all_nodes.size() == 0)
         templates = templates.stream().filter(t -> t.GetCodes().contains("e"))
               .collect(Collectors.toCollection(ArrayList::new));

      IExpandStepper child = s_child_factory.MakeChild(
            m_graph, node, templates, m_random);

      return new Expander.ExpandRetInner(Expander.ExpandStatus.StepIn,
            child, "Trying to expand node: " + node.GetName());
   }

   public static void SetChildFactory(IChildFactory factory)
   {
      s_child_factory = factory;
   }

   private final Graph m_graph;
   private final TemplateStore m_templates;
   private final Collection<INode> m_all_nodes;
   private final Random m_random;

   static IChildFactory s_child_factory;
}
