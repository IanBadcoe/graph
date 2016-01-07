package game;

import engine.*;
import processing.core.PApplet;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

@SuppressWarnings("WeakerAccess")
public class Main extends processing.core.PApplet implements IDraw
{
   public static void main(String[] args) {
      processing.core.PApplet.main("game.Main", args);
   }

   public Main()
   {
      m_config = new LevelGeneratorConfiguration(85);
      m_generator = new LevelGenerator(10, m_config);
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
         playKeyPressed();

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
   }

   private void playKeyPressed()
   {
      m_keys.keyPressed(keyCode);

      if (key == 'r')
      {
         m_rotating = !m_rotating;
      }

      if (key == '+')
      {
         m_scale += 0.5;
      }

      if (key == '=')
      {
         m_scale -= 0.5;
      }
   }

   @Override
   public void keyReleased()
   {
      m_keys.keyReleased(keyCode);
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

//      saveFrame("..\\graph non-git\\Frame####.jpg");
   }

   private void play()
   {
      processKeys();

      m_level.step(0.1);

      translate((float)(width / 2), (float)(height / 2));

      scale((float)m_scale);

      if (m_rotating)
      {
         m_decaying_ori = (m_decaying_ori * 9 + m_player.getOrientation()) / 10;

         rotate((float)(Math.PI + m_decaying_ori));
      }

      translate((float)(-m_player.getPosition().X), (float)(-m_player.getPosition().Y));

      drawLevel(m_level, m_player.getPosition());

//      for(Annotations.Annotation annotation : m_annotations)
//      {
//         annotation.draw(this);
//      }
   }

   private void processKeys()
   {
      if (m_keys.isPressed(LEFT_KEY))
      {
         m_player.turnLeft();
      }

      if (m_keys.isPressed(RIGHT_KEY))
      {
         m_player.turnRight();
      }

      if (m_keys.isPressed(FORWARDS_KEY))
      {
         m_player.accelerate();
      }

      if (m_keys.isPressed(BACKWARDS_KEY))
      {
         m_player.brake();
      }
   }

   private void startPlay()
   {
      m_player = new Player();
      m_player.setPosition(m_level.startPos());
      m_player.setOrientation(Math.PI / 4);

      m_level.addMovable(m_player);

      m_playing = true;

      m_scale = 2.0;

      m_keys = new KeyTracker();

      m_keys.addKey(LEFT_KEY, KeyEvent.VK_LEFT);
      m_keys.addKey(RIGHT_KEY, KeyEvent.VK_RIGHT);
      m_keys.addKey(FORWARDS_KEY, KeyEvent.VK_UP);
      m_keys.addKey(BACKWARDS_KEY, KeyEvent.VK_DOWN);
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

   void drawGraph(Graph g, boolean show_labels, boolean show_connections,
                         @SuppressWarnings("SameParameterValue") boolean show_circles, boolean show_arrows)
   {
      if (show_circles)
      {
         g.allGraphNodes().forEach(this::drawNode);
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
         g.allGraphNodes().forEach(this::drawLabel);
      }
   }

   void drawNode(INode n)
   {
      s_app.noStroke();
      s_app.fill(n.getColour());
      s_app.ellipse((float) n.getPos().X, (float) n.getPos().Y,
            (float) n.getRad(), (float) n.getRad());
   }

   void drawLabel(INode n)
   {
      s_app.fill(255, 255, 255);
      s_app.text(n.getName(),
            (float) n.getPos().X, (float) n.getPos().Y);
   }

   void drawConnections(INode n, boolean show_arrows)
   {
      // in connections are drawn by the other node...
      for(DirectedEdge e : n.getOutConnections())
      {
         s_app.stroke(e.GetColour());
         strokeWidth(e.HalfWidth * 1.9, false);
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

   void drawLevel(Level level, XY visibility_pos)
   {
      s_app.background(0xff201010);

      s_app.stroke(0xff808080);
      strokeWidth(2, true);

      s_app.stroke(0xff808080);
      s_app.fill(180, 120, 120);

      Box bounds = level.getBounds();

      s_app.beginShape();
      s_app.vertex((float)bounds.Max.X + 1000, (float)bounds.Max.Y + 1000);
      s_app.vertex((float)bounds.Min.X - 1000, (float)bounds.Max.Y + 1000);
      s_app.vertex((float)bounds.Min.X - 1000, (float)bounds.Min.Y - 1000);
      s_app.vertex((float)bounds.Max.X + 1000, (float)bounds.Min.Y - 1000);

      level.getWallLoops().forEach(this::drawWallLoop);
      s_app.endShape(CLOSE);

      s_app.stroke(0xfff0f0f0);
      strokeWidth(2, true);

      for(Wall w : level.getVisibleWalls(visibility_pos))
      {
         line(w.Start, w.End);
//         line(w.midPoint(), w.midPoint().plus(w.Normal.multiply(10)));
      }

      for(IDrawable id : level.getDrawables())
      {
         id.draw(this);
      }
   }

   void drawWallLoop(WallLoop wl)
   {
      s_app.beginContour();
      for(Wall w : wl)
      {
         s_app.vertex((float)w.Start.X, (float)w.Start.Y);
      }
      s_app.endContour();
   }

   void drawLoopPoints(ArrayList<XY> pnts)
   {
      XY prev = pnts.get(pnts.size() - 1);

      for(XY curr : pnts)
      {
         line(prev, curr);

         prev = curr;
      }
   }

   @Override
   public void line(XY from, XY to)
   {
      s_app.line((float)from.X, (float)from.Y, (float)to.X, (float)to.Y);
   }

   @Override
   public void text(String text, XY pos)
   {
      s_app.text(text, (float)pos.X, (float)pos.Y);
   }

   @Override
   public void stroke(int red, int green, int blue)
   {
      s_app.stroke(red, green, blue);
   }

   @Override
   public void fill(int red, int green, int blue)
   {
      s_app.fill(red, green, blue);
   }

   @Override
   public void circle(XY pos, double rad)
   {
      s_app.ellipse((float)pos.X, (float)pos.Y, (float)rad, (float)rad);
   }

   @Override
   public double getScale()
   {
      return m_scale;
   }

   void scaleTo(Box b)
   {
      double shorter_display = Math.min(s_app.width, s_app.height);

      double larger_box = Math.max(b.DX(), b.DY());

      larger_box *= 1.1;

      s_app.translate(s_app.width / 2, s_app.height / 2);

      s_app.scale((float)(shorter_display / larger_box));

      s_app.translate((float)-b.Min.X,(float)-b.Min.Y);
   }

   void clear(@SuppressWarnings("SameParameterValue") int c)
   {
      s_app.background(c);
   }

   @Override
   public void strokeWidth(double d, boolean scaling)
   {
      if (scaling)
         d /= m_scale;

      s_app.strokeWeight((float)d);
   }

   private static PApplet s_app;

   // UI data
   private boolean m_auto_scale = true;
   private boolean m_labels = true;
   private boolean m_arrows = false;

   private double m_off_x = 0.0;
   private double m_off_y = 0.0;
   private double m_scale = 1.0;

   private LevelGeneratorConfiguration m_config;
   private LevelGenerator m_generator;
   private Level m_level;

   boolean m_playing = false;

   private Player m_player;

   private boolean m_rotating = true;

   private KeyTracker m_keys;

   private double m_decaying_ori = 0;

   private final static int LEFT_KEY = 0;
   private final static int RIGHT_KEY = 1;
   private final static int FORWARDS_KEY = 2;
   private final static int BACKWARDS_KEY = 3;
}
