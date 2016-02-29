package game.objects;

import engine.XYZ;
import engine.controllers.TurretController;
import engine.modelling.LoDModel;
import engine.modelling.LoDModelBuilder;
import engine.modelling.MeshInstance;
import engine.modelling.Positioner;
import engine.modelling.Static;

public class TurretFactory
{
   // pure static class
   private TurretFactory() {}

   public enum TurretType
   {
      FloorBasedTwinGun,
      CeilingMountedCamera
   }

   public static Static makeTurret(TurretType type, XYZ pos)
   {
      return new Static(makeTurretGeometry(type), pos, new TurretController());
   }

   private static LoDModel makeTurretGeometry(TurretType type)
   {
      switch(type)
      {
         case FloorBasedTwinGun:
            return m_twin_gun;
         case CeilingMountedCamera:
            return m_ceiling_camera;

         default:
            assert false;
      }

      return null;
   }

   private static LoDModel makeTwinGun()
   {
      LoDModelBuilder builder = new LoDModelBuilder(1);

      LoDModelBuilder.MeshSet pillar = builder.createCone(0.5, 0.25, 1.5, false, false, 6, -1, false);
      builder.insertMeshSet(pillar, null, Colours.MidGreyGreen,
            null, new Positioner(0, -Math.PI / 2),
            MeshInstance.TrackMode.None);

      LoDModelBuilder.MeshSet body = builder.createSphere(1, -1, 0.8, false, true, true);
      builder.insertMeshSet(body, pillar, Colours.LightGrey,
            new Positioner(new XYZ(0, 0, 2)), null,
            MeshInstance.TrackMode.Rotation);

      LoDModelBuilder.MeshSet left_mount = builder.createCylinder(0.5, 0.2, false, true, 6, -1, false);
      builder.insertMeshSet(left_mount, body, Colours.LightGrey,
            new Positioner(new XYZ(0, 0.85, 0)), new Positioner(Math.PI / 2, Math.PI / 2),
            MeshInstance.TrackMode.Elevation);

      LoDModelBuilder.MeshSet right_mount = builder.createCylinder(0.5, 0.2, true, false, 6, -1, false);
      builder.insertMeshSet(right_mount, body, Colours.LightGrey,
            new Positioner(new XYZ(0, -1.05, 0)), new Positioner(Math.PI / 2, Math.PI / 2),
            MeshInstance.TrackMode.Elevation);

      LoDModelBuilder.MeshSet barrel = builder.createCylinder(0.15, 2, true, true, -1, -1, true);
      builder.insertMeshSet(barrel, left_mount, Colours.LightGreyRed,
            null, new Positioner(new XYZ(-0.7, 0.2, 0)),
            MeshInstance.TrackMode.None);
      builder.insertMeshSet(barrel, right_mount, Colours.LightGreyRed,
            null, new Positioner(new XYZ(-0.7, 0, 0)),
            MeshInstance.TrackMode.None);

      return builder.makeModel();
   }

   private static LoDModel makeCeilingCamera()
   {
      LoDModelBuilder builder = new LoDModelBuilder(1);

      LoDModelBuilder.MeshSet mount = builder.createCylinder(0.2, 0.03, true, false, true);
      builder.insertMeshSet(mount, null, Colours.MidGreyGreen,
            new Positioner(new XYZ(0, 0, 3.97)), new Positioner(0, -Math.PI / 2),
            MeshInstance.TrackMode.None);

      LoDModelBuilder.MeshSet hanger = builder.createCylinder(0.05, 0.2, false, false, true);
      builder.insertMeshSet(hanger, mount, Colours.MidGrey,
            new Positioner(new XYZ(0, 0, -0.2)), new Positioner(0, -Math.PI / 2),
            MeshInstance.TrackMode.None);

      LoDModelBuilder.MeshSet body = builder.createCylinder(0.07, 0.1, true, true, true);
      builder.insertMeshSet(body, hanger, Colours.LightGrey,
            null, new Positioner(new XYZ(-0.05, 0, 0), Math.PI / 2, 0),
            MeshInstance.TrackMode.Rotation);

      LoDModelBuilder.MeshSet camera = builder.createCuboid(0.2, 0.09, 0.15);
      builder.insertMeshSet(camera, body, Colours.LightGrey,
            new Positioner(new XYZ(0, -.095, 0)), null,
            MeshInstance.TrackMode.Elevation);

      LoDModelBuilder.MeshSet lens1 = builder.createCylinder(0.04, 0.11, true, false, true);
      builder.insertMeshSet(lens1, camera, Colours.DarkGrey,
            new Positioner(new XYZ(-0.21, 0, 0)), null,
            MeshInstance.TrackMode.None);

      LoDModelBuilder.MeshSet lens2 = builder.createCylinder(0.06, 0.02, true, true, 6, 1, true);
      builder.insertMeshSet(lens2, lens1, Colours.LightGrey,
            null, new Positioner(new XYZ(0.01, 0, 0)),
            MeshInstance.TrackMode.None);

      return builder.makeModel();
   }

   private static final LoDModel m_twin_gun = makeTwinGun();
   private static final LoDModel m_ceiling_camera = makeCeilingCamera();
}
