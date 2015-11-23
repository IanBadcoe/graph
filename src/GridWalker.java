class GridWalker
{
   GridWalker(double grid_size, XY begin, XY dir, double length)
   {
      m_grid_size = grid_size;
      m_begin = begin;
      m_dir = dir;
      m_length = m_reduced_length = length;

      m_major_axis = Math.abs(dir.X) > Math.abs(dir.Y) ? MajorAxis.X : MajorAxis.Y;
      m_major_axis_dir = (int)Math.signum(m_major_axis == MajorAxis.X ? dir.X : dir.Y);

      int major_cell = ordinateToCell(m_major_axis == MajorAxis.X ? begin.X : begin.Y);

      // this is the major-axis cell row within which begin lies
      // we can however need to be one row before that, if we are close to the edge
      //
      // however use cell-centre to calculate this, as otherwise we need to know which edge we need to look at

      double prev_cell_centre = cellCentre(major_cell - m_major_axis_dir);
   }

   private double cellCentre(int cellIndex)
   {
      return cellIndex * m_grid_size + m_grid_size * 0.5;
   }

   private int ordinateToCell(double ord)
   {
      return (int)Math.floor(ord / m_grid_size);
   }

   private final double m_grid_size;
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
}
