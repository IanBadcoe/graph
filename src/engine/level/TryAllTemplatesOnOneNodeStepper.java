package engine.level;

import engine.graph.Graph;
import engine.graph.INode;
import engine.graph.Template;

import java.util.Collection;

public class TryAllTemplatesOnOneNodeStepper implements IStepper
{

   public TryAllTemplatesOnOneNodeStepper(IoCContainer m_ioc_container,
                                          Graph graph, INode node, Collection<Template> templates,
                                          LevelGeneratorConfiguration c)
   {
      m_graph = graph;
      m_node = node;
      m_templates = templates;
      m_config = c;
      this.m_ioc_container = m_ioc_container;
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

      // no matter what other previous status, if we run out of templates we're a fail
      if (m_templates.size() == 0)
      {
         return new StepperController.StatusReportInner(StepperController.Status.StepOutFailure,
               null, "engine.Node: " + m_node.getName() + " failed to expand");
      }

      Template t = LevelUtil.removeRandom(m_config.Rand, m_templates);

      IStepper child = m_ioc_container.NodeTemplateExpanderFactory.makeNodeTemplateExpander(
            m_ioc_container, m_graph, m_node, t, m_config);

      //noinspection ConstantConditions
      return new StepperController.StatusReportInner(StepperController.Status.StepIn,
            child, "Trying to expand node: " + m_node.getName() + " with template: " + t.GetName());
   }

   private final Graph m_graph;
   private final INode m_node;
   private final Collection<Template> m_templates;
   private final LevelGeneratorConfiguration m_config;
   private final IoCContainer m_ioc_container;

}
