package models;

import engine.IDraw;
import engine.XYZ;

@SuppressWarnings("WeakerAccess")
public class Model
{
   public Model(Mesh[] loDs, double radius)
   {
      LoDs = loDs;
      Radius = radius;
   }

   public void draw(IDraw draw, XYZ position, double orientation, int lod)
   {
      assert lod >= 0 && lod < LoDs.length;

      draw.pushTransform();
      draw.translate(position);
      draw.rotateZ(orientation);

      LoDs[lod].draw(draw);
      draw.popTransform();
   }

   private final Mesh[] LoDs;
   public final double Radius;
}
