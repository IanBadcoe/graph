import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class TryAllTemplatesOnOneNodeStepperTest
{
   private final ArrayList<Template> m_templates = new ArrayList<>();

   class FailStepperLoggingTemplates extends TestStepper
   {
      FailStepperLoggingTemplates(Template template)
      {
         super(false, null);
         m_templates.add(template);
      }

      @Override
      public StepperController.StatusReportInner step(StepperController.Status status)
      {
         return new StepperController.StatusReportInner(StepperController.Status.StepOutFailure,
               null, "");
      }
   }

   @Test
   public void testTryAllTemplates() throws Exception
   {
      TryAllTemplatesOnOneNodeStepper.SetChildFactory(
            (a, b, c, d) -> new FailStepperLoggingTemplates(c));

      Graph g = new Graph();

      INode n1 = g.addNode("", "", "", 0);

      TemplateStore ts = new TemplateStore1();

      StepperController e = new StepperController(g,
            new TryAllTemplatesOnOneNodeStepper(g, n1, ts.GetTemplatesCopy(), new LevelGeneratorConfiguration(1)));

      StepperController.StatusReport ret;

      m_templates.clear();

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(StepperController.Status.StepOutFailure, ret.Status);

      assertEquals(ts.NumTemplates(), m_templates.size());

      for(Template t : ts.GetTemplatesCopy())
      {
         assertTrue(m_templates.contains(t));
      }
   }

   @Test
   public void testSuccess() throws Exception
   {
      TryAllTemplatesOnOneNodeStepper.SetChildFactory(
            (a, b, c, d) -> new TestStepper(true, null));

      Graph g = new Graph();

      INode n1 = g.addNode("", "", "", 0);

      TemplateStore ts = new TemplateStore1();

      StepperController e = new StepperController(g,
            new TryAllTemplatesOnOneNodeStepper(g, n1, ts.GetTemplatesCopy(), new LevelGeneratorConfiguration(1)));

      StepperController.StatusReport ret;

      m_templates.clear();

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(StepperController.Status.StepOutSuccess, ret.Status);
   }
}