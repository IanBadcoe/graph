package models;

import engine.IDraw;
import engine.XYZ;

@SuppressWarnings("WeakerAccess")
public class MeshInstance
{
   public MeshInstance(Mesh mesh, int colour, XYZ offset, double orientation, double elevation)
   {
      Mesh = mesh;
      Colour = colour;
      Offset = offset;
      Orientation = orientation;
      Elevation = elevation;
   }

   void draw(IDraw draw)
   {
      draw.pushTransform();

      draw.translate(Offset);
      draw.rotateY(Elevation);
      draw.rotateZ(Orientation);

      draw.fill(Colour);
      draw.noStroke();

      Mesh.draw(draw);

      draw.popTransform();
   }

   final Mesh Mesh;
   final int Colour;
   final XYZ Offset;
   final double Orientation;
   final double Elevation;
}
