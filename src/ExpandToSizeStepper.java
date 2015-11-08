import java.util.Random;

class ExpandToSizeStepper implements IExpandStepper
{
   public interface IChildFactory
   {
      IExpandStepper MakeChild(Graph g, TemplateStore ts, Random r);
   }

   ExpandToSizeStepper(Graph graph, int required_size, TemplateStore templates,
         Random random)
   {
      m_graph = graph;
      m_orig_size = m_graph == null ? 0 : m_graph.NumNodes();
      m_required_size = required_size;
      m_templates = templates;
      m_random = random;
   }

   @Override
   public Expander.ExpandRetInner Step(Expander.ExpandStatus status)
   {
      switch (status)
      {
         case StepIn:
         case StepOutSuccess:
            if (m_graph.NumNodes() >= m_required_size)
            {
               return new Expander.ExpandRetInner(Expander.ExpandStatus.StepOutSuccess,
                     null, "Target size reached");
            }

            IExpandStepper child = m_child_factory.MakeChild(m_graph, m_templates, m_random);

            return new Expander.ExpandRetInner(Expander.ExpandStatus.StepIn,
                  child, "More expansion required.");

         case StepOutFailure:
            if (m_graph.NumNodes() > m_orig_size)
            {
               return new Expander.ExpandRetInner(Expander.ExpandStatus.StepOutSuccess,
                     null, "Partial success");
            }

            return new Expander.ExpandRetInner(Expander.ExpandStatus.StepOutFailure,
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
   private final Random m_random;
   private final int m_orig_size;

   private static IChildFactory m_child_factory;
}
