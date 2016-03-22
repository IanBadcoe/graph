package engine.graph;

// directed edges but we don't care what order they are in in the pair
public class DirectedEdgePair
{
   final DirectedEdge m_e1;
   final DirectedEdge m_e2;
   final double m_t1;
   final double m_t2;

   public DirectedEdgePair(DirectedEdge e1, DirectedEdge e2, double t1, double t2)
   {
      m_e1 = e1;
      m_e2 = e2;
      m_t1 = t1;
      m_t2 = t2;
   }

   @Override
   public int hashCode()
   {
      int x = m_e1.hashCode();
      int y = m_e2.hashCode();

      return x ^ y;
   }

   @Override
   public boolean equals(Object o)
   {
      if (!(o instanceof DirectedEdgePair))
         return false;

      DirectedEdgePair dep = (DirectedEdgePair)o;

      return (m_e1.equals(dep.m_e1) && m_e2.equals(dep.m_e2))
            || (m_e1.equals(dep.m_e2) && m_e2.equals(dep.m_e1));
   }
}
