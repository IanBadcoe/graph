package engine.level;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StepperControllerTest
{
   @Test
   public void testStatusReportCtor() throws Exception
   {
      {
         StepperController.StatusReport sr = new StepperController.StatusReport(StepperController.Status.Iterate,
               "bob", true);

         assertEquals(StepperController.Status.Iterate, sr.Status);
         assertEquals("bob", sr.Log);
         assertEquals(true, sr.Complete);
      }

      {
         StepperController.StatusReport sr = new StepperController.StatusReport(StepperController.Status.StepOutFailure,
               "dug", false);

         assertEquals(StepperController.Status.StepOutFailure, sr.Status);
         assertEquals("dug", sr.Log);
         assertEquals(false, sr.Complete);
      }
   }
}
