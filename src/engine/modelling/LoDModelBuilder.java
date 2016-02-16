package engine.modelling;

import engine.XYZ;

import java.util.ArrayList;

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
         MeshInstance[] mis = lb.Meshes.stream().toArray(MeshInstance[]::new);
         lods[i] = new LoD(mis);
      }

      return new LoDModel(lods, Radius);
   }

   static class LoDBuilder
   {
      public final ArrayList<MeshInstance> Meshes = new ArrayList<>();
   }

   // collects a set of meshes representing the same mesh from different LoDs
   // allows us to insert the same Meshes in (all the LoDs in) different models
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

   public void insertMeshSet(MeshSet ms, int colour, XYZ offset, double orientation, double elevation)
   {
      // could allow number of supplied meshes to exceed NumLoDs, as long as we knew which ones to take...
      assert ms.Meshes.length == NumLoDs;

      for(int i = 0; i < NumLoDs; i++)
      {
         LoDBuilders[i].Meshes.add(new MeshInstance(
               ms.Meshes[i],
               colour,
               offset, orientation, elevation));
      }
   }

   public MeshSet createCylinder(double radius, double length,
                                 boolean capBase, boolean capTop)
   {
      return createCylinder(radius, length, capBase, capTop, -1, -1);
   }

   public MeshSet createCylinder(double radius, double length,
                                 boolean capBase, boolean capTop,
                                 int maxSlicesRound, int maxSlicesUp)
   {
      Mesh[] meshes = new Mesh[NumLoDs];

      for(int i = 0; i < NumLoDs; i++)
      {
         meshes[i] = Mesh.createCylinder(radius, length, LoDFacetFactors[i],
               capBase, capTop,
               maxSlicesRound, maxSlicesUp);
      }

      return new MeshSet(meshes);
   }

   public MeshSet createCone(double baseRadius, double topRadius, double length,
                             boolean capBase, boolean capTop)
   {
      return createCone(baseRadius, topRadius, length, capBase, capTop, -1, -1);
   }

   public MeshSet createCone(double baseRadius, double topRadius, double length,
                             boolean capBase, boolean capTop,
                             int maxSlicesRound, int maxSlicesUp)
   {
      Mesh[] meshes = new Mesh[NumLoDs];

      for(int i = 0; i < NumLoDs; i++)
      {
         meshes[i] = Mesh.createCone(baseRadius, topRadius, length, LoDFacetFactors[i],
               capBase, capTop,
               maxSlicesRound, maxSlicesUp);
      }

      return new MeshSet(meshes);
   }

   public MeshSet createSphere(double radius, double baseHeight, double topHeight,
                               boolean capBase, boolean capTop)
   {
      return createSphere(radius, baseHeight, topHeight, capBase, capTop, -1, -1);
   }

   public MeshSet createSphere(double radius, double baseHeight, double topHeight,
                               boolean capBase, boolean capTop,
                               int maxSlicesRound, int maxSlicesUp)
   {
      Mesh[] meshes = new Mesh[NumLoDs];

      for(int i = 0; i < NumLoDs; i++)
      {
         meshes[i] = Mesh.createSphere(radius, baseHeight, topHeight, LoDFacetFactors[i],
               capBase, capTop,
               maxSlicesRound, maxSlicesUp);
      }

      return new MeshSet(meshes);
   }

   public MeshSet createCuboid(double width, double length, double height)
   {
      Mesh[] meshes = new Mesh[NumLoDs];

      for(int i = 0; i < NumLoDs; i++)
      {
         meshes[i] = Mesh.createCuboid(width, length, height, LoDFacetFactors[i]);
      }

      return new MeshSet(meshes);
   }

   private final double[] LoDFacetFactors;
   private final int NumLoDs;
   private final LoDBuilder[] LoDBuilders;
   private final double Radius;
}
