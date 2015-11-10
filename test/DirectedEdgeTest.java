import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Nobody on 10/21/2015.
 */
public class DirectedEdgeTest
{
   @Test
   public void testHashCode() throws Exception
   {
      {
         Node n1 = new Node("a", "1", "a1", 0);
         Node n2 = new Node("b", "2", "a2", 1);

         DirectedEdge e1 = new DirectedEdge(n1, n2, 0, 0, 0);
         DirectedEdge e2 = new DirectedEdge(n1, n2, 1, 2, 3);

         // only node-identity affects the hash
         assertEquals(e1.hashCode(), e2.hashCode());
      }

      {
         Node n1 = new Node("a", "1", "a1", 0);
         Node n2 = new Node("b", "2", "a2", 1);
         Node n3 = new Node("c", "3", "a3", 2);
         Node n4 = new Node("d", "4", "a4", 3);

         DirectedEdge e1 = new DirectedEdge(n1, n2, 0, 0, 0);
         DirectedEdge e2 = new DirectedEdge(n1, n3, 0, 0, 0);
         DirectedEdge e3 = new DirectedEdge(n3, n2, 0, 0, 0);

         // any node-identity affects the hash
         assertNotEquals(e1.hashCode(), e2.hashCode());
         assertNotEquals(e1.hashCode(), e3.hashCode());
      }

      {
         Node n1 = new Node("a", "1", "a1", 0);
         Node n2 = new Node("b", "2", "a2", 1);

         DirectedEdge e1 = new DirectedEdge(n1, n2, 0, 0, 0);
         DirectedEdge e2 = new DirectedEdge(n2, n1, 0, 0, 0);

         // asymmetric
         assertNotEquals(e1.hashCode(), e2.hashCode());
      }
   }

   @Test
   public void testEquals() throws Exception
   {
      Node n1 = new Node("a", "1", "a1", 0);
      Node n2 = new Node("b", "2", "a2", 1);
      Node n3 = new Node("a", "1", "a1", 0);
      Node n4 = new Node("b", "2", "a2", 1);

      DirectedEdge e1 = new DirectedEdge(n1, n2, 0, 0, 0);

      // only the node identities affect edge identity
      assertFalse(e1.equals(null));
      assertFalse(e1.equals(new Integer(1)));
      assertTrue(e1.equals(new DirectedEdge(n1, n2, 0, 0, 0)));
      assertTrue(e1.equals(new DirectedEdge(n1, n2, 1, 1, 1)));
      assertFalse(e1.equals(new DirectedEdge(n2, n1, 1, 1, 1)));
      assertFalse(e1.equals(new DirectedEdge(n1, n4, 1, 1, 1)));
      assertFalse(e1.equals(new DirectedEdge(n3, n2, 1, 1, 1)));
   }

   @Test
   public void testOtherNode() throws Exception
   {
      Node n1 = new Node("a", "1", "a1", 0);
      Node n2 = new Node("b", "2", "a2", 1);
      Node n3 = new Node("a", "1", "a1", 0);

      DirectedEdge e1 = new DirectedEdge(n1, n2, 0, 0, 0);

      // only the node (reference) identities affect edge identity
      assertNull(e1.OtherNode(null));
      assertNull(e1.OtherNode(n3));
      assertEquals(n2, e1.OtherNode(n1));
      assertEquals(n1, e1.OtherNode(n2));
   }

   @Test
   public void testLength() throws Exception
   {
      Node n1 = new Node("a", "1", "a1", 0);
      Node n2 = new Node("b", "2", "a2", 1);

      DirectedEdge e1 = new DirectedEdge(n1, n2, 0, 0, 0);

      assertEquals(0, e1.Length(), 0);
      n1.setPos(new XY(1, 0));
      assertEquals(1, e1.Length(), 0);
      n2.setPos(new XY(0, 1));
      assertEquals(Math.sqrt(2), e1.Length(), 0);
   }

   @Test
   public void testConnects() throws Exception
   {
      Node n1 = new Node("a", "1", "a1", 0);
      Node n2 = new Node("b", "2", "a2", 1);
      Node n3 = new Node("a", "1", "a1", 0);

      DirectedEdge e1 = new DirectedEdge(n1, n2, 0, 0, 0);

      assertTrue(e1.Connects(n1));
      assertTrue(e1.Connects(n2));
      assertFalse(e1.Connects(n3));
   }
}