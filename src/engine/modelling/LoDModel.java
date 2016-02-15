package engine.modelling;

import engine.IDraw;
import engine.XYZ;

@SuppressWarnings("WeakerAccess")
public class LoDModel
{
   public LoDModel(LoD[] loDs, double radius)
   {
      LoDs = loDs;
      Radius = radius;
   }

   public void draw(IDraw draw, XYZ position, double orientation, double elevation, int lod)
   {
      assert lod >= 0 && lod < LoDs.length;

      draw.pushTransform();

      draw.translate(position);
      draw.rotateY(elevation);
      draw.rotateZ(orientation);

      LoDs[lod].draw(draw);

      draw.popTransform();
   }

   private final LoD[] LoDs;
   public final double Radius;
}
