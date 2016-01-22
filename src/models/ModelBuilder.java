package models;

import engine.XYZ;

import java.util.ArrayList;

public class ModelBuilder
{
   ModelBuilder(double[] lodFacetFactors)
   {
      LoDFacetFactors = lodFacetFactors;
      NumLoDs = LoDFacetFactors.length;
      LoDBuilders = new LoDBuilder[NumLoDs];

      for(int i = 0; i < NumLoDs; i++)
      {
         LoDBuilders[i] = new LoDBuilder();
      }
   }

   static class LoDBuilder
   {
      public ArrayList<MeshInstance> Meshes = new ArrayList<>();
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
      Mesh[] meshes = new Mesh[NumLoDs];

      for(int i = 0; i < NumLoDs; i++)
      {
         meshes[i] = Mesh.createCylinder(radius, length, LoDFacetFactors[i], capBase, capTop);
      }

      return new MeshSet(meshes);
   }

   public MeshSet createCone(double baseRadius, double topRadius, double length,
                             boolean capBase, boolean capTop)
   {
      Mesh[] meshes = new Mesh[NumLoDs];

      for(int i = 0; i < NumLoDs; i++)
      {
         meshes[i] = Mesh.createCone(baseRadius, topRadius, length, LoDFacetFactors[i], capBase, capTop);
      }

      return new MeshSet(meshes);
   }

   public MeshSet createSphere(double radius, double topHeight, double baseHeight,
                               boolean capBase, boolean capTop)
   {
      Mesh[] meshes = new Mesh[NumLoDs];

      for(int i = 0; i < NumLoDs; i++)
      {
         meshes[i] = Mesh.createSphere(radius, topHeight, baseHeight, LoDFacetFactors[i], capBase, capTop);
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

   final double[] LoDFacetFactors;
   final int NumLoDs;
   final LoDBuilder[] LoDBuilders;
}
