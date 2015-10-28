import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by badcoei on 23/10/2015.
 */
public class TemplateStoreTest
{

   @Test
   public void testAddTemplate() throws Exception
   {
      TemplateStore ts = new TemplateStore();

      TemplateBuilder tb1 = new TemplateBuilder("a", "");
      TemplateBuilder tb2 = new TemplateBuilder("b", "");
      TemplateBuilder tb1a = new TemplateBuilder("a", "");

      Template t1 = tb1.Build();
      Template t2 = tb2.Build();
      Template t1a = tb1a.Build();

      assertEquals(0, ts.GetNumTemplates());

      assertTrue(ts.AddTemplate(t1));
      assertEquals(1, ts.GetNumTemplates());

      // can't add same one again
      assertFalse(ts.AddTemplate(t1));
      assertEquals(1, ts.GetNumTemplates());

      // can't add one with same name
      assertFalse(ts.AddTemplate(t1a));
      assertEquals(1, ts.GetNumTemplates());

      assertTrue(ts.AddTemplate(t2));
      assertEquals(2, ts.GetNumTemplates());
   }

   @Test
   public void testGetTemplatesCopy() throws Exception
   {
      TemplateStore ts = new TemplateStore();

      TemplateBuilder tb1 = new TemplateBuilder("a", "");
      TemplateBuilder tb2 = new TemplateBuilder("b", "");

      Template t1 = tb1.Build();
      Template t2 = tb2.Build();

      ts.AddTemplate(t1);
      ts.AddTemplate(t2);

      ArrayList<Template> copy = ts.GetTemplatesCopy();

      assertEquals(2, copy.size());
      assertTrue(copy.contains(t1));
      assertTrue(copy.contains(t2));

      // master list not changed by editing copy...
      copy.clear();
      assertEquals(2, ts.GetNumTemplates());
   }

   @Test
   public void testFindByName() throws Exception
   {
      TemplateStore ts = new TemplateStore();

      TemplateBuilder tb1 = new TemplateBuilder("a", "");
      TemplateBuilder tb2 = new TemplateBuilder("b", "");

      Template t1 = tb1.Build();
      Template t2 = tb2.Build();

      ts.AddTemplate(t1);
      ts.AddTemplate(t2);

      assertEquals(t1, ts.FindByName("a"));
      assertEquals(t2, ts.FindByName("b"));
      assertNull(ts.FindByName("Richard of York"));
   }

   @Test
   public void testContains()
   {
      TemplateStore ts = new TemplateStore();

      TemplateBuilder tb1 = new TemplateBuilder("a", "");
      TemplateBuilder tb2 = new TemplateBuilder("b", "");

      Template t1 = tb1.Build();
      Template t2 = tb2.Build();

      ts.AddTemplate(t1);
      ts.AddTemplate(t2);

      assertTrue(ts.Contains("a"));
      assertTrue(ts.Contains("b"));
      assertFalse(ts.Contains("Ambivalent Bob"));
   }
}