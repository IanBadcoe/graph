import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;

/**
 * uses the Floyd-Warshall algorithm to find all shortest path lengths in a graph
 *
 * at the moment, we ultimately want the minimum of the shortest path through the graph
 * between two nodes, and the two nodes summed radii, so we could see this with the summed radii
 * instead fo 1e30.  However writing this as an un-mucked-about "shortest path through graph edges"
 * algorithm feels more likely to have other uses later...
 */
class ShortestPathFinder<T>
{
   static double[][] FindPathLengths(Graph g, Function<DirectedEdge, Double> get_edge_length)
   {
      double[][] ret = new double[g.NumNodes()][g.NumNodes()];

      for (int i = 0; i < g.NumNodes(); i++)
      {
         // if you try to build a dungeon bigger than this you deserve all you get
         Arrays.fill(ret[i], Double.MAX_VALUE);
      }

      int count = 0;

      for(INode n : g.AllGraphNodes())
      {
         n.SetIdx(count);

         ret[count][count] = 0;

         count++;
      }

      for(DirectedEdge de : g.AllGraphEdges())
      {
         int si = de.Start.GetIdx();
         int ei = de.End.GetIdx();

         double len = get_edge_length.apply(de); //(de.MaxLength + de.MinLength) / 2;

         ret[si][ei] = ret[ei][si] = len;
      }

      for(INode nk : g.AllGraphNodes())
      {
         int k = nk.GetIdx();
         for(INode ni : g.AllGraphNodes())
         {
            int i = ni.GetIdx();
            for(INode nj : g.AllGraphNodes())
            {
               int j = nj.GetIdx();
               ret[i][j] = Math.min(ret[i][j], ret[i][k] + ret[k][j]);
            }
         }
      }

      return ret;
   }
}
