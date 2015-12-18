package engine;

@SuppressWarnings("WeakerAccess")
public class Transform
{
   // can have other ways to initialise later if required
   Transform(Movable.DynamicsPosition pos)
   {
      m_translate = pos.Position;
      m_rotate = new Matrix2D(pos.Orientation);
   }

   XY transform(XY in)
   {
      return m_rotate.multiply(in).plus(m_translate);
   }

   // can transform other types later if required

   private final XY m_translate;
   private final Matrix2D m_rotate;
}
