import java.util.*;

public class TemplateBuilder
{
   TemplateBuilder(String name, String codes)
   {
      m_name = name;
      m_codes = codes;

      // a dummy entry used to represent the node we are replacing in positioning rules
      m_nodes.put("<target>", new Template.NodeRecord(Template.NodeType.Target, "<target>",
            false, null, null, null,
            null, 0));
   }

   // don't want people recovering from these as just bad programming
   public static abstract class TemplateException extends RuntimeException
   {
      TemplateException(String message)
      {
         super(message);
      }
   }

   public static class UnknownNodeException extends TemplateException
   {
      UnknownNodeException(String name, String argument)
      {
         super("Attempt to reference a node: '" + name + "' which does not exist.");

         NodeName = name;
         Argument = argument;
      }

      final String NodeName;
      final String Argument;
   }

   public static class DuplicateNodeException extends TemplateException
   {
      DuplicateNodeException(String name)
      {
         super("Attempt to add a node: '" + name + "' to a template when a node of name is already present.");

         NodeName = name;
      }

      final String NodeName;
   }

   void AddNode(Template.NodeType type, String name) throws TemplateException
   {
      AddNode(type, name, false, "<target>", null, null, "", 0f);
   }

   // types In and Out ignore all parameters after "name"
   void AddNode(Template.NodeType type, String name, boolean nudge,
                String positionOnName, String positionTowardsName,
                String positionAwayFromName,
                String codes, double radius) throws TemplateException
   {
      if (name.contains("->"))
         throw new IllegalArgumentException("Node name: '" + name + "' cannot contain '->'.");

      if (name.contains("<target>"))
         throw new IllegalArgumentException("Node name: '" + name + "' is reserved.");

      if (positionOnName == null)
         throw new NullPointerException("'positionOnName' cannot be null.");

      if (type == Template.NodeType.Target)
         throw new IllegalArgumentException("User cannot add a node of type 'Target'.");

      if (FindNodeRecord(name) != null)
         throw new DuplicateNodeException(name);

      // required
      Template.NodeRecord positionOn = null;
      // optional
      Template.NodeRecord positionTowards = null;
      Template.NodeRecord positionAwayFrom = null;

      if (type == Template.NodeType.Internal)
      {
         positionOn = FindNodeRecord(positionOnName);

         if (positionOn == null)
            throw new UnknownNodeException(positionOnName, "positionOnName");

         if (positionTowardsName != null)
         {
            positionTowards = FindNodeRecord(positionTowardsName);

            if (positionTowards == null)
               throw new UnknownNodeException(positionTowardsName, "positionTowardsName");
         }

         if (positionAwayFromName != null)
         {
            positionAwayFrom = FindNodeRecord(positionAwayFromName);

            if (positionAwayFrom == null)
               throw new UnknownNodeException(positionAwayFromName, "positionAwayFromName");
         }
      }

      m_nodes.put(name, new Template.NodeRecord(type, name, nudge,
            positionOn, positionTowards, positionAwayFrom,
            codes, radius));

      switch (type)
      {
         case In:
            m_num_in_nodes++;
            break;
         case Out:
            m_num_out_nodes++;
            break;
         case Internal:
            m_num_internal_nodes++;
            break;
      }
   }

   public void Connect(String from, String to,
                       double min_length, double max_length,
                       double width) throws IllegalArgumentException
   {
      if (from == null)
         throw new NullPointerException("Null node name: 'from'.");

      if (to == null)
         throw new NullPointerException("Null node name: 'to'.");

      // only one connection between nodes is permitted
      if (FindConnectionRecord(from, to) != null)
         throw new IllegalArgumentException("A connection from '" + from + "' to '" + to + "' already exists.");

      // nor are we allowed forwards and backwards connections
      if (FindConnectionRecord(to, from) != null)
         throw new IllegalArgumentException("A connection from '" + to + "' from '" + to + "' already exists.");

      Template.NodeRecord nrf = FindNodeRecord(from);
      Template.NodeRecord nrt = FindNodeRecord(to);

      if (nrf == null)
         throw new UnknownNodeException(from, "from");

      if (nrt == null)
         throw new UnknownNodeException(to, "to");

      if (nrf.Type == Template.NodeType.Target)
         throw new IllegalArgumentException("Cannot connect from node 'Target' as it is being replaced.");

      if (nrt.Type == Template.NodeType.Target)
         throw new IllegalArgumentException("Cannot connect to node 'Target' as it is being replaced.");

      m_connections.put(
            Template.MakeConnectionName(from, to),
            new Template.ConnectionRecord(nrf, nrt, min_length, max_length, width));
   }

   public Template.NodeRecord FindNodeRecord(String name)
   {
      return m_nodes.get(name);
   }

   public Template.ConnectionRecord FindConnectionRecord(String from, String to)
   {
      return m_connections.get(Template.MakeConnectionName(from, to));
   }

   public Map<String, Template.NodeRecord> GetUnmodifiableNodes() {
      return Collections.unmodifiableMap(m_nodes);
   }

   public Map<String, Template.ConnectionRecord> GetUnmodifiableConnections() {
      return Collections.unmodifiableMap(m_connections);
   }

   String GetCodes()
   {
      return m_codes;
   }

   String GetName()
   {
      return m_name;
   }

   int GetNumInNodes()
   {
      return m_num_in_nodes;
   }

   int GetNumOutNodes()
   {
      return m_num_out_nodes;
   }

   int GetNumInternalNodes()
   {
      return m_num_internal_nodes;
   }

   void Clear()
   {
      m_nodes = null;
      m_connections = null;

      m_num_in_nodes = -1;
      m_num_out_nodes = -1;
      m_num_internal_nodes = -1;
   }

   Template Build()
   {
      return new Template(this);
   }

   private final String m_name;

   HashMap<String, Template.NodeRecord> m_nodes = new HashMap<>();
   HashMap<String, Template.ConnectionRecord> m_connections = new HashMap<>();

   // just to avoid keeping counting
   int m_num_in_nodes = 0;
   int m_num_out_nodes = 0;
   int m_num_internal_nodes = 0;

   private String m_codes;

   boolean m_cleared = false;
}
