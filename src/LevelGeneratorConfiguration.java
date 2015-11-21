import java.util.Random;

class LevelGeneratorConfiguration
{
   LevelGeneratorConfiguration(int rand_seed)
   {
      Rand = new Random(rand_seed);
   }

   LevelGeneratorConfiguration(LevelGeneratorConfiguration old)
   {
      RelaxationMinimumSeparation = old.RelaxationMinimumSeparation;

      EdgeToNodeForceScale = old.EdgeToNodeForceScale;
      EdgeLengthForceScale = old.EdgeLengthForceScale;
      NodeToNodeForceScale = old.NodeToNodeForceScale;

      RelaxationMaxMove = old.RelaxationMaxMove;

      RelaxationForceTarget = old.RelaxationForceTarget;
      RelaxationMoveTarget = old.RelaxationMoveTarget;

      Rand = old.Rand;
   }

   // --------------------------------------------------------------------------------------------------------------
   // vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
   // relaxation parameters

   // The node sizes and edge widths define the space that rooms and corridors will occupy internally
   // this is added on to give an additional minimum wall thickness between them
   public double RelaxationMinimumSeparation = 5;

   // these scale the strength of the three fundamental forces
   // current theory is to set EdgeLength force significantly weaker so that edge stretch can be used to detect
   // when they need splitting
   public double EdgeToNodeForceScale = 1.0;
   public double EdgeLengthForceScale = 0.01;
   public double NodeToNodeForceScale = 1.0;

   // time steps are scaled down if they would lead to any node moving further than this
   public double RelaxationMaxMove = 1.0;

   // relaxation is considered complete when the max force or max move seen on a node
   // drops below both of these
   public double RelaxationForceTarget = 0.001;
   public double RelaxationMoveTarget = 0.01;

   // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
   // --------------------------------------------------------------------------------------------------------------

   // --------------------------------------------------------------------------------------------------------------
   // vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
   // random number source

   public Random Rand;

   // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
   // --------------------------------------------------------------------------------------------------------------
}