package engine;

import java.util.ArrayList;

public class RelaxerStepper implements IStepper
{
   public RelaxerStepper(Graph graph, LevelGeneratorConfiguration c)
   {
      m_graph = graph;

      m_config = c;
   }

   private void setUp()
   {
      m_nodes = m_graph.allGraphNodes();
      m_edges = m_graph.allGraphEdges();
      // these are shortest path lengths through the graph
      //
      // irrespective of node <-> node or node <-> edge forces, we don't want to be pushed further than this
      // so we shorten the distances of those so they don't stretch edges too far
      //
      // (node <-> node and node <-> edge forces have to be stronger than edge forces
      // as we rely on edges stretching (in other cases) to tell ue when we need to
      // lengthen an edge (inserting a corner)
      m_node_dists = ShortestPathFinder.FindPathLengths(m_graph, x -> (x.MaxLength + x.MinLength) / 2);

      m_setup_done = true;
   }

   @Override
   public StepperController.StatusReportInner step(StepperController.Status status)
   {
      if (!m_setup_done)
      {
         setUp();
      }

      return RelaxStep();
   }

   // step is scaled so that the max force we see causes a movement of max_move
   // until that means a step of > 1, then we start letting the system slow down :-)
   private StepperController.StatusReportInner RelaxStep()
   {
      double maxf = 0.0;

      m_nodes.forEach(INode::resetForce);

      double max_edge_stretch = 1.0;
      double max_edge_squeeze = 1.0;

      for(DirectedEdge e : m_edges)
      {
         double ratio = AddEdgeForces(e, e.MinLength, e.MaxLength);
         max_edge_stretch = Math.max(ratio, max_edge_stretch);
         max_edge_squeeze = Math.min(ratio, max_edge_squeeze);
      }

      double max_edge_side_squeeze = 0.0;

      for(DirectedEdge e : m_edges)
      {
         for(INode n : m_nodes)
         {
            if (!e.Connects(n))
            {
               double ratio = AddNodeEdgeForces(e, n);
               max_edge_side_squeeze = Math.min(ratio, max_edge_side_squeeze);
            }
         }
      }

      double max_node_squeeze = 0.0;

      for(INode n : m_nodes)
      {
         for (INode m : m_nodes)
         {
            if (n == m)
               break;

            if (!n.connects(m))
            {
               double fraction = AddNodeForces(n, m);

               // fraction too close, if any...
               max_node_squeeze = Math.max(max_node_squeeze, 1 - fraction);
            }
         }
      }

      for(INode n : m_nodes)
      {
         maxf = Math.max(n.getForce(), maxf);
      }

      boolean ended = true;
      double maxd = 0.0;
      double step = 0.0;

      if (maxf > 0)
      {
         step = Math.min(m_config.RelaxationMaxMove / maxf, m_config.RelaxationMaxMove);

         for (INode n : m_nodes)
         {
            maxd = Math.max(n.step(step), maxd);
         }

         ended = maxd < m_config.RelaxationMoveTarget && maxf < m_config.RelaxationForceTarget;
      }

      int crossings = Util.findCrossingEdges(m_edges).size();

      if (crossings > 0)
      {
         return new StepperController.StatusReportInner(StepperController.Status.StepOutFailure,
               null, "Generated crossing edges during relaxation.");
      } else if (ended)
      {
         return new StepperController.StatusReportInner(StepperController.Status.StepOutSuccess,
               null, "Relaxed to still-point tolerances.");
      }

      return new StepperController.StatusReportInner(StepperController.Status.Iterate,
            null,
            " move:" + maxd +
            " time step:" + step +
            " force:" + maxf +
            " max edge stretch:" + max_edge_stretch +
            " max edge squeeze: " + max_edge_squeeze +
            " max edge side squeeze: " + max_edge_side_squeeze +
            " max node squeeze: " + max_node_squeeze);
   }


   // returns the edge length as a fraction of d0
   private double AddEdgeForces(DirectedEdge e, double dmin, double dmax)
   {
      assert dmin <= dmax;

      INode nStart = e.Start;
      INode nEnd = e.End;

      XY d = nEnd.getPos().minus(nStart.getPos());

      // in this case can just ignore these as we hope (i) won't happen and (ii) there will be other non-zero
      // forces to pull them apart
      if (d.isZero())
         return 1.0;

      double l = d.length();
      d = d.divide(l);

      OrderedPair<Double, Double> fd = Util.unitEdgeForce(l, dmin, dmax);

      double ratio = fd.First;
      double force = fd.Second * m_config.EdgeLengthForceScale;

      XY f = d.multiply(force);
      nStart.addForce(f);
      nEnd.addForce(f.negate());

/*      if (notes != null)
      {
         notes.add(new Annotation(nStart.getPos(), nEnd.getPos(), 128, 128, 255,
               String.format("%6.4f\n%6.4f", force, ratio)));
      } */

      return ratio;
   }

   // returns separation as a fraction of summed_radii
   private double AddNodeForces(INode node1, INode node2)
   {
      XY d = node2.getPos().minus(node1.getPos());
      double adjusted_radius = Math.min(m_node_dists[node1.getIdx()][node2.getIdx()],
            node1.getRad() + node2.getRad() + m_config.RelaxationMinimumSeparation);

      // in this case can just ignore these as we hope (i) won't happen and (ii) there will be other non-zero
      // forces to pull them apart
      if (d.isZero())
         return 0.0;

      double l = d.length();
      d = d.divide(l);

      OrderedPair<Double, Double> fd = Util.unitNodeForce(l, adjusted_radius);

      double ratio = fd.First;

      if (ratio != 0)
      {
         double force = fd.Second * m_config.NodeToNodeForceScale;

         XY f = d.multiply(force);
         node1.addForce(f);
         node2.addForce(f.negate());

/*         if (notes != null)
         {
            notes.add(new Annotation(node1.getPos(), node2.getPos(), 255, 128, 128,
                  String.format("%6.4f\n%6.4f", force, 1 - ratio)));
         } */
      }

      return ratio;
   }

   private double AddNodeEdgeForces(DirectedEdge e, INode n)
   {
      Util.NEDRet vals = Util.nodeEdgeDistDetailed(n.getPos(), e.Start.getPos(), e.End.getPos());

      if (vals == null)
         return 1.0;

      double summed_radii = Math.min(m_node_dists[e.Start.getIdx()][n.getIdx()],
            Math.min(m_node_dists[e.End.getIdx()][n.getIdx()],
                  n.getRad() + e.HalfWidth) + m_config.RelaxationMinimumSeparation);

      if (vals.Dist > summed_radii)
      {
         return 1.0;
      }

      double ratio = vals.Dist / summed_radii;

      double force = (ratio - 1) * m_config.EdgeToNodeForceScale;

      XY f = vals.Direction.multiply(force);

      n.addForce(f);
      // the divide by two seems to be important, otherwise we can add "momentum" to the system and it can spin without ever converging
      f = f.negate().divide(2);
      e.Start.addForce(f);
      e.End.addForce(f);

      return ratio;
   }

   void AdjustPathLengthsForRadii()
   {
      for(INode nj : m_graph.allGraphNodes())
      {
         int j = nj.getIdx();
         for(INode nk : m_graph.allGraphNodes())
         {
            int k = nk.getIdx();

            m_node_dists[j][k] = Math.min(nj.getRad() + nk.getRad(),
                  m_node_dists[j][k]);
         }
      }
   }

   private final Graph m_graph;
   private ArrayList<INode> m_nodes;
   private ArrayList<DirectedEdge> m_edges;

   // whichever is smaller out of the summed-radii and the
   // shortest path through the graph between two nodes
   // we use this as d0 in the node <-> node force function
   // because otherwise a large node can force its second-closest
   // neighbour (and further) so far away that the edge gets split
   // and then the new second-closest neighbour is in the same position
   private double[][] m_node_dists;

   private final LevelGeneratorConfiguration m_config;

   private boolean m_setup_done = false;
}
