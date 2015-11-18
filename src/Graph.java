import java.util.*;
import java.util.stream.Collectors;

class Graph
{
   INode AddNode(String name, String codes, String template, double rad)
   {
      return AddNode(name, codes, template, CircularGeomLayout::createFromNode, rad);
   }

   INode AddNode(String name, String codes, String template,
                 GeomLayout.IGeomLayoutCreateFromNode geomCreator, double rad)
   {
      Node n = new Node(name, codes, template, geomCreator, rad);

      if (m_restore != null)
      {
         m_restore.AddNode(n);
      }

      AddNode_Inner(n);

      return n;
   }

   private void AddNode_Inner(Node n)
   {
      m_nodes.add(n);
   }

   boolean RemoveNode(INode inode)
   {
      if (!Contains(inode))
         return false;

      if (inode.getConnections().size() > 0)
         return false;

      Node node = (Node) inode;

      if (m_restore != null)
      {
         m_restore.RemoveNode(node);
      }

      RemoveNode_Inner(node);

      return true;
   }

   private void RemoveNode_Inner(Node node)
   {
      m_nodes.remove(node);
   }

   DirectedEdge Connect(INode from, INode to,
         double min_length, double max_length, double half_width)
   {
      if (from == to
            || !Contains(from)
            || !Contains(to)
            || from.connects(to))
         throw new UnsupportedOperationException();

      DirectedEdge temp = new DirectedEdge(from, to, min_length, max_length, half_width);

      if (m_restore != null)
      {
         m_restore.Connect(temp);
      }

      return Connect_Inner(temp);
   }

   private DirectedEdge Connect_Inner(DirectedEdge e)
   {
      assert !m_edges.contains(e);

      DirectedEdge real_edge = ((Node)e.Start).connect((Node)e.End, e.MinLength, e.MaxLength, e.HalfWidth);

      m_edges.add(real_edge);

      return real_edge;
   }

   boolean Disconnect(INode from, INode to)
   {
      if (!Contains(from) || !Contains(to))
         return false;

      DirectedEdge e = from.getConnectionTo(to);

      if (e == null)
         return false;

      if (m_restore != null)
      {
         m_restore.Disconnect(e);
      }

      Disconnect_Inner(e);

      return true;
   }

   private void Disconnect_Inner(DirectedEdge e)
   {
      Node n_from = (Node)e.Start;
      Node n_to = (Node)e.End;

      n_from.disconnect(n_to);

      assert m_edges.contains(e);
      m_edges.remove(e);
   }

   int NumNodes()
   {
      return m_nodes.size();
   }

   int NumEdges()
   {
      return m_edges.size();
   }

   ArrayList<DirectedEdge> AllGraphEdges()
   {
      return new ArrayList<>(m_edges);
   }

   ArrayList<INode> AllGraphNodes()
   {
      return new ArrayList<>(m_nodes);
   }

   IGraphRestore CreateRestorePoint()
   {
      GraphRestore gr = new GraphRestore(m_restore);

      m_restore = gr;

      return gr;
   }

   Box XYBounds()
   {
      if (m_nodes.size() == 0)
         return new Box();

      ArrayList<INode> nodes = AllGraphNodes();
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

   IGraphRestore CurrentRestore()
   {
      return m_restore;
   }

   String Print()
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
               Graph.this.AllGraphNodes()
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

               Graph.this.Disconnect_Inner(e);
            }
         }

         // which means we must be able to remove anything we added
         m_nodes_added.forEach(Graph.this::RemoveNode_Inner);

         // put back anything we removed
         m_nodes_removed.forEach(Graph.this::AddNode_Inner);

         // which means we must be able to restore the original connections
         for (Map.Entry<DirectedEdge, RestoreAction> me : m_connections.entrySet())
         {
            DirectedEdge e = me.getKey();

            if (me.getValue() == RestoreAction.Make)
            {
               assert !e.Start.connects(e.End);

               Graph.this.Connect_Inner(e);
            }
         }

         int restored_size = Graph.this.NumNodes();
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

   void ClearRestore()
   {
      GraphRestore root = m_restore;

      while(root.m_chain_from_restore != null)
      {
         root = root.m_chain_from_restore;
      }

      root.CleanUp();
   }

   boolean Contains(INode n)
   {
      //noinspection RedundantCast
      return m_nodes.contains((Node)n);
   }

   private final HashSet<Node> m_nodes = new HashSet<>();
   private final HashSet<DirectedEdge> m_edges = new HashSet<>();

   private GraphRestore m_restore;
}
