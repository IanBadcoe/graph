package engine.modelling;

import java.util.ArrayList;


// we can build models from meshes, meshes can be aritrary shapes but for the moment we have utility methods for making
// simple-ish forms: cylinders, spheres etc.
//
// each model can exist an N LoDs, which get simpler as the list is ascended and are used at increasing distance from
// the camera
//
// the same Mesh can be inserted in more than one place in a model
//
// each inserted Mesh is represented by (wrapped by) a MeshInstance
//
// the same mesh selected across all LoDs is represented by the MeshSet (see below)
//
// the mesh-building utilities on Mesh build single meshes, but the mesh-building untilities here
// build a MeshSet with one Mesh for each required LoD
//
// insertMeshSet inserts the Meshes from the set into the appropriate LoDs
//
// each MeshInstance optionally positions and/or rotates its Meshes coordinate system
//
// each MeshInstance further (WIP) positions and rotates its mesh within that coordinate system
// (allowing the mesh to have its position tuned without changing the positioning of all its children
//  this is especially important for "rotation" and "elevation" as these assume they are the Z and Y axes respectively
//  so a rotating MeshInstance cannot be the "wrong" way up, even if its Mesh needs to be...)
//
// each MeshInstance can optionally have a parent MeshInstance, in which case it is positioned within the coordinate
// system of the parent
//
// each MeshInstance can optionally track "rotation" and/or "elevation" these are two hard-wired "posing parameters"
// intended to allow "turret-like" rotation and elevation
//
// the mesh primitives built from the utility methods below come out as:
// - cone - oriented with its "top" on +ve X and its bottom on -ve X
//        - option to cap top and/or bottom
//        - length, top-radius, bottom-radius
// - cylinder - exactly like a cone but with only one radius
// - sphere - oriented with its North pole on +ve X and its South Pole on -ve X
//          - option to truncate top or bottom (cutting through on a plane, leaving a circular section)
//          - option to cap top or bottom (only relevant if truncated)
//
// all the above additionally have:
// - choice of smooth or facetted
// - ability to cap upper limit of how much to divide around the equator (logitudes) or top to bottom (lattitudes)
//
// additionally there is a cuboid which just has xSize, ySize and zSize
//
// to make different LoDs, the shape is just divided to a greater or lesser extent (except when constrained by a
// cap on the division
public class LoDModelBuilder
{
   public LoDModelBuilder(double radius)
   {
      this(LoDDrawable.FacetingFactors, radius);
   }

   @SuppressWarnings("WeakerAccess")
   public LoDModelBuilder(double[] lodFacetFactors, double radius)
   {
      LoDFacetFactors = lodFacetFactors;
      Radius = radius;
      NumLoDs = LoDFacetFactors.length;
      LoDBuilders = new LoDBuilder[NumLoDs];

      for(int i = 0; i < NumLoDs; i++)
      {
         LoDBuilders[i] = new LoDBuilder();
      }
   }

   public LoDModel makeModel()
   {
      LoD[] lods = new LoD[NumLoDs];

      for(int i = 0; i < NumLoDs; i++)
      {
         LoDBuilder lb = LoDBuilders[i];
         // take only the top-level mesh-instances as others get drawn recursively
         // if we need to keep the flat list beyond this point, could do this differently
         MeshInstance[] mis = lb.MeshesInstances.stream().filter(x -> x.Parent == null).toArray(MeshInstance[]::new);
         lods[i] = new LoD(mis);
      }

      return new LoDModel(lods, Radius);
   }

   static class LoDBuilder
   {
      public final ArrayList<MeshInstance> MeshesInstances = new ArrayList<>();

      public MeshInstance findInstanceFor(Mesh mesh)
      {
         return MeshesInstances.stream().filter(x -> x.Mesh == mesh).findFirst().get();
      }
   }

   // collects a set of meshes representing the same mesh from different LoDs
   // allows us to insert the same MeshesInstances in (all the LoDs in) different models
   // or at different locations in the same Model
   //
   // e.g
   // Model
   // +- LoD1   MeshInstance(Mesh1L1)   MeshInstance(Mesh2L1)
   // +- LoD2   MeshInstance(Mesh1L2)   MeshInstance(Mesh2L2)
   // +- LoD3   MeshInstance(Mesh1L3)   MeshInstance(Mesh2L3)
   //                        ^^^^^^^
   // This column contains the meshes that might come from one MeshSet.
   //
   // Inserting a MeshSet into a model injects them, into each LoD as appropriate, each wrapped in a MashInstance
   // with the transform and colour specified in the insertion call
   public static class MeshSet
   {
      public MeshSet(Mesh[] meshes)
      {
         Meshes = meshes;
      }

      public final Mesh[] Meshes;
   }

   public void insertMeshSet(MeshSet ms, MeshSet parent, int colour,
                             Positioner position, Positioner meshOffset,
                             MeshInstance.TrackMode tracking)
   {
      // could allow number of supplied meshes to exceed NumLoDs, as long as we knew which ones to take...
      assert ms.Meshes.length == NumLoDs;

      for(int i = 0; i < NumLoDs; i++)
      {
         MeshInstance parent_mi = null;

         if (parent != null)
            parent_mi = LoDBuilders[i].findInstanceFor(parent.Meshes[i]);

         LoDBuilders[i].MeshesInstances.add(new MeshInstance(
               ms.Meshes[i],
               parent_mi,
               colour,
               position, meshOffset,
               tracking));
      }
   }

   public MeshSet createCylinder(double radius, double length,
                                 boolean capBase, boolean capTop,
                                 boolean smooth)
   {
      return createCylinder(radius, length, capBase, capTop, -1, -1, smooth);
   }

   public MeshSet createCylinder(double radius, double length,
                                 boolean capBase, boolean capTop,
                                 int maxSlicesRound, int maxSlicesUp,
                                 boolean smooth)
   {
      Mesh[] meshes = new Mesh[NumLoDs];

      for(int i = 0; i < NumLoDs; i++)
      {
         meshes[i] = Mesh.createCylinder(radius, length, LoDFacetFactors[i],
               capBase, capTop,
               maxSlicesRound, maxSlicesUp,
               smooth);
      }

      return new MeshSet(meshes);
   }

   public MeshSet createCone(double baseRadius, double topRadius, double length,
                             boolean capBase, boolean capTop,
                             boolean smooth)
   {
      return createCone(baseRadius, topRadius, length, capBase, capTop, -1, -1, smooth);
   }

   public MeshSet createCone(double baseRadius, double topRadius, double length,
                             boolean capBase, boolean capTop,
                             int maxSlicesRound, int maxSlicesUp,
                             boolean smooth)
   {
      Mesh[] meshes = new Mesh[NumLoDs];

      for(int i = 0; i < NumLoDs; i++)
      {
         meshes[i] = Mesh.createCone(baseRadius, topRadius, length, LoDFacetFactors[i],
               capBase, capTop,
               maxSlicesRound, maxSlicesUp,
               smooth);
      }

      return new MeshSet(meshes);
   }

   public MeshSet createSphere(double radius, double baseHeight, double topHeight,
                               boolean capBase, boolean capTop,
                               boolean smooth)
   {
      return createSphere(radius, baseHeight, topHeight, capBase, capTop, -1, -1, smooth);
   }

   @SuppressWarnings("WeakerAccess")
   public MeshSet createSphere(double radius, double baseHeight, double topHeight,
                               boolean capBase, boolean capTop,
                               int maxSlicesRound, int maxSlicesUp,
                               boolean smooth)
   {
      Mesh[] meshes = new Mesh[NumLoDs];

      for(int i = 0; i < NumLoDs; i++)
      {
         meshes[i] = Mesh.createSphere(radius, baseHeight, topHeight, LoDFacetFactors[i],
               capBase, capTop,
               maxSlicesRound, maxSlicesUp,
               smooth);
      }

      return new MeshSet(meshes);
   }

   public MeshSet createCuboid(double xSize, double ySize, double zSize)
   {
      Mesh[] meshes = new Mesh[NumLoDs];

      for(int i = 0; i < NumLoDs; i++)
      {
         meshes[i] = Mesh.createCuboid(xSize, ySize, zSize, LoDFacetFactors[i]);
      }

      return new MeshSet(meshes);
   }

   private final double[] LoDFacetFactors;
   private final int NumLoDs;
   private final LoDBuilder[] LoDBuilders;
   private final double Radius;
}
