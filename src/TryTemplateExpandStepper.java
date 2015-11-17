import java.util.ArrayList;
import java.util.Random;

class TryTemplateExpandStepper implements IExpandStepper
{
   interface IRelaxerFactory
   {
      IExpandStepper MakeRelaxer(Graph g, @SuppressWarnings("SameParameterValue") double max_step, @SuppressWarnings("SameParameterValue") double target_force, @SuppressWarnings("SameParameterValue") double target_move);
   }

   interface IAdjusterFactory
   {
      IExpandStepper MakeAdjuster(Graph graph, DirectedEdge edge);
   }

   TryTemplateExpandStepper(Graph graph, INode node, Template template, Random random)
   {
      m_graph = graph;
      m_node = node;
      m_template = template;
      m_random = random;

      m_phase = Phase.ExpandRelax;
   }

   @Override
   public Expander.ExpandRetInner Step(Expander.ExpandStatus status)
   {
      if (status == Expander.ExpandStatus.StepIn)
      {
         if (m_template.Expand(m_graph, m_node, m_random))
         {
            IExpandStepper child = m_relaxer_factory.MakeRelaxer(m_graph, 1.0, 0.001, 0.01);

            return new Expander.ExpandRetInner(Expander.ExpandStatus.StepIn,
                  child, "Relaxing successful expansion.");
         }

         return new Expander.ExpandRetInner(Expander.ExpandStatus.StepOutFailure,
               null, "Failed to expand");
      }

      if (m_phase == Phase.ExpandRelax)
      {
         return ExpandRelaxReturn(status);
      }

      return EdgeRelaxReturn(status);
   }

   private Expander.ExpandRetInner ExpandRelaxReturn(Expander.ExpandStatus status)
   {
      switch (status)
      {
         // succeeded in relaxing expanded graph,
         // look for a first edge to relax
         case StepOutSuccess:
            m_phase = Phase.EdgeCorrection;

            Expander.ExpandRetInner ret = TryLaunchEdgeAdjust();

            if (ret != null)
            {
               return ret;
            }

            return new Expander.ExpandRetInner(Expander.ExpandStatus.StepOutSuccess,
                  null, "No stressed edges to adjust");

         case StepOutFailure:
            return new Expander.ExpandRetInner(Expander.ExpandStatus.StepOutFailure,
                  null, "Failed to relax expanded node.");
      }

      // should never get here, just try to blow things up

      throw new UnsupportedOperationException();
   }

   private Expander.ExpandRetInner EdgeRelaxReturn(Expander.ExpandStatus status)
   {
      switch (status)
      {
         // succeeded in relaxing expanded graph,
         // look for a first edge to relax
         case StepOutSuccess:
            Expander.ExpandRetInner ret = TryLaunchEdgeAdjust();

            if (ret != null)
            {
               return ret;
            }

            return new Expander.ExpandRetInner(Expander.ExpandStatus.StepOutSuccess,
                  null, "No more stressed edges to adjust");

         case StepOutFailure:
            return new Expander.ExpandRetInner(Expander.ExpandStatus.StepOutFailure,
                  null, "Failed to adjust edge.");
      }

      // should never get here, just try to blow things up

      throw new UnsupportedOperationException();
   }

   private Expander.ExpandRetInner TryLaunchEdgeAdjust()
   {
      DirectedEdge e = MostStressedEdge(m_graph.AllGraphEdges());

      if (e == null)
      {
         return null;
      }

      IExpandStepper child = m_adjuster_factory.MakeAdjuster(m_graph, e);

      return new Expander.ExpandRetInner(Expander.ExpandStatus.StepIn,
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
   private final Random m_random;

   private Phase m_phase;

   private static IRelaxerFactory m_relaxer_factory;
   private static IAdjusterFactory m_adjuster_factory;
}
