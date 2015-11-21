import java.util.ArrayList;

@SuppressWarnings("WeakerAccess")
public class RectangularGeomLayout extends GeomLayout
{
   RectangularGeomLayout(XY start, XY end, double half_width)
   {
      m_start = start;
      m_end = end;
      m_half_width = half_width;
   }

   @Override
   Loop makeBaseGeometry()
   {
      XY dir = m_end.minus(m_start);
      double length = dir.length();
      dir = dir.divide(length);

      XY width_dir = dir.rot270();
      XY half_width = width_dir.multiply(m_half_width);

      XY start_left = m_start.plus(half_width);
      XY start_right = m_start.minus(half_width);
      XY end_left = m_end.plus(half_width);
      XY end_right = m_end.minus(half_width);

      ArrayList<Curve> curves = new ArrayList<>();
      curves.add(new LineCurve(start_left, dir, length));
      curves.add(new LineCurve(end_left, width_dir.negate(), m_half_width * 2));
      curves.add(new LineCurve(end_right, dir.negate(), length));
      curves.add(new LineCurve(start_right, width_dir, m_half_width * 2));

      assert  curves.get(0).endPos().equals(curves.get(1).startPos(), 1e-6);
      assert  curves.get(1).endPos().equals(curves.get(2).startPos(), 1e-6);
      assert  curves.get(2).endPos().equals(curves.get(3).startPos(), 1e-6);
      assert  curves.get(3).endPos().equals(curves.get(0).startPos(), 1e-6);

      return new Loop(curves);
   }

   @Override
   LoopSet makeDetailGeometry()
   {
      return null;
   }

   private final XY m_start;
   private final XY m_end;
   private final double m_half_width;
}
