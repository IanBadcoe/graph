package engine;

import engine.*;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.*;

public class GraphTest
{
   @Test
   public void testAddNode() throws Exception
   {
      Graph g = new Graph();

      INode n = g.addNode("n", "x", "y", 10);

      assertEquals(1, g.numNodes());
      assertEquals(0, g.numEdges());
      assertTrue(g.contains(n));

      assertEquals("n", n.getName());
      assertEquals("x", n.getCodes());
      assertEquals("y", n.getTemplate());
      assertEquals(10.0, n.getRad(), 0);

      INode n2 = g.addNode("n2", "x2", "y2", 20);

      assertEquals(2, g.numNodes());
      assertEquals(0, g.numEdges());
      assertTrue(g.contains(n2));
      assertTrue(g.contains(n));

      assertEquals("n2", n2.getName());
      assertEquals("x2", n2.getCodes());
      assertEquals("y2", n2.getTemplate());
      assertEquals(20.0, n2.getRad(), 0);
   }

   @Test
   public void testRemoveNode() throws Exception
   {
      Graph g = new Graph();

      INode n = g.addNode("n", "x", "y", 10);
      INode n2 = g.addNode("n2", "x2", "y2", 20);
      g.connect(n, n2, 0, 0, 0);

      assertEquals(2, g.numNodes());
      assertEquals(1, g.numEdges());
      assertTrue(g.contains(n2));
      assertTrue(g.contains(n));

      // cannot remove a connected node
      assertFalse(g.removeNode(n));
      assertFalse(g.removeNode(n2));

      assertEquals(2, g.numNodes());
      assertTrue(g.contains(n2));
      assertTrue(g.contains(n));

      g.disconnect(n, n2);
      assertEquals(0, g.numEdges());

      // cannot remove a node we never heard of
      assertFalse(g.removeNode(new Node("", "", "", 0)));

      assertTrue(g.removeNode(n));
      assertEquals(1, g.numNodes());
      assertTrue(g.contains(n2));
      assertTrue(!g.contains(n));

      assertTrue(g.removeNode(n2));
      assertEquals(0, g.numNodes());
      assertTrue(!g.contains(n2));
   }

   @Test
   public void testConnect() throws Exception
   {
      Graph g = new Graph();

      assertEquals(0, g.numEdges());

      INode n = g.addNode("n", "x", "y", 10);

      INode n2 = g.addNode("n2", "x2", "y2", 20);

      assertFalse(n.connects(n2));
      assertFalse(n2.connects(n));
      assertEquals(0, g.numEdges());

      assertNotNull(g.connect(n, n2, 1, 2, 3));
      assertEquals(1, g.numEdges());
      assertTrue(n.connects(n2));
      assertTrue(n2.connects(n));

      DirectedEdge e = n.getConnectionTo(n2);
      assertEquals(n, e.Start);
      assertEquals(n2, e.End);
      assertEquals(1, e.MinLength, 0);
      assertEquals(2, e.MaxLength, 0);
      assertEquals(3, e.HalfWidth, 0);
   }

   @Test
   public void testDisconnect() throws Exception
   {
      Graph g = new Graph();

      // cannot disconnect two unknown nodes
      assertFalse(g.disconnect(new Node("", "", "", 0),
            new Node("", "", "", 0)));

      INode n = g.addNode("n", "x", "y", 10);

      {
         // cannot diconnect a node we know and one we don't
         INode dummy = new Node("", "", "", 0);
         assertFalse(g.disconnect(n, dummy));
      }

      INode n2 = g.addNode("n2", "x2", "y2", 20);
      g.connect(n, n2, 0, 0, 0);
      assertEquals(1, g.numEdges());
      assertTrue(n.connects(n2));

      assertTrue((g.disconnect(n, n2)));
      assertEquals(0, g.numEdges());
      assertFalse(n.connects(n2));
   }

   @Test
   public void testAllGraphEdges() throws Exception
   {
      Graph g = new Graph();
      INode n = g.addNode("n", "", "", 0);
      INode m = g.addNode("m", "", "", 0);
      INode o = g.addNode("o", "", "", 0);

      assertEquals(0, g.numEdges());

      g.connect(n, m, 0, 0, 0);

      assertEquals(1, g.numEdges());
      assertTrue(g.allGraphEdges().contains(new DirectedEdge(n, m, 0, 0, 0)));

      g.connect(m, o, 0, 0, 0);

      assertEquals(2, g.numEdges());
      assertTrue(g.allGraphEdges().contains(new DirectedEdge(m, o, 0, 0, 0)));

      g.connect(o, n, 0, 0, 0);

      assertEquals(3, g.numEdges());
      assertTrue(g.allGraphEdges().contains(new DirectedEdge(o, n, 0, 0, 0)));

      g.disconnect(n, m);

      assertEquals(2, g.numEdges());
      assertFalse(g.allGraphEdges().contains(new DirectedEdge(n, m, 0, 0, 0)));
   }

   @Test
   public void testAllGraphNodes() throws Exception
   {
      Graph g = new Graph();

      assertEquals(0, g.numNodes());

      INode n = g.addNode("n", "", "", 0);

      assertEquals(1, g.numNodes());
      assertTrue(g.allGraphNodes().contains(n));

      INode m = g.addNode("m", "", "", 0);

      assertEquals(2, g.numNodes());
      assertTrue(g.allGraphNodes().contains(m));

      INode o = g.addNode("o", "", "", 0);

      assertEquals(3, g.numNodes());
      assertTrue(g.allGraphNodes().contains(o));

      g.removeNode(n);

      assertEquals(2, g.numNodes());
      assertFalse(g.allGraphNodes().contains(n));
   }

   class GraphRecord
   {
      GraphRecord(Graph g)
      {
         m_nodes.addAll(g.allGraphNodes());
         m_edges.addAll(g.allGraphEdges());

         for(INode n : m_nodes)
         {
            m_positions.put(n, n.getPos());
         }
      }

      boolean Compare(Graph g)
      {
         if (m_nodes.size() != g.numNodes())
            return false;

         if (!m_nodes.containsAll(g.allGraphNodes()))
            return false;

         if (m_edges.size() != g.numEdges())
            return false;

         if (!m_edges.containsAll(g.allGraphEdges()))
            return false;

         for(INode n : g.allGraphNodes())
         {
            if (!n.getPos().equals(m_positions.get(n)))
               return false;

            for(DirectedEdge e : n.getConnections())
            {
               if (!m_edges.contains(e))
                  return false;
            }
         }

         // check that the nodes know about the connections
         for(DirectedEdge e : m_edges)
         {
            Node start = (Node)e.Start;
            Node end = (Node)e.End;

            if (!start.connectsForwards(end))
               return false;

            if (!end.connectsBackwards(start))
               return false;
         }

         return true;
      }

      final HashSet<INode> m_nodes = new HashSet<>();
      final HashSet<DirectedEdge> m_edges = new HashSet<>();
      final HashMap<INode, XY> m_positions = new HashMap<>();
   }

   @Test
   public void testGraphRecord() throws Exception
   {
      // same if empty
      {
         Graph g = new Graph();

         GraphRecord gr = new GraphRecord(g);

         assertTrue(gr.Compare(g));
      }

      // same if one node
      {
         Graph g = new Graph();

         g.addNode("", "", "", 0);

         GraphRecord gr = new GraphRecord(g);

         assertTrue(gr.Compare(g));
      }

      // same if two nodes and an edge
      {
         Graph g = new Graph();

         INode n1 = g.addNode("", "", "", 0);

         INode n2 = g.addNode("", "", "", 0);

         g.connect(n1, n2, 0, 0, 0);

         GraphRecord gr = new GraphRecord(g);

         assertTrue(gr.Compare(g));
      }

      // same if node added and removed
      {
         Graph g = new Graph();

         GraphRecord gr = new GraphRecord(g);

         INode n1 = g.addNode("", "", "", 0);
         g.removeNode(n1);

         assertTrue(gr.Compare(g));
      }

      // same if edge added and removed
      {
         Graph g = new Graph();

         INode n1 = g.addNode("", "", "", 0);

         INode n2 = g.addNode("", "", "", 0);

         GraphRecord gr = new GraphRecord(g);

         g.connect(n1, n2, 0, 0, 0);

         g.disconnect(n1, n2);

         assertTrue(gr.Compare(g));
      }

      // different if node added
      {
         Graph g = new Graph();

         GraphRecord gr = new GraphRecord(g);

         g.addNode("", "", "", 0);

         assertFalse(gr.Compare(g));
      }

      // different if edge added
      {
         Graph g = new Graph();

         INode n1 = g.addNode("", "", "", 0);

         INode n2 = g.addNode("", "", "", 0);

         GraphRecord gr = new GraphRecord(g);

         g.connect(n1, n2, 0, 0, 0);

         assertFalse(gr.Compare(g));
      }

      // different if node moved
      {
         Graph g = new Graph();

         INode n1 = g.addNode("", "", "", 0);

         GraphRecord gr = new GraphRecord(g);

         n1.setPos(new XY(1, 0));

         assertFalse(gr.Compare(g));
      }
   }

   @Test
   public void testCreateRestorePoint() throws Exception
   {
      // nop
      {
         Graph g = new Graph();

         GraphRecord gr = new GraphRecord(g);

         IGraphRestore igr = g.createRestorePoint();

         assertTrue(igr.Restore());
         // restores only work once...
         assertFalse(igr.Restore());

         assertTrue(gr.Compare(g));
      }

      // add node to empty
      {
         Graph g = new Graph();

         GraphRecord gr = new GraphRecord(g);

         IGraphRestore igr = g.createRestorePoint();

         g.addNode("", "", "", 0);

         igr.Restore();

         assertTrue(gr.Compare(g));
      }

      // add nodes and edge to empty
      {
         Graph g = new Graph();

         GraphRecord gr = new GraphRecord(g);

         IGraphRestore igr = g.createRestorePoint();

         INode n1 = g.addNode("", "", "", 0);
         INode n2 = g.addNode("", "", "", 0);

         g.connect(n1, n2, 0, 0, 0);

         igr.Restore();

         assertTrue(gr.Compare(g));
      }

      // get back removed nodes and edges
      {
         Graph g = new Graph();

         INode n1 = g.addNode("", "", "", 0);
         INode n2 = g.addNode("", "", "", 0);

         g.connect(n1, n2, 0, 0, 0);

         GraphRecord gr = new GraphRecord(g);

         IGraphRestore igr = g.createRestorePoint();

         g.disconnect(n1, n2);

         g.removeNode(n1);
         g.removeNode(n2);

         igr.Restore();

         assertTrue(gr.Compare(g));
      }

      // add and remove a node shouldn't break anything
      {
         Graph g = new Graph();

         GraphRecord gr = new GraphRecord(g);

         IGraphRestore igr = g.createRestorePoint();

         INode n1 = g.addNode("", "", "", 0);

         g.removeNode(n1);

         igr.Restore();

         assertTrue(gr.Compare(g));
      }

      // add and remove an edge shouldn't break anything
      {
         Graph g = new Graph();

         INode n1 = g.addNode("", "", "", 0);
         INode n2 = g.addNode("", "", "", 0);

         GraphRecord gr = new GraphRecord(g);

         IGraphRestore igr = g.createRestorePoint();

         g.connect(n1, n2, 0, 0, 0);
         g.disconnect(n1, n2);

         igr.Restore();

         assertTrue(gr.Compare(g));
      }

      // add and remove and re-add an edge shouldn't break anything
      {
         Graph g = new Graph();

         INode n1 = g.addNode("", "", "", 0);
         INode n2 = g.addNode("", "", "", 0);

         GraphRecord gr = new GraphRecord(g);

         IGraphRestore igr = g.createRestorePoint();

         g.connect(n1, n2, 0, 0, 0);
         g.disconnect(n1, n2);
         g.connect(n1, n2, 0, 0, 0);

         igr.Restore();

         assertTrue(gr.Compare(g));
      }

      // remove and add an edge shouldn't break anything
      {
         Graph g = new Graph();

         INode n1 = g.addNode("", "", "", 0);
         INode n2 = g.addNode("", "", "", 0);
         g.connect(n1, n2, 0, 0, 0);

         GraphRecord gr = new GraphRecord(g);

         IGraphRestore igr = g.createRestorePoint();

         g.disconnect(n1, n2);
         g.connect(n1, n2, 0, 0, 0);

         igr.Restore();

         assertTrue(gr.Compare(g));
      }

      // remove and add and re-remove an edge shouldn't break anything
      {
         Graph g = new Graph();

         INode n1 = g.addNode("", "", "", 0);
         INode n2 = g.addNode("", "", "", 0);
         g.connect(n1, n2, 0, 0, 0);

         GraphRecord gr = new GraphRecord(g);

         IGraphRestore igr = g.createRestorePoint();

         g.disconnect(n1, n2);
         g.connect(n1, n2, 0, 0, 0);
         g.disconnect(n1, n2);

         igr.Restore();

         assertTrue(gr.Compare(g));
      }

      // multiple restore, unchained
      {
         Graph g = new Graph();

         GraphRecord gr1 = new GraphRecord(g);

         IGraphRestore igr1 = g.createRestorePoint();

         INode n1 = g.addNode("", "", "", 0);
         INode n2 = g.addNode("", "", "", 0);

         g.connect(n1, n2, 0, 0, 0);

         GraphRecord gr2 = new GraphRecord(g);

         IGraphRestore igr2 = g.createRestorePoint();

         INode n3 = g.addNode("", "", "", 0);
         INode n4 = g.addNode("", "", "", 0);

         g.connect(n3, n4, 0, 0, 0);

         assertEquals(igr2, g.currentRestore());

         igr2.Restore();

         assertFalse(igr2.CanBeRestored());
         assertTrue(igr1.CanBeRestored());
         assertEquals(igr1, g.currentRestore());

         assertTrue(gr2.Compare(g));

         igr1.Restore();
         assertFalse(igr1.CanBeRestored());

         assertTrue(gr1.Compare(g));
      }

      // chained restore
      {
         Graph g = new Graph();

         GraphRecord gr1 = new GraphRecord(g);

         IGraphRestore igr1 = g.createRestorePoint();

         INode n1 = g.addNode("", "", "", 0);
         INode n2 = g.addNode("", "", "", 0);

         g.connect(n1, n2, 0, 0, 0);

         IGraphRestore igr2 = g.createRestorePoint();

         INode n3 = g.addNode("", "", "", 0);
         INode n4 = g.addNode("", "", "", 0);

         g.connect(n3, n4, 0, 0, 0);

         igr1.Restore();
         assertFalse(igr2.CanBeRestored());
         assertEquals(null, g.currentRestore());

         assertTrue(gr1.Compare(g));
      }

      // restore to intermediate point then start a new restore
      {
         Graph g = new Graph();

         GraphRecord gr1 = new GraphRecord(g);

         IGraphRestore igr1 = g.createRestorePoint();

         g.addNode("", "", "", 0);

         GraphRecord gr2 = new GraphRecord(g);

         IGraphRestore igr2 = g.createRestorePoint();

         g.addNode("", "", "", 0);

         igr2.Restore();

         assertEquals(igr1, g.currentRestore());
         assertFalse(igr2.CanBeRestored());
         assertTrue(igr1.CanBeRestored());

         assertTrue(gr2.Compare(g));

         IGraphRestore igr3 = g.createRestorePoint();

         g.addNode("", "", "", 0);

         igr1.Restore();

         assertTrue(gr1.Compare(g));

         assertFalse(igr1.CanBeRestored());
         assertFalse(igr3.CanBeRestored());
         assertEquals(null, g.currentRestore());
      }

      // keep a restore but then abandon it, committing to all our changes

      // restore to intermediate point then start a new restore
      {
         Graph g = new Graph();

         IGraphRestore igr1 = g.createRestorePoint();

         g.addNode("", "", "", 0);

         IGraphRestore igr2 = g.createRestorePoint();

         g.addNode("", "", "", 0);

         IGraphRestore igr3 = g.createRestorePoint();

         g.addNode("", "", "", 0);

         GraphRecord gr1 = new GraphRecord(g);

         g.clearRestore();

         // should still have all our changes
         assertTrue(gr1.Compare(g));
         // and all the restores shoudl know they are now invalid
         assertFalse(igr1.CanBeRestored());
         assertFalse(igr2.CanBeRestored());
         assertFalse(igr3.CanBeRestored());
      }
   }

   @Test
   public void testXYBounds() throws Exception
   {
      Graph g = new Graph();

      assertTrue(g.bounds().equals(new Box()));

      INode n1 = g.addNode("", "", "", 1.0);

      assertTrue(g.bounds().equals(new Box(new XY(-1, -1), new XY(1, 1))));

      INode n2 = g.addNode("", "", "", 2.0);

      assertTrue(g.bounds().equals(new Box(new XY(-2, -2), new XY(2, 2))));

      n1.setPos(new XY(-2, 0));

      assertTrue(g.bounds().equals(new Box(new XY(-3, -2), new XY(2, 2))));

      n2.setPos(new XY(10, 10));

      assertTrue(g.bounds().equals(new Box(new XY(-3, -1), new XY(12, 12))));
   }

   @Test
   public void testPrint()
   {
      {
         Graph g = new Graph();

         g.addNode("xx", "yy", "zz", 1.0);
         g.addNode("aa", "bb", "cc", 1.0);

         String s = g.print();

         assertTrue(s.contains("xx"));
         assertTrue(s.contains("yy"));
         assertTrue(s.contains("zz"));
         assertTrue(s.contains("aa"));
         assertTrue(s.contains("bb"));
         assertTrue(s.contains("cc"));
         assertTrue(s.contains("{"));
         assertTrue(s.contains("}"));
      }

      {
         Graph g = new Graph();

         INode n1 = g.addNode("xx", "yy", "zz", 1.0);
         INode n2 = g.addNode("aa", "bb", "cc", 1.0);
         g.connect(n1, n2, 0, 0, 0);

         String s = g.print();

         String splits[] = s.split("[\\{\\}]");

         assertEquals(5, splits.length);

         // after the last closing "}" wer have only a linefeed
         assertEquals("", splits[4].trim());

         // the two nodes can come out in any order
         int first = splits[0].contains("aa") ? 0 : 2;
         int second = 2 - first;

         assertTrue(splits[first].contains("aa"));
         assertTrue(splits[second].contains("xx"));

         // each node should be followed by a connects block that mentions the other
         assertTrue(splits[first + 1].contains("xx"));
         assertTrue(splits[second + 1].contains("aa"));
      }
   }

   private void testCatchUnsupported(Runnable action)
   {
      boolean thrown = false;

      try
      {
         action.run();
      }
      catch (UnsupportedOperationException uoe)
      {
         thrown = true;
      }

      assertTrue(thrown);
   }

   @Test
   public void testConnect_Exceptions()
   {
      Graph g = new Graph();

      // cannot connect two nodes we never neard of
      testCatchUnsupported(() -> g.connect(new Node("", "", "", 0),
            new Node("", "", "", 0), 0, 0, 0));

      INode n = g.addNode("n", "x", "y", 10);

      // cannot connect a node we know and one we don't
      testCatchUnsupported(() -> g.connect(n, new Node("", "", "", 0), 0, 0, 0));
   }
}
