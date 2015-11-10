import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by badcoei on 26/10/2015.
 */
public class NodeTest
{
   @Test
   public void testNumConnections() throws Exception
   {
      Node n1 = new Node("", "", "", 0);
      Node n2 = new Node("", "", "", 0);

      assertEquals(0, n1.numConnections());

      n1.connect(n2, 0, 0, 0);

      assertEquals(1, n1.numConnections());

      n1.disconnect(n2);

      assertEquals(0, n1.numConnections());
   }

   @Test
   public void testConnect_Exception()
   {
      {
         Node n1 = new Node("xxx", "", "", 0);
         Node n2 = new Node("yyy", "", "", 0);
         n1.connect(n2, 0, 0, 0);

         boolean thrown = false;
         try
         {
            n1.connect(n2, 0, 0, 0);
         }
         catch(IllegalArgumentException iae)
         {
            thrown = true;

            // message shoulkd mention both nodes
            assertTrue(iae.getMessage().contains("xxx"));
            assertTrue(iae.getMessage().contains("yyy"));
         }

         assertTrue(thrown);
      }

      {
         Node n1 = new Node("xxx", "", "", 0);
         Node n2 = new Node("yyy", "", "", 0);
         n1.connect(n2, 0, 0, 0);

         boolean thrown = false;
         try
         {
            n2.connect(n1, 0, 0, 0);
         }
         catch(IllegalArgumentException iae)
         {
            thrown = true;

            // message shoulkd mention both nodes
            assertTrue(iae.getMessage().contains("xxx"));
            assertTrue(iae.getMessage().contains("yyy"));
         }

         assertTrue(thrown);
      }
   }

   @Test
   public void testGetConnectionTo()
   {
      {
         Node n1 = new Node("xxx", "", "", 0);
         Node n2 = new Node("yyy", "", "", 0);
         n1.connect(n2, 0, 0, 0);

         DirectedEdge de = n1.getConnectionTo(n2);

         assertNotNull(de);
         assertEquals(de.Start, n1);
         assertEquals(de.End, n2);
      }

      {
         Node n1 = new Node("xxx", "", "", 0);
         Node n2 = new Node("yyy", "", "", 0);
         n1.connect(n2, 0, 0, 0);

         DirectedEdge de = n1.getConnectionTo(null);

         assertNull(de);

         // these are found by node-identity, not name
         de = n1.getConnectionTo(new Node("yyy", "", "", 0));

         assertNull(de);
      }
   }

   @Test
   public void testGetConnectionFrom()
   {
      {
         Node n1 = new Node("xxx", "", "", 0);
         Node n2 = new Node("yyy", "", "", 0);
         n1.connect(n2, 0, 0, 0);

         DirectedEdge de = n2.getConnectionFrom(n1);

         assertNotNull(de);
         assertEquals(de.Start, n1);
         assertEquals(de.End, n2);
      }

      {
         Node n1 = new Node("xxx", "", "", 0);
         Node n2 = new Node("yyy", "", "", 0);
         n1.connect(n2, 0, 0, 0);

         DirectedEdge de = n1.getConnectionFrom(null);

         assertNull(de);

         // these are found by node-identity, not name
         de = n1.getConnectionFrom(new Node("xxx", "", "", 0));

         assertNull(de);
      }
   }

   @Test
   public void testConnects()
   {
      Node n1 = new Node("", "", "", 0);
      Node n2 = new Node("", "", "", 0);
      Node n3 = new Node("", "", "", 0);

      n1.connect(n2, 1, 2, 3);

      assertTrue(n1.connects(n2));
      assertTrue(n2.connects(n1));
      assertFalse(n1.connects(n3));
      assertFalse(n3.connects(n1));
      assertFalse(n2.connects(n3));
      assertFalse(n3.connects(n2));

      assertTrue(n1.connectsForwards(n2));
      assertFalse(n2.connectsForwards(n1));
      assertFalse(n1.connectsBackwards(n2));
      assertTrue(n2.connectsBackwards(n1));
   }

   @Test
   public void testSetName()
   {
      Node n1 = new Node("", "", "", 0);

      assertEquals("", n1.getName());

      n1.setName("x");
      assertEquals("x", n1.getName());

      boolean thrown = false;

      try
      {
         n1.setName(null);
      }
      catch(NullPointerException npe)
      {
         thrown = true;
      }

      assertTrue(thrown);
   }

   @Test
   public void testSetColour()
   {
      Node n1 = new Node("", "", "", 0);

      assertEquals(0xff8c8c8c, n1.getColour());

      n1.setColour(0x12345678);
      assertEquals(0x12345678, n1.getColour());
   }

   static GeomLayout dummy(INode n)
   {
      return null;
   }

   @Test
   public void testGeomLayoutCreator()
   {
      Node n1 = new Node("", "", "", 0);

      assertEquals(Node.DefaultLayourGreator, n1.geomLayoutCreator());

      GeomLayout.IGeomLayoutCreateFromNode iDummy = NodeTest::dummy;
      Node n2 = new Node("", "", "", iDummy, 0);

      assertEquals(iDummy, n2.geomLayoutCreator());
   }
}