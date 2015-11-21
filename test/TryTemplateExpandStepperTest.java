import org.junit.Test;

import static org.junit.Assert.*;

public class TryTemplateExpandStepperTest
{
   @Test
   public void testTemplateExpandFail() throws Exception
   {
      TemplateBuilder tb = new TemplateBuilder("", "");
      tb.AddNode(Template.NodeType.In, "in1");

      Graph g = new Graph();

      // one node with no input doesn't match the template
      INode n1 = g.AddNode("", "", "", 0);

      StepperController e = new StepperController(g,
            new TryTemplateExpandStepper(g, n1, tb.Build(), new LevelGeneratorConfiguration(1)));

      StepperController.StatusReport ret;

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(StepperController.Status.StepOutFailure, ret.Status);
   }

   private int m_fail_count = 0;

   @Test
   public void testExpandedTemplateRelaxFail() throws Exception
   {
      TryTemplateExpandStepper.SetRelaxerFactory(
            (a, b) -> new TestStepper(false, () -> m_fail_count++));

      TemplateBuilder tb = new TemplateBuilder("", "");
      tb.AddNode(Template.NodeType.In, "in1");
      tb.AddNode(Template.NodeType.Internal, "internal",
            false, "<target>", null, null, "", 0);

      Graph g = new Graph();

      // one node with one input does matches the template
      INode n1 = g.AddNode("", "", "", 0);
      INode n2 = g.AddNode("", "", "", 0);
      g.Connect(n1, n2, 0, 0, 0);

      StepperController e = new StepperController(g,
            new TryTemplateExpandStepper(g, n2, tb.Build(), new LevelGeneratorConfiguration(1)));

      StepperController.StatusReport ret;

      m_fail_count = 0;

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(StepperController.Status.StepOutFailure, ret.Status);
      assertEquals(1, m_fail_count);
   }

   private int m_success_count;

   @Test
   public void testEdgeAdjustFail()
   {
      TryTemplateExpandStepper.SetRelaxerFactory(
            (a, b) -> new TestStepper(true, () -> m_success_count++));
      TryTemplateExpandStepper.SetAdjusterFactory(
            (a, b, c) -> new TestStepper(false, () -> m_fail_count++));

      TemplateBuilder tb = new TemplateBuilder("", "");
      tb.AddNode(Template.NodeType.In, "in1");
      tb.AddNode(Template.NodeType.Internal, "internal",
            false, "<target>", null, null, "", 0);
      tb.Connect("in1", "internal", 5, 5, 0);

      Graph g = new Graph();

      // one node with one input does matches the template
      INode n1 = g.AddNode("", "", "", 0);
      INode n2 = g.AddNode("", "", "", 0);
      g.Connect(n1, n2, 0, 0, 0);
      // space nodes far enough apart that the edge the template adds will
      // be "stressed"
      n2.setPos(new XY(10, 0));

      StepperController e = new StepperController(g,
            new TryTemplateExpandStepper(g, n2, tb.Build(), new LevelGeneratorConfiguration(1)));

      StepperController.StatusReport ret;

      m_fail_count = 0;
      m_success_count = 0;

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(StepperController.Status.StepOutFailure, ret.Status);
      assertEquals(1, m_success_count);
      assertEquals(1, m_fail_count);
   }

   class SimpleEdgeAdjuster implements IStepper
   {
      SimpleEdgeAdjuster( DirectedEdge e)
      {
         m_e = e;
      }

      @Override
      public StepperController.StatusReportInner step(StepperController.Status status)
      {
         m_e.Start.setPos(new XY(5, 0));

         m_success_count++;

         return new StepperController.StatusReportInner(StepperController.Status.StepOutSuccess,
               null, "");
      }

      final DirectedEdge m_e;
   }

   boolean m_first = true;

   @Test
   public void testEdgeAdjustSucceed()
   {
      TryTemplateExpandStepper.SetRelaxerFactory(
            (a, b) -> new TestStepper(true, () -> m_success_count++));

      TemplateBuilder tb = new TemplateBuilder("", "");
      tb.AddNode(Template.NodeType.In, "in1");
      tb.AddNode(Template.NodeType.Internal, "internal",
            false, "<target>", null, null, "", 0);
      tb.Connect("in1", "internal", 5, 5, 0);

      Graph g = new Graph();

      // one node with one input does matches the template
      INode n1 = g.AddNode("n1", "", "", 0);
      INode n2 = g.AddNode("n2", "", "", 0);
      g.Connect(n1, n2, 0, 0, 0);
      // space nodes far enough apart that the edge the template adds will
      // be "stressed"
      n2.setPos(new XY(10, 0));

      // move n1 as n2 will have been replaced by the template
      TryTemplateExpandStepper.SetAdjusterFactory(
            (a, b, c) -> new SimpleEdgeAdjuster(b));

      StepperController e = new StepperController(g,
            new TryTemplateExpandStepper(g, n2, tb.Build(), new LevelGeneratorConfiguration(1)));

      StepperController.StatusReport ret;

      m_fail_count = 0;
      m_success_count = 0;

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(StepperController.Status.StepOutSuccess, ret.Status);
      assertEquals(2, m_success_count);
      // we should have replaced n2
      assertEquals(2, g.NumNodes());
      assertEquals(1, g.NumEdges());
      assertTrue(g.AllGraphNodes().stream().anyMatch(x -> x.getName().equals("internal")));
      assertTrue(g.AllGraphNodes().stream().anyMatch(x -> x.getName().equals("n1")));
   }

   @Test
   public void testEdgeTwoAdjustsSucceed()
   {
      TryTemplateExpandStepper.SetRelaxerFactory(
            (a, b) -> new TestStepper(true, () -> m_success_count++));

      TemplateBuilder tb = new TemplateBuilder("", "");
      tb.AddNode(Template.NodeType.In, "in1");
      tb.AddNode(Template.NodeType.In, "in2");
      tb.AddNode(Template.NodeType.Internal, "internal",
            false, "<target>", null, null, "", 0);
      tb.Connect("in1", "internal", 5, 5, 0);
      tb.Connect("in2", "internal", 5, 5, 0);

      Graph g = new Graph();

      // one node with one input does matches the template
      INode n1 = g.AddNode("n1", "", "", 0);
      INode n11 = g.AddNode("n1a", "", "", 0);
      INode n2 = g.AddNode("n2", "", "", 0);
      g.Connect(n1, n2, 0, 0, 0);
      g.Connect(n11, n2, 0, 0, 0);
      // space nodes far enough apart both the edges the template adds will
      // be "stressed"
      n2.setPos(new XY(10, 0));

      // move n1 as n2 will have been replaced by the template
      TryTemplateExpandStepper.SetAdjusterFactory(
            (a, b, c) -> new SimpleEdgeAdjuster(b));

      StepperController e = new StepperController(g,
            new TryTemplateExpandStepper(g, n2, tb.Build(), new LevelGeneratorConfiguration(1)));

      StepperController.StatusReport ret;

      m_fail_count = 0;
      m_success_count = 0;

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(StepperController.Status.StepOutSuccess, ret.Status);
      assertEquals(3, m_success_count);
      // we should have replaced n2
      assertEquals(3, g.NumNodes());
      assertEquals(2, g.NumEdges());
      assertTrue(g.AllGraphNodes().stream().anyMatch(x -> x.getName().equals("internal")));
      assertTrue(g.AllGraphNodes().stream().anyMatch(x -> x.getName().equals("n1")));
      assertTrue(g.AllGraphNodes().stream().anyMatch(x -> x.getName().equals("n1a")));
   }

   @Test
   public void testNoEdgeAdjustRequired()
   {
      TryTemplateExpandStepper.SetRelaxerFactory(
            (a, b) -> new TestStepper(true, () -> m_success_count++));
      // won't be called
      TryTemplateExpandStepper.SetAdjusterFactory(
            (a, b, c) -> new TestStepper(false, () -> assertTrue(false)));

      TemplateBuilder tb = new TemplateBuilder("", "");
      tb.AddNode(Template.NodeType.In, "in1");
      tb.AddNode(Template.NodeType.Internal, "internal",
            false, "<target>", null, null, "", 0);
      tb.Connect("in1", "internal", 5, 5, 0);

      Graph g = new Graph();

      // one node with one input does matches the template
      INode n1 = g.AddNode("n1", "", "", 0);
      INode n2 = g.AddNode("n2", "", "", 0);
      g.Connect(n1, n2, 0, 0, 0);
      // space nodes far as the new edge requires
      n2.setPos(new XY(5, 0));

      StepperController e = new StepperController(g,
            new TryTemplateExpandStepper(g, n2, tb.Build(), new LevelGeneratorConfiguration(1)));

      StepperController.StatusReport ret;

      m_fail_count = 0;
      m_success_count = 0;

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(StepperController.Status.StepOutSuccess, ret.Status);
      assertEquals(1, m_success_count);
      // we should have replaced n2
      assertEquals(2, g.NumNodes());
      assertEquals(1, g.NumEdges());
      assertTrue(g.AllGraphNodes().stream().anyMatch(x -> x.getName().equals("internal")));
      assertTrue(g.AllGraphNodes().stream().anyMatch(x -> x.getName().equals("n1")));
   }

   @Test
   public void testCrazyPrevStatus_1()
   {
      TryTemplateExpandStepper.SetRelaxerFactory(
            (a, b) -> new TestStepper(true, () -> m_success_count++));

      TemplateBuilder tb = new TemplateBuilder("", "");
      tb.AddNode(Template.NodeType.In, "in1");
      tb.AddNode(Template.NodeType.Internal, "internal",
            false, "<target>", null, null, "", 0);
      tb.Connect("in1", "internal", 5, 5, 0);

      Graph g = new Graph();

      // one node with one input does matches the template
      INode n1 = g.AddNode("n1", "", "", 0);
      INode n2 = g.AddNode("n2", "", "", 0);
      g.Connect(n1, n2, 0, 0, 0);
      // space nodes far enough apart that the edge the template adds will
      // be "stressed"
      n2.setPos(new XY(10, 0));

      // move n1 as n2 will have been replaced by the template
      TryTemplateExpandStepper.SetAdjusterFactory(
            (a, b, c) -> new SimpleEdgeAdjuster(b));

      IStepper tes = new TryTemplateExpandStepper(g, n2, tb.Build(), new LevelGeneratorConfiguration(1));
      StepperController e = new StepperController(g, tes);

      for(int i = 0; i < 4; i++)
      {
         e.Step();
      }

      boolean thrown = false;

      try
      {
         // should be looking for return value from edge-relaxer now
         // give it the one value it can never expect...
         tes.step(StepperController.Status.Iterate);
      }
      catch (UnsupportedOperationException uoe)
      {
         thrown = true;
      }

      assertTrue(thrown);
   }

   @Test
   public void testCrazyPrevStatus_2()
   {
      TryTemplateExpandStepper.SetRelaxerFactory(
            (a, b) -> new TestStepper(true, () -> m_success_count++));

      TemplateBuilder tb = new TemplateBuilder("", "");
      tb.AddNode(Template.NodeType.In, "in1");
      tb.AddNode(Template.NodeType.Internal, "internal",
            false, "<target>", null, null, "", 0);
      tb.Connect("in1", "internal", 5, 5, 0);

      Graph g = new Graph();

      // one node with one input does matches the template
      INode n1 = g.AddNode("n1", "", "", 0);
      INode n2 = g.AddNode("n2", "", "", 0);
      g.Connect(n1, n2, 0, 0, 0);
      // space nodes far enough apart that the edge the template adds will
      // be "stressed"
      n2.setPos(new XY(10, 0));

      // move n1 as n2 will have been replaced by the template
      TryTemplateExpandStepper.SetAdjusterFactory(
            (a, b, c) -> new SimpleEdgeAdjuster(b));

      IStepper tes = new TryTemplateExpandStepper(g, n2, tb.Build(), new LevelGeneratorConfiguration(1));
      StepperController e = new StepperController(g, tes);

      for(int i = 0; i < 2; i++)
      {
         e.Step();
      }

      boolean thrown = false;

      try
      {
         // should be looking for return value from edge-adjuster now
         // give it the one value it can never expect...
         tes.step(StepperController.Status.Iterate);
      }
      catch (UnsupportedOperationException uoe)
      {
         thrown = true;
      }

      assertTrue(thrown);
   }
}