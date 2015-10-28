import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class TemplateBuilderTest
{
   @Test
   public void testAddNode() throws Exception
   {
      {
         TemplateBuilder t = new TemplateBuilder("", "");

         assertNull(t.FindNodeRecord("n1"));

         t.AddNode(Template.NodeType.Internal, "n1");

         Template.NodeRecord nr = t.FindNodeRecord("n1");

         assertNotNull(nr);
         assertEquals("n1", nr.Name);
         assertEquals(Template.NodeType.Internal, nr.Type);
         assertEquals(false, nr.Nudge);
         // internal nodes always have "PositionOn"
         assertNotNull(nr.PositionOn);
         assertNull(nr.PositionTowards);
         assertNull(nr.PositionAwayFrom);
         assertEquals(0.0, nr.Radius, 0.0);
         assertEquals("", nr.Codes);
      }

      {
         TemplateBuilder t = new TemplateBuilder("", "");

         assertNull(t.FindNodeRecord("n1"));

         t.AddNode(Template.NodeType.In, "n1");

         Template.NodeRecord nr = t.FindNodeRecord("n1");

         assertNotNull(nr);
         assertEquals("n1", nr.Name);
         assertEquals(Template.NodeType.In, nr.Type);
         assertEquals(false, nr.Nudge);
         // In nodes have none of these three
         assertNull(nr.PositionOn);
         assertNull(nr.PositionTowards);
         assertNull(nr.PositionAwayFrom);
         assertEquals(0.0, nr.Radius, 0.0);
         assertEquals("", nr.Codes);
      }

      {
         TemplateBuilder t = new TemplateBuilder("", "");

         assertNull(t.FindNodeRecord("n1"));

         t.AddNode(Template.NodeType.Out, "n1");

         Template.NodeRecord nr = t.FindNodeRecord("n1");

         assertNotNull(nr);
         assertEquals("n1", nr.Name);
         assertEquals(Template.NodeType.Out, nr.Type);
         assertEquals(false, nr.Nudge);
         // Out nodes have none of these three
         assertNull(nr.PositionOn);
         assertNull(nr.PositionTowards);
         assertNull(nr.PositionAwayFrom);
         assertEquals(0.0, nr.Radius, 0.0);
         assertEquals("", nr.Codes);
      }

      {
         TemplateBuilder t = new TemplateBuilder("", "");

         t.AddNode(Template.NodeType.In, "n1");

         Template.NodeRecord nr = t.FindNodeRecord("n1");

         assertNotNull(nr);
         assertEquals(Template.NodeType.In, nr.Type);
      }

      {
         TemplateBuilder t = new TemplateBuilder("", "");

         t.AddNode(Template.NodeType.Out, "n1");

         Template.NodeRecord nr = t.FindNodeRecord("n1");

         assertNotNull(nr);
         assertEquals(Template.NodeType.Out, nr.Type);
      }

      {
         TemplateBuilder t = new TemplateBuilder("", "");

         t.AddNode(Template.NodeType.In, "In");
         t.AddNode(Template.NodeType.Out, "Out");

         t.AddNode(Template.NodeType.Internal, "n1",
               true, "In", "Out", null,
               "xx", 3.0);

         Template.NodeRecord nr = t.FindNodeRecord("n1");

         assertNotNull(nr);
         assertEquals("n1", nr.Name);
         assertEquals(Template.NodeType.Internal, nr.Type);
         assertEquals(true, nr.Nudge);
         assertNotNull(nr.PositionTowards);
         assertNotNull(nr.PositionOn);
         assertNull(nr.PositionAwayFrom);
         assertEquals("In", nr.PositionOn.Name);
         assertEquals("Out", nr.PositionTowards.Name);
         assertEquals(3.0, nr.Radius, 0.0);
         assertEquals("xx", nr.Codes);
      }

      {
         TemplateBuilder t = new TemplateBuilder("", "");

         t.AddNode(Template.NodeType.In, "In");
         t.AddNode(Template.NodeType.Out, "Out");

         t.AddNode(Template.NodeType.Internal, "n1",
               true, "In", null, "Out",
               "xx", 3.0);

         Template.NodeRecord nr = t.FindNodeRecord("n1");

         assertNotNull(nr);
         assertEquals("n1", nr.Name);
         assertEquals(Template.NodeType.Internal, nr.Type);
         assertEquals(true, nr.Nudge);
         assertNotNull(nr.PositionOn);
         assertNull(nr.PositionTowards);
         assertNotNull(nr.PositionAwayFrom);
         assertEquals("In", nr.PositionOn.Name);
         assertEquals("Out", nr.PositionAwayFrom.Name);
         assertEquals(3.0, nr.Radius, 0.0);
         assertEquals("xx", nr.Codes);
      }
   }

   @Test
   public void testAddNode_Exceptions() throws Exception
   {
      {
         TemplateBuilder t = new TemplateBuilder("", "");

         boolean thrown = false;

         try
         {
            t.AddNode(Template.NodeType.Internal, null);
         }
         catch(NullPointerException npe)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         TemplateBuilder t = new TemplateBuilder("", "");

         boolean thrown = false;

         // we use "->" to separate node names when naming connections
         // therefore do nto permit in node names themselves...
         try
         {
            t.AddNode(Template.NodeType.Internal, "x->");
         }
         catch(IllegalArgumentException iae)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         TemplateBuilder t = new TemplateBuilder("", "");

         boolean thrown = false;

         // we use "<target>" to mean the node we are replacing, so we can't
         // highjack that
         try
         {
            t.AddNode(Template.NodeType.Internal, "<target>");
         }
         catch(IllegalArgumentException iae)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         TemplateBuilder t = new TemplateBuilder("", "");

         boolean thrown = false;

         // we use "<target>" to mean the node we are replacing, so we can't
         // highjack that
         try
         {
            t.AddNode(Template.NodeType.Target, "x");
         }
         catch(IllegalArgumentException iae)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         TemplateBuilder t = new TemplateBuilder("", "");

         t.AddNode(Template.NodeType.Internal, "n1");

         boolean thrown = false;

         try
         {
            t.AddNode(Template.NodeType.Internal, "n1");
         }
         catch(TemplateBuilder.DuplicateNodeException dne)
         {
            thrown = true;
            assertEquals("n1", dne.NodeName);
         }

         assertTrue(thrown);
      }

      {
         TemplateBuilder t = new TemplateBuilder("", "");

         boolean thrown = false;

         try
         {
            t.AddNode(Template.NodeType.Internal, "n1",
                  true, "q", null, null,
                  "", 0.0);
         }
         catch (TemplateBuilder.UnknownNodeException une)
         {
            thrown = true;
            assertEquals("q", une.NodeName);
            assertEquals("positionOnName", une.Argument);
         }

         assertTrue(thrown);
      }

      {
         TemplateBuilder t = new TemplateBuilder("", "");

         boolean thrown = false;

         try
         {
            t.AddNode(Template.NodeType.Internal, "n1",
                  true, "<target>", "qq", null,
                  "", 0.0);
         }
         catch (TemplateBuilder.UnknownNodeException une)
         {
            thrown = true;
            assertEquals("qq", une.NodeName);
            assertEquals("positionTowardsName", une.Argument);
         }

         assertTrue(thrown);
      }

      {
         TemplateBuilder t = new TemplateBuilder("", "");

         boolean thrown = false;

         try
         {
            t.AddNode(Template.NodeType.Internal, "n1",
                  true, "<target>", null, "qqq",
                  "", 0.0);
         }
         catch (TemplateBuilder.UnknownNodeException une)
         {
            thrown = true;
            assertEquals("qqq", une.NodeName);
            assertEquals("positionAwayFromName", une.Argument);
         }

         assertTrue(thrown);
      }

      {
         TemplateBuilder t = new TemplateBuilder("", "");

         boolean thrown = false;

         try
         {
            t.AddNode(Template.NodeType.Internal, "x",
                  false, null, null, null,
                  "", 0);
         }
         catch(NullPointerException npe)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }
   }

   @Test
   public void testConnect_Exceptions() throws Exception
   {
      {
         TemplateBuilder t = new TemplateBuilder("", "");

         boolean thrown = false;

         try
         {
            t.Connect(null, "x", 0, 0, 0);
         }
         catch (NullPointerException npe)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         TemplateBuilder t = new TemplateBuilder("", "");

         boolean thrown = false;

         try
         {
            t.Connect("x", null, 0, 0, 0);
         }
         catch (NullPointerException npe)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         TemplateBuilder t = new TemplateBuilder("", "");

         boolean thrown = false;

         t.AddNode(Template.NodeType.In, "y");

         try
         {
            t.Connect("x", "y", 0, 0, 0);
         }
         catch (TemplateBuilder.UnknownNodeException une)
         {
            assertEquals("from", une.Argument);
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         TemplateBuilder t = new TemplateBuilder("", "");

         boolean thrown = false;

         t.AddNode(Template.NodeType.In, "x");

         try
         {
            t.Connect("x", "y", 0, 0, 0);
         }
         catch (TemplateBuilder.UnknownNodeException une)
         {
            assertEquals("to", une.Argument);
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         TemplateBuilder t = new TemplateBuilder("", "");

         boolean thrown = false;

         t.AddNode(Template.NodeType.In, "x");

         try
         {
            t.Connect("x", "<target>", 0, 0, 0);
         }
         catch (IllegalArgumentException une)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         TemplateBuilder t = new TemplateBuilder("", "");

         boolean thrown = false;

         t.AddNode(Template.NodeType.In, "x");

         try
         {
            t.Connect("<target>", "x", 0, 0, 0);
         }
         catch (IllegalArgumentException une)
         {
            thrown = true;
         }

         assertTrue(thrown);
      }

      {
         TemplateBuilder t = new TemplateBuilder("", "");

         t.AddNode(Template.NodeType.In, "a");
         t.AddNode(Template.NodeType.In, "b");
         t.Connect("a", "b", 0, 0, 0);

         {
            boolean thrown = false;

            try
            {
               t.Connect("a", "b", 1, 2, 3);
            }
            catch (IllegalArgumentException iae)
            {
               thrown = true;
            }

            assertTrue(thrown);
         }

         {
            boolean thrown = false;

            try
            {
               t.Connect("b", "a", 0, 0, 0);
            }
            catch (IllegalArgumentException iae)
            {
               thrown = true;
            }

            assertTrue(thrown);
         }
      }
   }

   @Test
   public void testConnect() throws Exception
   {
      {
         TemplateBuilder t = new TemplateBuilder("", "");

         assertNull(t.FindConnectionRecord("a", "b"));

         t.AddNode(Template.NodeType.In, "a");
         t.AddNode(Template.NodeType.Internal, "b");

         Template.NodeRecord nra = t.FindNodeRecord("a");
         Template.NodeRecord nrb = t.FindNodeRecord("b");

         t.Connect("a", "b", 1, 2, 3);

         assertNull(t.FindConnectionRecord("b", "a"));

         Template.ConnectionRecord cr = t.FindConnectionRecord("a", "b");

         assertNotNull(cr);
         assertEquals(nra, cr.From);
         assertEquals(nrb, cr.To);
         assertEquals(1, cr.MinLength, 0.0);
         assertEquals(2, cr.MaxLength, 0.0);
         assertEquals(3, cr.Width, 0.0);
      }
   }
}