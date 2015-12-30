package engine;

import java.util.Collection;

public interface ICollidable
{
   Collection<ColRet> collide(Collection<Track> tracks, double radius, XY centre, Movable activeMovable);

   class Track
   {
      public final XY Start;
      public final XY End;

      public Track(XY start, XY end)
      {
         Start = start;
         End = end;
      }

      public XY interp(Double p)
      {
         return Start.plus(End.minus(Start).multiply(p));
      }
   }

   class Edge
   {
      public final XY Start;
      public final XY End;
      public final XY Normal;
      private Edge m_next;
      private Edge m_prev;

      Edge(XY start, XY end, XY normal)
      {
         Start = start;
         End = end;
         Normal = normal;
      }

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

   // purpose of this is just to make edges in ColRet a different class from Edge and any other derived classes
   // point of that is that on long straight edges we cut them into many small edges but we keep the one long super-edge
   // for physics purposes (basically so we don't see just one small bit in the middle of a long colliding edge)
   class SuperEdge extends Edge
   {
      SuperEdge(XY start, XY end, XY normal)
      {
         super(start, end, normal);
      }
   }

   class ColRet
   {
      final public Movable ActiveMovable;
      final public Movable InactiveMovable;

      final public Track ActiveTrack;
      final public double ActiveTrackFrac;

      final public Edge InactiveEdge;
      final public double InactiveEdgeFrac;

      final public XY WorldPosition;

      ColRet(Movable activeMovable, Movable inactiveMovable,
            Track activeTrack, double activeTrackFrac, Edge inactiveEdge,
            double inactiveEdgeFrac, XY worldPosition)
      {
         ActiveMovable = activeMovable;
         InactiveMovable = inactiveMovable;

         ActiveTrack = activeTrack;
         InactiveEdge = inactiveEdge;

         ActiveTrackFrac = activeTrackFrac;
         InactiveEdgeFrac = inactiveEdgeFrac;
         WorldPosition = worldPosition;
      }
   }

}
