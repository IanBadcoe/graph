import java.util.Stack;

public class Expander
{
   enum ExpandStatus
   {
      Iterate,          // current stepper requires more steps
      StepIn,           // move down into a child stepper
      StepOutSuccess,   // current stepper successfully completed, move back to parent
      StepOutFailure    // current stepper failed, revert graph and move back to parent
   }

   static class ExpandRet
   {
      final ExpandStatus Status;
      final IExpandStepper ChildStepper;
      final String Log;

      ExpandRet(ExpandStatus status,
                IExpandStepper childStepper,
                String log)
      {
         Status = status;
         ChildStepper = childStepper;
         Log = log;
      }
   }

   Expander(Graph graph, IExpandStepper initial_stepper)
   {
      m_graph = graph;
      PushStepper(initial_stepper);
      // we start with a (conceptual) step in from the invoking code
      m_last_step_status = ExpandStatus.StepIn;
   }

   ExpandRet Step()
   {
      IExpandStepper stepper = CurrentStepper();

      if (stepper == null)
         throw new NullPointerException("Attempt to step without an initial stepper.  Either you failed to supply one, or this Expander has completed.");

      ExpandRet ret = stepper.Step(m_last_step_status);

      m_last_step_status = ret.Status;

      switch (m_last_step_status)
      {
         case StepIn:
            PushStepper(ret.ChildStepper);
            break;

         case StepOutFailure:
            PopStepper(false);
            break;

         case StepOutSuccess:
            PopStepper(true);
            break;
      }

      // we don't need to tell the client about our stepping in and out
      // we just need to continue iteration if there is something to iterate
      if (CurrentStepper() != null)
      {
         ret = new ExpandRet(ExpandStatus.Iterate,
               ret.ChildStepper, ret.Log);
      }

      return ret;
   }

   private void PushStepper(IExpandStepper stepper)
   {
      m_stack.push(
            new OrderedPair<>(stepper, m_graph.CreateRestorePoint()));
   }

   private IExpandStepper CurrentStepper()
   {
      if (m_stack.empty())
         return null;

      return m_stack.peek().First;
   }

   private void PopStepper(boolean success)
   {
      if (!success)
      {
         m_stack.pop().Second.Restore();
      }
      else
      {
         m_stack.pop().Second.Commit();
      }
   }

   private Stack<OrderedPair<IExpandStepper, IGraphRestore>>
         m_stack = new Stack<>();
   private Graph m_graph;
   private ExpandStatus m_last_step_status;
}
