package game.objects;

import engine.XYZ;
import engine.controllers.VehicleController;
import engine.modelling.LoDModel;
import engine.modelling.LoDModelBuilder;
import engine.modelling.MeshInstance;
import engine.modelling.Movable;
import engine.modelling.Positioner;

import java.util.HashMap;

public class VehicleFactory
{
   // pure static class
   private VehicleFactory() {}

   public enum VehicleType
   {
      MiniSphereTank,
   }

   public static Movable makeVehicle(VehicleType type, XYZ pos)
   {
      return new Movable(makeVehicleGeometry(type), pos,
            m_vehicle_radii.get(VehicleType.MiniSphereTank), new VehicleController(), 1.0);
   }

   private static LoDModel makeVehicleGeometry(VehicleType type)
   {
      switch(type)
      {
         case MiniSphereTank:
            return m_mini_sphere_tank;

         default:
            assert false;
      }

      return null;
   }

   private static LoDModel makeMiniSphereTank()
   {
      LoDModelBuilder builder = new LoDModelBuilder(1);

      LoDModelBuilder.MeshSet body = builder.createSphere(1, -1, 1, false, false, true);
      builder.insertMeshSet(body, null, Colours.MidGreyGreen,
            new Positioner(new XYZ(0, 0, 1.1)), null,
            MeshInstance.TrackMode.None);

      LoDModelBuilder.MeshSet wheel = builder.createSphere(0.25, -0.2, 0.2, true, true, true);
      builder.insertMeshSet(wheel, body, Colours.LightGrey,
            new Positioner(new XYZ(0.8, 0.8, -0.75)), new Positioner(Math.PI / 2, 0),
            MeshInstance.TrackMode.None);
      builder.insertMeshSet(wheel, body, Colours.LightGrey,
            new Positioner(new XYZ(-0.8, 0.8, -0.75)), new Positioner(Math.PI / 2, 0),
            MeshInstance.TrackMode.None);
      builder.insertMeshSet(wheel, body, Colours.LightGrey,
            new Positioner(new XYZ(0.8, -0.8, -0.75)), new Positioner(Math.PI / 2, 0),
            MeshInstance.TrackMode.None);
      builder.insertMeshSet(wheel, body, Colours.LightGrey,
            new Positioner(new XYZ(-0.8, -0.8, -0.75)), new Positioner(Math.PI / 2, 0),
            MeshInstance.TrackMode.None);

      return builder.makeModel();
   }

   private static final HashMap<VehicleType, Double> m_vehicle_radii = new HashMap<>();

   static
   {
      m_vehicle_radii.put(VehicleType.MiniSphereTank, 1.0);
   }

   private static final LoDModel m_mini_sphere_tank = makeMiniSphereTank();
}
