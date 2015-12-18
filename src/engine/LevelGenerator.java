package engine;

import java.util.*;

public class LevelGenerator
{
   public LevelGenerator(LevelGeneratorConfiguration config)
   {
      m_config = config;
   }

   public StepperController.StatusReport step()
   {
      switch (m_phase)
      {
         case Init:
            return initStep();

         case GraphExpand:
            return graphExpandStep();

         case FinalRelax:
            return finalRelaxStep();

         case BaseGeometry:
            return baseGeometryStep();

         case Union:
            return unionStep();

         case Done:
            return doneStep();
      }

      // really shouldn't happen

      return null;
   }

   private StepperController.StatusReport doneStep()
   {
      m_level = m_union_helper.makeLevel(m_config.CellSize, m_config.WallFacetLength);

      m_union_helper = null;

      return new StepperController.StatusReport(
            StepperController.Status.StepOutSuccess,
            "engine.Level complete",
            true);
   }

   private StepperController.StatusReport initStep()
   {
      m_graph = MakeSeed();

      m_expander = new StepperController(m_graph,
            new ExpandToSizeStepper(m_graph, m_reqSize, m_templates,
                  m_config));

      LevelGeneratorConfiguration temp = new LevelGeneratorConfiguration(m_config);
      temp.RelaxationForceTarget /= 5;
      temp.RelaxationMoveTarget /= 5;

      m_final_relaxer = new StepperController(m_graph,
            new RelaxerStepper(m_graph, temp));

      m_phase = Phase.GraphExpand;

      return new StepperController.StatusReport(
            new StepperController.StatusReportInner(StepperController.Status.Iterate,
               null, "engine.Level creation initialised"),
            false);
   }

   private StepperController.StatusReport graphExpandStep()
   {
      StepperController.StatusReport ret = null;

      for (int i = 0; i < m_config.ExpandStepsToRun; i++)
      {
         ret = m_expander.Step();

         if (ret.Complete)
         {
            m_phase = Phase.FinalRelax;

            return new StepperController.StatusReport(
                  StepperController.Status.Iterate,
                  ret.Log,
                  false);
         }
      }

      return ret;
   }

   private StepperController.StatusReport finalRelaxStep()
   {
      StepperController.StatusReport ret = null;

      for (int i = 0; i < m_config.ExpandStepsToRun; i++)
      {
         ret = m_final_relaxer.Step();

         if (ret.Complete)
         {
            m_phase = Phase.BaseGeometry;

            return new StepperController.StatusReport(
                  StepperController.Status.Iterate,
                  ret.Log,
                  false);
         }
      }

      return ret;
   }

   private StepperController.StatusReport baseGeometryStep()
   {
      m_union_helper = new UnionHelper();

      m_union_helper.generateGeometry(m_graph);

      m_phase = Phase.Union;

      return new StepperController.StatusReport(
            new StepperController.StatusReportInner(StepperController.Status.Iterate,
                  null, "engine.Level base geometry generated"),
            false);
   }

   private StepperController.StatusReport unionStep()
   {
      boolean done = m_union_helper.unionOne(m_config.Rand);

      if (done)
      {
         m_phase = Phase.Done;

         return new StepperController.StatusReport(
               new StepperController.StatusReportInner(StepperController.Status.StepOutSuccess,
                     null, "Geometry merged."),
               false);
      }

      return new StepperController.StatusReport(
            new StepperController.StatusReportInner(StepperController.Status.Iterate,
                  null, "Merging geometry"),
            false);
   }

   private Graph MakeSeed()
   {
      Graph ret = new Graph();
      INode start = ret.addNode("Start", "<", "Seed", 55f);
      INode expander = ret.addNode("engine.StepperController", "e", "Seed", 55f);
      INode end = ret.addNode("End", ">", "Seed", 55f);

      start.setPos(new XY(-100, 0));
      expander.setPos(new XY(0, 0));
      end.setPos(new XY(0, 100));

      ret.connect(start, expander, 90, 110, 10);
      ret.connect(expander, end, 90, 110, 10);

      //not expandable, which simplifies expansion as start won't need replacing
      return ret;
   }

   public Level getLevel()
   {
      return m_level;
   }

   public Graph getGraph()
   {
      return m_graph;
   }

   public Phase getPhase()
   {
      return m_phase;
   }

   public enum Phase
   {
      Init,
      GraphExpand,
      FinalRelax,
      BaseGeometry,
      Union,
      Done
   }

   private Phase m_phase = Phase.Init;

   private Graph m_graph;

   private final TemplateStore m_templates = new TemplateStore1();

   @SuppressWarnings("FieldCanBeLocal")
   private final int m_reqSize = 30;

   private StepperController m_expander;
   private StepperController m_final_relaxer;

   private boolean m_lay_out_running = true;
   private boolean m_level_generated = false;
   private boolean m_unions_done = false;
   private boolean m_final_relaxation = false;

   private final Random m_union_random = new Random(1);

   private final LevelGeneratorConfiguration m_config;

   private Level m_level;

   private UnionHelper m_union_helper;
}
