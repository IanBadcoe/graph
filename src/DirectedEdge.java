class DirectedEdge {
   public final INode Start;
   public final INode End;
   public final double MinLength;
   public final double MaxLength;
   public final double HalfWidth;

   public DirectedEdge(INode start, INode end,
                       double min_length, double max_length,
                       double half_width) {
      assert start != null;
      assert end != null;

      Start = start;
      End = end;
      MinLength = min_length;
      MaxLength = max_length;
      HalfWidth = half_width;
   }

   @Override
   public int hashCode()
   {
      int x = Start.hashCode();
      int y = End.hashCode();

      // we don't intend to ever have two edges between the same
      // pair of nodes, so no need to look at lengths
      return x * 31 + y;
   }

   @Override
   public boolean equals(Object o)
   {
      if (!(o instanceof DirectedEdge))
         return false;

      DirectedEdge e = (DirectedEdge)o;

      return (Start == e.Start && End == e.End);
   }

   INode OtherNode(INode n)
   {
      if (n == Start)
      {
         return End;
      }
      else if (n == End)
      {
         return Start;
      }

      return null;
   }

   double Length()
   {
      return End.getPos().minus(Start.getPos()).length();
   }

   boolean Connects(INode n)
   {
      return n == Start || n == End;
   }

   int GetColour()
   {
      return m_colour;
   }

   void SetColour(int c)
   {
      m_colour = c;
   }

   private int m_colour = 0xff4b4b4b;
} 