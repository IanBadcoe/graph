import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

public class TryAllTemplatesOnOneNodeStepperTest
{
   private final ArrayList<Template> m_templates = new ArrayList<>();

   class FailStepperLoggingTemplates extends TestStepperController
   {
      FailStepperLoggingTemplates(Template template)
      {
         super(false, null);
         m_templates.add(template);
      }

      @Override
      public StepperController.ExpandRetInner Step(StepperController.ExpandStatus status)
      {
         return new StepperController.ExpandRetInner(StepperController.ExpandStatus.StepOutFailure,
               null, "");
      }
   }

   @Test
   public void testTryAllTemplates() throws Exception
   {
      TryAllTemplatesOnOneNodeStepper.SetChildFactory(
            (a, b, c, d) -> new FailStepperLoggingTemplates(c));

      Graph g = new Graph();

      INode n1 = g.AddNode("", "", "", 0);

      TemplateStore ts = new TemplateStore1();

      StepperController e = new StepperController(g,
            new TryAllTemplatesOnOneNodeStepper(g, n1, ts.GetTemplatesCopy(), new Random(1)));

      StepperController.ExpandRet ret;

      m_templates.clear();

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(StepperController.ExpandStatus.StepOutFailure, ret.Status);

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
            (a, b, c, d) -> new TestStepperController(true, null));

      Graph g = new Graph();

      INode n1 = g.AddNode("", "", "", 0);

      TemplateStore ts = new TemplateStore1();

      StepperController e = new StepperController(g,
            new TryAllTemplatesOnOneNodeStepper(g, n1, ts.GetTemplatesCopy(), new Random(1)));

      StepperController.ExpandRet ret;

      m_templates.clear();

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(StepperController.ExpandStatus.StepOutSuccess, ret.Status);
   }
}