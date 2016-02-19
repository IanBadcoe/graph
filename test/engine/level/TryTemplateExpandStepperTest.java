package engine.level;

import engine.XY;
import engine.graph.DirectedEdge;
import engine.graph.Graph;
import engine.graph.INode;
import engine.graph.Template;
import engine.graph.TemplateBuilder;
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
      INode n1 = g.addNode("", "", "", 0);

      StepperController e = new StepperController(g,
            new TryTemplateExpandStepper(null, g, n1, tb.Build(), new LevelGeneratorConfiguration(1)));

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
      IoCContainer ioc_container = new IoCContainer(
            (x, a, b) -> new TestStepper(false, () -> m_fail_count++),
            null,
            null,
            null,
            null);

      TemplateBuilder tb = new TemplateBuilder("", "");
      tb.AddNode(Template.NodeType.In, "in1");
      tb.AddNode(Template.NodeType.Internal, "internal",
            false, "<target>", null, null, "", 0);

      Graph g = new Graph();

      // one node with one input does matches the template
      INode n1 = g.addNode("", "", "", 0);
      INode n2 = g.addNode("", "", "", 0);
      g.connect(n1, n2, 0, 0, 0);

      StepperController e = new StepperController(g,
            new TryTemplateExpandStepper(ioc_container, g, n2, tb.Build(), new LevelGeneratorConfiguration(1)));

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
      IoCContainer ioc_container = new IoCContainer(
            (x, a, b) -> new TestStepper(true, () -> m_success_count++),
            null,
            null,
            null,
            (x, a, b, c) -> new TestStepper(false, () -> m_fail_count++));

      TemplateBuilder tb = new TemplateBuilder("", "");
      tb.AddNode(Template.NodeType.In, "in1");
      tb.AddNode(Template.NodeType.Internal, "internal",
            false, "<target>", null, null, "", 0);
      tb.Connect("in1", "internal", 5, 5, 0);

      Graph g = new Graph();

      // one node with one input does matches the template
      INode n1 = g.addNode("", "", "", 0);
      INode n2 = g.addNode("", "", "", 0);
      g.connect(n1, n2, 0, 0, 0);
      // space nodes far enough apart that the edge the template adds will
      // be "stressed"
      n2.setPos(new XY(10, 0));

      StepperController e = new StepperController(g,
            new TryTemplateExpandStepper(ioc_container, g, n2, tb.Build(), new LevelGeneratorConfiguration(1)));

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
      IoCContainer ioc_container = new IoCContainer(
            (x, a, b) -> new TestStepper(true, () -> m_success_count++),
            null,
            null,
            null,
            (x, a, b, c) -> new SimpleEdgeAdjuster(b));

      TemplateBuilder tb = new TemplateBuilder("", "");
      tb.AddNode(Template.NodeType.In, "in1");
      tb.AddNode(Template.NodeType.Internal, "internal",
            false, "<target>", null, null, "", 0);
      tb.Connect("in1", "internal", 5, 5, 0);

      Graph g = new Graph();

      // one node with one input does matches the template
      INode n1 = g.addNode("n1", "", "", 0);
      INode n2 = g.addNode("n2", "", "", 0);
      g.connect(n1, n2, 0, 0, 0);
      // space nodes far enough apart that the edge the template adds will
      // be "stressed"
      n2.setPos(new XY(10, 0));

      StepperController e = new StepperController(g,
            new TryTemplateExpandStepper(ioc_container, g, n2, tb.Build(), new LevelGeneratorConfiguration(1)));

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
      assertEquals(2, g.numNodes());
      assertEquals(1, g.numEdges());
      assertTrue(g.allGraphNodes().stream().anyMatch(x -> x.getName().equals("internal")));
      assertTrue(g.allGraphNodes().stream().anyMatch(x -> x.getName().equals("n1")));
   }

   @Test
   public void testEdgeTwoAdjustsSucceed()
   {
      IoCContainer ioc_container = new IoCContainer(
            (x, a, b) -> new TestStepper(true, () -> m_success_count++),
            null,
            null,
            null,
            (x, a, b, c) -> new SimpleEdgeAdjuster(b));

      TemplateBuilder tb = new TemplateBuilder("", "");
      tb.AddNode(Template.NodeType.In, "in1");
      tb.AddNode(Template.NodeType.In, "in2");
      tb.AddNode(Template.NodeType.Internal, "internal",
            false, "<target>", null, null, "", 0);
      tb.Connect("in1", "internal", 5, 5, 0);
      tb.Connect("in2", "internal", 5, 5, 0);

      Graph g = new Graph();

      // one node with one input does matches the template
      INode n1 = g.addNode("n1", "", "", 0);
      INode n11 = g.addNode("n1a", "", "", 0);
      INode n2 = g.addNode("n2", "", "", 0);
      g.connect(n1, n2, 0, 0, 0);
      g.connect(n11, n2, 0, 0, 0);
      // space nodes far enough apart both the edges the template adds will
      // be "stressed"
      n2.setPos(new XY(10, 0));

      // move n1 as n2 will have been replaced by the template

      StepperController e = new StepperController(g,
            new TryTemplateExpandStepper(ioc_container, g, n2, tb.Build(), new LevelGeneratorConfiguration(1)));

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
      assertEquals(3, g.numNodes());
      assertEquals(2, g.numEdges());
      assertTrue(g.allGraphNodes().stream().anyMatch(x -> x.getName().equals("internal")));
      assertTrue(g.allGraphNodes().stream().anyMatch(x -> x.getName().equals("n1")));
      assertTrue(g.allGraphNodes().stream().anyMatch(x -> x.getName().equals("n1a")));
   }

   @Test
   public void testNoEdgeAdjustRequired()
   {
      IoCContainer ioc_container = new IoCContainer(
            (x, a, b) -> new TestStepper(true, () -> m_success_count++),
            null,
            null,
            null,
            // won't be called
            (x, a, b, c) -> new TestStepper(false, () -> assertTrue(false)));

      TemplateBuilder tb = new TemplateBuilder("", "");
      tb.AddNode(Template.NodeType.In, "in1");
      tb.AddNode(Template.NodeType.Internal, "internal",
            false, "<target>", null, null, "", 0);
      tb.Connect("in1", "internal", 5, 5, 0);

      Graph g = new Graph();

      // one node with one input does matches the template
      INode n1 = g.addNode("n1", "", "", 0);
      INode n2 = g.addNode("n2", "", "", 0);
      g.connect(n1, n2, 0, 0, 0);
      // space nodes far as the new edge requires
      n2.setPos(new XY(5, 0));

      StepperController e = new StepperController(g,
            new TryTemplateExpandStepper(ioc_container, g, n2, tb.Build(), new LevelGeneratorConfiguration(1)));

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
      assertEquals(2, g.numNodes());
      assertEquals(1, g.numEdges());
      assertTrue(g.allGraphNodes().stream().anyMatch(x -> x.getName().equals("internal")));
      assertTrue(g.allGraphNodes().stream().anyMatch(x -> x.getName().equals("n1")));
   }

   @Test
   public void testCrazyPrevStatus_1()
   {
      IoCContainer ioc_container = new IoCContainer(
            (x, a, b) -> new TestStepper(true, () -> m_success_count++),
            null,
            null,
            null,
            // won't be called
            (x, a, b, c) -> new SimpleEdgeAdjuster(b));

      TemplateBuilder tb = new TemplateBuilder("", "");
      tb.AddNode(Template.NodeType.In, "in1");
      tb.AddNode(Template.NodeType.Internal, "internal",
            false, "<target>", null, null, "", 0);
      tb.Connect("in1", "internal", 5, 5, 0);

      Graph g = new Graph();

      // one node with one input does matches the template
      INode n1 = g.addNode("n1", "", "", 0);
      INode n2 = g.addNode("n2", "", "", 0);
      g.connect(n1, n2, 0, 0, 0);
      // space nodes far enough apart that the edge the template adds will
      // be "stressed"
      n2.setPos(new XY(10, 0));

      // move n1 as n2 will have been replaced by the template

      IStepper tes = new TryTemplateExpandStepper(ioc_container, g, n2, tb.Build(), new LevelGeneratorConfiguration(1));
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
      IoCContainer ioc_container = new IoCContainer(
            (x, a, b) -> new TestStepper(true, () -> m_success_count++),
            null,
            null,
            null,
            // won't be called
            (x, a, b, c) -> new SimpleEdgeAdjuster(b));

      TemplateBuilder tb = new TemplateBuilder("", "");
      tb.AddNode(Template.NodeType.In, "in1");
      tb.AddNode(Template.NodeType.Internal, "internal",
            false, "<target>", null, null, "", 0);
      tb.Connect("in1", "internal", 5, 5, 0);

      Graph g = new Graph();

      // one node with one input does matches the template
      INode n1 = g.addNode("n1", "", "", 0);
      INode n2 = g.addNode("n2", "", "", 0);
      g.connect(n1, n2, 0, 0, 0);
      // space nodes far enough apart that the edge the template adds will
      // be "stressed"
      n2.setPos(new XY(10, 0));

      // move n1 as n2 will have been replaced by the template

      IStepper tes = new TryTemplateExpandStepper(ioc_container, g, n2, tb.Build(), new LevelGeneratorConfiguration(1));
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
