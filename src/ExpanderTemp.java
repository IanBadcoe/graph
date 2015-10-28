import java.util.*;

class ExpanderTemp
{
   ExpanderTemp(Graph graph, int target_size, Random random)
   {
      m_graph = graph;
      m_target_size = target_size;
      m_nodes = new ArrayList<>(m_graph.AllGraphNodes());
      m_edges = new ArrayList<>(m_graph.AllGraphEdges());
      m_size = m_nodes.size();
      m_random = random;
   }

   public final static class ExpRet
   {
      // 1 == continue
      // 2 == complete
      // 0 == failure
      public int Step;
      public int RetCode;
      public int Size;
      public RelRet RelaxResult;

      ExpRet(int step, int code, int size, RelRet rr)
      {
         Step = step;
         RetCode = code;
         Size = size;
         RelaxResult = rr;
      }
   }

   ExpRet ExpandStep(Main app, TemplateStore templates, ArrayList<Annotation> notes)
   {
      if (m_step == 0)
      {
         // starting a new attempted change, create a new restore-point
         m_restore = m_graph.CreateRestorePoint();

         return ExpandStepInner(app, templates, notes, m_restore);
      }

      notes.clear();

      RelRet rr = RelaxStep(app, 1.0, notes, 0.001, 0.01);

      m_step++;

      if (rr.Crossings > 0)
      {
         m_restore.Restore();
         CacheEdgesAndNodes();

         // relaxation failed
         if (m_fails > 3)
            return new ExpRet(m_step, 0, m_size, rr);

         m_fails++;
         m_step = 0;

         return new ExpRet(m_step, 1, m_size, rr);
      }

      if (!rr.Completed)
      {
         // more relaxation required
         return new ExpRet(m_step, 1, m_size, rr);
      }

      if (m_size >= m_target_size)
      {
         // done
         return new ExpRet(m_step, 2, m_size, rr);
      }

      m_step = 0;

      // more expansion required
      return new ExpRet(m_step, 1, m_size, rr);
   }

   ExpRet ExpandStepInner(Main app, TemplateStore templates, ArrayList<Annotation> notes, IGraphRestore restore)
   {
      m_step++;

      DirectedEdge e = MostStressedEdge(m_edges, 100.0);

      if (e != null)
      {
         AddEdgeCorner(e);
      }
      else
      {
         if (!Expand(templates))
            return new ExpRet(m_step, 0, m_size, null);
      }

      CacheEdgesAndNodes();

      Main.print("size:", m_size, "--------------------------------------------------\n");

//      app.m_go = false;

      // we need to run more
      return new ExpRet(m_step, 1, m_size, null);
   }

   void CacheEdgesAndNodes()
   {
      m_nodes = new ArrayList<>(m_graph.AllGraphNodes());
      m_edges = new ArrayList<>(m_graph.AllGraphEdges());
      m_size = m_nodes.size();
   }

   final static class RelRet
   {
      boolean Completed;
      double MaxForce;
      double Step;
      double MaxMovement;
      int Crossings;
      double MaxEdgeStretch;
      double MaxEdgeSqueeze;
      double MaxEdgeSideSqueeze;
      double MaxNodeSqueeze;

      RelRet(boolean completed, double max_force, double step_size, double max_movement, int crossings,
             double max_edge_stretch, double max_edge_squeeze, double max_edge_side_squeeze, double max_node_squeeze)
      {
         Completed = completed;
         MaxForce = max_force;
         Step = step_size;
         MaxMovement = max_movement;
         Crossings = crossings;
         MaxEdgeStretch = max_edge_stretch;
         MaxEdgeSqueeze = max_edge_squeeze;
         MaxEdgeSideSqueeze = max_edge_side_squeeze;
         MaxNodeSqueeze = max_node_squeeze;
      }
   }

   // step is scaled so that the max force we see causes a movement of max_move
   // until that means a step of > 1, then we start letting the system slow down :-)
   RelRet RelaxStep(Main app, double max_move, ArrayList<Annotation> notes, double force_target, double move_target)
   {
      double maxf = 0.0;

      for(INode n : m_nodes)
      {
         n.ResetForce();
      }

      double max_edge_stretch = 1.0;
      double max_edge_squeeze = 1.0;

      for(DirectedEdge e : m_edges)
      {
         double ratio = AddEdgeForces(e, e.MinLength, e.MaxLength, notes);
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
               double ratio = AddNodeEdgeForces(app, e, n, notes);
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

            if (!n.Connects(m))
            {
               double fraction = AddNodeForces(n, m, notes);

               // fraction too close, if any...
               max_node_squeeze = Math.max(max_node_squeeze, 1 - fraction);
            }
         }
      }

      for(INode n : m_nodes)
      {
         maxf = Math.max(n.GetForce(), maxf);
      }

      boolean ended = true;
      double maxd = 0.0;
      double step = 0.0;

      if (maxf > 0)
      {
         step = Math.min(max_move / maxf, max_move);

         for (INode n : m_nodes)
         {
            maxd = Math.max(n.Step(step), maxd);
         }

         ended = maxd < move_target && maxf < force_target;
      }

      HashSet<DirectedEdgePair> directedEdgePairs = Util.FindCrossingEdges(m_edges);
      int crossings = directedEdgePairs.size();

      if (crossings > 0)
      {
         int i = 4;
      }

      return new RelRet(ended, maxf, step, maxd, crossings,
            max_edge_stretch, max_edge_squeeze, max_edge_side_squeeze, max_node_squeeze);
   }

   private void AddEdgeCorner(DirectedEdge e)
   {
      INode c = m_graph.AddNode("c", "", "EdgeExtend", e.Width);

      XY mid = e.Start.GetPos().Plus(e.End.GetPos()).Divide(2);

      c.SetPos(mid);

      m_graph.Disconnect(e.Start, e.End);
      // idea of lengths is to force no more length but allow
      // a longer corridor if required
      m_graph.Connect(e.Start, c, e.MinLength / 2, e.MaxLength, e.Width);
      m_graph.Connect(c, e.End, e.MinLength / 2, e.MaxLength, e.Width);
   }

   private boolean Expand(TemplateStore templates)
   {
      Collection<INode> expandable_nodes = Util.FilterByCodes(m_graph.AllGraphNodes(), "e");

      // we'll keep the graph expandable at least until size reaches targetSize
      assert expandable_nodes.size() > 0;

      while(expandable_nodes.size() > 0)
      {
         INode n = Util.RemoveRandom(m_random, expandable_nodes);

         ArrayList<Template> temp_templates = templates.GetTemplatesCopy();

         assert temp_templates.size() > 0;

         while(temp_templates.size() > 0)
         {
            Template template = Util.RemoveRandom(m_random, temp_templates);

            // if we're down to the last expandable node and this expansion isn't enough to reach
            // the target, then we can only take a template that produces a graph that can expand further
            if (expandable_nodes.size() == 0 && m_size + template.NodesAdded() < m_target_size && !template.GetCodes().contains("e"))
               continue;

            // for the moment just ask any template if it can expand a particular node
            // could later have a ways of filterint template list down to relevant ones
            if (template.Expand(m_graph, n, m_random))
            {
               return true;
            }

            m_restore.Restore();
         }
      }

      return false;
   }

   // only stresses above 10% are considered
   DirectedEdge MostStressedEdge(ArrayList<DirectedEdge> edges, double d0)
   {
      double max_stress = 1.1;
      DirectedEdge ret = null;

      for(DirectedEdge e : edges)
      {
         double stress = e.Length() / d0;

         if (stress > max_stress)
         {
            ret = e;
            max_stress = stress;
         }
      }

      return ret;
   }

   // returns the edge length as a fraction of d0
   double AddEdgeForces(DirectedEdge e, double dmin, double dmax, ArrayList<Annotation> notes)
   {
      assert dmin <= dmax;

      INode nStart = e.Start;
      INode nEnd = e.End;

      XY d = nEnd.GetPos().Minus(nStart.GetPos());

      // in this case can just ignore these as we hope (i) won't happen and (ii) there will be other non-zero
      // forces to pull them apart
      if (d.IsZero())
         return 1.0;

      double l = d.Length();
      d = d.Divide(l);

      OrderedPair<Double, Double> fd = Util.UnitEdgeForce(l, dmin, dmax);

      double ratio = fd.First;
      double force = fd.Second * EDGE_FORCE_SCALE;

      XY f = d.Multiply(force);
      nStart.AddForce(f);
      nEnd.AddForce(f.Negate());

      if (notes != null)
      {
         notes.add(new Annotation(nStart.GetPos(), nEnd.GetPos(), 128, 128, 255,
               String.format("%6.4f\n%6.4f", force, ratio)));
      }

      return ratio;
   }

   // returns separation as a fraction of summed_radii
   double AddNodeForces(INode node1, INode node2, ArrayList<Annotation> notes)
   {
      XY d = node2.GetPos().Minus(node1.GetPos());
      double summed_radii = node1.GetRad() + node2.GetRad();

      // in this case can just ignore these as we hope (i) won't happen and (ii) there will be other non-zero
      // forces to pull them apart
      if (d.IsZero())
         return 0.0;

      double l = d.Length();
      d = d.Divide(l);

      OrderedPair<Double, Double> fd = Util.UnitNodeForce(l, summed_radii);

      double ratio = fd.First;

      if (ratio != 0)
      {
         double force = fd.Second * NODE_FORCE_SCALE;

         XY f = d.Multiply(force);
         node1.AddForce(f);
         node2.AddForce(f.Negate());

         if (notes != null)
         {
            notes.add(new Annotation(node1.GetPos(), node2.GetPos(), 255, 128, 128,
                  String.format("%6.4f\n%6.4f", force, 1 - ratio)));
         }
      }

      return ratio;
   }

   double AddNodeEdgeForces(Main app, DirectedEdge e, INode n, ArrayList<Annotation> notes)
   {
      Util.NEDRet vals = Util.NodeEdgeDist(n.GetPos(), e.Start.GetPos(), e.End.GetPos());

      if (vals == null)
         return 1.0;

      double summed_radii = n.GetRad() + e.Width;

      if (vals.Dist > summed_radii)
      {
         return 1.0;
      }

      double ratio = vals.Dist / summed_radii;

      double force = (ratio - 1) * EDGE_NODE_FORCE_SCALE;

      XY f = vals.Direction.Multiply(force);

      n.AddForce(f);
      // the divide by two seems to be important, otherwise we can add "momentum" to the system and it can spin without ever converging
      f = f.Negate().Divide(2);
      e.Start.AddForce(f);
      e.End.AddForce(f);

      return ratio;
   }

   Graph m_graph;

   int m_target_size;

   int m_size;

   int m_count = 0;

   // zero for ready to expand, greater for relaxing after an expand
   int m_step = 0;

   ArrayList<INode> m_nodes;
   ArrayList<DirectedEdge> m_edges;

   IGraphRestore m_restore;

   int m_fails = 0;

   Random m_random;

   static final double EDGE_NODE_FORCE_SCALE = 1.0;
   static final double EDGE_FORCE_SCALE = 0.01;
   static final double NODE_FORCE_SCALE = 1.0;
}