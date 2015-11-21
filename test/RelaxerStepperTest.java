import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RelaxerStepperTest
{
   @Before
   public void setUp() throws Exception
   {
      m_config = new LevelGeneratorConfiguration(1);
      m_config.RelaxationMoveTarget = 1e-3;
      m_config.RelaxationForceTarget = 1e-4;
      m_config.RelaxationMinimumSeparation = 0;
   }

   @Test
   public void testEdgeRelaxation() throws Exception
   {
      Graph g = new Graph();
      INode n1 = g.AddNode("n1", "", "", 0);
      INode n2 = g.AddNode("n2", "", "", 0);
      INode n3 = g.AddNode("n3", "", "", 0);
      INode n4 = g.AddNode("n4", "", "", 0);
      INode n5 = g.AddNode("n5", "", "", 0);

      // place them non-overlapping and separated in both dimensions
      n1.setPos(new XY(0, 0));
      n2.setPos(new XY(-100, 0));
      n3.setPos(new XY(0, -100));
      n4.setPos(new XY(100, 0));
      n5.setPos(new XY(0, 100));

      // a possible triangle and two single-connected nodes
      g.Connect(n1, n2, 100, 100, 0);
      g.Connect(n2, n3, 80, 80, 0);
      g.Connect(n3, n1, 60, 60, 0);
      g.Connect(n3, n4, 120, 120, 0);
      g.Connect(n1, n5, 40, 40, 0);

      // run it to a tighter convergence than usual
      RelaxerStepper rs = new RelaxerStepper(g, m_config);

      StepperController.StatusReportInner ret;
      int count = 0;
      do
      {
         count++;
         // RelaxerStepper doesn't use previous status
         ret = rs.step(StepperController.Status.Iterate);
      }
      while(ret.Status == StepperController.Status.Iterate);

      // simple case should succeed
      assertEquals(StepperController.Status.StepOutSuccess, ret.Status);

      // with a possible triangle and no other constraints
      // should get all lengths within a small tolerance of the target

      // however how close we actually get is phenomonological (e.g. just what I see while writing this)
      // but we can return with higher expectations later if required

      // again just what I saw initially, these are broadly parabolic forces so maybe a better optimisation algorithm
      // would do this _much_ faster, OTOH these forces have edges and the complexity of things moving around each
      // other may mean this is really a much more complex force landscape than you'd think
      assertTrue(count < 30000);

      DirectedEdge e12 = n1.getConnectionTo(n2);
      assertEquals(100, e12.Length(), 1);

      DirectedEdge e23 = n2.getConnectionTo(n3);
      assertEquals(80, e23.Length(), 1);

      DirectedEdge e31 = n3.getConnectionTo(n1);
      assertEquals(60, e31.Length(), 1);

      DirectedEdge e34 = n3.getConnectionTo(n4);
      assertEquals(120, e34.Length(), 1);

      DirectedEdge e15 = n1.getConnectionTo(n5);
      assertEquals(40, e15.Length(), 1);
   }

   @Test
   public void testEdgeContradictionRelaxation() throws Exception
   {
      Graph g = new Graph();

      INode n1 = g.AddNode("n1", "", "", 0);
      INode n2 = g.AddNode("n2", "", "", 0);
      INode n3 = g.AddNode("n3", "", "", 0);

      // place them non-overlapping and separated in both dimensions
      n1.setPos(new XY(0, 0));
      n2.setPos(new XY(-100,    0));
      n3.setPos(new XY(0, -100));

      // an impossible triangle
      g.Connect(n1, n2, 100, 100, 0);
      g.Connect(n2, n3, 40, 40, 0);
      g.Connect(n3, n1, 40, 40, 0);

      // run it to a tighter convergence than usual
      RelaxerStepper rs = new RelaxerStepper(g, m_config);

      StepperController.StatusReportInner ret;
      int count = 0;
      do
      {
         count++;
         // RelaxerStepper doesn't use previous status
         ret = rs.step(StepperController.Status.Iterate);
      }
      while(ret.Status == StepperController.Status.Iterate);

      // simple case should succeed
      assertEquals(StepperController.Status.StepOutSuccess, ret.Status);

      // should arrive at a compromise, close to linear with
      // n1 -> n2 a bit compressed and the other two edges stretched

      // see comments in testEdgeRelaxation about how these accuracies and count aren't at all definitive

      assertTrue(count < 40000);

      DirectedEdge e12 = n1.getConnectionTo(n2);
      assertEquals(90, e12.Length(), 2);

      DirectedEdge e23 = n2.getConnectionTo(n3);
      assertEquals(45, e23.Length(), 1);

      DirectedEdge e31 = n3.getConnectionTo(n1);
      assertEquals(45, e31.Length(), 1);
   }

   @Test
   public void testNodeWideSeparationRelaxation() throws Exception
   {
      Graph g = new Graph();
      INode n1 = g.AddNode("n1", "", "", 10.0);
      INode n2 = g.AddNode("n2", "", "", 10.0);

      n1.setPos(new XY(   0,    0));
      n2.setPos(new XY(-100, 0));

      // run it to a tighter convergence than usual
      RelaxerStepper rs = new RelaxerStepper(g, m_config);

      StepperController.StatusReportInner ret;
      int count = 0;
      do
      {
         count++;
         // RelaxerStepper doesn't use previous status
         ret = rs.step(StepperController.Status.Iterate);
      }
      while(ret.Status == StepperController.Status.Iterate);

      // simple case should succeed
      assertEquals(StepperController.Status.StepOutSuccess, ret.Status);

      // should take a single cycle to see that nothing needs to move
      assertEquals(1, count);
      assertEquals(0, n1.getPos().X, 0);
      assertEquals(0, n1.getPos().Y, 0);
      assertEquals(-100, n2.getPos().X, 0);
      assertEquals(0, n2.getPos().Y, 0);
   }

   @Test
   public void testNodeTooCloseRelaxation() throws Exception
   {
      Graph g = new Graph();
      INode n1 = g.AddNode("n1", "", "", 10.0);
      INode n2 = g.AddNode("n2", "", "", 10.0);

      n1.setPos(new XY(   0,    0));
      n2.setPos(new XY(  -1,    0));

      // run it to a tighter convergence than usual
      RelaxerStepper rs = new RelaxerStepper(g, m_config);

      StepperController.StatusReportInner ret;
      int count = 0;
      do
      {
         count++;
         // RelaxerStepper doesn't use previous status
         ret = rs.step(StepperController.Status.Iterate);
      }
      while(ret.Status == StepperController.Status.Iterate);

      // simple case should succeed
      assertEquals(StepperController.Status.StepOutSuccess, ret.Status);

      // shouldn't take many cycle to bring them close to the target separation
      assertTrue(count < 100);

      double dist = n2.getPos().minus(n1.getPos()).length();

      assertEquals(20.0, dist, 0.1);
   }

   @Test
   public void testEdgeWideSeparationRelaxation() throws Exception
   {
      Graph g = new Graph();

      INode n1 = g.AddNode("edge1start", "", "", 10.0);
      INode n2 = g.AddNode("edge1end", "", "", 10.0);
      INode n3 = g.AddNode("edge2start", "", "", 10.0);
      INode n4 = g.AddNode("edge2end", "", "", 10.0);

      n1.setPos(new XY(0, 0));
      n2.setPos(new XY(0, 20));
      n3.setPos(new XY(100, 0));
      n4.setPos(new XY(100, 20));

      g.Connect(n1, n2, 20, 20, 10);
      g.Connect(n3, n4, 20, 20, 10);

      // run it to a tighter convergence than usual
      RelaxerStepper rs = new RelaxerStepper(g, m_config);

      StepperController.StatusReportInner ret;
      int count = 0;
      do
      {
         count++;
         // RelaxerStepper doesn't use previous status
         ret = rs.step(StepperController.Status.Iterate);
      }
      while(ret.Status == StepperController.Status.Iterate);

      // simple case should succeed
      assertEquals(StepperController.Status.StepOutSuccess, ret.Status);

      // should take a single cycle to see that nothing needs to move
      assertEquals(1, count);
      assertEquals(0, n1.getPos().X, 0);
      assertEquals(0, n1.getPos().Y, 0);
      assertEquals(0, n2.getPos().X, 0);
      assertEquals(20, n2.getPos().Y, 0);
      assertEquals(100, n3.getPos().X, 0);
      assertEquals(0, n3.getPos().Y, 0);
      assertEquals(100, n4.getPos().X, 0);
      assertEquals(20, n4.getPos().Y, 0);
   }

   @Test
   public void testEdgeTooCloseRelaxation() throws Exception
   {
      Graph g = new Graph();

      INode n1 = g.AddNode("edge1start", "", "", 10.0);
      INode n2 = g.AddNode("edge1end", "", "", 10.0);
      INode n3 = g.AddNode("edge2start", "", "", 10.0);
      INode n4 = g.AddNode("edge2end", "", "", 10.0);

      n1.setPos(new XY(0, 0));
      n2.setPos(new XY(0, 20));
      n3.setPos(new XY(1, 0));
      n4.setPos(new XY(1, 20));

      g.Connect(n1, n2, 20, 20, 10);
      g.Connect(n3, n4, 20, 20, 10);

      // run it to a tighter convergence than usual
      RelaxerStepper rs = new RelaxerStepper(g, m_config);

      StepperController.StatusReportInner ret;
      int count = 0;
      do
      {
         count++;
         // RelaxerStepper doesn't use previous status
         ret = rs.step(StepperController.Status.Iterate);
      }
      while(ret.Status == StepperController.Status.Iterate);

      // simple case should succeed
      assertEquals(StepperController.Status.StepOutSuccess, ret.Status);

      // shouldn't take long to push the edges apart
      assertTrue(count < 50);
      // should have just slid sideways along X
      assertEquals(20, n3.getPos().X - n1.getPos().X, 0.1);
      assertEquals(20, n4.getPos().X - n2.getPos().X, 0.1);

      assertEquals(0, n1.getPos().Y, 0);
      assertEquals(20, n2.getPos().Y, 0);
      assertEquals(0, n3.getPos().Y, 0);
      assertEquals(20, n4.getPos().Y, 0);
   }

   @Test
   public void testEdgeNodeTooCloseRelaxation() throws Exception
   {
      Graph g = new Graph();

      INode n1 = g.AddNode("edge1start", "", "", 10.0);
      INode n2 = g.AddNode("edge1end", "", "", 10.0);
      INode n3 = g.AddNode("node", "", "", 10.0);

      // edge long enough that there is no n1->n3 or n2->n3 interaction
      n1.setPos(new XY(0, 0));
      n2.setPos(new XY(0, 100));
      n3.setPos(new XY(1, 50));

      g.Connect(n1, n2, 100, 100, 10);

      // run it to a tighter convergence than usual
      RelaxerStepper rs = new RelaxerStepper(g, m_config);

      StepperController.StatusReportInner ret;
      int count = 0;
      do
      {
         count++;
         // RelaxerStepper doesn't use previous status
         ret = rs.step(StepperController.Status.Iterate);
      }
      while(ret.Status == StepperController.Status.Iterate);

      // simple case should succeed
      assertEquals(StepperController.Status.StepOutSuccess, ret.Status);

      // takes a little time to push the edge and node apart
      assertTrue(count < 120);
      // should have just slid sideways along X
      assertEquals(20, n3.getPos().X - n1.getPos().X, 0.1);

      assertEquals(0, n1.getPos().Y, 0);
      assertEquals(100, n2.getPos().Y, 0);
      assertEquals(50, n3.getPos().Y, 0);
   }

   @Test
   public void testCrossingEdge_Error()
   {
      Graph g = new Graph();

      INode n1 = g.AddNode("edge1start", "", "", 10.0);
      INode n2 = g.AddNode("edge1end", "", "", 10.0);
      INode n3 = g.AddNode("edge2start", "", "", 10.0);
      INode n4 = g.AddNode("edge2end", "", "", 10.0);

      // two clearly crossing edges
      n1.setPos(new XY(0, -100));
      n2.setPos(new XY(0, 100));
      n3.setPos(new XY(-100, 0));
      n4.setPos(new XY(100, 0));

      g.Connect(n1, n2, 100, 100, 10);
      g.Connect(n3, n4, 100, 100, 10);

      // run it to a tighter convergence than usual
      RelaxerStepper rs = new RelaxerStepper(g, m_config);

      StepperController.StatusReportInner ret;
      int count = 0;
      do
      {
         count++;
         // RelaxerStepper doesn't use previous status
         ret = rs.step(StepperController.Status.Iterate);
      }
      while(ret.Status == StepperController.Status.Iterate);

      assertEquals(StepperController.Status.StepOutFailure, ret.Status);

      // should detect immediately
      assertTrue(count == 1);
      assertTrue(ret.Log.contains("crossing edges"));
   }

   @Test
   public void testDegeneracy()
   {
      // edge lengths of zero and edge-node distances of zero shouldn't crash anything and should
      // even relax as long as there is some other force to pull them apart

      // zero length edge
      {
         Graph g = new Graph();

         INode n1 = g.AddNode("edgesstart", "", "", 10.0);
         INode n2 = g.AddNode("edgesmiddle", "", "", 10.0);
         INode n3 = g.AddNode("edgesend", "", "", 10.0);

         // zero length edge and a non-zero one attached at one end that will separate
         // the overlying nodes
         n1.setPos(new XY(0, 0));
         n2.setPos(new XY(0, 0));
         n3.setPos(new XY(-110, 0));

         g.Connect(n1, n2, 100, 100, 10);
         g.Connect(n2, n3, 100, 100, 10);

         // run it to a tighter convergence than usual
         RelaxerStepper rs = new RelaxerStepper(g, m_config);

         StepperController.StatusReportInner ret;
         int count = 0;
         do
         {
            count++;
            // RelaxerStepper doesn't use previous status
            ret = rs.step(StepperController.Status.Iterate);
         }
         while(ret.Status == StepperController.Status.Iterate);

         assertEquals(StepperController.Status.StepOutSuccess, ret.Status);

         // rather a lot?
         assertTrue(count < 50000);
         assertEquals(100, n1.getPos().minus(n2.getPos()).length(), 1);
         assertEquals(100, n2.getPos().minus(n3.getPos()).length(), 1);
         assertTrue(n1.getPos().minus(n3.getPos()).length() > 20);
      }

      // zero node separation
      {
         Graph g = new Graph();

         INode n1 = g.AddNode("edgestart", "", "", 10.0);
         INode n2 = g.AddNode("edgeend", "", "", 10.0);
         INode n3 = g.AddNode("node", "", "", 10.0);

         // two zero separation nodes and an edge attached to one that will separate
         // the overlying nodes
         n1.setPos(new XY(0, 0));
         n2.setPos(new XY(110, 0));
         n3.setPos(new XY(0, 0));

         g.Connect(n1, n2, 100, 100, 10);

         // run it to a tighter convergence than usual
         RelaxerStepper rs = new RelaxerStepper(g, m_config);

         StepperController.StatusReportInner ret;
         int count = 0;
         do
         {
            count++;
            // RelaxerStepper doesn't use previous status
            ret = rs.step(StepperController.Status.Iterate);
         }
         while(ret.Status == StepperController.Status.Iterate);

         assertEquals(StepperController.Status.StepOutSuccess, ret.Status);

         // rather a lot?
         assertTrue(count < 10000);
         assertEquals(100, n1.getPos().minus(n2.getPos()).length(), 1);
         assertTrue(n1.getPos().minus(n3.getPos()).length() > 20);
         assertTrue(n2.getPos().minus(n3.getPos()).length() > 20);
      }
   }

   @Test
   public void testAdjoiningEdgeOverridesRadii()
   {
      Graph g = new Graph();
      INode n1 = g.AddNode("n1", "", "", 100);
      INode n2 = g.AddNode("n2", "", "", 100);

      // place them non-overlapping and separated in both dimensions
      n1.setPos(new XY(0, 0));
      n2.setPos(new XY(-100, 0));

      // edge wants distance of 100, node-radii want 200 but node-radii
      // should be ignored between connected nodes
      g.Connect(n1, n2, 100, 100, 0);

      // run it to a tighter convergence than usual
      RelaxerStepper rs = new RelaxerStepper(g, m_config);

      StepperController.StatusReportInner ret;
      int count = 0;
      do
      {
         count++;
         // RelaxerStepper doesn't use previous status
         ret = rs.step(StepperController.Status.Iterate);
      }
      while(ret.Status == StepperController.Status.Iterate);

      // simple case should succeed
      assertEquals(StepperController.Status.StepOutSuccess, ret.Status);

      // should be already relaxed
      assertEquals(1, count);

      DirectedEdge e12 = n1.getConnectionTo(n2);
      assertEquals(100, e12.Length(), 1);
   }

   @Test
   public void testNonAdjoiningEdgesOverrideRadii()
   {
      Graph g = new Graph();
      INode n1 = g.AddNode("n1", "", "", 6);
      INode n2 = g.AddNode("n2", "", "", 0);
      INode n3 = g.AddNode("n3", "", "", 0);
      INode n4 = g.AddNode("n4", "", "", 0);
      INode n5 = g.AddNode("n5", "", "", 0);

      // place them non-overlapping and separated in both dimensions
      n1.setPos(new XY(0, 0));
      n2.setPos(new XY(10, 0));
      n3.setPos(new XY(10, 10));
      n4.setPos(new XY(20, 10));
      n5.setPos(new XY(20, 20));

      // edges wants distances of 2, n1 radius wants 6 but shortest path through
      // graph should come out below that (for n2, n3) and let them get closer
      g.Connect(n1, n2, 2, 2, 0);
      g.Connect(n2, n3, 2, 2, 0);
      g.Connect(n3, n4, 2, 2, 0);
      g.Connect(n4, n5, 2, 2, 0);

      // run it to a tighter convergence than usual
      RelaxerStepper rs = new RelaxerStepper(g, m_config);

      StepperController.StatusReportInner ret;
      int count = 0;
      do
      {
         count++;
         // RelaxerStepper doesn't use previous status
         ret = rs.step(StepperController.Status.Iterate);
      }
      while(ret.Status == StepperController.Status.Iterate);

      // should be already relaxed
      assertTrue(count < 3000);

      // simple case should succeed
      assertEquals(StepperController.Status.StepOutSuccess, ret.Status);

      // all edges should be able to reach ~2 even if that violates the radius of n1
      DirectedEdge e12 = n1.getConnectionTo(n2);
      assertEquals(2, e12.Length(), .1);
      DirectedEdge e23 = n2.getConnectionTo(n3);
      assertEquals(2, e23.Length(), .1);
      DirectedEdge e34 = n3.getConnectionTo(n4);
      assertEquals(2, e34.Length(), .1);
      DirectedEdge e45 = n4.getConnectionTo(n5);
      assertEquals(2, e45.Length(), .1);

      // n4 and n5 hve enough edge length to get far enough from n1 and should do so
      assertTrue(n1.getPos().minus(n4.getPos()).length() >= 6);
      assertTrue(n1.getPos().minus(n5.getPos()).length() >= 6);
   }

   @Test
   public void testMinimumSeparation()
   {
      {
         Graph g = new Graph();
         INode n1 = g.AddNode("n1", "", "", 10.0);
         INode n2 = g.AddNode("n2", "", "", 10.0);

         n1.setPos(new XY(   0,    0));
         n2.setPos(new XY(  -1,    0));

         // run it to a tighter convergence than usual
         // add 1 unit of extra separation
         m_config.RelaxationMinimumSeparation = 1;
         RelaxerStepper rs = new RelaxerStepper(g, m_config);

         StepperController.StatusReportInner ret;
         int count = 0;
         do
         {
            count++;
            // RelaxerStepper doesn't use previous status
            ret = rs.step(StepperController.Status.Iterate);
         }
         while(ret.Status == StepperController.Status.Iterate);

         // simple case should succeed
         assertEquals(StepperController.Status.StepOutSuccess, ret.Status);

         // shouldn't take many cycle to bring them close to the target separation
         assertTrue(count < 100);

         double dist = n2.getPos().minus(n1.getPos()).length();

         // should get rad+rad+separation
         assertEquals(21.0, dist, 0.1);
      }

      {
         Graph g = new Graph();

         INode n1 = g.AddNode("edge1start", "", "", 10.0);
         INode n2 = g.AddNode("edge1end", "", "", 10.0);
         INode n3 = g.AddNode("node", "", "", 10.0);

         // edge long enough that there is no n1->n3 or n2->n3 interaction
         n1.setPos(new XY(0, 0));
         n2.setPos(new XY(0, 100));
         n3.setPos(new XY(1, 50));

         g.Connect(n1, n2, 100, 100, 10);

         // run it to a tighter convergence than usual
         // add an extra separation of 1 unit
         m_config.RelaxationMinimumSeparation = 1;
         RelaxerStepper rs = new RelaxerStepper(g, m_config);

         StepperController.StatusReportInner ret;
         int count = 0;
         do
         {
            count++;
            // RelaxerStepper doesn't use previous status
            ret = rs.step(StepperController.Status.Iterate);
         }
         while(ret.Status == StepperController.Status.Iterate);

         // simple case should succeed
         assertEquals(StepperController.Status.StepOutSuccess, ret.Status);

         // takes a little time to push the edge and node apart
         assertTrue(count < 130);
         // should have just slid sideways along X
         // to a total dist of node radius + edge half-width +
         // separation
         assertEquals(21, n3.getPos().X - n1.getPos().X, 0.1);

         assertEquals(0, n1.getPos().Y, 0);
         assertEquals(100, n2.getPos().Y, 0);
         assertEquals(50, n3.getPos().Y, 0);
      }
   }

   LevelGeneratorConfiguration m_config;
}