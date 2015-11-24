class GridWalker
{
   GridWalker(double cell_size, XY begin, XY end, double feature_radius)
   {
      m_cell_size = cell_size;
      m_begin = begin;
      m_end = end;

      m_dir = m_end.minus(m_begin).makeUnit();

      m_feature_radius = feature_radius;

      // this is how far we have to get (orthogonally) from a cell centre, before
      // any wall in that cell cannot possibly hit us
      m_range = (m_cell_size + m_feature_radius) / 2;

      m_major_axis = Math.abs(m_dir.X) > Math.abs(m_dir.Y) ? MajorAxis.X : MajorAxis.Y;
      calculateLineParams();

      m_major_axis_dir = (int)Math.signum(m_major_axis == MajorAxis.X ? m_dir.X : m_dir.Y);

      double maj_ord_start = m_major_axis == MajorAxis.X ? m_begin.X : m_begin.Y;
      double maj_ord_end = m_major_axis == MajorAxis.X ? m_end.X : m_end.Y;

      double min_ord_end = m_major_axis == MajorAxis.X ? m_end.Y : m_end.X;

      // check our line-equation is correct
      assert Math.abs(calculateLine(maj_ord_end) - min_ord_end) < 1e-12;

      // start and end row/columns on major axis
      m_curr_major_cell = adjustCellLimit(maj_ord_start, -m_major_axis_dir);
      m_major_axis_end = adjustCellLimit(maj_ord_end, m_major_axis_dir);

      double min_ord_start = m_major_axis == MajorAxis.X ? m_begin.Y : m_begin.X;

      m_low_minor = min_ord_start < min_ord_end ? min_ord_start : min_ord_end;
      m_high_minor = min_ord_start > min_ord_end ? min_ord_start : min_ord_end;

      calculateMinorLimits();
   }

   private void calculateLineParams()
   {
      XY diff = m_end.minus(m_begin);

      if (m_major_axis == MajorAxis.X)
      {
         m_line_slope = diff.Y / diff.X;
         m_line_offset = m_begin.Y - m_begin.X * m_line_slope;
      }
      else
      {
         m_line_slope = diff.X / diff.Y;
         m_line_offset = m_begin.X - m_begin.Y * m_line_slope;
      }
   }

   private void calculateMinorLimits()
   {
      double cell_edge_before = cellEdge(m_curr_major_cell);
      double cell_edge_after = cellEdge(m_curr_major_cell + 1);

      // we're going to interpolate on the infinite line through start and end
      // however the edges of this cell might be beyond start or end position
      // this can lead to us finding cells we don't need (they are within range
      // of the infinite line but not the finite segment
      //
      // if we just clamp to the minor-axis-range of the segment
      // we clip these away

      cell_edge_before = Math.max(cell_edge_before, m_low_minor);
      cell_edge_before = Math.min(cell_edge_before, m_high_minor);
      cell_edge_after = Math.max(cell_edge_after, m_low_minor);
      cell_edge_after = Math.min(cell_edge_after, m_high_minor);

      // this can make the two edges the same, this is not a problem

      double minor_pos_before = calculateLine(cell_edge_before);
      double minor_pos_after = calculateLine(cell_edge_after);

      double higher_lim = minor_pos_after > minor_pos_before ? minor_pos_after : minor_pos_before;
      double lower_lim = minor_pos_after < minor_pos_before ? minor_pos_after : minor_pos_before;

      m_curr_minor_cell =  adjustCellLimit(lower_lim, -1);
      m_minor_axis_end = adjustCellLimit(higher_lim, 1);
   }

   // calculates a position on the minor axis, given on one the major axis
   private double calculateLine(double major)
   {
      return m_line_slope * major + m_line_offset;
   }

   CC nextCell()
   {
      if (m_state != State.InProgress)
      {
         return null;
      }

      CC ret;

      if (m_major_axis == MajorAxis.X)
      {
         ret = new CC(m_curr_major_cell, m_curr_minor_cell);
      }
      else
      {
         ret = new CC(m_curr_minor_cell, m_curr_major_cell);
      }

      next();

      return ret;
   }

   private void next()
   {
      if (m_curr_minor_cell == m_minor_axis_end)
      {
         if (offMajorEnd())
         {
            m_state = State.Done;

            return;
         }

         m_curr_major_cell += m_major_axis_dir;
         calculateMinorLimits();

         return;
      }

      m_curr_minor_cell++;
   }

   private boolean offMajorEnd()
   {
      int diff = m_curr_major_cell - m_major_axis_end;

      // this detects us being beyond the end, as well as exactly at it
      // scaling by dir makes negative and positive stepping directions
      // both work with >=
      return diff * m_major_axis_dir >= 0;
   }

   private int adjustCellLimit(double ord, int dir)
   {
      // cell row/column the ordinate lies within
      int cell = ordinateToCell(ord);

      // if we are close enough to the edge of the row/column, then we need to examine the adjoining
      // row/column
      //
      // easy way to do this is by distance from cell centres
      // (because we can measure to center in either direction, but examining an edge would rely
      // on working out which edge (before or after) for the way we were going...)
      if (Math.abs(cellCentre(cell + dir) - ord) <= m_range)
      {
         cell += dir;
      }

      return cell;
   }

   private double cellEdge(double cellIndex)
   {
      return cellEdge(cellIndex, m_cell_size);
   }

   private static double cellEdge(double cellIndex, double cellSize)
   {
      return cellIndex * cellSize;
   }

   private double cellCentre(int cellIndex)
   {
      return cellCentre(cellIndex, m_cell_size);
   }

   private static double cellCentre(int cellIndex, double cellSize)
   {
      return cellEdge(cellIndex, cellSize) + cellSize * 0.5;
   }

   private int ordinateToCell(double ord)
   {
      return ordinateToCell(ord, m_cell_size);
   }

   public static int ordinateToCell(double ord, double cellSize)
   {
      return (int)Math.floor(ord / cellSize);
   }

   private CC posToGridCell(XY pos)
   {
      return posToGridCell(pos, m_cell_size);
   }

   public static CC posToGridCell(XY pos, double cellSize)
   {
      return new CC((int)(pos.X / cellSize), (int)(pos.Y / cellSize));
   }

   private XY gridCellToPos(CC cell)
   {
      return gridCellToPos(cell, m_cell_size);
   }

   public static XY gridCellToPos(CC cell, double cellSize)
   {
      return new XY(cell.First * cellSize, cell.Second * cellSize);
   }

   public void resetRayEnd(XY end)
   {
      // new end really shouldn't be in a different direction
      assert end.minus(m_begin).dot(m_dir.rot90()) < 1e-12;

      m_end = end;

      double ordEnd = m_major_axis == MajorAxis.X ? m_end.X : m_end.Y;

      // redo end row/columns on major axis
      m_major_axis_end = adjustCellLimit(ordEnd, m_major_axis_dir);

      // if this puts our current row off the end of the major axis range
      // we will complete the row before we go to done
      //
      // this is as good a thing as any to do in what is a slightly nonsensical
      // situation
   }

   enum MajorAxis
   {
      X,
      Y
   }

   enum State
   {
      InProgress,
      Done
   }

   private State m_state = State.InProgress;

   private final double m_cell_size;
   private final double m_feature_radius;

   private final XY m_begin;
   // when our client finds a candidate result at some point along our line
   // if we are looking for the closest, then we can reduce the furthest out that a further interesting
   // point might be found
   //
   // (eg. our initial use-case is for finding the closest wall along a line
   // once we have found one intersect with that line, any closer intersect can be at most
   // half a wall-length further out...)
   //
   // so m_end isn't final
   private XY m_end;

   // only used for asserting any changed end is in the same direction
   private final XY m_dir;
   private double m_line_slope;
   private double m_line_offset;

   private final MajorAxis m_major_axis;

   private int m_major_axis_end;
   private final int m_major_axis_dir;

   private final double m_range;

   private int m_minor_axis_end;

   private int m_curr_major_cell;
   private int m_curr_minor_cell;

   private double m_low_minor;
   private double m_high_minor;
}
