class TestStepper implements IExpandStepper
{
   TestStepper(boolean succeed, Runnable action)
   {
      m_succeed = succeed;
      m_action = action;
   }

   @Override
   public Expander.ExpandRetInner Step(Expander.ExpandStatus status)
   {
      if (m_action != null)
      {
         m_action.run();
      }

      if (m_succeed)
      {
         return new Expander.ExpandRetInner(Expander.ExpandStatus.StepOutSuccess,
               null, "");
      }
      else
      {
         return new Expander.ExpandRetInner(Expander.ExpandStatus.StepOutFailure,
               null, "");
      }
   }

   private final boolean m_succeed;
   private final Runnable m_action;
}

