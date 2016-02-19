package engine.modelling;

import engine.IDraw;
import engine.XYZ;

import java.util.ArrayList;

/**
 * The difference between "offset" and "position" is:
 * - Position - places this mesh and all its children within the coordinate-space of the parent mesh or model
 * - Offset - adjusts the draw position of this mesh relative to that
 *
 * Any orientation and elevation rotations applied to this mesh (and thus to its children) are relative to Position
 *
 * So Position establishes the transform origin for this level of the hierarchy...  Offset just moves the placing of
 * this mesh relative to that.
 *
 * Either can be null, to mean (0, 0, 0)
 */
@SuppressWarnings("WeakerAccess")
public class MeshInstance
{
   public MeshInstance(Mesh mesh, MeshInstance parent, int colour, XYZ position, XYZ offset, double orientation, double elevation)
   {
      Mesh = mesh;
      Colour = colour;
      Position = position;
      Offset = offset;
      Orientation = orientation;
      Elevation = elevation;
      Parent = parent;

      if (Parent != null)
         Parent.addChild(this);
   }

   void draw(IDraw draw)
   {
      draw.pushTransform();

      if (Position != null)
         draw.translate(Position);

      draw.rotateY(Elevation);
      draw.rotateZ(Orientation);

      if (m_children != null)
         m_children.forEach(x -> x.draw(draw));

      if (Offset != null)
         draw.translate(Offset);

      draw.fill(Colour);
      draw.noStroke();

      Mesh.draw(draw);

      draw.popTransform();
   }

   public void addChild(MeshInstance child)
   {
      // current order of events sets this first...
      assert child.Parent == this;

      if (m_children == null)
         m_children = new ArrayList<>();

      m_children.add(child);
   }

   final Mesh Mesh;
   final int Colour;
   final XYZ Position;
   final XYZ Offset;
   final double Orientation;
   final double Elevation;

   final MeshInstance Parent;

   private ArrayList<MeshInstance> m_children = null;
}
