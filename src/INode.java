import com.sun.istack.internal.NotNull;

import java.util.Collection;

// read/write for position but read-only for connectivity as we want Graph to
// control that
interface INode
{
   // read/write forces and integration
   void resetForce();
   double getForce();
   double step(double interval);
   void addForce(XY force);

   // read/write position
   void setPos(XY pos);
   @NotNull
   XY getPos();

   // read-only connections
   boolean connects(INode n);
   boolean connectsForwards(INode to);
   boolean connectsBackwards(INode from);
   Collection<DirectedEdge> getConnections();
   Collection<DirectedEdge> getInConnections();
   Collection<DirectedEdge> getOutConnections();
   DirectedEdge getConnectionTo(INode node);
   DirectedEdge getConnectionFrom(INode from);

   // other read-only properties
   String longName();
   String getCodes();
   String getTemplate();
   double getRad();

   // misc
   public String print(int tab, boolean full);

   // indices created and read-back for handy data access when relaxing
   void setIdx(int i);
   int getIdx();

   // colour for pretty drawing
   int getColour();
   void setColour(int c);

   // name (currently writable for showing door <-> key relations etc
   String getName();
   void setName(String s);

   // access the geometry-layout object for this node...
   GeomLayout.IGeomLayoutCreateFromNode geomLayoutCreator();

}
