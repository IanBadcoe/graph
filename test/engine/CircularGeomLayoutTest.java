package engine;

import engine.brep.CircleCurve;
import engine.brep.Loop;
import org.junit.Test;

import static org.junit.Assert.*;

public class CircularGeomLayoutTest
{
   @Test
   public void testCreateFromNode() throws Exception
   {
      Node n = new Node("x", "y", "z", 3);
      n.setPos(new XY(5, 6));

      CircularGeomLayout cgl = CircularGeomLayout.createFromNode(n);

      assertTrue(n.getPos().equals(cgl.Position));
      assertEquals(3, cgl.Radius, 0);
   }

   @Test
   public void testMakeBaseGeometry() throws Exception
   {
      CircularGeomLayout cgl = new CircularGeomLayout(new XY(7, 8), 9);

      Loop l = cgl.makeBaseGeometry();

      assertEquals(1, l.numCurves());

      CircleCurve cc = (CircleCurve)l.getCurves().get(0);
      assertNotNull(cc);

      assertTrue(new XY(7, 8).equals(cc.Position));
      assertTrue(cc.isCyclic());
      assertEquals(9, cc.Radius, 0);
   }

   @Test
   public void testMakeDetailGeometry() throws Exception
   {
      CircularGeomLayout cgl = new CircularGeomLayout(new XY(7, 8), 9);

      assertNull(cgl.makeDetailGeometry());
   }
}
