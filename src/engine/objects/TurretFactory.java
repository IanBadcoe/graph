package engine.objects;

import engine.XYZ;
import engine.controllers.TurretController;
import engine.modelling.LoDModel;
import engine.modelling.LoDModelBuilder;
import engine.modelling.Static;

public class TurretFactory
{
   // pure static class
   private TurretFactory() {}

   public enum TurretType
   {
      TwinGun
   }

   public static Static makeTurret(TurretType type, XYZ pos)
   {
      return new Static(makeTurretGeometry(type), pos, m_controller);
   }

   private static LoDModel makeTurretGeometry(TurretType type)
   {
      switch(type)
      {
         case TwinGun:
            return m_twin_gun;
         default:
            assert false;
      }

      return null;
   }

   private static LoDModel makeTwinGun()
   {
      LoDModelBuilder builder = new LoDModelBuilder(1);

      LoDModelBuilder.MeshSet pillar = builder.createCone(0.5, 0.25, 1.5, false, false, 6, -1);
      builder.insertMeshSet(pillar, Colours.MidGreyGreen, new XYZ(), 0, -Math.PI / 2);

      LoDModelBuilder.MeshSet body = builder.createSphere(1, -1, 0.8, false, true);
      builder.insertMeshSet(body, Colours.LightGrey, new XYZ(0, 0, 2), 0, 0);

      LoDModelBuilder.MeshSet left_mount = builder.createCylinder(0.5, 0.2, false, true, 6, -1);
      builder.insertMeshSet(left_mount, Colours.LightGrey, new XYZ(0, 0.85, 2), Math.PI / 2, Math.PI / 2);

      LoDModelBuilder.MeshSet right_mount = builder.createCylinder(0.5, 0.2, true, false, 6, -1);
      builder.insertMeshSet(right_mount, Colours.LightGrey, new XYZ(0, -1.05, 2), Math.PI / 2, Math.PI / 2);

      LoDModelBuilder.MeshSet barrel = builder.createCylinder(0.15, 2, true, true, -1, -1);
      builder.insertMeshSet(barrel, Colours.LightGreyRed, new XYZ(-0.7, 1.05, 2), 0, 0);
      builder.insertMeshSet(barrel, Colours.LightGreyRed, new XYZ(-0.7, -1.05, 2), 0, 0);

      return builder.makeModel();
   }

   private static final TurretController m_controller = new TurretController();

   private static final LoDModel m_twin_gun = makeTwinGun();
}
