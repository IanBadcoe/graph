package engine;

import engine.graph.Graph;
import engine.graph.IGraphRestore;

import java.util.Stack;

public class StepperController
{
   public enum Status
   {
      Iterate,          // current stepper requires more steps
      StepIn,           // move down into a child stepper
      StepOutSuccess,   // current stepper successfully completed, move back to parent
      StepOutFailure    // current stepper failed, revert graph and move back to parent
   }

   static class StatusReportInner
   {
      final StepperController.Status Status;
      final IStepper ChildStepper;
      final String Log;

      StatusReportInner(StepperController.Status status,
                IStepper childStepper,
                String log)
      {
         Status = status;
         ChildStepper = childStepper;
         Log = log;
      }
   }

   public static class StatusReport
   {
      public final StepperController.Status Status;
      public final String Log;
      public final boolean Complete;

      StatusReport(StatusReportInner eri,
                boolean complete)
      {
         Status = eri.Status;
         Log = eri.Log;

         Complete = complete;
      }

      StatusReport(StepperController.Status status,
            String log,
            boolean complete)
      {
         Status = status;
         Log = log;

         Complete = complete;
      }
   }

   StepperController(Graph graph, IStepper initial_stepper)
   {
      m_graph = graph;
      PushStepper(initial_stepper);
      // we start with a (conceptual) step in from the invoking code
      m_last_step_status = Status.StepIn;
   }

   StatusReport Step()
   {
      IStepper stepper = CurrentStepper();

      if (stepper == null)
         throw new NullPointerException("Attempt to step without an initial stepper.  Either you failed to supply one, or this engine.StepperController has completed.");

      StatusReportInner eri = stepper.step(m_last_step_status);

      m_last_step_status = eri.Status;

      switch (m_last_step_status)
      {
         case StepIn:
            PushStepper(eri.ChildStepper);
            break;

         case StepOutFailure:
            PopStepper(false);
            break;

         case StepOutSuccess:
            PopStepper(true);
            break;
      }

      return new StatusReport(eri, CurrentStepper() == null);
   }

   private void PushStepper(IStepper stepper)
   {
      m_stack.push(
            new OrderedPair<>(stepper, m_graph != null ? m_graph.createRestorePoint() : null));
   }

   private IStepper CurrentStepper()
   {
      if (m_stack.empty())
         return null;

      return m_stack.peek().First;
   }

   private void PopStepper(boolean success)
   {
      IGraphRestore igr = m_stack.pop().Second;

      if (!success && igr != null)
      {
         igr.Restore();
      }
   }

   private final Stack<OrderedPair<IStepper, IGraphRestore>>
         m_stack = new Stack<>();
   private final Graph m_graph;
   private Status m_last_step_status;
}
