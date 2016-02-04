package engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class TryAllNodesExpandStepper implements IStepper
{

   public TryAllNodesExpandStepper(IoCContainer m_ioc_container, Graph graph, TemplateStore templates,
         LevelGeneratorConfiguration c)
   {
      this.m_ioc_container = m_ioc_container;
      m_graph = graph;
      m_templates = templates;
      m_all_nodes = graph.allGraphNodes().stream().filter(n -> n.getCodes().contains("e"))
            .collect(Collectors.toCollection(ArrayList::new));
      m_config = c;
   }

   @Override
   public StepperController.StatusReportInner step(StepperController.Status status)
   {
      // if our child succeeds, we succeed
      if (status == StepperController.Status.StepOutSuccess)
      {
         return new StepperController.StatusReportInner(StepperController.Status.StepOutSuccess,
               null, "engine.Graph Expand step Succeeded");
      }

      // no matter what other previous status, if we run out of nodes we're a fail
      if (m_all_nodes.size() == 0)
      {
         return new StepperController.StatusReportInner(StepperController.Status.StepOutFailure,
               null, "All nodes failed to expand");
      }

      INode node = Util.removeRandom(m_config.Rand, m_all_nodes);

      Collection<Template> templates = m_templates.GetTemplatesCopy();

      // if this was our last chance at a node, take only templates that expand further
      // (could also allow those that expand enough, but that would involve copying the
      // required size down here...
      if (m_all_nodes.size() == 0)
         templates = templates.stream().filter(t -> t.GetCodes().contains("e"))
               .collect(Collectors.toCollection(ArrayList::new));

      IStepper child = m_ioc_container.NodeExpanderFactory.makeNodeExpander(
            m_ioc_container, m_graph, node, templates, m_config);

      //noinspection ConstantConditions
      return new StepperController.StatusReportInner(StepperController.Status.StepIn,
            child, "Trying to expand node: " + node.getName());
   }

   private final Graph m_graph;
   private final TemplateStore m_templates;
   private final Collection<INode> m_all_nodes;
   private final LevelGeneratorConfiguration m_config;

   private final IoCContainer m_ioc_container;
}
