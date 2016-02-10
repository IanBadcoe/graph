package engine.objects;

import engine.ICollidable;
import engine.XY;
import engine.XYZ;

public class Static extends WorldObject
{
   public Static(LoDModel loDModel, XYZ pos, double spin)
   {
      super(loDModel);

      m_position = pos;
      m_spin = spin;
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

   @Override
   public XYZ getEye()
   {
      return null;
   }

   @Override
   public ColRet collide(Movable m, XY where, XY direction, XY wherePrevious)
   {
      return null;
   }

   public void step(double timeStep)
   {
      m_orientation += m_spin;
   }

   private XYZ m_position;
   private double m_orientation;
   private double m_elevation;
   private double m_spin;
}
