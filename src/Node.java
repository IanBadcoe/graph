import java.util.*;

class Node implements INode
{
   Node(String name, String codes, String template, double rad)
   {
      this(name, codes, template, DefaultLayourGreator, rad);
   }

   Node(String name, String codes, String template,
        GeomLayout.IGeomLayoutCreateFromNode gl_creator, double rad)
   {
      m_name = name;
      m_connections = new HashSet<>();
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
   public boolean connects(INode n)
   {
      return connectsForwards(n) || connectsBackwards(n);
   }

   @Override
   public boolean connectsForwards(INode n)
   {
      return m_connections.contains(new DirectedEdge(this, n, 0, 0, 0));
   }

   @Override
   public boolean connectsBackwards(INode n)
   {
      return m_connections.contains(new DirectedEdge(n, this, 0, 0, 0));
   }

   void disconnect(Node n)
   {
      if (!connects(n))
         return;

      // simplest just to try removing the forward and reverse edges
      m_connections.remove(new DirectedEdge(this, n, 0, 0, 0));
      m_connections.remove(new DirectedEdge(n, this, 0, 0, 0));

      n.disconnect(this);
   }

   // must have an empty slot available to take the new connection
   DirectedEdge connect(Node n, double min_distance, double max_distance, double width)
   {
      // cannot multiply connect the same node, forwards or backwards
      if (connects(n))
         throw new IllegalArgumentException("Cannot multiply connect from '" + m_name +
               "' to '" + n.getName() + "'");

      DirectedEdge e = new DirectedEdge(this, n, min_distance, max_distance, width);

      connect(e);
      n.connect(e);

      return e;
   }

   private void connect(DirectedEdge e)
   {
      m_connections.add(e);
   }

   String tab(int tab)
   {
      String ret = "";

      for(int i = 0; i < tab; i++)
      {
         ret += "   ";
      }

      return ret;
   }

   public String longName()
   {
      return m_name + "(#" + m_num + ")";
   }

   @Override
   public String print(int tab, boolean full)
   {
      String ret = "";

      if (full)
      {
         ret += tab(tab) + longName() + " (" + m_template + ", " + m_codes + ")\n";

         ret += tab(tab) + "{\n";

         for(DirectedEdge e : m_connections)
         {
            ret += e.OtherNode(this).print(tab + 1, false);
         }

         ret += tab(tab) + "}\n";
      }
      else
      {
         ret += tab(tab) + longName() + "\n";
      }

      return ret;
   }

   @Override
   public String getCodes()
   {
      return m_codes;
   }

   @Override
   public String getTemplate()
   {
      return m_template;
   }

   @Override
   public String getName()
   {
      return m_name;
   }

   @Override
   public void setName(String s)
   {
      if (s == null)
         throw new NullPointerException("Null node name not permitted.");

      m_name = s;
   }

   @Override
   public GeomLayout.IGeomLayoutCreateFromNode geomLayoutCreator()
   {
      return m_gl_creator;
   }

   public final static GeomLayout.IGeomLayoutCreateFromNode DefaultLayourGreator = CircularGeomLayout::createFromNode;

   int numConnections()
   {
      return m_connections.size();
   }

   @Override
   public Collection<DirectedEdge> getConnections()
   {
      return new HashSet<>(m_connections);
   }

   @Override
   public Collection<DirectedEdge> getInConnections()
   {
      HashSet<DirectedEdge> ret = new HashSet<>();

      for(DirectedEdge e : m_connections)
      {
         if (e.End == this)
            ret.add(e);
      }

      return ret;
   }

   @Override
   public Collection<DirectedEdge> getOutConnections()
   {
      HashSet<DirectedEdge> ret = new HashSet<>();

      for(DirectedEdge e : m_connections)
      {
         if (e.Start == this)
            ret.add(e);
      }

      return ret;
   }

   @Override
   public DirectedEdge getConnectionTo(INode to)
   {
      for(DirectedEdge e : m_connections)
      {
         if (e.End == to)
            return e;
      }

      return null;
   }

   @Override
   public DirectedEdge getConnectionFrom(INode from)
   {
      for(DirectedEdge e : m_connections)
      {
         if (e.Start == from)
            return e;
      }

      return null;
   }

   @Override
   public void setPos(XY pos)
   {
      m_pos = pos;
   }

   @Override
   public XY getPos()
   {
      return m_pos;
   }

   @Override
   public void resetForce()
   {
      m_force = new XY();
   }

   @Override
   public void addForce(XY force)
   {
      m_force = m_force.plus(force);
   }

   @Override
   public double getForce()
   {
      return Math.sqrt(m_force.X * m_force.X + m_force.Y * m_force.Y);
   }

   @Override
   public double step(double t)
   {
      XY d = m_force.multiply(t);
      m_pos = m_pos.plus(d);

      return d.length();
   }

   @Override
   public double getRad()
   {
      return m_rad;
   }

   @Override
   public void setIdx(int i)
   {
      m_idx = i;
   }

   @Override
   public int getIdx()
   {
      return m_idx;
   }

   @Override
   public int getColour()
   {
      return m_colour;
   }

   @Override
   public void setColour(int c)
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

   private XY m_force = new XY();

   private double m_rad;

   private final static Random s_rand = new Random(1);

   private int m_idx;

   private int m_colour = 0xff8c8c8c;

   private final GeomLayout.IGeomLayoutCreateFromNode m_gl_creator;
}
