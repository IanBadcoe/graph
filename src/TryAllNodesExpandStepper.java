import java.util.Collection;
import java.util.Random;

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
      m_all_nodes = Util.FilterByCodes(graph.AllGraphNodes(), "e");
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

      IExpandStepper child = s_child_factory.MakeChild(
            m_graph, node, m_templates.GetTemplatesCopy(), m_random);

      return new Expander.ExpandRetInner(Expander.ExpandStatus.StepIn,
            child, "Trying to expand node: " + node.GetName());
   }

   public static void SetChildFactory(IChildFactory factory)
   {
      s_child_factory = factory;
   }

   private Graph m_graph;
   private TemplateStore m_templates;
   private Collection<INode> m_all_nodes;
   private Random m_random;

   static IChildFactory s_child_factory;
}
