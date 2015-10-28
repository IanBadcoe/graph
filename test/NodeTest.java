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

      assertEquals(0, n1.NumConnections());

      n1.Connect(n2, 0, 0, 0);

      assertEquals(1, n1.NumConnections());

      n1.Disconnect(n2);

      assertEquals(0, n1.NumConnections());
   }

   @Test
   public void testConnect_Exception()
   {
      {
         Node n1 = new Node("xxx", "", "", 0);
         Node n2 = new Node("yyy", "", "", 0);
         n1.Connect(n2, 0, 0, 0);

         boolean thrown = false;
         try
         {
            n1.Connect(n2, 0, 0, 0);
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
         n1.Connect(n2, 0, 0, 0);

         boolean thrown = false;
         try
         {
            n2.Connect(n1, 0, 0, 0);
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
         n1.Connect(n2, 0, 0, 0);

         DirectedEdge de = n1.GetConnectionTo(n2);

         assertNotNull(de);
         assertEquals(de.Start, n1);
         assertEquals(de.End, n2);
      }

      {
         Node n1 = new Node("xxx", "", "", 0);
         Node n2 = new Node("yyy", "", "", 0);
         n1.Connect(n2, 0, 0, 0);

         DirectedEdge de = n1.GetConnectionTo(null);

         assertNull(de);

         // these are found by node-identity, not name
         de = n1.GetConnectionTo(new Node("yyy", "", "", 0));

         assertNull(de);
      }
   }

   @Test
   public void testGetConnectionFrom()
   {
      {
         Node n1 = new Node("xxx", "", "", 0);
         Node n2 = new Node("yyy", "", "", 0);
         n1.Connect(n2, 0, 0, 0);

         DirectedEdge de = n2.GetConnectionFrom(n1);

         assertNotNull(de);
         assertEquals(de.Start, n1);
         assertEquals(de.End, n2);
      }

      {
         Node n1 = new Node("xxx", "", "", 0);
         Node n2 = new Node("yyy", "", "", 0);
         n1.Connect(n2, 0, 0, 0);

         DirectedEdge de = n1.GetConnectionFrom(null);

         assertNull(de);

         // these are found by node-identity, not name
         de = n1.GetConnectionFrom(new Node("xxx", "", "", 0));

         assertNull(de);
      }
   }
}