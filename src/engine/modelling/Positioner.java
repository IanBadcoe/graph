package engine.modelling;

import engine.XYZ;

// for positioning model components relative to one another
// could eventually be a general transform, but for the moment just directly storing
// the sorts of data I need for turret tracking
public class Positioner
{
   public Positioner() {}

   public Positioner(double rotation, double elevation)
   {
      Rotation = rotation;
      Elevation = elevation;
   }

   public Positioner(XYZ position)
   {
      Position = position;
   }

   public Positioner(XYZ position, double rotation, double elevation)
   {
      Position = position;
      Rotation = rotation;
      Elevation = elevation;
   }

   // translation in the coordinate space of the parent
   public XYZ Position = null;
   // rotate horizontally in the parent coordinate system (Z is up, so this is rotation about Z)
   public double Rotation = 0;
   // rotate to raise or lower the "front" of the mesh, this is rotation about Y (in our parent coordinate system)
   public double Elevation = 0;
}
