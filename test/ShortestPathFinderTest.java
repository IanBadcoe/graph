import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertArrayEquals;

public class ShortestPathFinderTest
{
   @Test
   public void testFindDoublePathLengths() throws Exception
   {
      Graph g = new Graph();

      INode[] nodes = new INode[5];
      INode n1 = nodes[0] = g.addNode("", "", "", 0);
      INode n2 = nodes[1] = g.addNode("", "", "", 0);
      INode n3 = nodes[2] = g.addNode("", "", "", 0);
      INode n4 = nodes[3] = g.addNode("", "", "", 0);
      INode n5 = nodes[4] = g.addNode("", "", "", 0);

      //               n3 --3-> n5
      //               ^\      ^
      //              /  5    /
      //             3    \  2
      //            /      v/
      // n1 -10-> n2 --4-> n4

      g.connect(n1, n2, 10.1, 10, 0);
      g.connect(n2, n3, 3.1, 3, 0);
      g.connect(n2, n4, 4.1, 4, 0);
      g.connect(n3, n4, 5.1, 5, 0);
      g.connect(n4, n5, 2.1, 2, 0);
      g.connect(n3, n5, 3.1, 3, 0);

      double[][] ans = ShortestPathFinder.FindPathLengths(g, x -> x.MinLength);

      double[][] exp = new double[][]
            {{0, 10.1, 13.2, 14.2, 16.3},
             {10.1, 0, 3.1, 4.1, 6.2},
             {13.2, 3.1, 0, 5.1, 3.1},
             {14.2, 4.1, 5.1, 0, 2.1},
             {16.3, 6.2, 3.1, 2.1, 0}};

      for(int i = 0; i < g.numNodes(); i++)
      {
         for(int j = 0; j < g.numNodes(); j++)
         {
            assertEquals(exp[i][j], ans[nodes[i].getIdx()][nodes[j].getIdx()], 1e-9);
         }
      }
   }
}