import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

public class TryAllTemplatesOnOneNodeStepperTest
{
   ArrayList<Template> m_templates = new ArrayList<>();

   class FaiStepperLoggingTemplates extends TestStepper
   {
      FaiStepperLoggingTemplates(Template template)
      {
         super(false, null);
         m_templates.add(template);
      }

      @Override
      public Expander.ExpandRetInner Step(Expander.ExpandStatus status)
      {
         return new Expander.ExpandRetInner(Expander.ExpandStatus.StepOutFailure,
               null, "");
      }
   }

   @Test
   public void testTryAllTemplates() throws Exception
   {
      TryAllTemplatesOnOneNodeStepper.SetChildFactory(
            (a, b, c, d) -> new FaiStepperLoggingTemplates(c));

      Graph g = new Graph();

      INode n1 = g.AddNode("", "", "", 0);

      TemplateStore ts = new TemplateStore1();

      Expander e = new Expander(g,
            new TryAllTemplatesOnOneNodeStepper(g, n1, ts.GetTemplatesCopy(), new Random(1)));

      Expander.ExpandRet ret;

      m_templates.clear();

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(Expander.ExpandStatus.StepOutFailure, ret.Status);

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

      INode n1 = g.AddNode("", "", "", 0);

      TemplateStore ts = new TemplateStore1();

      Expander e = new Expander(g,
            new TryAllTemplatesOnOneNodeStepper(g, n1, ts.GetTemplatesCopy(), new Random(1)));

      Expander.ExpandRet ret;

      m_templates.clear();

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(Expander.ExpandStatus.StepOutSuccess, ret.Status);
   }
}