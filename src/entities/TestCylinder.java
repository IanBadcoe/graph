package entities;

import engine.XYZ;
import models.*;

public class TestCylinder extends Object3D
{
   public TestCylinder()
   {
      super(SharedModel);
   }

   private static models.Model makeModel()
   {
      Mesh mesh1 = Mesh.createCylinder(1, 1, 0.3, true, true);
      Mesh mesh2 = Mesh.createCylinder(1, 1, 0.6, true, true);
      Mesh mesh3 = Mesh.createCylinder(1, 1, 1, true, true);

      MeshInstance mi1 = new MeshInstance(mesh1, 0xff806040, new XYZ(0, 0, 0), 0, 0);
      MeshInstance mi2 = new MeshInstance(mesh2, 0xff806040, new XYZ(0, 0, 0), 0, 0);
      MeshInstance mi3 = new MeshInstance(mesh3, 0xff806040, new XYZ(0, 0, 0), 0, 0);

      LoD l1 = new LoD(new MeshInstance[] { mi1 });
      LoD l2 = new LoD(new MeshInstance[] { mi2 });
      LoD l3 = new LoD(new MeshInstance[] { mi3 });

      return new Model(new LoD[] { l1, l2, l3 }, 1.5);
   }

   private static final Model SharedModel = makeModel();
}
