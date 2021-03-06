package engine.level;

import engine.graph.DirectedEdge;
import engine.graph.GeomLayout;
import engine.graph.Graph;
import engine.graph.INode;
import engine.graph.Node;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EdgeAdjusterStepperTest
{
   @SuppressWarnings("RedundantThrows")
   @Before
   public void setUp() throws Exception
   {
      m_config = new LevelGeneratorConfiguration(1);
   }

   @Test
   public void testDefaultGeomMaker()
   {
      EdgeAdjusterStepper eas = new EdgeAdjusterStepper(null, null, null, m_config);

      INode n = new Node("", "", "", 1);
      GeomLayout gl = eas.getGeomMaker().create(n);

      assertNotNull(gl);
   }

   @Test
   public void testStep_Fail() throws Exception
   {
      IoCContainer ioc_container = new IoCContainer(
            (x, a, b) -> new TestStepper(false, null),
            null,
            null,
            null,
            null);

      Graph g = new Graph();
      INode n1 = g.addNode("", "", "", 0);
      INode n2 = g.addNode("", "", "", 0);
      DirectedEdge de = g.connect(n1, n2, 0, 0, 0);

      EdgeAdjusterStepper eas = new EdgeAdjusterStepper(ioc_container, g, de, m_config);
      eas.setGeomMaker(null);

      StepperController e = new StepperController(g, eas);

      StepperController.StatusReport ret;

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(StepperController.Status.StepOutFailure, ret.Status);
   }

   @Test
   public void testStep_Succeed() throws Exception
   {
      IoCContainer ioc_container = new IoCContainer(
            (x, a, b) -> new TestStepper(true, null),
            null,
            null,
            null,
            null);

      Graph g = new Graph();
      INode n1 = g.addNode("", "", "", 0);
      INode n2 = g.addNode("", "", "", 0);
      DirectedEdge de = g.connect(n1, n2, 0, 0, 0);

      StepperController e = new StepperController(g, new EdgeAdjusterStepper(ioc_container, g, de, m_config));

      StepperController.StatusReport ret;

      do
      {
         ret = e.Step();
      }
      while(!ret.Complete);

      assertEquals(StepperController.Status.StepOutSuccess, ret.Status);
      assertEquals(3, g.numNodes());
      assertFalse(n1.connects(n2));
      assertEquals(0, n1.getInConnections().size());
      assertEquals(1, n1.getOutConnections().size());
      assertEquals(1, n2.getInConnections().size());
      assertEquals(0, n2.getOutConnections().size());

      DirectedEdge n1_out = n1.getOutConnections().stream().findFirst().get();
      DirectedEdge n2_in = n2.getInConnections().stream().findFirst().get();
      assertEquals(n1_out.End, n2_in.Start);
   }

   @Test
   public void testThrowsForIteration()
   {
      // steppers only get called with iterate if they return it, and this one doesn't
      boolean thrown = false;

      try
      {
         // none of these parameters used in this case
         EdgeAdjusterStepper etss = new EdgeAdjusterStepper(null, null, null, null);

         etss.step(StepperController.Status.Iterate);
      }
      catch(UnsupportedOperationException uoe)
      {
         thrown = true;
      }

      assertTrue(thrown);
   }

   private LevelGeneratorConfiguration m_config;
}
