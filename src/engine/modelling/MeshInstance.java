package engine.modelling;

import engine.IDraw;

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
   public MeshInstance(Mesh mesh, MeshInstance parent, int colour,
                       Positioner position,
                       Positioner meshOffset,
                       MeshInstance.TrackMode tracking)
   {
      Mesh = mesh;
      Parent = parent;
      Colour = colour;

      Position = position;
      MeshOffset = meshOffset;

      Tracking = tracking;

      if (Parent != null)
         Parent.addChild(this);
   }

   void draw(IDraw draw, double rotation, double elevation)
   {
      draw.pushTransform();

      if (Position != null)
         draw.position(Position);

      if (Tracking == Tracking.Both || Tracking == Tracking.Elevation)
         draw.rotateY(elevation);

      if (Tracking == Tracking.Both || Tracking == Tracking.Rotation)
         draw.rotateZ(rotation);

      if (m_children != null)
         m_children.forEach(x -> x.draw(draw, rotation, elevation));

      if (MeshOffset != null)
         draw.position(MeshOffset);

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

   public enum TrackMode
   {
      None,
      Rotation,
      Elevation,
      Both
   }

   final Mesh Mesh;
   final int Colour;

   // position this mesh and all its children in its parent coordinate system
   Positioner Position;
   // further position the mesh within the mesh-instance
   //
   // this avoids the need to turn or move the instance (and hence all its children, and any control axes)
   // just to get the geometry the right way up
   Positioner MeshOffset;

   final TrackMode Tracking;

   final MeshInstance Parent;

   private ArrayList<MeshInstance> m_children = null;
}
