package engine.modelling;

import engine.IDraw;

@SuppressWarnings("WeakerAccess")
public class LoD
{
   public LoD(MeshInstance[] meshes)
   {
      Meshes = meshes;
   }

   void draw(IDraw draw, double rotation, double elevation)
   {
      for(MeshInstance mi : Meshes)
      {
         mi.draw(draw, rotation, elevation);
      }
   }

   final MeshInstance[] Meshes;
}
