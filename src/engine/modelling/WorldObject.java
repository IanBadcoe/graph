package engine.modelling;

import engine.ICollidable;
import engine.IDrawable;
import engine.XY;
import engine.XYZ;
import engine.controllers.IController;

// base class for "real" things that can be inserted into levels
// e.g. they are drawable and collidable, this is going to mean:
// movables: players, enemies, maybe munitions
// scenery: doors etc (can change shape but not move)
// there's two things that might be around but which are less real:
// fx - will be drawable but not cvollidable
// triggers - might be collidable but not drawable (if that kind of collision proves similar enough
public abstract class WorldObject extends LoDDrawable implements ICollidable, IDrawable
{
   public WorldObject(LoDModel loDModel, XYZ pos)
   {
      super(loDModel);

      m_position = pos;
   }

   public WorldObject(LoDModel loDModel, XYZ pos, IController controller)
   {
      this(loDModel, pos);

      SetController(controller);
   }

   private void SetController(IController controller)
   {
      m_controller = controller;
   }

   public XYZ getViewDir()
   {
      XY dir_2d = XY.makeDirectionVector(getOrientation());

      return new XYZ(dir_2d, 0);
   }

   public void step(double timeStep)
   {
      if (m_controller != null)
      {
         m_controller.step(timeStep, this);
      }
   }

   @Override
   public XYZ getPos3D()
   {
      return m_position;
   }

   @Override
   public XY getPos2D()
   {
      return new XY(m_position);
   }

   public void setPos3D(XYZ pos)
   {
      m_position = pos;
   }

   public void setPos2D(XY pos)
   {
      // change XY, leave Z unchanged
      setPos3D(new XYZ(pos, m_position.Z));
   }

   @Override
   public double getOrientation()
   {
      return m_orientation;
   }

   @Override
   public double getElevation()
   {
      return m_elevation;
   }

   private XYZ m_position;
   private double m_orientation;
   private double m_elevation;

   private IController m_controller;
}
