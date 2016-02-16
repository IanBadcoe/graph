package game;

import engine.*;
import engine.modelling.Movable;

public class Player extends Movable
{
   public Player()
   {
      // to draw the player from outside, we will eventually need a model here
      // but for the moment skip that
      super(null, new XYZ(), 2);
   }

   public void turnLeft()
   {
      addOrientation(TurnFactor);
   }

   public void turnRight()
   {
      addOrientation(-TurnFactor);
   }

   public void accelerate()
   {
      if (getSpeed() < -0.2)
      {
         addVelocity(getVelocity().asUnit().multiply(BrakingFactor), BrakingFactor);
      }
      else if (getSpeed() >= 0)
      {
         addVelocity(XY.makeDirectionVector(getOrientation()).multiply(AcclerationFactor), AcclerationFactor);
      }
      else
      {
         addVelocity(getVelocity().negate(), getVelocity().length());
      }
   }

   public void reverse()
   {
      if (getSpeed() > 0.2)
      {
         addVelocity(getVelocity().negate().asUnit().multiply(BrakingFactor), -BrakingFactor);
      }
      else if (getSpeed() <= 0)
      {
         addVelocity(XY.makeDirectionVector(getOrientation()).negate().multiply(ReverseFactor), -ReverseFactor);
      }
      else
      {
         addVelocity(getVelocity().negate(), -getVelocity().length());
      }
   }

   @Override
   public void draw2D(IDraw draw)
   {
      draw.stroke(200, 200, 200);
      draw.strokeWidth(1, true);
      draw.circle(getPos2D(), getRadius());
      draw.strokeWidth(1, false);
      draw.line(getPos2D(), getPos2D().plus(XY.makeDirectionVector(getOrientation()).multiply(getRadius() * 1.1)));
   }

   @Override
   public void draw3D(IDraw draw, XYZ eye)
   {
      // HUD??
   }

   @Override
   public XYZ getPos3D()
   {
      return new XYZ(getPos2D(), 0);
   }

   @Override
   public double getElevation()
   {
      return 0;
   }

   public XYZ getEye()
   {
      return new XYZ(getPos2D(), 2);
   }

   private static final double AcclerationFactor = 0.7;
   private static final double BrakingFactor = 1.4;
   private static final double ReverseFactor = 0.35;
   private static final double TurnFactor = Math.PI / 96;
}
