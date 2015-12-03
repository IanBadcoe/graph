import java.util.ArrayList;

class ViewCircle
{
   ViewCircle(XY centre)
   {
      m_centre = centre;

      // by doing this we make ourselves describe a uniform large distance in all directions
      m_circle.add(new OrderedPair<>(0.0, 1e6));
   }

   void insert(Wall w)
   {
      double start_ang = Util.fixupAngle(Util.atan2(w.Start.minus(m_centre)));
      double end_ang = Util.fixupAngle(Util.atan2(w.End.minus(m_centre)));

      int idx = findFirstEntryAbove(start_ang);
   }

   private int findFirstEntryAbove(double start_ang)
   {
      m_circle.
   }

   final private XY m_centre;
   final private ArrayList<OrderedPair<Double, Double>> m_circle = new ArrayList<>();
}
