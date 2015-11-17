import java.util.*;

class Template
{
   enum NodeType
   {
      In,
      Out,
      Internal,
      Target
   }

   interface IPostExpand
   {
      void AfterExpand(INode n);
      void Done();
   }

   Template(TemplateBuilder builder)
   {
      m_name = builder.GetName();
      m_codes = builder.GetCodes();

      m_nodes = builder.GetUnmodifiableNodes();
      m_connections = builder.GetUnmodifiableConnections();

      m_num_in_nodes = builder.GetNumInNodes();
      m_num_out_nodes = builder.GetNumOutNodes();
      m_num_internal_nodes = builder.GetNumInternalNodes();

      m_post_expand = builder.GetPostExpand();

      // cannot use this again
      builder.Clear();
   }

   public static String MakeConnectionName(String from, String to)
   {
      return from + "->" + to;
   }

   private NodeRecord FindNodeRecord(@SuppressWarnings("SameParameterValue") String name)
   {
      return m_nodes.get(name);
   }

   int NodesAdded()
   {
      // we add a node for each internal node but we
      // remove the one we are replacing
      return m_num_internal_nodes - 1;
   }

   boolean Expand(Graph graph, INode target, Random random)
   {
      Collection<DirectedEdge> target_in_connections = target.getInConnections();
      Collection<DirectedEdge> target_out_connections = target.getOutConnections();

      if (m_num_in_nodes != target_in_connections.size())
      {
         return false;
      }

      if (m_num_out_nodes != target_out_connections.size())
      {
         return false;
      }

      // here we might check codes, if we haven't already

      HashMap<NodeRecord, INode> template_to_graph = new HashMap<>();

      template_to_graph.put(FindNodeRecord("<target>"), target);

      // create nodes for each we are adding and map to their NodeRecords
      for (NodeRecord nr : m_nodes.values())
      {
         if (nr.Type == NodeType.Internal)
         {
            INode n = graph.AddNode(nr.Name, nr.Codes, m_name, nr.GeomCreator, nr.Radius);
            template_to_graph.put(nr, n);
            n.setColour(nr.Colour);
         }
      }

      // find nodes for in connections and map to their NodeRecords
      {
         Iterator<DirectedEdge> g_it = target_in_connections.iterator();

         for (NodeRecord nr : m_nodes.values())
         {
            if (nr.Type == NodeType.In)
            {
               assert g_it.hasNext();

               INode g_conn = g_it.next().Start;

               template_to_graph.put(nr, g_conn);
            }
         }
      }

      // find nodes for out connections and map to their NodeRecords
      {
         Iterator<DirectedEdge> g_it = target_out_connections.iterator();

         for (NodeRecord nr : m_nodes.values())
         {
            if (nr.Type == NodeType.Out)
            {
               assert g_it.hasNext();

               INode g_conn = g_it.next().End;

               template_to_graph.put(nr, g_conn);
            }
         }
      }

      ApplyConnections(target, template_to_graph, graph);

      // make three attempts to position the nodes
      // no point if no random components, but pretty cheap to do...
      for (int i = 0; i < 3; i++)
      {
         if (TryPositions(graph, template_to_graph, random))
         {
            // we needed target for use in position calculations
            // but now we're done with it
            graph.RemoveNode(target);

            ApplyPostExpand(template_to_graph);

            return true;
         }
      }

      return false;
   }

   private boolean TryPositions(Graph graph,
                        HashMap<NodeRecord, INode> template_to_graph,
                        Random rand)
   {
      // position new nodes relative to known nodes
      for (NodeRecord nr : m_nodes.values())
      {
         if (nr.Type == NodeType.Internal)
         {
            INode positionOn = template_to_graph.get(nr.PositionOn);

            XY pos = positionOn.getPos();
            XY towards_step = new XY();
            XY away_step = new XY();

            if (nr.PositionTowards != null)
            {
               INode positionTowards = template_to_graph.get(nr.PositionTowards);

               XY d = positionTowards.getPos().minus(pos);

               towards_step = d.multiply(0.1);
            }

            if (nr.PositionAwayFrom != null)
            {
               INode positionAwayFrom = template_to_graph.get(nr.PositionAwayFrom);

               XY d = positionAwayFrom.getPos().minus(pos);

               away_step = d.multiply(0.1);
            }

            pos = pos.plus(towards_step).minus(away_step);

            if (nr.Nudge)
            {
               // we make the typical scale of edges and node radii on the order of
               // 100, so a displacement of 5 should be enough to separate things enough to avoid
               // stupid forces, while being nothing like as far as the nearest existing neighbours
               double angle = (rand.nextFloat() * (2 * Math.PI));
               pos = pos.plus(new XY(Math.sin(angle) * 5, Math.cos(angle) * 5));
            }

            INode n = template_to_graph.get(nr);

            n.setPos(pos);
         }
      }

      return Util.findCrossingEdges(graph.AllGraphEdges()).size() == 0;
   }

   private void ApplyConnections(INode node_replacing, HashMap<NodeRecord, INode> template_to_graph,
                         Graph graph)
   {
      for(DirectedEdge e : node_replacing.getConnections())
      {
         graph.Disconnect(e.Start, e.End);
      }

      // apply new connections
      for (ConnectionRecord cr : m_connections.values())
      {
         INode nf = template_to_graph.get(cr.From);
         INode nt = template_to_graph.get(cr.To);

         DirectedEdge de = graph.Connect(nf, nt, cr.MinLength, cr.MaxLength, cr.Width);
         de.SetColour(cr.Colour);
      }
   }

   private void ApplyPostExpand(HashMap<NodeRecord, INode> template_to_graph)
   {
      if (m_post_expand == null)
         return;

      for(NodeRecord nr : m_nodes.values())
      {
         // could have chance to modify existing (e.g. In/Out nodes?)
         if (nr.Type == NodeType.Internal)
         {
            m_post_expand.AfterExpand(template_to_graph.get(nr));
         }
      }

      m_post_expand.Done();
   }

   final static class NodeRecord
   {
      final public NodeType Type;
      final public String Name;
      final public boolean Nudge;
      final public NodeRecord PositionOn;       // required
      final public NodeRecord PositionTowards;  // null for none
      final public NodeRecord PositionAwayFrom; // null for none
      final public String Codes;                // copied onto node
      final public double Radius;
      final public int Colour;
      final public GeomLayout.IGeomLayoutCreateFromNode GeomCreator;

      NodeRecord(NodeType type, String name,
            boolean nudge, NodeRecord positionOn, NodeRecord positionTowards, NodeRecord positionAwayFrom,
            String codes, double radius, int colour,
            GeomLayout.IGeomLayoutCreateFromNode geomCreator)
      {
         Type = type;
         Name = name;
         Nudge = nudge;
         PositionOn = positionOn;
         PositionTowards = positionTowards;
         PositionAwayFrom = positionAwayFrom;
         Codes = codes;
         Radius = radius;
         Colour = colour;
         GeomCreator = geomCreator;
      }
   }

   public static final class ConnectionRecord
   {
      final public NodeRecord From;
      final public NodeRecord To;
      final public double MinLength;
      final public double MaxLength;
      final public double Width;
      final public int Colour;

      ConnectionRecord(NodeRecord from, NodeRecord to,
                       double min_length, double max_length,
                       double width,
            int colour)
      {
         From = from;
         To = to;
         MinLength = min_length;
         MaxLength = max_length;
         Width = width;
         Colour = colour;
      }
   }

   String GetCodes()
   {
      return m_codes;
   }

   String GetName()
   {
      return m_name;
   }

   private final String m_name;

   final private Map<String, NodeRecord> m_nodes;
   final private Map<String, ConnectionRecord> m_connections;

   // just to avoid keeping counting
   final private int m_num_in_nodes;
   final private int m_num_out_nodes;
   final private int m_num_internal_nodes;

   final private String m_codes;

   final private IPostExpand m_post_expand;
}