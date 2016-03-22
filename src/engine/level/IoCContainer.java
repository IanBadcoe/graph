package engine.level;

import engine.graph.DirectedEdge;
import engine.graph.Graph;
import engine.graph.INode;
import engine.graph.Template;
import engine.graph.TemplateStore;

import java.util.Collection;

public class IoCContainer
{
   final public IRelaxerFactory RelaxerFactory;
   final public IAllNodesExpanderFactory AllNodesExpanderFactory;
   final public INodeExpanderFactory NodeExpanderFactory;
   final public INodeTemplateExpanderFactory NodeTemplateExpanderFactory;
   final public IAdjusterFactory AdjusterFactory;

   public IoCContainer(
         IRelaxerFactory relaxerFactory,
         IAllNodesExpanderFactory allNodesExpanderFactory,
         INodeExpanderFactory nodeExpanderFactory,
         INodeTemplateExpanderFactory nodeTemplateExpanderFactory,
         IAdjusterFactory adjusterFactory)
   {
      RelaxerFactory = relaxerFactory;
      AllNodesExpanderFactory = allNodesExpanderFactory;
      NodeExpanderFactory = nodeExpanderFactory;
      NodeTemplateExpanderFactory = nodeTemplateExpanderFactory;
      AdjusterFactory = adjusterFactory;
   }

   public interface IRelaxerFactory
   {
      IStepper makeRelaxer(IoCContainer ioc_container,
            Graph g, LevelGeneratorConfiguration c);
   }

   public interface IAllNodesExpanderFactory
   {
      IStepper makeAllNodesExpander(IoCContainer ioc_container,
                                    Graph g, TemplateStore ts, LevelGeneratorConfiguration c);
   }

   public interface INodeExpanderFactory
   {
      IStepper makeNodeExpander(IoCContainer ioc_container,
                                Graph g, INode n, Collection<Template> templates,
                                LevelGeneratorConfiguration c);
   }

   public interface INodeTemplateExpanderFactory
   {
      IStepper makeNodeTemplateExpander(IoCContainer ioc_container,
            Graph g, INode n, Template t, LevelGeneratorConfiguration c);
   }

   public interface IAdjusterFactory
   {
      IStepper MakeAdjuster(IoCContainer ioc_container,
                            Graph graph, DirectedEdge edge, LevelGeneratorConfiguration c);
   }
}
