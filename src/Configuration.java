class Configuration
{
   private Configuration()
   {
   }

   // --------------------------------------------------------------------------------------------------------------
   // vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
   // relaxation parameters

   // The node sizes and edge widths define the space that rooms and corridors will occupy internally
   // this is added on to give an additional minimum wall thickness between them
   public static double RelaxationMinimumSeparation = 1;

   // these scale the strength of the three fundamental forces
   // current theory is to set EdgeLength force significantly weaker so that edge stretch can be used to detect
   // when they need splitting
   public static double EdgeToNodeForceScale = 1.0;
   public static double EdgeLengthForceScale = 0.01;
   public static double NodeToNodeForceScale = 1.0;

   // time steps are scaled down if they would lead to any node moving further than this
   public static double RelaxationMaxMove = 1.0;

   // relaxation is considered complete when the max force or max move seen on a node
   // drops below both of these
   public static double RelaxationForceTarget = 0.001;
   public static double RelaxationMoveTarget = 0.01;

   // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
   // --------------------------------------------------------------------------------------------------------------
}
