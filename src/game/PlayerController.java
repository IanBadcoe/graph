package game;

import engine.KeyTracker;
import engine.XY;
import engine.controllers.IController;
import engine.level.Level;
import engine.modelling.Movable;
import engine.modelling.WorldObject;

class PlayerController implements IController
{
   public PlayerController(KeyTracker keys)
   {
      m_keys = keys;
   }

   @Override
   public void timeStep(double timeStep, WorldObject controlled, Level level)
   {
      Movable player = (Movable)controlled;

      if (m_keys.isPressed(Main.LEFT_KEY))
      {
         turnLeft(player);
      }

      if (m_keys.isPressed(Main.RIGHT_KEY))
      {
         turnRight(player);
      }

      if (m_keys.isPressed(Main.FORWARDS_KEY))
      {
         accelerate(player);
      }

      if (m_keys.isPressed(Main.BACKWARDS_KEY))
      {
         reverse(player);
      }
   }


   private void turnLeft(Movable player)
   {
      player.addOrientation(TurnFactor);
   }

   private void turnRight(Movable player)
   {
      player.addOrientation(-TurnFactor);
   }

   private void accelerate(Movable player)
   {
      if (player.getSpeed() < -0.2)
      {
         player.addVelocity(player.getVelocity().asUnit().multiply(BrakingFactor), BrakingFactor);
      }
      else if (player.getSpeed() >= 0)
      {
         player.addVelocity(XY.makeDirectionVector(player.getOrientation()).multiply(AcclerationFactor), AcclerationFactor);
      }
      else
      {
         player.addVelocity(player.getVelocity().negate(), player.getVelocity().length());
      }
   }

   private void reverse(Movable player)
   {
      if (player.getSpeed() > 0.2)
      {
         player.addVelocity(player.getVelocity().negate().asUnit().multiply(BrakingFactor), -BrakingFactor);
      }
      else if (player.getSpeed() <= 0)
      {
         player.addVelocity(XY.makeDirectionVector(player.getOrientation()).negate().multiply(ReverseFactor), -ReverseFactor);
      }
      else
      {
         player.addVelocity(player.getVelocity().negate(), -player.getVelocity().length());
      }
   }

   private static final double AcclerationFactor = 0.7;
   private static final double BrakingFactor = 1.4;
   private static final double ReverseFactor = 0.35;
   private static final double TurnFactor = Math.PI / 96;

   private final KeyTracker m_keys;
}
