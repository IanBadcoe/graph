class TestStepperController implements IStepper
{
   TestStepperController(boolean succeed, Runnable action)
   {
      m_succeed = succeed;
      m_action = action;
   }

   @Override
   public StepperController.ExpandRetInner Step(StepperController.ExpandStatus status)
   {
      if (m_action != null)
      {
         m_action.run();
      }

      if (m_succeed)
      {
         return new StepperController.ExpandRetInner(StepperController.ExpandStatus.StepOutSuccess,
               null, "");
      }
      else
      {
         return new StepperController.ExpandRetInner(StepperController.ExpandStatus.StepOutFailure,
               null, "");
      }
   }

   private final boolean m_succeed;
   private final Runnable m_action;
}

