import org.junit.Test;

import static org.junit.Assert.*;

public class DirectedEdgePairTest
{
   @Test
   public void testHashCode() throws Exception
   {
      Node n1 = new Node("", "", "", 0);
      Node n2 = new Node("", "", "", 0);
      Node n3 = new Node("", "", "", 0);
      Node n4 = new Node("", "", "", 0);
      Node n5 = new Node("", "", "", 0);

      DirectedEdge de1 = new DirectedEdge(n1, n2, 0, 0, 0);
      DirectedEdge de1a = new DirectedEdge(n1, n2, 1, 2, 3);
      DirectedEdge de2 = new DirectedEdge(n3, n4, 0, 0, 0);
      DirectedEdge de2a = new DirectedEdge(n3, n4, 3, 2, 1);
      DirectedEdge de3 = new DirectedEdge(n1, n5, 0, 0, 0);
      DirectedEdge de4 = new DirectedEdge(n5, n2, 0, 0, 0);
      DirectedEdge de5 = new DirectedEdge(n3, n5, 0, 0, 0);
      DirectedEdge de6 = new DirectedEdge(n5, n4, 0, 0, 0);

      DirectedEdgePair dep1 = new DirectedEdgePair(de1, de2, 0.1, 0.2);
      DirectedEdgePair dep1a = new DirectedEdgePair(de1a, de2a, 0.3, 0.4);
      DirectedEdgePair dep1b = new DirectedEdgePair(de2, de1, 0.3, 0.4);

      // only the edge hashes contribute to the overall hash
      // and those should only care about node identities
      assertEquals(dep1.hashCode(), dep1a.hashCode());

      // we don't care about the edge order
      assertEquals(dep1.hashCode(), dep1b.hashCode());

      DirectedEdgePair dep2 = new DirectedEdgePair(de3, de2, 0, 0);
      DirectedEdgePair dep3 = new DirectedEdgePair(de4, de2, 0, 0);
      DirectedEdgePair dep4 = new DirectedEdgePair(de1, de5, 0, 0);
      DirectedEdgePair dep5 = new DirectedEdgePair(de1, de6, 0, 0);

      // and any one node changing should change the DEP
      assertNotEquals(dep1.hashCode(), dep2.hashCode());
      assertNotEquals(dep1.hashCode(), dep3.hashCode());
      assertNotEquals(dep1.hashCode(), dep4.hashCode());
      assertNotEquals(dep1.hashCode(), dep5.hashCode());
   }

   @Test
   public void testEquals() throws Exception
   {
      Node n1 = new Node("", "", "", 0);
      Node n2 = new Node("", "", "", 0);
      Node n3 = new Node("", "", "", 0);
      Node n4 = new Node("", "", "", 0);
      Node n5 = new Node("", "", "", 0);

      DirectedEdge de1 = new DirectedEdge(n1, n2, 0, 0, 0);
      DirectedEdge de1a = new DirectedEdge(n1, n2, 1, 2, 3);
      DirectedEdge de2 = new DirectedEdge(n3, n4, 0, 0, 0);
      DirectedEdge de2a = new DirectedEdge(n3, n4, 3, 2, 1);
      DirectedEdge de3 = new DirectedEdge(n1, n5, 0, 0, 0);
      DirectedEdge de4 = new DirectedEdge(n5, n2, 0, 0, 0);
      DirectedEdge de5 = new DirectedEdge(n3, n5, 0, 0, 0);
      DirectedEdge de6 = new DirectedEdge(n5, n4, 0, 0, 0);

      DirectedEdgePair dep1 = new DirectedEdgePair(de1, de2, 0.1, 0.2);
      DirectedEdgePair dep1a = new DirectedEdgePair(de1a, de2a, 0.3, 0.4);
      DirectedEdgePair dep1b = new DirectedEdgePair(de2, de1, 0.3, 0.4);

      // only the edge hashes contribute to the overall hash
      // and those should only care about node identities
      assertTrue(dep1.equals(dep1a));

      // we don't care about the edge order
      assertTrue(dep1.equals(dep1b));

      DirectedEdgePair dep2 = new DirectedEdgePair(de3, de2, 0, 0);
      DirectedEdgePair dep3 = new DirectedEdgePair(de4, de2, 0, 0);
      DirectedEdgePair dep4 = new DirectedEdgePair(de1, de5, 0, 0);
      DirectedEdgePair dep5 = new DirectedEdgePair(de1, de6, 0, 0);

      // and any one node changing should change the DEP identity
      assertFalse(dep1.equals(dep2));
      assertFalse(dep1.equals(dep3));
      assertFalse(dep1.equals(dep4));
      assertFalse(dep1.equals(dep5));

      assertFalse(dep1.equals(new Integer(1)));
   }
}