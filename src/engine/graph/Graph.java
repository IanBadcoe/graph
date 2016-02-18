package engine.graph;

import engine.Box;
import engine.XY;

import java.util.*;
import java.util.stream.Collectors;

public class Graph
{
   public INode addNode(String name, String codes, String template, double rad)
   {
      return addNode(name, codes, template, rad, CircularGeomLayout::createFromNode);
   }

   public INode addNode(String name, String codes, String template, double rad,
                        GeomLayout.IGeomLayoutCreateFromNode geomCreator)
   {
      Node n = new Node(name, codes, template, geomCreator, rad);

      if (m_restore != null)
      {
         m_restore.AddNode(n);
      }

      addNodeInner(n);

      return n;
   }

   private void addNodeInner(Node n)
   {
      m_nodes.add(n);
   }

   boolean removeNode(INode inode)
   {
      if (!contains(inode))
         return false;

      if (inode.getConnections().size() > 0)
         return false;

      Node node = (Node) inode;

      if (m_restore != null)
      {
         m_restore.RemoveNode(node);
      }

      removeNodeInner(node);

      return true;
   }

   private void removeNodeInner(Node node)
   {
      m_nodes.remove(node);
   }

   public DirectedEdge connect(INode from, INode to,
                               double min_length, double max_length, double half_width)
   {
      return connect(from, to, min_length, max_length, half_width,
            GeomLayout::makeDefaultCorridor);
   }

   public DirectedEdge connect(INode from, INode to,
                               double min_length, double max_length, double half_width,
                               GeomLayout.IGeomLayoutCreateFromDirectedEdge layoutCreator)
   {
      if (from == to
            || !contains(from)
            || !contains(to)
            || from.connects(to))
         throw new UnsupportedOperationException();

      DirectedEdge temp = new DirectedEdge(from, to, min_length, max_length, half_width, layoutCreator);

      if (m_restore != null)
      {
         m_restore.Connect(temp);
      }

      return connectInner(temp);
   }

   private DirectedEdge connectInner(DirectedEdge e)
   {
      assert !m_edges.contains(e);

      DirectedEdge real_edge = ((Node)e.Start).connect((Node)e.End, e.MinLength, e.MaxLength, e.HalfWidth,
            e.LayoutCreator);

      m_edges.add(real_edge);

      return real_edge;
   }

   public boolean disconnect(INode from, INode to)
   {
      if (!contains(from) || !contains(to))
         return false;

      DirectedEdge e = from.getConnectionTo(to);

      if (e == null)
         return false;

      if (m_restore != null)
      {
         m_restore.Disconnect(e);
      }

      disconnectInner(e);

      return true;
   }

   private void disconnectInner(DirectedEdge e)
   {
      Node n_from = (Node)e.Start;
      Node n_to = (Node)e.End;

      n_from.disconnect(n_to);

      assert m_edges.contains(e);
      m_edges.remove(e);
   }

   public int numNodes()
   {
      return m_nodes.size();
   }

   public int numEdges()
   {
      return m_edges.size();
   }

   public ArrayList<DirectedEdge> allGraphEdges()
   {
      return new ArrayList<>(m_edges);
   }

   public ArrayList<INode> allGraphNodes()
   {
      return new ArrayList<>(m_nodes);
   }

   public IGraphRestore createRestorePoint()
   {
      GraphRestore gr = new GraphRestore(m_restore);

      m_restore = gr;

      return gr;
   }

   public Box bounds()
   {
      if (m_nodes.size() == 0)
         return new Box();

      ArrayList<INode> nodes = allGraphNodes();
      XY min = nodes.get(0).getPos();
      XY max = nodes.get(0).getPos();

      for (INode n : nodes)
      {
         XY rad_box = new XY(n.getRad(), n.getRad());

         // extend by node radius
         min = min.min(n.getPos().minus(rad_box));
         max = max.max(n.getPos().plus(rad_box));
      }

      return new Box(min, max);
   }

   IGraphRestore currentRestore()
   {
      return m_restore;
   }

   String print()
   {
      String ret = "";

      for(INode n : m_nodes)
      {
         ret += n.print(0, true);

         ret += "\n";
      }

      return ret;
   }

   enum RestoreAction
   {
      Make,
      Break
   }

   private final static class NodePos
   {
      public final XY Pos;
      public final INode N;

      NodePos(INode n, XY pos)
      {
         Pos = pos;
         N = n;
      }
   }

   class GraphRestore implements IGraphRestore
   {
      GraphRestore(GraphRestore chain_from_restore)
      {
         m_chain_from_restore = chain_from_restore;

         // m_chain_from_restore is an older restore than we are, so if it is restored
         // it needs to know that it needs to restore us first...
         if (m_chain_from_restore != null)
         {
            // anything the chain-from used to be chained-to should be already gone,
            // e.g. restored, before we are able to make another new chain
            assert m_chain_from_restore.m_chain_to_restore == null;

            m_chain_from_restore.m_chain_to_restore = this;
         }

         m_positions.addAll(
               Graph.this.allGraphNodes()
                     .stream()
                     .map(n -> new NodePos(n, n.getPos())).collect(Collectors.toList()));
      }

      void AddNode(Node n)
      {
         if (!m_nodes_removed.remove(n))
         {
            m_nodes_added.add(n);
         }
      }

      void RemoveNode(Node node)
      {
         if (!m_nodes_added.remove(node))
         {
            m_nodes_removed.add(node);
         }
      }

      public void Disconnect(DirectedEdge e)
      {
         RestoreAction ra = m_connections.get(e);

         if (ra != null)
         {
            // only way we can already know about an edge we are removing is if it was added in the context of this
            // restore point, so the only restore-action it can already have is "break"

            // in which case the net effect of an edge added and removed is nothing
            assert ra == RestoreAction.Break;
            m_connections.remove(e);
         }
         else
         {
            m_connections.put(e, RestoreAction.Make);
         }
      }

      public void Connect(DirectedEdge e)
      {
         RestoreAction ra = m_connections.get(e);

         if (ra != null)
         {
            // only way we can already know about an edge we are adding is if it was already removed once in the
            // context of this restore point, so the only restore-action it can already have is "break"

            // in which case the net effect of an edge added and removed is nothing
            assert ra == RestoreAction.Make;
            m_connections.remove(e);
         }
         else
         {
            m_connections.put(e, RestoreAction.Break);
         }
      }

      @Override
      public boolean Restore()
      {
         if (!m_can_be_restored)
            return false;

         if (m_chain_to_restore != null)
         {
            // first undo any newer restore points
            m_chain_to_restore.Restore();
         }

         // disconnect anything we connected
         for (Map.Entry<DirectedEdge, RestoreAction> me : m_connections.entrySet())
         {
            DirectedEdge e = me.getKey();

            if (me.getValue() == RestoreAction.Break)
            {
               assert e.Start.connects(e.End);

               Graph.this.disconnectInner(e);
            }
         }

         // which means we must be able to remove anything we added
         m_nodes_added.forEach(Graph.this::removeNodeInner);

         // put back anything we removed
         m_nodes_removed.forEach(Graph.this::addNodeInner);

         // which means we must be able to restore the original connections
         for (Map.Entry<DirectedEdge, RestoreAction> me : m_connections.entrySet())
         {
            DirectedEdge e = me.getKey();

            if (me.getValue() == RestoreAction.Make)
            {
               assert !e.Start.connects(e.End);

               Graph.this.connectInner(e);
            }
         }

         int restored_size = Graph.this.numNodes();
         int prev_size = m_positions.size();

         // putting connections back should leave us the same size as before...
         assert restored_size == prev_size;

         // and finally put all the positions back
         for (NodePos np : m_positions)
         {
            np.N.setPos(np.Pos);
         }

         CleanUp();

         return true;
      }

      void CleanUp()
      {
         if (m_chain_to_restore != null)
            m_chain_to_restore.CleanUp();

         // once we are undone or committed, the user goes back to whatever their previous restore level was
         Graph.this.m_restore = m_chain_from_restore;

         // we're restored, so whoever might have wanted to chain us mustn't any more
         if (m_chain_from_restore != null)
            m_chain_from_restore.m_chain_to_restore = null;

         m_can_be_restored = false;
      }

      @Override
      public boolean CanBeRestored()
      {
         return m_can_be_restored;
      }

      private final HashMap<DirectedEdge, RestoreAction> m_connections = new HashMap<>();

      private final ArrayList<NodePos> m_positions = new ArrayList<>();

      private final ArrayList<Node> m_nodes_added = new ArrayList<>();
      private final ArrayList<Node> m_nodes_removed = new ArrayList<>();

      final GraphRestore m_chain_from_restore;
      GraphRestore m_chain_to_restore;

      // because we can only be used for a restored once...
      boolean m_can_be_restored = true;
   }

   void clearRestore()
   {
      GraphRestore root = m_restore;

      while(root.m_chain_from_restore != null)
      {
         root = root.m_chain_from_restore;
      }

      root.CleanUp();
   }

   boolean contains(INode n)
   {
      //noinspection RedundantCast
      return m_nodes.contains((Node)n);
   }

   private final HashSet<Node> m_nodes = new HashSet<>();
   private final HashSet<DirectedEdge> m_edges = new HashSet<>();

   private GraphRestore m_restore;
}
