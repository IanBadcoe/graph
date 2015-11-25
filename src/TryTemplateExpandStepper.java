import java.util.ArrayList;

class TryTemplateExpandStepper implements IStepper
{
   interface IRelaxerFactory
   {
      IStepper MakeRelaxer(Graph g, LevelGeneratorConfiguration c);
   }

   interface IAdjusterFactory
   {
      IStepper MakeAdjuster(Graph graph, DirectedEdge edge, LevelGeneratorConfiguration c);
   }

   TryTemplateExpandStepper(Graph graph, INode node, Template template, LevelGeneratorConfiguration c)
   {
      m_graph = graph;
      m_node = node;
      m_template = template;
      m_config = c;

      m_phase = Phase.ExpandRelax;
   }

   @Override
   public StepperController.StatusReportInner step(StepperController.Status status)
   {
      if (status == StepperController.Status.StepIn)
      {
         if (m_template.Expand(m_graph, m_node, m_config.Rand))
         {
            IStepper child = m_relaxer_factory.MakeRelaxer(m_graph, m_config);

            return new StepperController.StatusReportInner(StepperController.Status.StepIn,
                  child, "Relaxing successful expansion.");
         }

         return new StepperController.StatusReportInner(StepperController.Status.StepOutFailure,
               null, "Failed to expand");
      }

      if (m_phase == Phase.ExpandRelax)
      {
         return ExpandRelaxReturn(status);
      }

      return EdgeRelaxReturn(status);
   }

   private StepperController.StatusReportInner ExpandRelaxReturn(StepperController.Status status)
   {
      switch (status)
      {
         // succeeded in relaxing expanded graph,
         // look for a first edge to relax
         case StepOutSuccess:
            m_phase = Phase.EdgeCorrection;

            StepperController.StatusReportInner ret = TryLaunchEdgeAdjust();

            if (ret != null)
            {
               return ret;
            }

            return new StepperController.StatusReportInner(StepperController.Status.StepOutSuccess,
                  null, "No stressed edges to adjust");

         case StepOutFailure:
            return new StepperController.StatusReportInner(StepperController.Status.StepOutFailure,
                  null, "Failed to relax expanded node.");
      }

      // should never get here, just try to blow things up

      throw new UnsupportedOperationException();
   }

   private StepperController.StatusReportInner EdgeRelaxReturn(StepperController.Status status)
   {
      switch (status)
      {
         // succeeded in relaxing expanded graph,
         // look for a first edge to relax
         case StepOutSuccess:
            StepperController.StatusReportInner ret = TryLaunchEdgeAdjust();

            if (ret != null)
            {
               return ret;
            }

            return new StepperController.StatusReportInner(StepperController.Status.StepOutSuccess,
                  null, "No more stressed edges to adjust");

         case StepOutFailure:
            return new StepperController.StatusReportInner(StepperController.Status.StepOutFailure,
                  null, "Failed to adjust edge.");
      }

      // should never get here, just try to blow things up

      throw new UnsupportedOperationException();
   }

   private StepperController.StatusReportInner TryLaunchEdgeAdjust()
   {
      DirectedEdge e = MostStressedEdge(m_graph.allGraphEdges());

      if (e == null)
      {
         return null;
      }

      IStepper child = m_adjuster_factory.MakeAdjuster(m_graph, e, m_config);

      return new StepperController.StatusReportInner(StepperController.Status.StepIn,
            child, "Adjusting an edge.");
   }

   // only stresses above 10% are considered
   private DirectedEdge MostStressedEdge(ArrayList<DirectedEdge> edges)
   {
      double max_stress = 1.1;
      DirectedEdge ret = null;

      for(DirectedEdge e : edges)
      {
         double stress = e.Length() / e.MaxLength;

         if (stress > max_stress)
         {
            ret = e;
            max_stress = stress;
         }
      }

      return ret;
   }

   private enum Phase
   {
      ExpandRelax,
      EdgeCorrection
   }

   public static void SetRelaxerFactory(IRelaxerFactory factory)
   {
      m_relaxer_factory = factory;
   }

   public static void SetAdjusterFactory(IAdjusterFactory factory)
   {
      m_adjuster_factory = factory;
   }

   private final Graph m_graph;
   private final INode m_node;
   private final Template m_template;
   private final LevelGeneratorConfiguration m_config;

   private Phase m_phase;

   private static IRelaxerFactory m_relaxer_factory;
   private static IAdjusterFactory m_adjuster_factory;
}
