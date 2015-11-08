import java.util.*;

class Node implements INode
{
   Node(String name, String codes, String template, double rad)
   {
      this(name, codes, template, GeomLayoutCircular::createFromNode, rad);
   }

   Node(String name, String codes, String template,
         GeomLayout.GeomLayoutCreateFromNode gl_creator, double rad)
   {
      m_name = name;
      m_connections = new HashSet<DirectedEdge>();
      m_codes = codes;
      m_template = template;

      m_num = s_rand.nextInt();

      m_rad = rad;

      m_gl_creator = gl_creator;
   }

   // we need hashsets of these and things built from these to be in a defined order
   // however, we want all instances still to be unequal so don't touch that
   // (is this deadly?)
   @Override
   public int hashCode()
   {
      return m_num;
   }

   @Override
   public boolean Connects(INode n)
   {
      return ConnectsForwards(n) || ConnectsBackwards(n);
   }

   @Override
   public boolean ConnectsForwards(INode n)
   {
      return m_connections.contains(new DirectedEdge(this, n, 0, 0, 0));
   }

   @Override
   public boolean ConnectsBackwards(INode n)
   {
      return m_connections.contains(new DirectedEdge(n, this, 0, 0, 0));
   }

   void Disconnect(Node n)
   {
      if (!Connects(n))
         return;

      // simplest just to try removing the forward and reverse edges
      m_connections.remove(new DirectedEdge(this, n, 0, 0, 0));
      m_connections.remove(new DirectedEdge(n, this, 0, 0, 0));

      n.Disconnect(this);
   }

   // must have an empty slot available to take the new connection
   DirectedEdge Connect(Node n, double min_distance, double max_distance, double width)
   {
      // cannot multiply connect the same node, forwards or backwards
      if (Connects(n))
         throw new IllegalArgumentException("Cannot multiply connect from '" + m_name +
               "' to '" + n.GetName() + "'");

      DirectedEdge e = new DirectedEdge(this, n, min_distance, max_distance, width);

      Connect(e);
      n.Connect(e);

      return e;
   }

   private void Connect(DirectedEdge e)
   {
      m_connections.add(e);
   }

   String Tab(int tab)
   {
      String ret = "";

      for(int i = 0; i < tab; i++)
      {
         ret += "   ";
      }

      return ret;
   }

   public String LongName()
   {
      return m_name + "(#" + m_num + ")";
   }

   @Override
   public String Print(int tab, boolean full)
   {
      String ret = "";

      if (full)
      {
         ret += Tab(tab) + LongName() + " (" + m_template + ", " + m_codes + ")\n";

         ret += Tab(tab) + "{\n";

         for(DirectedEdge e : m_connections)
         {
            ret += e.OtherNode(this).Print(tab + 1, false);
         }

         ret += Tab(tab) + "}\n";
      }
      else
      {
         ret += Tab(tab) + LongName() + "\n";
      }

      return ret;
   }

   @Override
   public String GetCodes()
   {
      return m_codes;
   }

   @Override
   public String GetTemplate()
   {
      return m_template;
   }

   @Override
   public String GetName()
   {
      return m_name;
   }

   @Override
   public void SetName(String s)
   {
      m_name = s;
   }

   @Override
   public GeomLayout.GeomLayoutCreateFromNode geomLayoutCreator()
   {
      return m_gl_creator;
   }

   int NumConnections()
   {
      return m_connections.size();
   }

   @Override
   public Collection<DirectedEdge> GetConnections()
   {
      return new HashSet<DirectedEdge>(m_connections);
   }

   @Override
   public Collection<DirectedEdge> GetInConnections()
   {
      HashSet<DirectedEdge> ret = new HashSet<DirectedEdge>();

      for(DirectedEdge e : m_connections)
      {
         if (e.End == this)
            ret.add(e);
      }

      return ret;
   }

   @Override
   public Collection<DirectedEdge> GetOutConnections()
   {
      HashSet<DirectedEdge> ret = new HashSet<DirectedEdge>();

      for(DirectedEdge e : m_connections)
      {
         if (e.Start == this)
            ret.add(e);
      }

      return ret;
   }

   @Override
   public DirectedEdge GetConnectionTo(INode to)
   {
      for(DirectedEdge e : m_connections)
      {
         if (e.End == to)
            return e;
      }

      return null;
   }

   @Override
   public DirectedEdge GetConnectionFrom(INode from)
   {
      for(DirectedEdge e : m_connections)
      {
         if (e.Start == from)
            return e;
      }

      return null;
   }

   @Override
   public void SetPos(XY pos)
   {
      m_pos = pos;
   }

   @Override
   public XY GetPos()
   {
      return m_pos;
   }

   @Override
   public void resetForce()
   {
      m_force = new XY();
   }

   @Override
   public void AddForce(XY force)
   {
      m_force = m_force.Plus(force);
   }

   @Override
   public double getForce()
   {
      return Math.sqrt(m_force.X * m_force.X + m_force.Y * m_force.Y);
   }

   @Override
   public double step(double t)
   {
      XY d = m_force.Multiply(t);
      m_pos = m_pos.Plus(d);

      return d.Length();
   }

   @Override
   public double GetRad()
   {
      return m_rad;
   }

   @Override
   public void SetIdx(int i)
   {
      m_idx = i;
   }

   @Override
   public int GetIdx()
   {
      return m_idx;
   }

   @Override
   public int GetColour()
   {
      return m_colour;
   }

   @Override
   public void SetColour(int c)
   {
      m_colour = c;
   }

   private HashSet<DirectedEdge> m_connections;

   // e : Expandable
   // < : Start (always alone?)
   // > : End (always alone?)
   // c : put on dummy connections in templates, possibly never searched for...
   private String m_codes;

   private String m_name;
   private final String m_template;

   private final int m_num;

   private XY m_pos = new XY();

   private XY m_force = new XY();;

   private double m_rad;

   private final static Random s_rand = new Random(1);

   private int m_idx;

   private int m_colour = 0xff8c8c8c;

   private final GeomLayout.GeomLayoutCreateFromNode m_gl_creator;
}
