package engine.controllers;

import engine.level.Level;
import engine.modelling.WorldObject;

import java.util.Random;

public class VehicleController implements IController
{
   public VehicleController()
   {
      newTarget();
   }

   private void newTarget()
   {
      m_target_elevation = (m_random.nextDouble() - 0.5) * Math.PI / 2;
      m_target_rotation = m_random.nextDouble() * Math.PI * 2;
   }

   @Override
   public void timeStep(double timeStep, WorldObject controlled, Level level)
   {
      double d_ele = m_target_elevation - controlled.getElevation();
      double d_rot = m_target_rotation - controlled.getRotation();

      if (Math.abs(d_ele) < 0.01 && Math.abs(d_rot) < 0.01)
      {
         newTarget();
         return;
      }

      d_ele = Math.max(Math.min(d_ele, 0.01), -0.01);
      d_rot = Math.max(Math.min(d_rot, 0.01), -0.01);

      controlled.setElevation(controlled.getElevation() + d_ele);
      controlled.setRotation(controlled.getRotation() + d_rot);
   }

   private double m_target_elevation;
   private double m_target_rotation;
   private final Random m_random = new Random(1);
}
