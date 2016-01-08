package engine;

public class EdgeAdjusterStepper implements IStepper
{
   public interface IChildFactory
   {
      IStepper MakeChild(Graph g, LevelGeneratorConfiguration c);
   }

   public EdgeAdjusterStepper(Graph graph, DirectedEdge edge, LevelGeneratorConfiguration c)
   {
      m_graph = graph;
      m_edge = edge;
      m_config = c;
   }

   @Override
   public StepperController.StatusReportInner step(StepperController.Status status)
   {
      switch (status)
      {
         case StepIn:
            SplitEdge();

            IStepper child = m_child_factory.MakeChild(m_graph, m_config);

            return new StepperController.StatusReportInner(StepperController.Status.StepIn,
                  child, "Relaxing split edge.");

         case StepOutSuccess:
            return new StepperController.StatusReportInner(StepperController.Status.StepOutSuccess,
                  null, "Successfully relaxed split edge.");

         case StepOutFailure:
            return new StepperController.StatusReportInner(StepperController.Status.StepOutFailure,
                  null, "Failed to relax split edge.");
      }

      // shouldn't get here, crash horribly

      throw new UnsupportedOperationException();
   }

   private void SplitEdge()
   {
      INode c = m_graph.addNode("c", "", "EdgeExtend",
            m_edge.HalfWidth * 2,
            n -> new CircularGeomLayout(n.getPos(), n.getRad() / 2));

      XY mid = m_edge.Start.getPos().plus(m_edge.End.getPos()).divide(2);

      c.setPos(mid);

      m_graph.disconnect(m_edge.Start, m_edge.End);
      // idea of lengths is to force no more length but allow
      // a longer corridor if required
      DirectedEdge de1 =  m_graph.connect(m_edge.Start, c, m_edge.MinLength / 2, m_edge.MaxLength, m_edge.HalfWidth);
      DirectedEdge de2 =  m_graph.connect(c, m_edge.End, m_edge.MinLength / 2, m_edge.MaxLength, m_edge.HalfWidth);

      de1.SetColour(m_edge.GetColour());
      de2.SetColour(m_edge.GetColour());
   }

   public static void SetChildFactory(IChildFactory factory)
   {
      m_child_factory = factory;
   }

   private final Graph m_graph;
   private final DirectedEdge m_edge;
   private final LevelGeneratorConfiguration m_config;


   private static IChildFactory m_child_factory;
}
