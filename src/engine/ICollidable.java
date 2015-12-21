package engine;

import java.util.Collection;

public interface ICollidable
{
   ColRet collide(Collection<Edge> edges, double radius, XY centre, Movable activeMovable);

   class Edge
   {
      public final XY Normal;
      private Edge m_next;
      private Edge m_prev;

      Edge(XY start, XY end, XY normal)
      {
         Start = start;
         End = end;
         Normal = normal;
      }

      public final XY Start;
      public final XY End;

      public void setNext(Edge next)
      {
         this.m_next = next;
      }

      public Edge getNext()
      {
         return m_next;
      }

      public void setPrev(Edge prev)
      {
         this.m_prev = prev;
      }

      public Edge getPrev()
      {
         return m_prev;
      }

      public XY midPoint()
      {
         return End.plus(Start).divide(2);
      }
   }

   class ColRet
   {
      final Movable ActiveMovable;
      final Movable InactiveMovable;

      final Edge MovingEdge;
      final Edge StationaryEdge;
      final double MovingEdgeFrac;
      final double StationaryEdgeFrac;

      ColRet(Movable activeMovable, Movable inactiveMovable,
            Edge movingEdge, Edge stationaryEdge,
            double movingEdgeFrac, double stationaryEdgeFrac)
      {
         ActiveMovable = activeMovable;
         InactiveMovable = inactiveMovable;

         MovingEdge = movingEdge;
         StationaryEdge = stationaryEdge;

         MovingEdgeFrac = movingEdgeFrac;
         StationaryEdgeFrac = stationaryEdgeFrac;
      }
   }

}
