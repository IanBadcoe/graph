package game;

import engine.*;

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

      // configure our crude IoC system
      m_ioc_container = new IoCContainer(
            RelaxerStepper::new,
            TryAllNodesExpandStepper::new,
            TryAllTemplatesOnOneNodeStepper::new,
            TryTemplateExpandStepper::new,
            EdgeAdjusterStepper::new);

      m_generator = new LevelGenerator(m_ioc_container, 10, m_config, new TemplateStore1());
   }

   @Override
   public void settings()
   {
//      size(500, 500, P3D);
      fullScreen(P3D);
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

      if (m_complete && key == 'p')
      {
         startPlay();
      }
   }

   private void playKeyPressed()
   {
      if (m_keys != null)
      {
         m_keys.keyPressed(keyCode);
      }

      if (key == 'm')
      {
         m_map = !m_map;
      }

      if (m_map)
      {
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
   }

   @Override
   public void keyReleased()
   {
      if (m_keys != null)
      {
         m_keys.keyReleased(keyCode);
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

      if (m_generator != null && !m_complete)
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

            m_complete = true;
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

      if (m_map)
      {
         drawMap();
      }
      else
      {
         draw3D();
      }
   }

   void drawMap()
   {
      camera();
      perspective();

      translate((float)(width / 2), (float)(height / 2));

      scale((float)m_scale);

      if (m_rotating)
      {
         m_decaying_ori = (m_decaying_ori * 9 + m_player.getOrientation()) / 10;

         rotate((float)(Math.PI + m_decaying_ori));
      }

      translate(m_player.getPos2D().negate());

      drawLevel(m_level, m_player.getPos2D());
   }

   void draw3D()
   {
      m_level.drawLevel3D(m_player, this, width, height);
   }

   @Override
   public void camera(XYZ eye, XYZ target, XYZ up)
   {
      camera(
            (float)eye.X, (float)eye.Y, (float)eye.Z,
            (float)target.X, (float)target.Y, (float)target.Z,
            (float)up.X, (float)up.Y, (float)up.Z);
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
         m_player.reverse();
      }
   }

   private void startPlay()
   {
      m_generator = null;
      m_config = null;

      m_player = new Player();
      m_player.setPos3D(new XYZ(m_level.startPos(), 0));
      m_player.setOrientation(Math.PI / 4);

      m_level.addObject(m_player);

      m_playing = true;

      m_scale = 2.0;

      m_keys = new KeyTracker();

      m_keys.addKey(LEFT_KEY, KeyEvent.VK_LEFT);
      m_keys.addKey(RIGHT_KEY, KeyEvent.VK_RIGHT);
      m_keys.addKey(FORWARDS_KEY, KeyEvent.VK_UP);
      m_keys.addKey(BACKWARDS_KEY, KeyEvent.VK_DOWN);
//
//      {
//         LoDModelBuilder mb = new LoDModelBuilder(1);
//
//         LoDModelBuilder.MeshSet ms = mb.createCone(1, 0.5, 1, true, true);
//
//         mb.insertMeshSet(ms, 0xffa08060, new XYZ(0, 0, 0), 0, Math.PI / 4);
//
//         LoDModel m = mb.makeModel();
//
//         Static o = new Static(m, m_player.getEye().plus(new XYZ(2, 0, 0)));
//         m_level.addObject(o);
//      }
//
//      {
//         LoDModelBuilder mb = new LoDModelBuilder(1);
//
//         LoDModelBuilder.MeshSet ms = mb.createCylinder(0.5, 1, true, true);
//
//         mb.insertMeshSet(ms, 0xffa08060, new XYZ(0, 0, 0), 0, Math.PI / 4);
//
//         LoDModel m = mb.makeModel();
//
//         Static o = new Static(m, m_player.getEye().plus(new XYZ(0, 2, 0)), 0.01);
//         m_level.addObject(o);
//      }
//
//      {
//         LoDModelBuilder mb = new LoDModelBuilder(1);
//
//         LoDModelBuilder.MeshSet ms = mb.createSphere(0.5, -0.25, 0.4, true, true);
//
//         mb.insertMeshSet(ms, 0xffa08060, new XYZ(0, 0, 0), 0, Math.PI / 4);
//
//         LoDModel m = mb.makeModel();
//
//         Static o = new Static(m, m_player.getEye().plus(new XYZ(0, -2, 0)), 0.01);
//         m_level.addObject(o);
//      }
//
//      {
//         LoDModelBuilder mb = new LoDModelBuilder(1);
//
//         LoDModelBuilder.MeshSet ms = mb.createCuboid(0.5, 0.25, 0.125);
//
//         mb.insertMeshSet(ms, 0xffa08060, new XYZ(0, 0, 0), 0, Math.PI / 4);
//
//         LoDModel m = mb.makeModel();
//
//         Static o = new Static(m, m_player.getEye().plus(new XYZ(-2, 0, 0)), 0.01);
//         m_level.addObject(o);
//      }
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
      noStroke();
      fill(n.getColour());
      ellipse((float) n.getPos().X, (float) n.getPos().Y,
            (float) n.getRad(), (float) n.getRad());
   }

   void drawLabel(INode n)
   {
      fill(255, 255, 255);
      text(n.getName(),
            (float) n.getPos().X, (float) n.getPos().Y);
   }

   void drawConnections(INode n, boolean show_arrows)
   {
      // in connections are drawn by the other node...
      for(DirectedEdge e : n.getOutConnections())
      {
         stroke(e.GetColour());
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
      background(0xff201010);

      stroke(0xff808080);
      strokeWidth(2, true);

      stroke(0xff808080);
      fill(180, 120, 120);

      Box bounds = level.getBounds();

      beginShape();
      vertex((float)bounds.Max.X + 1000, (float)bounds.Max.Y + 1000);
      vertex((float)bounds.Min.X - 1000, (float)bounds.Max.Y + 1000);
      vertex((float)bounds.Min.X - 1000, (float)bounds.Min.Y - 1000);
      vertex((float)bounds.Max.X + 1000, (float)bounds.Min.Y - 1000);

      level.getWallLoops().forEach(this::drawWallLoop);
      endShape(CLOSE);

      stroke(0xfff0f0f0);
      strokeWidth(2, true);

      for(Wall w : level.getVisibleWalls(visibility_pos))
      {
         line(w.Start, w.End);
//         line(w.midPoint(), w.midPoint().plus(w.Normal.multiply(10)));
      }

      for(IDrawable id : level.getObjects())
      {
         id.draw2D(this);
      }
   }

   void drawWallLoop(WallLoop wl)
   {
      beginContour();
      for(Wall w : wl)
      {
         vertex((float)w.Start.X, (float)w.Start.Y);
      }
      endContour();
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
   public void translate(XY offset)
   {
      translate((float)offset.X, (float)offset.Y);
   }

   @Override
   public void translate(XYZ offset)
   {
      translate((float)offset.X, (float)offset.Y, (float)offset.Z);
   }

   @Override
   public void rotateX(double ori)
   {
      rotateX((float)ori);
   }

   @Override
   public void rotateY(double ori)
   {
      rotateY((float)ori);
   }

   @Override
   public void rotateZ(double ori)
   {
      rotateZ((float)ori);
   }

   @Override
   public void pushTransform()
   {
      pushMatrix();
   }

   @Override
   public void popTransform()
   {
      popMatrix();
   }

   @Override
   public void line(XY from, XY to)
   {
      line((float)from.X, (float)from.Y, (float)to.X, (float)to.Y);
   }

   @Override
   public void text(String text, XY pos)
   {
      text(text, (float)pos.X, (float)pos.Y);
   }

   @Override
   public void circle(XY pos, double rad)
   {
      ellipse((float)pos.X, (float)pos.Y, (float)rad, (float)rad);
   }

   @Override
   public void fill(int red, int green, int blue)
   {
      super.fill(red, green, blue);
   }

   @Override
   public double getScale()
   {
      return m_scale;
   }

   @Override
   public void beginTriangles()
   {
      beginShape(TRIANGLES);
   }

   @Override
   public void triangle(XYZ p1, XYZ p2, XYZ p3, XYZ n1, XYZ n2, XYZ n3)
   {
      normal(n1);
      vertex(p1);
      normal(n2);
      vertex(p2);
      normal(n3);
      vertex(p3);
   }

   @Override
   public void endTriangles()
   {
      endShape();
   }

   @Override
   public void pointLight(int r, int g, int b, XYZ pos)
   {
      pointLight(r, g, b, (float)pos.X, (float)pos.Y, (float)pos.Z);
   }

   private void normal(XYZ n)
   {
      assert n.isUnit();
      normal((float)n.X, (float)n.Y, (float)n.Z);
   }

   public void vertex(XYZ v)
   {
      vertex((float)v.X, (float)v.Y, (float)v.Z);
   }

   @Override
   public void perspective(double angleOfView, double aspectRatio, double nearDistance, double farDistance)
   {
      perspective((float)angleOfView, (float)aspectRatio, (float)nearDistance, (float)farDistance);
   }

   void scaleTo(Box b)
   {
      double shorter_display = Math.min(width, height);

      double larger_box = Math.max(b.DX(), b.DY());

      larger_box *= 1.1;

      translate(width / 2, height / 2);

      scale((float)(shorter_display / larger_box));

      translate((float)-b.Min.X,(float)-b.Min.Y);
   }

   public void clear(@SuppressWarnings("SameParameterValue") int c)
   {
      background(c);
   }

   @Override
   public void stroke(int red, int green, int blue)
   {
      super.stroke(red, green, blue);
   }

   @Override
   public void strokeWidth(double d, boolean scaling)
   {
      if (scaling)
         d /= m_scale;

      strokeWeight((float)d);
   }

   // UI data
   private boolean m_auto_scale = true;
   private boolean m_labels = true;
   private boolean m_arrows = false;

   private double m_off_x = 0.0;
   private double m_off_y = 0.0;
   private double m_scale = 1.0;

   private boolean m_map = false;

   private LevelGeneratorConfiguration m_config;
   private LevelGenerator m_generator;
   private Level m_level;

   boolean m_playing = false;
   boolean m_complete = false;

   private Player m_player;

   private boolean m_rotating = true;

   private KeyTracker m_keys;

   private double m_decaying_ori = 0;

   private final static int LEFT_KEY = 0;
   private final static int RIGHT_KEY = 1;
   private final static int FORWARDS_KEY = 2;
   private final static int BACKWARDS_KEY = 3;

   private final IoCContainer m_ioc_container;
}
