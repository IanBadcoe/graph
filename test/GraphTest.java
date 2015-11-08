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

      INode n = g.AddNode("n", "x", "y", 10);

      assertEquals(1, g.NumNodes());
      assertEquals(0, g.NumEdges());
      assertTrue(g.Contains(n));

      assertEquals("n", n.GetName());
      assertEquals("x", n.GetCodes());
      assertEquals("y", n.GetTemplate());
      assertEquals(10.0, n.GetRad(), 0);

      INode n2 = g.AddNode("n2", "x2", "y2", 20);

      assertEquals(2, g.NumNodes());
      assertEquals(0, g.NumEdges());
      assertTrue(g.Contains(n2));
      assertTrue(g.Contains(n));

      assertEquals("n2", n2.GetName());
      assertEquals("x2", n2.GetCodes());
      assertEquals("y2", n2.GetTemplate());
      assertEquals(20.0, n2.GetRad(), 0);
   }

   @Test
   public void testRemoveNode() throws Exception
   {
      Graph g = new Graph();

      INode n = g.AddNode("n", "x", "y", 10);
      INode n2 = g.AddNode("n2", "x2", "y2", 20);
      g.Connect(n, n2, 0, 0, 0);

      assertEquals(2, g.NumNodes());
      assertEquals(1, g.NumEdges());
      assertTrue(g.Contains(n2));
      assertTrue(g.Contains(n));

      // cannot remove a connected node
      assertFalse(g.RemoveNode(n));
      assertFalse(g.RemoveNode(n2));

      assertEquals(2, g.NumNodes());
      assertTrue(g.Contains(n2));
      assertTrue(g.Contains(n));

      g.Disconnect(n, n2);
      assertEquals(0, g.NumEdges());

      // cannot remove a node we never heard of
      assertFalse(g.RemoveNode(new Node("", "", "", 0)));

      assertTrue(g.RemoveNode(n));
      assertEquals(1, g.NumNodes());
      assertTrue(g.Contains(n2));
      assertTrue(!g.Contains(n));

      assertTrue(g.RemoveNode(n2));
      assertEquals(0, g.NumNodes());
      assertTrue(!g.Contains(n2));
   }

   @Test
   public void testConnect() throws Exception
   {
      Graph g = new Graph();

      assertEquals(0, g.NumEdges());

      INode n = g.AddNode("n", "x", "y", 10);

      INode n2 = g.AddNode("n2", "x2", "y2", 20);

      assertFalse(n.Connects(n2));
      assertFalse(n2.Connects(n));
      assertEquals(0, g.NumEdges());

      assertNotNull(g.Connect(n, n2, 1, 2, 3));
      assertEquals(1, g.NumEdges());
      assertTrue(n.Connects(n2));
      assertTrue(n2.Connects(n));

      DirectedEdge e = n.GetConnectionTo(n2);
      assertEquals(n, e.Start);
      assertEquals(n2, e.End);
      assertEquals(1, e.MinLength, 0);
      assertEquals(2, e.MaxLength, 0);
      assertEquals(3, e.Width, 0);
   }

   @Test
   public void testDisconnect() throws Exception
   {
      Graph g = new Graph();

      // cannot disconnect two unknown nodes
      assertFalse(g.Disconnect(new Node("", "", "", 0),
            new Node("", "", "", 0)));

      INode n = g.AddNode("n", "x", "y", 10);

      {
         // cannot diconnect a node we know and one we don't
         INode dummy = new Node("", "", "", 0);
         assertFalse(g.Disconnect(n, dummy));
      }

      INode n2 = g.AddNode("n2", "x2", "y2", 20);
      g.Connect(n, n2, 0, 0, 0);
      assertEquals(1, g.NumEdges());
      assertTrue(n.Connects(n2));

      assertTrue((g.Disconnect(n, n2)));
      assertEquals(0, g.NumEdges());
      assertFalse(n.Connects(n2));
   }

   @Test
   public void testAllGraphEdges() throws Exception
   {
      Graph g = new Graph();
      INode n = g.AddNode("n", "", "", 0);
      INode m = g.AddNode("m", "", "", 0);
      INode o = g.AddNode("o", "", "", 0);

      assertEquals(0, g.NumEdges());

      g.Connect(n, m, 0, 0, 0);

      assertEquals(1, g.NumEdges());
      assertTrue(g.AllGraphEdges().contains(new DirectedEdge(n, m, 0, 0, 0)));

      g.Connect(m, o, 0, 0, 0);

      assertEquals(2, g.NumEdges());
      assertTrue(g.AllGraphEdges().contains(new DirectedEdge(m, o, 0, 0, 0)));

      g.Connect(o, n, 0, 0, 0);

      assertEquals(3, g.NumEdges());
      assertTrue(g.AllGraphEdges().contains(new DirectedEdge(o, n, 0, 0, 0)));

      g.Disconnect(n, m);

      assertEquals(2, g.NumEdges());
      assertFalse(g.AllGraphEdges().contains(new DirectedEdge(n, m, 0, 0, 0)));
   }

   @Test
   public void testAllGraphNodes() throws Exception
   {
      Graph g = new Graph();

      assertEquals(0, g.NumNodes());

      INode n = g.AddNode("n", "", "", 0);

      assertEquals(1, g.NumNodes());
      assertTrue(g.AllGraphNodes().contains(n));

      INode m = g.AddNode("m", "", "", 0);

      assertEquals(2, g.NumNodes());
      assertTrue(g.AllGraphNodes().contains(m));

      INode o = g.AddNode("o", "", "", 0);

      assertEquals(3, g.NumNodes());
      assertTrue(g.AllGraphNodes().contains(o));

      g.RemoveNode(n);

      assertEquals(2, g.NumNodes());
      assertFalse(g.AllGraphNodes().contains(n));
   }

   class GraphRecord
   {
      GraphRecord(Graph g)
      {
         m_nodes.addAll(g.AllGraphNodes());
         m_edges.addAll(g.AllGraphEdges());

         for(INode n : m_nodes)
         {
            m_positions.put(n, n.GetPos());
         }
      }

      boolean Compare(Graph g)
      {
         if (m_nodes.size() != g.NumNodes())
            return false;

         if (!m_nodes.containsAll(g.AllGraphNodes()))
            return false;

         if (m_edges.size() != g.NumEdges())
            return false;

         if (!m_edges.containsAll(g.AllGraphEdges()))
            return false;

         for(INode n : g.AllGraphNodes())
         {
            if (!n.GetPos().equals(m_positions.get(n)))
               return false;

            for(DirectedEdge e : n.GetConnections())
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

            if (!start.ConnectsForwards(end))
               return false;

            if (!end.ConnectsBackwards(start))
               return false;
         }

         return true;
      }

      HashSet<INode> m_nodes = new HashSet<>();
      HashSet<DirectedEdge> m_edges = new HashSet<>();
      HashMap<INode, XY> m_positions = new HashMap<>();
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

         g.AddNode("", "", "", 0);

         GraphRecord gr = new GraphRecord(g);

         assertTrue(gr.Compare(g));
      }

      // same if two nodes and an edge
      {
         Graph g = new Graph();

         INode n1 = g.AddNode("", "", "", 0);

         INode n2 = g.AddNode("", "", "", 0);

         g.Connect(n1, n2, 0, 0, 0);

         GraphRecord gr = new GraphRecord(g);

         assertTrue(gr.Compare(g));
      }

      // same if node added and removed
      {
         Graph g = new Graph();

         GraphRecord gr = new GraphRecord(g);

         INode n1 = g.AddNode("", "", "", 0);
         g.RemoveNode(n1);

         assertTrue(gr.Compare(g));
      }

      // same if edge added and removed
      {
         Graph g = new Graph();

         INode n1 = g.AddNode("", "", "", 0);

         INode n2 = g.AddNode("", "", "", 0);

         GraphRecord gr = new GraphRecord(g);

         g.Connect(n1, n2, 0, 0, 0);

         g.Disconnect(n1, n2);

         assertTrue(gr.Compare(g));
      }

      // different if node added
      {
         Graph g = new Graph();

         GraphRecord gr = new GraphRecord(g);

         g.AddNode("", "", "", 0);

         assertFalse(gr.Compare(g));
      }

      // different if edge added
      {
         Graph g = new Graph();

         INode n1 = g.AddNode("", "", "", 0);

         INode n2 = g.AddNode("", "", "", 0);

         GraphRecord gr = new GraphRecord(g);

         g.Connect(n1, n2, 0, 0, 0);

         assertFalse(gr.Compare(g));
      }

      // different if node moved
      {
         Graph g = new Graph();

         INode n1 = g.AddNode("", "", "", 0);

         GraphRecord gr = new GraphRecord(g);

         n1.SetPos(new XY(1, 0));

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

         IGraphRestore igr = g.CreateRestorePoint();

         assertTrue(igr.Restore());
         // restores only work once...
         assertFalse(igr.Restore());

         assertTrue(gr.Compare(g));
      }

      // add node to empty
      {
         Graph g = new Graph();

         GraphRecord gr = new GraphRecord(g);

         IGraphRestore igr = g.CreateRestorePoint();

         g.AddNode("", "", "", 0);

         igr.Restore();

         assertTrue(gr.Compare(g));
      }

      // add nodes and edge to empty
      {
         Graph g = new Graph();

         GraphRecord gr = new GraphRecord(g);

         IGraphRestore igr = g.CreateRestorePoint();

         INode n1 = g.AddNode("", "", "", 0);
         INode n2 = g.AddNode("", "", "", 0);

         g.Connect(n1, n2, 0, 0, 0);

         igr.Restore();

         assertTrue(gr.Compare(g));
      }

      // get back removed nodes and edges
      {
         Graph g = new Graph();

         INode n1 = g.AddNode("", "", "", 0);
         INode n2 = g.AddNode("", "", "", 0);

         g.Connect(n1, n2, 0, 0, 0);

         GraphRecord gr = new GraphRecord(g);

         IGraphRestore igr = g.CreateRestorePoint();

         g.Disconnect(n1, n2);

         g.RemoveNode(n1);
         g.RemoveNode(n2);

         igr.Restore();

         assertTrue(gr.Compare(g));
      }

      // add and remove a node shouldn't break anything
      {
         Graph g = new Graph();

         GraphRecord gr = new GraphRecord(g);

         IGraphRestore igr = g.CreateRestorePoint();

         INode n1 = g.AddNode("", "", "", 0);

         g.RemoveNode(n1);

         igr.Restore();

         assertTrue(gr.Compare(g));
      }

      // add and remove an edge shouldn't break anything
      {
         Graph g = new Graph();

         INode n1 = g.AddNode("", "", "", 0);
         INode n2 = g.AddNode("", "", "", 0);

         GraphRecord gr = new GraphRecord(g);

         IGraphRestore igr = g.CreateRestorePoint();

         g.Connect(n1, n2, 0, 0, 0);
         g.Disconnect(n1, n2);

         igr.Restore();

         assertTrue(gr.Compare(g));
      }

      // add and remove and re-add an edge shouldn't break anything
      {
         Graph g = new Graph();

         INode n1 = g.AddNode("", "", "", 0);
         INode n2 = g.AddNode("", "", "", 0);

         GraphRecord gr = new GraphRecord(g);

         IGraphRestore igr = g.CreateRestorePoint();

         g.Connect(n1, n2, 0, 0, 0);
         g.Disconnect(n1, n2);
         g.Connect(n1, n2, 0, 0, 0);

         igr.Restore();

         assertTrue(gr.Compare(g));
      }

      // remove and add an edge shouldn't break anything
      {
         Graph g = new Graph();

         INode n1 = g.AddNode("", "", "", 0);
         INode n2 = g.AddNode("", "", "", 0);
         g.Connect(n1, n2, 0, 0, 0);

         GraphRecord gr = new GraphRecord(g);

         IGraphRestore igr = g.CreateRestorePoint();

         g.Disconnect(n1, n2);
         g.Connect(n1, n2, 0, 0, 0);

         igr.Restore();

         assertTrue(gr.Compare(g));
      }

      // remove and add and re-remove an edge shouldn't break anything
      {
         Graph g = new Graph();

         INode n1 = g.AddNode("", "", "", 0);
         INode n2 = g.AddNode("", "", "", 0);
         g.Connect(n1, n2, 0, 0, 0);

         GraphRecord gr = new GraphRecord(g);

         IGraphRestore igr = g.CreateRestorePoint();

         g.Disconnect(n1, n2);
         g.Connect(n1, n2, 0, 0, 0);
         g.Disconnect(n1, n2);

         igr.Restore();

         assertTrue(gr.Compare(g));
      }

      // multiple restore, unchained
      {
         Graph g = new Graph();

         GraphRecord gr1 = new GraphRecord(g);

         IGraphRestore igr1 = g.CreateRestorePoint();

         INode n1 = g.AddNode("", "", "", 0);
         INode n2 = g.AddNode("", "", "", 0);

         g.Connect(n1, n2, 0, 0, 0);

         GraphRecord gr2 = new GraphRecord(g);

         IGraphRestore igr2 = g.CreateRestorePoint();

         INode n3 = g.AddNode("", "", "", 0);
         INode n4 = g.AddNode("", "", "", 0);

         g.Connect(n3, n4, 0, 0, 0);

         assertEquals(igr2, g.CurrentRestore());

         igr2.Restore();

         assertFalse(igr2.CanBeRestored());
         assertTrue(igr1.CanBeRestored());
         assertEquals(igr1, g.CurrentRestore());

         assertTrue(gr2.Compare(g));

         igr1.Restore();
         assertFalse(igr1.CanBeRestored());

         assertTrue(gr1.Compare(g));
      }

      // chained restore
      {
         Graph g = new Graph();

         GraphRecord gr1 = new GraphRecord(g);

         IGraphRestore igr1 = g.CreateRestorePoint();

         INode n1 = g.AddNode("", "", "", 0);
         INode n2 = g.AddNode("", "", "", 0);

         g.Connect(n1, n2, 0, 0, 0);

         IGraphRestore igr2 = g.CreateRestorePoint();

         INode n3 = g.AddNode("", "", "", 0);
         INode n4 = g.AddNode("", "", "", 0);

         g.Connect(n3, n4, 0, 0, 0);

         igr1.Restore();
         assertFalse(igr2.CanBeRestored());
         assertEquals(null, g.CurrentRestore());

         assertTrue(gr1.Compare(g));
      }

      // restore to intermediate point then start a new restore
      {
         Graph g = new Graph();

         GraphRecord gr1 = new GraphRecord(g);

         IGraphRestore igr1 = g.CreateRestorePoint();

         INode n1 = g.AddNode("", "", "", 0);

         GraphRecord gr2 = new GraphRecord(g);

         IGraphRestore igr2 = g.CreateRestorePoint();

         INode n2 = g.AddNode("", "", "", 0);

         igr2.Restore();

         assertEquals(igr1, g.CurrentRestore());
         assertFalse(igr2.CanBeRestored());
         assertTrue(igr1.CanBeRestored());

         assertTrue(gr2.Compare(g));

         IGraphRestore igr3 = g.CreateRestorePoint();

         INode n3 = g.AddNode("", "", "", 0);

         igr1.Restore();

         assertTrue(gr1.Compare(g));

         assertFalse(igr1.CanBeRestored());
         assertFalse(igr3.CanBeRestored());
         assertEquals(null, g.CurrentRestore());
      }

      // keep a restore but then abandon it, committing to all our changes

      // restore to intermediate point then start a new restore
      {
         Graph g = new Graph();

         IGraphRestore igr1 = g.CreateRestorePoint();

         INode n1 = g.AddNode("", "", "", 0);

         IGraphRestore igr2 = g.CreateRestorePoint();

         INode n2 = g.AddNode("", "", "", 0);

         IGraphRestore igr3 = g.CreateRestorePoint();

         INode n3 = g.AddNode("", "", "", 0);

         GraphRecord gr1 = new GraphRecord(g);

         g.ClearRestore();

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

      assertTrue(g.XYBounds().equals(new Box()));

      INode n1 = g.AddNode("", "", "", 1.0);

      assertTrue(g.XYBounds().equals(new Box(new XY(-1, -1), new XY(1, 1))));

      INode n2 = g.AddNode("", "", "", 2.0);

      assertTrue(g.XYBounds().equals(new Box(new XY(-2, -2), new XY(2, 2))));

      n1.SetPos(new XY(-2, 0));

      assertTrue(g.XYBounds().equals(new Box(new XY(-3, -2), new XY(2, 2))));

      n2.SetPos(new XY(10, 10));

      assertTrue(g.XYBounds().equals(new Box(new XY(-3, -1), new XY(12, 12))));
   }

   @Test
   public void testPrint()
   {
      {
         Graph g = new Graph();

         g.AddNode("xx", "yy", "zz", 1.0);
         g.AddNode("aa", "bb", "cc", 1.0);

         String s = g.Print();

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

         INode n1 = g.AddNode("xx", "yy", "zz", 1.0);
         INode n2 = g.AddNode("aa", "bb", "cc", 1.0);
         g.Connect(n1, n2, 0, 0, 0);

         String s = g.Print();

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
      testCatchUnsupported(() -> g.Connect(new Node("", "", "", 0),
            new Node("", "", "", 0), 0, 0, 0));

      INode n = g.AddNode("n", "x", "y", 10);

      // cannot connect a node we know and one we don't
      testCatchUnsupported(() -> g.Connect(n, new Node("", "", "", 0), 0, 0, 0));
   }
}