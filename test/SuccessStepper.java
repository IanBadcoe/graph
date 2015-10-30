class SuccessStepper implements IExpandStepper
{
   @Override
   public Expander.ExpandRetInner Step(Expander.ExpandStatus status)
   {
      return new Expander.ExpandRetInner(Expander.ExpandStatus.StepOutSuccess,
            null, "");
   }
}

