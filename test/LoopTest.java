import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

class LoopTest
{
   @Test
   void testUnion() throws Exception
   {
      CurveCircle cc1 = new CurveCircle(new XY(0, 0), 10);

      Loop l1 = new Loop(cc1);
      Loop l2 = new Loop(cc1);

      l1.union(l2);

      assertEquals(1, l1.numCurves());

      assertEquals(cc1.getPosition(), l1.
   }
}