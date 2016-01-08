package engine;

import java.util.Arrays;
import java.util.function.Function;

//
// uses the Floyd-Warshall algorithm to find all shortest path lengths in a graph
//
// at the moment, we ultimately want the minimum of the shortest path through the graph
// between two nodes, and the two nodes summed radii, so we could seed this with the summed radii
// instead of 1e30.  However writing this as an un-mucked-about "shortest path through graph edges"
// algorithm feels more likely to have other uses later...
//
class ShortestPathFinder
{
   public static double[][] FindPathLengths(Graph g, Function<DirectedEdge, Double> get_edge_length)
   {
      double[][] ret = new double[g.numNodes()][g.numNodes()];

      for (int i = 0; i < g.numNodes(); i++)
      {
         // if you try to build a dungeon bigger than this you deserve all you get
         Arrays.fill(ret[i], Double.MAX_VALUE);
      }

      int count = 0;

      for(INode n : g.allGraphNodes())
      {
         n.setIdx(count);

         ret[count][count] = 0;

         count++;
      }

      for(DirectedEdge de : g.allGraphEdges())
      {
         int si = de.Start.getIdx();
         int ei = de.End.getIdx();

         double len = get_edge_length.apply(de); //(de.MaxLength + de.MinLength) / 2;

         ret[si][ei] = ret[ei][si] = len;
      }

      for(INode nk : g.allGraphNodes())
      {
         int k = nk.getIdx();
         for(INode ni : g.allGraphNodes())
         {
            int i = ni.getIdx();
            for(INode nj : g.allGraphNodes())
            {
               int j = nj.getIdx();
               ret[i][j] = Math.min(ret[i][j], ret[i][k] + ret[k][j]);
            }
         }
      }

      return ret;
   }
}
