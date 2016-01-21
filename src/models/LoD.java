package models;

import engine.IDraw;

@SuppressWarnings("WeakerAccess")
public class LoD
{
   public LoD(MeshInstance[] meshes)
   {
      Meshes = meshes;
   }

   void draw(IDraw draw)
   {
      for(MeshInstance mi : Meshes)
      {
         mi.draw(draw);
      }
   }

   final MeshInstance[] Meshes;
}
