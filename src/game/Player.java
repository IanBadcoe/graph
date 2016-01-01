package game;

import engine.*;

public class Player extends Movable implements IDrawable
{
   public Player()
   {
      super(5);
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
      addVelocity(XY.makeDirectionVector(getOrientation()).multiply(AcclerationFactor));
   }

   public void brake()
   {
      addVelocity(getVelocity().negate().asUnit().multiply(BrakingFactor));
   }

   @Override
   public void draw(IDraw draw)
   {
      draw.stroke(200, 200, 200);
      draw.strokeWidth(0.5);
      draw.circle(getPosition(), getRadius());
      draw.strokeWidth(2.0);
      draw.line(getPosition(), getPosition().plus(XY.makeDirectionVector(getOrientation()).multiply(getRadius() * 1.1)));
   }

   private static final double AcclerationFactor = 1.0;
   private static final double BrakingFactor = 2.0;
   private static final double TurnFactor = Math.PI / 64;
}
