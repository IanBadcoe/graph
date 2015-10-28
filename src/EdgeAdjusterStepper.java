class EdgeAdjusterStepper implements IExpandStepper
{
   EdgeAdjusterStepper(Graph graph, DirectedEdge edge)
   {
      m_graph = graph;
      m_edge = edge;
   }

   @Override
   public Expander.ExpandRet Step(Expander.ExpandStatus status)
   {
      switch (status)
      {
         case StepIn:
            SplitEdge();

            IExpandStepper child = new RelaxerStepper(m_graph, 1.0, 0.001, 0.01);

            return new Expander.ExpandRet(Expander.ExpandStatus.StepIn,
                  child, "Relaxing split edge.");

         case StepOutSuccess:
            return new Expander.ExpandRet(Expander.ExpandStatus.StepOutSuccess,
                  null, "Successfully relaxed split edge.");

         case StepOutFailure:
            return new Expander.ExpandRet(Expander.ExpandStatus.StepOutFailure,
                  null, "Failed to relax split edge.");
      }

      // shouldn't get here, crash horribly

      assert false;

      return null;
   }

   void SplitEdge()
   {
      INode c = m_graph.AddNode("c", "", "EdgeExtend", m_edge.Width);

      XY mid = m_edge.Start.GetPos().Plus(m_edge.End.GetPos()).Divide(2);

      c.SetPos(mid);

      m_graph.Disconnect(m_edge.Start, m_edge.End);
      // idea of lengths is to force no more length but allow
      // a longer corridor if required
      m_graph.Connect(m_edge.Start, c, m_edge.MinLength / 2, m_edge.MaxLength, m_edge.Width);
      m_graph.Connect(c, m_edge.End, m_edge.MinLength / 2, m_edge.MaxLength, m_edge.Width);
   }

   Graph m_graph;
   DirectedEdge m_edge;
}
