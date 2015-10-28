import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by badcoei on 28/10/2015.
 */
public class ExpanderTempTest
{
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
      n1.SetPos(new XY(0, 0));
      n2.SetPos(new XY(-100, 0));
      n3.SetPos(new XY(0, -100));
      n4.SetPos(new XY(100, 0));
      n5.SetPos(new XY(0, 100));

      // a possible triangle and two single-connected nodes
      g.Connect(n1, n2, 100, 100, 0);
      g.Connect(n2, n3, 80, 80, 0);
      g.Connect(n3, n1, 60, 60, 0);
      g.Connect(n3, n4, 1000, 1000, 0);
      g.Connect(n1, n5, 10, 10, 0);

      ExpanderTemp exp = new ExpanderTemp(g, 0, null);

      ExpanderTemp.RelRet ret;
      int count = 0;
      do
      {
         count++;
         // run it to a tighter convergence than usual
         ret = exp.RelaxStep(null, 1.0, null, 1e-3, 1e-4);
      }
      while(!ret.Completed);

      // with a possible triangle and no other constraints
      // should get all lengths within a small tolerance of the target

      // however how close we actually get is phenomonological (e.g. just what I see while writing this)
      // but we can return with higher expectations later if required

      // again just what I saw initially, these are broadly parabolic forces so maybe a better optimisation algorithm
      // would do this _much_ faster, OTOH these forces have edges and the complexity of things moving around each
      // other may mean this is really a much more complex force landscape than you'd think
      assertTrue(count < 400000);

      DirectedEdge e12 = n1.GetConnectionTo(n2);
      assertEquals(100, e12.Length(), 1);

      DirectedEdge e23 = n2.GetConnectionTo(n3);
      assertEquals(80, e23.Length(), 1);

      DirectedEdge e31 = n3.GetConnectionTo(n1);
      assertEquals(60, e31.Length(), 1);

      DirectedEdge e34 = n3.GetConnectionTo(n4);
      assertEquals(1000, e34.Length(), 20);

      DirectedEdge e15 = n1.GetConnectionTo(n5);
      assertEquals(10, e15.Length(), 0.1);
   }

   @Test
   public void testEdgeContradictionRelaxation() throws Exception
   {
      Graph g = new Graph();

      INode n1 = g.AddNode("n1", "", "", 0);
      INode n2 = g.AddNode("n2", "", "", 0);
      INode n3 = g.AddNode("n3", "", "", 0);

      // place them non-overlapping and separated in both dimensions
      n1.SetPos(new XY(0, 0));
      n2.SetPos(new XY(-100,    0));
      n3.SetPos(new XY(0, -100));

      // an impossible triangle
      g.Connect(n1, n2, 100, 100, 0);
      g.Connect(n2, n3, 40, 40, 0);
      g.Connect(n3, n1, 40, 40, 0);

      ExpanderTemp exp = new ExpanderTemp(g, 0, null);

      ExpanderTemp.RelRet ret;
      int count = 0;
      do
      {
         count++;
         // run it to a tighter convergence than usual
         ret = exp.RelaxStep(null, 1.0, null, 1e-3, 1e-4);
      }
      while(!ret.Completed);

      // should arrive at a compromise, close to linear with
      // n1 -> n2 a bit compressed and the other two edges stretched

      // see comments in testEdgeRelaxation about how these accuracies and count aren't at all definitive

      assertTrue(count < 40000);

      DirectedEdge e12 = n1.GetConnectionTo(n2);
      assertEquals(90, e12.Length(), 2);

      DirectedEdge e23 = n2.GetConnectionTo(n3);
      assertEquals(45, e23.Length(), 1);

      DirectedEdge e31 = n3.GetConnectionTo(n1);
      assertEquals(45, e31.Length(), 1);
   }

   @Test
   public void testNodeWideSeparationRelaxation() throws Exception
   {
      Graph g = new Graph();
      INode n1 = g.AddNode("n1", "", "", 10.0);
      INode n2 = g.AddNode("n2", "", "", 10.0);

      n1.SetPos(new XY(   0,    0));
      n2.SetPos(new XY(-100, 0));

      ExpanderTemp exp = new ExpanderTemp(g, 0, null);

      ExpanderTemp.RelRet ret;
      int count = 0;
      do
      {
         count++;
         // run it to a tighter convergence than usual
         ret = exp.RelaxStep(null, 1.0, null, 1e-3, 1e-4);
      }
      while(!ret.Completed);

      // should take a single cycle to see that nothing needs to move
      assertEquals(1, count);
      assertEquals(0, n1.GetPos().X, 0);
      assertEquals(0, n1.GetPos().Y, 0);
      assertEquals(-100, n2.GetPos().X, 0);
      assertEquals(0, n2.GetPos().Y, 0);
   }

   @Test
   public void testNodeTooCloseRelaxation() throws Exception
   {
      Graph g = new Graph();
      INode n1 = g.AddNode("n1", "", "", 10.0);
      INode n2 = g.AddNode("n2", "", "", 10.0);

      n1.SetPos(new XY(   0,    0));
      n2.SetPos(new XY(  -1,    0));

      ExpanderTemp exp = new ExpanderTemp(g, 0, null);

      ExpanderTemp.RelRet ret;
      int count = 0;
      do
      {
         count++;
         // run it to a tighter convergence than usual
         ret = exp.RelaxStep(null, 1.0, null, 1e-3, 1e-4);
      }
      while(!ret.Completed);

      // shouldn't take many cycle to bring them close to the target separation
      assertTrue(count < 100);

      double dist = n2.GetPos().Minus(n1.GetPos()).Length();

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

      n1.SetPos(new XY(0, 0));
      n2.SetPos(new XY(0, 20));
      n3.SetPos(new XY(100, 0));
      n4.SetPos(new XY(100, 20));

      g.Connect(n1, n2, 20, 20, 10);
      g.Connect(n3, n4, 20, 20, 10);

      ExpanderTemp exp = new ExpanderTemp(g, 0, null);

      ExpanderTemp.RelRet ret;
      int count = 0;
      do
      {
         count++;
         // run it to a tighter convergence than usual
         ret = exp.RelaxStep(null, 1.0, null, 1e-3, 1e-4);
      }
      while(!ret.Completed);

      // should take a single cycle to see that nothing needs to move
      assertEquals(1, count);
      assertEquals(0, n1.GetPos().X, 0);
      assertEquals(0, n1.GetPos().Y, 0);
      assertEquals(0, n2.GetPos().X, 0);
      assertEquals(20, n2.GetPos().Y, 0);
      assertEquals(100, n3.GetPos().X, 0);
      assertEquals(0, n3.GetPos().Y, 0);
      assertEquals(100, n4.GetPos().X, 0);
      assertEquals(20, n4.GetPos().Y, 0);
   }

   @Test
   public void testEdgeTooCloseRelaxation() throws Exception
   {
      Graph g = new Graph();

      INode n1 = g.AddNode("edge1start", "", "", 10.0);
      INode n2 = g.AddNode("edge1end", "", "", 10.0);
      INode n3 = g.AddNode("edge2start", "", "", 10.0);
      INode n4 = g.AddNode("edge2end", "", "", 10.0);

      n1.SetPos(new XY(0, 0));
      n2.SetPos(new XY(0, 20));
      n3.SetPos(new XY(1, 0));
      n4.SetPos(new XY(1, 20));

      g.Connect(n1, n2, 20, 20, 10);
      g.Connect(n3, n4, 20, 20, 10);

      ExpanderTemp exp = new ExpanderTemp(g, 0, null);

      ExpanderTemp.RelRet ret;
      int count = 0;
      do
      {
         count++;
         // run it to a tighter convergence than usual
         ret = exp.RelaxStep(null, 1.0, null, 1e-3, 1e-4);
      }
      while(!ret.Completed);

      // shouldn't take long to push the edges apart
      assertTrue(count < 50);
      // should have just slid sideways along X
      assertEquals(20, n3.GetPos().X - n1.GetPos().X, 0.1);
      assertEquals(20, n4.GetPos().X - n2.GetPos().X, 0.1);

      assertEquals(0, n1.GetPos().Y, 0);
      assertEquals(20, n2.GetPos().Y, 0);
      assertEquals(0, n3.GetPos().Y, 0);
      assertEquals(20, n4.GetPos().Y, 0);
   }

   @Test
   public void testEdgeNodeTooCloseRelaxation() throws Exception
   {
      Graph g = new Graph();

      INode n1 = g.AddNode("edge1start", "", "", 10.0);
      INode n2 = g.AddNode("edge1end", "", "", 10.0);
      INode n3 = g.AddNode("node", "", "", 10.0);

      // edge long enough that there is no n1->n3 or n2->n3 interaction
      n1.SetPos(new XY(0, 0));
      n2.SetPos(new XY(0, 100));
      n3.SetPos(new XY(1, 50));

      g.Connect(n1, n2, 100, 100, 10);

      ExpanderTemp exp = new ExpanderTemp(g, 0, null);

      ExpanderTemp.RelRet ret;
      int count = 0;
      do
      {
         count++;
         // run it to a tighter convergence than usual
         ret = exp.RelaxStep(null, 1.0, null, 1e-3, 1e-4);
      }
      while(!ret.Completed);

      // takes a little time to push the edge and node apart
      assertTrue(count < 120);
      // should have just slid sideways along X
      assertEquals(20, n3.GetPos().X - n1.GetPos().X, 0.1);

      assertEquals(0, n1.GetPos().Y, 0);
      assertEquals(100, n2.GetPos().Y, 0);
      assertEquals(50, n3.GetPos().Y, 0);
   }
}
