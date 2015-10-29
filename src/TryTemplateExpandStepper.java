import java.util.ArrayList;
import java.util.Random;

class TryTemplateExpandStepper implements IExpandStepper
{
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
            IExpandStepper child = new RelaxerStepper(m_graph, 1.0, 0.001, 0.01);

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

      assert false;

      return null;
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

      assert false;

      return null;
   }

   private Expander.ExpandRetInner TryLaunchEdgeAdjust()
   {
      DirectedEdge e = MostStressedEdge(m_graph.AllGraphEdges(), 100.0);

      if (e == null)
      {
         return null;
      }

      IExpandStepper child = new EdgeAdjusterStepper(m_graph, e);

      return new Expander.ExpandRetInner(Expander.ExpandStatus.StepIn,
            null, "Adjusting an edge.");
   }

   // only stresses above 10% are considered
   DirectedEdge MostStressedEdge(ArrayList<DirectedEdge> edges, double d0)
   {
      double max_stress = 1.1;
      DirectedEdge ret = null;

      for(DirectedEdge e : edges)
      {
         double stress = e.Length() / d0;

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

   private Graph m_graph;
   private INode m_node;
   private Template m_template;
   private Random m_random;

   private Phase m_phase;
}
