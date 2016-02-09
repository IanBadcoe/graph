package engine;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class LevelGeneratorConfigurationTest
{
   @Test
   public void testCtor()
   {
      {
         LevelGeneratorConfiguration lcg
               = new LevelGeneratorConfiguration(4);

         assertEquals(new Random(4).nextInt(), lcg.Rand.nextInt());
      }

      {
         LevelGeneratorConfiguration lgc
               = new LevelGeneratorConfiguration(7);

         lgc.ExpandStepsToRun = 1;
         lgc.CellSize = 2;
         lgc.EdgeLengthForceScale = 3;
         lgc.EdgeToNodeForceScale = 4;
         lgc.RelaxationForceTarget = 5;
         lgc.RelaxationMaxMove = 6;
         lgc.RelaxationMinimumSeparation = 7;
         lgc.RelaxationMoveTarget = 8;
         lgc.WallFacetLength = 9;
         lgc.NodeToNodeForceScale = 10;

         LevelGeneratorConfiguration lgc2 = LevelGeneratorConfiguration.shallowCopy(lgc);

         // Intuitively you might expect a clone of Rand here, however
         // the usage we have for the copy ctor at the moment is to produce a modified
         // lgc for temporary use inside a process embedded in a longer process.
         // Both processes could use random numbers and it doesn't make any sense to give the
         // inner process a separate set of them, which is a subset of those used
         // in the outer process
         //
         // Which is why this is a shallow copy...
         assertEquals(lgc.Rand, lgc2.Rand);

         assertEquals(1, lgc.ExpandStepsToRun, 0);
         assertEquals(2, lgc.CellSize, 0);
         assertEquals(3, lgc.EdgeLengthForceScale, 0);
         assertEquals(4, lgc.EdgeToNodeForceScale, 0);
         assertEquals(5, lgc.RelaxationForceTarget, 0);
         assertEquals(6, lgc.RelaxationMaxMove, 0);
         assertEquals(7, lgc.RelaxationMinimumSeparation, 0);
         assertEquals(8, lgc.RelaxationMoveTarget, 0);
         assertEquals(9, lgc.WallFacetLength, 0);
         assertEquals(10, lgc.NodeToNodeForceScale, 0);
      }
   }
}