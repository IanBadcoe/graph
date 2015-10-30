import com.sun.istack.internal.NotNull;

import java.util.Collection;
import java.util.HashSet;

// read/write for position but read-only for connectivity as we want Graph to
// control that
interface INode
{
   // read/write forces and integration
   void ResetForce();
   double GetForce();
   double Step(double interval);
   void AddForce(XY force);

   // read/write position
   void SetPos(XY pos);
   @NotNull
   XY GetPos();

   // read-only connections
   boolean Connects(INode n);
   boolean ConnectsForwards(INode to);
   boolean ConnectsBackwards(INode from);
   Collection<DirectedEdge> GetConnections();
   Collection<DirectedEdge> GetInConnections();
   Collection<DirectedEdge> GetOutConnections();
   DirectedEdge GetConnectionTo(INode node);
   DirectedEdge GetConnectionFrom(INode from);

   // other read-only properties
   String GetName();
   String LongName();
   String GetCodes();
   String GetTemplate();
   double GetRad();

   // misc
   public String Print(int tab, boolean full);
}
