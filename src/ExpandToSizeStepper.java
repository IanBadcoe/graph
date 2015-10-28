import java.util.Random;

class ExpandToSizeStepper implements IExpandStepper
{
   ExpandToSizeStepper(Graph graph, int required_size, TemplateStore templates,
         Random random)
   {
      m_graph = graph;
      m_required_size = required_size;
      m_templates = templates;
      m_random = random;
   }

   @Override
   public Expander.ExpandRet Step(Expander.ExpandStatus status)
   {
      switch (status)
      {
         case StepIn:
         case StepOutSuccess:
            if (m_graph.NumNodes() >= m_required_size)
            {
               return new Expander.ExpandRet(Expander.ExpandStatus.StepOutSuccess,
                     null, "Target size reached");
            }

            IExpandStepper child = new TryAllNodesExpandStepper(m_graph, m_templates, m_random);

            return new Expander.ExpandRet(Expander.ExpandStatus.StepIn,
                  child, "More expansion required.");

         case StepOutFailure:
            return new Expander.ExpandRet(Expander.ExpandStatus.StepOutFailure,
                  null, "Failed.");
      }

      // don't expect to get here

      assert false;

      return null;
   }

   Graph m_graph;
   int m_required_size;
   TemplateStore m_templates;
   Random m_random;
}
