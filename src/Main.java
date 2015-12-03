import processing.core.PApplet;

import java.util.ArrayList;

@SuppressWarnings("WeakerAccess")
public class Main extends processing.core.PApplet
{
   public static void main(String[] args) {
      processing.core.PApplet.main("Main", args);
   }

   public Main()
   {
   }

   @Override
   public void settings()
   {
//      size(500, 500);
      fullScreen();

      s_app = this;

      // configure our crude IoC system
      TryAllNodesExpandStepper.SetChildFactory(
            TryAllTemplatesOnOneNodeStepper::new);
      TryAllTemplatesOnOneNodeStepper.SetChildFactory(
            TryTemplateExpandStepper::new);
      ExpandToSizeStepper.SetChildFactory(
            TryAllNodesExpandStepper::new);
      EdgeAdjusterStepper.SetChildFactory(
            RelaxerStepper::new);
      TryTemplateExpandStepper.SetRelaxerFactory(
            RelaxerStepper::new);
      TryTemplateExpandStepper.SetAdjusterFactory(
            EdgeAdjusterStepper::new);
   }

   @Override
   public void setup()
   {
      ellipseMode(RADIUS);
   }

   @Override
   public void keyPressed()
   {
      if (m_playing)
      {
         playKeyPress();

         return;
      }

      if (key == 'a')
         m_auto_scale = !m_auto_scale;

      double range = min(width, height);

      if (key == 'z')
         m_off_x -= range / m_scale / 20;

      if (key == 'x')
         m_off_x += range / m_scale / 20;

      if (key == '\'')
         m_off_y -= range / m_scale / 20;

      if (key == '/')
         m_off_y += range / m_scale / 20;

      if (key == '=')
      {
         m_scale *= 1.1;
         m_off_x /= 1.1;
         m_off_y /= 1.1;
      }

      if (key == '-')
      {
         m_scale /= 1.1;
         m_off_x *= 1.1;
         m_off_y *= 1.1;
      }

      if (key == 'l')
      {
         m_labels = !m_labels;
      }

      if (key == '>')
      {
         m_arrows = !m_arrows;
      }

      if (key == 'n')
      {
         m_show_notes = !m_show_notes;
      }
   }

   private void playKeyPress()
   {
      if (key == 'x')
      {
         m_player_pos = m_player_pos.minus(new XY(-5, 0));
      }

      if (key == 'z')
      {
         m_player_pos = m_player_pos.minus(new XY(5, 0));
      }

      if (key == '/')
      {
         m_player_pos = m_player_pos.minus(new XY(0, -5));
      }

      if (key == '\'')
      {
         m_player_pos = m_player_pos.minus(new XY(0, 5));
      }
   }

   @Override
   public void draw()
   {
      if (m_playing)
      {
         play();

         return;
      }

      background(128);
      strokeWeight(0.0f);
//      textSize(0.01f);

      StepperController.StatusReport ret;

      if (m_generator != null)
      {
         ret = m_generator.step();

         // take before complete so we can draw it...
         m_level = m_generator.getLevel();

         if (ret.Complete)
         {
            if (ret.Status != StepperController.Status.StepOutSuccess)
            {
               exit();
            }

            m_generator = null;
            m_config = null;

            startPlay();
         }
      }

      double range = min(width, height);

      if (m_auto_scale && m_generator != null && m_generator.getGraph() != null)
         autoScale(m_generator.getGraph(), range * 0.05, range * 0.95);

      translate((float)(range * 0.05), (float)(range * 0.05));

      scale((float)m_scale);

      translate((float)m_off_x, (float)m_off_y);

      if (m_generator != null && m_generator.getGraph() != null)
      {
         LevelGenerator.Phase p = m_generator.getPhase();
         drawGraph(m_generator.getGraph(),
               m_labels,
               p != LevelGenerator.Phase.Union,
               true,
               m_arrows);
      }

      if (m_show_notes)
      {
         m_notes.forEach(Annotation::Draw);
      }

//      saveFrame("..\\graph non-git\\Frame####.jpg");
   }

   private void play()
   {
      translate((float)(width / 2), (float)(height / 2));

      scale(2);

      translate((float)(-m_player_pos.X), (float)(-m_player_pos.Y));

      drawLevel(m_level, m_player_pos);
   }

   private void startPlay()
   {
      m_player_pos = m_level.startPos();

      m_playing = true;
   }

   private void autoScale(Graph g, double low, double high)
   {
      Box b = g.bounds();

      double sx = (high - low) / b.DX();
      double sy = (high - low) / b.DY();

      double smaller_scale = Math.min(sx, sy);

      m_off_x = -b.Min.X;
      m_off_y = -b.Min.Y;
      m_scale = smaller_scale;
   }


   static void line(XY from, XY to)
   {
      s_app.line((float)from.X, (float)from.Y, (float)to.X, (float)to.Y);
   }

   static void text(String text, XY pos)
   {
      s_app.text(text, (float)pos.X, (float)pos.Y);
   }

   static void drawGraph(Graph g, boolean show_labels, boolean show_connections,
                         @SuppressWarnings("SameParameterValue") boolean show_circles, boolean show_arrows)
   {
      if (show_circles)
      {
         g.allGraphNodes().forEach(Main::drawNode);
      }


      if (show_connections)
      {
         for (INode n : g.allGraphNodes())
         {
            drawConnections(n, show_arrows);
         }
      }

      if (show_labels)
      {
         g.allGraphNodes().forEach(Main::drawLabel);
      }
   }

   static void drawNode(INode n)
   {
      s_app.noStroke();
      s_app.fill(n.getColour());
      s_app.ellipse((float) n.getPos().X, (float) n.getPos().Y,
            (float) n.getRad(), (float) n.getRad());
   }

   static void drawLabel(INode n)
   {
      s_app.fill(255, 255, 255);
      s_app.text(n.getName(),
            (float) n.getPos().X, (float) n.getPos().Y);
   }

   static void drawConnections(INode n, boolean show_arrows)
   {
      // in connections are drawn by the other node...
      for(DirectedEdge e : n.getOutConnections())
      {
         s_app.stroke(e.GetColour());
         s_app.strokeWeight((float)(e.HalfWidth * 1.9));
         line(e.Start.getPos(), e.End.getPos());

         if (show_arrows)
         {
            XY d = e.End.getPos().minus(e.Start.getPos());
            d = d.divide(10);

            XY rot = d.rot90();

            line(e.End.getPos(), e.End.getPos().minus(d).minus(rot));
            line(e.End.getPos(), e.End.getPos().minus(d).plus(rot));
         }
      }
   }

   static void drawLevel(Level level, XY visibility_pos)
   {
      s_app.background(0xff201010);

      s_app.stroke(0xff808080);
      s_app.strokeWeight(1);

      s_app.stroke(240);
      s_app.fill(180, 120, 120);

      Box bounds = level.getBounds();

      s_app.beginShape();
      s_app.vertex((float)bounds.Max.X + 1000, (float)bounds.Max.Y + 1000);
      s_app.vertex((float)bounds.Min.X - 1000, (float)bounds.Max.Y + 1000);
      s_app.vertex((float)bounds.Min.X - 1000, (float)bounds.Min.Y - 1000);
      s_app.vertex((float)bounds.Max.X + 1000, (float)bounds.Min.Y - 1000);

      level.getWallLoops().forEach(Main::drawWallLoop);
      s_app.endShape(CLOSE);

      s_app.stroke(0xffc0c0c0);
      s_app.strokeWeight(2);
      s_app.fill(0xff606060);

      s_app.beginShape();
      level.getVisibilityPolygon(visibility_pos).forEach(x -> s_app.vertex((float)x.X, (float)x.Y));
      s_app.endShape(CLOSE);
   }

   static void drawWallLoop(WallLoop wl)
   {
      s_app.beginContour();
      for(Wall w : wl)
      {
         s_app.vertex((float)w.Start.X, (float)w.Start.Y);
      }
      s_app.endContour();
   }

   static void drawLoopPoints(ArrayList<XY> pnts)
   {
      XY prev = pnts.get(pnts.size() - 1);

      for(XY curr : pnts)
      {
         line(prev, curr);

         prev = curr;
      }
   }

   static void stroke(int red, int green, int blue)
   {
      s_app.stroke(red, green, blue);
   }

   static void fill(int red, int green, int blue)
   {
      s_app.fill(red, green, blue);
   }

   static void circle(double x, double y, double rad)
   {
      s_app.ellipse((float)x, (float)y, (float)rad, (float)rad);
   }

   static void scaleTo(Box b)
   {
      double shorter_display = Math.min(s_app.width, s_app.height);

      double larger_box = Math.max(b.DX(), b.DY());

      larger_box *= 1.1;

      s_app.translate(s_app.width / 2, s_app.height / 2);

      s_app.scale((float)(shorter_display / larger_box));

      s_app.translate((float)-b.Min.X,(float)-b.Min.Y);
   }

   static void clear(@SuppressWarnings("SameParameterValue") int c)
   {
      s_app.background(c);
   }

   public static void strokeWidth(double d)
   {
      s_app.strokeWeight((float)d);
   }

   private static PApplet s_app;

   // UI data
   private boolean m_auto_scale = false;
   private boolean m_labels = true;
   private boolean m_arrows = false;
   private boolean m_show_notes = true;

   @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
   private final ArrayList<Annotation> m_notes = new ArrayList<>();

   private double m_off_x = 0.0;
   private double m_off_y = 0.0;
   private double m_scale = 1.0;

   private LevelGeneratorConfiguration m_config = new LevelGeneratorConfiguration(85);
   private LevelGenerator m_generator = new LevelGenerator(m_config);
   private Level m_level;

   boolean m_playing = false;
   private XY m_player_pos;
}
