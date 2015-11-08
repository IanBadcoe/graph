class GeomLayoutCircular extends GeomLayout
{
   GeomLayoutCircular(Level level, XY position, double radius)
   {
      super(level);

      m_position = position;
      m_rad = radius;
   }

   static GeomLayoutCircular createFromNode(Level l, INode n)
   {
      return new GeomLayoutCircular(l, n.GetPos(), n.GetRad());
   }

   @Override
   void makeBaseGeometry()
   {
      double circumference = 2 * Math.PI * m_rad;
      int steps = (int)(circumference / GeomLayout.MaxEdgeLength);

      // don't go below hexagon
      steps = Math.max(steps, 6);

      double d_angle = 2 * Math.PI / steps;

      XY prev = pointForAngle(0.0);
      double angle = d_angle;

      for(int i = 0; i < steps - 1; i++)
      {
         XY curr = pointForAngle(angle);

         addEdge(new GeomEdge(prev, curr));

         prev = curr;

         angle += d_angle;
      }

      // interpolate 0.0 radians exactly
      XY curr = pointForAngle(0.0);

      addEdge(new GeomEdge(prev, curr));
   }

   private XY pointForAngle(double angle)
   {
      return new XY(Math.sin(angle) * m_rad + m_position.X,
            Math.cos(angle) * m_rad + m_position.Y);
   }

   private final XY m_position;
   private final double m_rad;
}
