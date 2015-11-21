class TestStepper implements IStepper
{
   TestStepper(boolean succeed, Runnable action)
   {
      m_succeed = succeed;
      m_action = action;
   }

   @Override
   public StepperController.StatusReportInner step(StepperController.Status status)
   {
      if (m_action != null)
      {
         m_action.run();
      }

      if (m_succeed)
      {
         return new StepperController.StatusReportInner(StepperController.Status.StepOutSuccess,
               null, "");
      }
      else
      {
         return new StepperController.StatusReportInner(StepperController.Status.StepOutFailure,
               null, "");
      }
   }

   private final boolean m_succeed;
   private final Runnable m_action;
}

