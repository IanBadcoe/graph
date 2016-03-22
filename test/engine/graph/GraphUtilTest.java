package engine.graph;

import engine.Util;
import engine.XY;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class GraphUtilTest
{
   @Test
   public void testEdgeIntersectSimple_Node() throws Exception
   {
      assertNotNull(GraphUtil.edgeIntersect(makeNodeAt(1, 0), makeNodeAt(-1, 0), makeNodeAt(0, 1), makeNodeAt(0, -1)));
      assertNull(GraphUtil.edgeIntersect(makeNodeAt(1, 0), makeNodeAt(-1, 0), makeNodeAt(1, 0), makeNodeAt(-1, 0)));

      assertNotNull(GraphUtil.edgeIntersect(makeNodeAt(-1, 0), makeNodeAt(1, 0), makeNodeAt(0, -1), makeNodeAt(0, 1)));
      assertNull(GraphUtil.edgeIntersect(makeNodeAt(-1, 0), makeNodeAt(1, 0), makeNodeAt(-1, 0), makeNodeAt(1, 0)));

      assertNotNull(GraphUtil.edgeIntersect(makeNodeAt(0, 1), makeNodeAt(0, -1), makeNodeAt(1, 0), makeNodeAt(-1, 0)));
      assertNull(GraphUtil.edgeIntersect(makeNodeAt(0, 1), makeNodeAt(0, -1), makeNodeAt(0, 1), makeNodeAt(0, -1)));

      assertNotNull(GraphUtil.edgeIntersect(makeNodeAt(0, -1), makeNodeAt(0, 1), makeNodeAt(-1, 0), makeNodeAt(1, 0)));
      assertNull(GraphUtil.edgeIntersect(makeNodeAt(0, -1), makeNodeAt(0, 1), makeNodeAt(0, -1), makeNodeAt(0, 1)));
   }

   @Test
   public void testEdgeIntersectAdjoining_Node() throws Exception
   {
      // we can detect end-collisions of edges
      assertNotNull(Util.edgeIntersect(new XY(1, 0), new XY(-1, 0), new XY(-1, 0), new XY(0, 1)));
      assertNotNull(Util.edgeIntersect(new XY(-1, 0), new XY(1, 0), new XY(-1, 0), new XY(0, 1)));
      assertNotNull(Util.edgeIntersect(new XY(1, 0), new XY(-1, 0), new XY(0, 1), new XY(-1, 0)));
      assertNotNull(Util.edgeIntersect(new XY(-1, 0), new XY(1, 0), new XY(0, 1), new XY(-1, 0)));

      Node n1 = makeNodeAt(1, 0);
      Node n2 = makeNodeAt(-1, 0);
      Node n3 = makeNodeAt(0, 1);

      // but we don't if we know it is deliberate edge concatenation
      // e.g. if the node is shared
      assertNull(GraphUtil.edgeIntersect(n1, n2, n2, n3));
      assertNull(GraphUtil.edgeIntersect(n2, n1, n2, n3));
      assertNull(GraphUtil.edgeIntersect(n1, n2, n3, n2));
      assertNull(GraphUtil.edgeIntersect(n2, n1, n3, n2));
   }

   @Test
   public void testEdgeIntersect_Edges()
   {
      {
         Node n1 = makeNodeAt(1, 0);
         Node n2 = makeNodeAt(-1, 0);
         Node n3 = makeNodeAt(0, 1);

         // check same adjoining-edge behaviour as we checked with nodes
         assertNull(GraphUtil.edgeIntersect(makeEdge(n1, n2), makeEdge(n2, n3)));
         assertNull(GraphUtil.edgeIntersect(makeEdge(n2, n1), makeEdge(n2, n3)));
         assertNull(GraphUtil.edgeIntersect(makeEdge(n1, n2), makeEdge(n3, n2)));
         assertNull(GraphUtil.edgeIntersect(makeEdge(n2, n1), makeEdge(n3, n2)));
      }

      // just repeat a couple of the above tests and check we get the same t values
      double[] values = {1e-6, 2e-6, 5e-6, 1e-5, 2e-5, 5e-5, 1e-4, 2e-4, 5e-4, 1e-3, 2e-3, 5e-3, 1e-2, 2e-2, 5e-2, 1e-1, 2e-1, 5e-1};

      Node n1 = makeNodeAt(0, 0);
      Node n2 = makeNodeAt(1, 0);
      DirectedEdge e1 = makeEdge(n1, n2);

      for (double f : values)
      {
         Node n3 = makeNodeAt(f, 0.5);
         Node n4 = makeNodeAt(f, -0.5);
         DirectedEdge e2 = makeEdge(n3, n4);

         {
            DirectedEdgePair ret = GraphUtil.edgeIntersect(e1, e2);

            //noinspection ConstantConditions
            assertEquals(e1, ret.m_e1);
            assertEquals(e2, ret.m_e2);
            assertEquals(f, ret.m_t1, 1e-8);
            assertEquals(0.5, ret.m_t2, 1e-8);
         }

         {
            DirectedEdgePair ret = GraphUtil.edgeIntersect(e2, e1);

            //noinspection ConstantConditions
            assertEquals(e2, ret.m_e1);
            assertEquals(e1, ret.m_e2);
            assertEquals(0.5, ret.m_t1, 1e-8);
            assertEquals(f, ret.m_t2, 1e-8);
         }
      }
   }

   // ------------------

   private Node makeNodeAt(double x, double y)
   {
      return makeRadiusNodeAt(x, y, 0.0);
   }

   private Node makeRadiusNodeAt(double x, double y, @SuppressWarnings("SameParameterValue") double radius)
   {
      Node ret = new Node("", "", "", radius);

      ret.setPos(new XY(x, y));

      return ret;
   }

   private DirectedEdge makeEdge(Node n1, Node n2)
   {
      return new DirectedEdge(n1, n2, 0, 0, 0);
   }
}
