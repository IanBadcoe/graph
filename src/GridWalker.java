class GridWalker
{
   GridWalker(double cell_size, XY begin, XY dir, double ray_length, double feature_radius)
   {
      m_cell_size = cell_size;
      m_begin = begin;
      m_dir = dir;
      m_length = m_reduced_length = ray_length;

      m_feature_radius = feature_radius;

      // this is how far we have to get (orthogonally) from a cell centre, before
      // any wall in that cell cannot possibly hit us
      m_range = (m_cell_size + m_feature_radius) / 2;

      m_major_axis = Math.abs(dir.X) > Math.abs(dir.Y) ? MajorAxis.X : MajorAxis.Y;

      XY end = begin.plus(dir.multiply(m_length));

      m_major_axis_dir = (int)Math.signum(m_major_axis == MajorAxis.X ? dir.X : dir.Y);

      double ordStart = m_major_axis == MajorAxis.X ? begin.X : begin.Y;
      double ordEnd = m_major_axis == MajorAxis.X ? end.X : end.Y;
      OrderedPair<Integer, Integer> maj_cell_lims = findCellLimits(ordStart, ordEnd, m_major_axis_dir);

      m_major_axis_start = maj_cell_lims.First;
      m_major_axis_end = maj_cell_lims.Second;
   }

   private OrderedPair<Integer,Integer> findCellLimits(double ordStart, double ordEnd, int dir)
   {
      // cell rows the beginning and end of line lie in
      int start_cell = ordinateToCell(ordStart);
      int end_cell = ordinateToCell(ordEnd);

      // if we are close enough to the edges of those rows, then we need to examine the previous
      // and following cells as well
      //
      // esy way to do this is by distance from those cells centres
      // (as can measure to center in either direction, but examining an edge would rely
      // on knowing which edge (before or after) for the way we were going...)
      if (Math.abs(cellCentre(start_cell - dir) - ordStart) < m_range)
      {
         start_cell -= dir;
      }

      if (Math.abs(cellCentre(end_cell + dir) - ordEnd) < m_range)
      {
         end_cell += dir;
      }

      return new OrderedPair<>(start_cell, end_cell);
   }

   private double cellCentre(int cellIndex)
   {
      return cellIndex * m_cell_size + m_cell_size * 0.5;
   }

   private int ordinateToCell(double ord)
   {
      return (int)Math.floor(ord / m_cell_size);
   }

   private final double m_cell_size;
   private final XY m_begin;
   private final XY m_dir;
   private final double m_length;
   // when our client finds a candidate result at some point along our line
   // if can reduce the furthest out an interesting further point might be
   // (eg. our initial use-case is for finding the closest wall along a line
   // once we have found one intersect with that line, any closer intersect can be at most
   // half a wall-length further out...)
   private double m_reduced_length;

   enum MajorAxis
   {
      X,
      Y
   }

   private final MajorAxis m_major_axis;
   private final int m_major_axis_start;
   private int m_major_axis_end;
   private final int m_major_axis_dir;
   private final double m_feature_radius;

   private final double m_range;

}
