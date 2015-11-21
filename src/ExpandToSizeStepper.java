class ExpandToSizeStepper implements IStepper
{
   public interface IChildFactory
   {
      IStepper MakeChild(Graph g, TemplateStore ts, LevelGeneratorConfiguration c);
   }

   ExpandToSizeStepper(Graph graph, int required_size, TemplateStore templates,
         LevelGeneratorConfiguration c)
   {
      m_graph = graph;
      m_orig_size = m_graph == null ? 0 : m_graph.NumNodes();
      m_required_size = required_size;
      m_templates = templates;
      m_config = c;
   }

   @Override
   public StepperController.ExpandRetInner Step(StepperController.ExpandStatus status)
   {
      switch (status)
      {
         case StepIn:
         case StepOutSuccess:
            if (m_graph.NumNodes() >= m_required_size)
            {
               return new StepperController.ExpandRetInner(StepperController.ExpandStatus.StepOutSuccess,
                     null, "Target size reached");
            }

            IStepper child = m_child_factory.MakeChild(m_graph, m_templates, m_config);

            return new StepperController.ExpandRetInner(StepperController.ExpandStatus.StepIn,
                  child, "More expansion required.");

         case StepOutFailure:
            if (m_graph.NumNodes() > m_orig_size)
            {
               return new StepperController.ExpandRetInner(StepperController.ExpandStatus.StepOutSuccess,
                     null, "Partial success");
            }

            return new StepperController.ExpandRetInner(StepperController.ExpandStatus.StepOutFailure,
                  null, "Failed.");
      }

      throw new UnsupportedOperationException();
   }

   static void SetChildFactory(IChildFactory factory)
   {
      m_child_factory = factory;
   }

   private final Graph m_graph;
   private final int m_required_size;
   private final TemplateStore m_templates;
   private final LevelGeneratorConfiguration m_config;
   private final int m_orig_size;

   private static IChildFactory m_child_factory;
}
