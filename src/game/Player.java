package game;

import engine.*;

public class Player extends Movable implements IDrawable
{
   public Player()
   {
      super(2);
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
      draw.strokeWidth(1, true);
      draw.circle(getPosition(), getRadius());
      draw.strokeWidth(1, false);
      draw.line(getPosition(), getPosition().plus(XY.makeDirectionVector(getOrientation()).multiply(getRadius() * 1.1)));
   }

   public XYZ getEye()
   {
      return new XYZ(getPosition(), 2);
   }

   public XYZ getViewDir()
   {
      XY dir_2d = XY.makeDirectionVector(getOrientation());

      return new XYZ(dir_2d, 0);
   }

   private static final double AcclerationFactor = 1.0;
   private static final double BrakingFactor = 2.0;
   private static final double TurnFactor = Math.PI / 64;
}
