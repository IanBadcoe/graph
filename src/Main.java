import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Random;

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

      m_graph = MakeSeed();
      m_expander = new Expander(m_graph,
            new ExpandToSizeStepper(m_graph, m_reqSize, m_templates,
                  new Random(85)));
   }

   @Override
   public void keyPressed()
   {
      if (key == ' ')
         m_step = true;

      if (key == 'g')
         m_go = !m_go;

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

   @Override
   public void draw()
   {
      background(128);
      strokeWeight(0.0f);
//      textSize(0.01f);

      Expander.ExpandRet ret;

      for(int i = 0; i < 1000; i++)
      {
         if ((m_step || m_go) && m_lay_out_running)
         {
            m_step = false;

            ret = m_expander.Step();

            m_lay_out_running = !ret.Complete;

//            print(ret.Log, "\n");
         }
         else
         {
            break;
         }
      }

      if (!m_lay_out_running && !m_level_generated)
      {
         m_level = new Level(m_graph);

         m_level.generateGeometry();

         m_level_generated = true;

//         m_go = false;
      }

      if ((m_step || m_go) && m_level_generated && !m_unions_done)
      {
         m_unions_done = !m_level.unionOne(m_union_random);

         m_step = false;
      }

      double range = min(width, height);

      if (m_auto_scale)
         autoScale(m_graph, range * 0.05, range * 0.95);

      translate((float)(range * 0.05), (float)(range * 0.05));

      scale((float)m_scale);

      translate((float)m_off_x, (float)m_off_y);

      drawGraph(m_graph, m_labels, !m_unions_done, true, m_arrows);

      if (m_level != null)
      {
         drawLevel(m_level);
      }

      if (m_show_notes)
      {
         m_notes.forEach(Annotation::Draw);
      }

      saveFrame("Frame####.jpg");
   }

   private void autoScale(Graph g, double low, double high)
   {
      Box b = g.XYBounds();

      double sx = (high - low) / b.DX();
      double sy = (high - low) / b.DY();

      double smaller_scale = Math.min(sx, sy);

      m_off_x = -b.Min.X;
      m_off_y = -b.Min.Y;
      m_scale = smaller_scale;
   }

   private Graph MakeSeed()
   {
      Graph ret = new Graph();
      INode start = ret.AddNode("Start", "<", "Seed", 55f);
      INode expander = ret.AddNode("Expander", "e", "Seed", 55f);
      INode end = ret.AddNode("End", ">", "Seed", 55f);

      start.setPos(new XY(-100, 0));
      expander.setPos(new XY(0, 0));
      end.setPos(new XY(0, 100));

      ret.Connect(start, expander, 90, 110, 10);
      ret.Connect(expander, end, 90, 110, 10);

      //not expandable, which simplifies expansion as start won't need replacing
      return ret;
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
         g.AllGraphNodes().forEach(Main::drawNode);
      }


      if (show_connections)
      {
         for (INode n : g.AllGraphNodes())
         {
            drawConnections(n, show_arrows);
         }
      }

      if (show_labels)
      {
         g.AllGraphNodes().forEach(Main::drawLabel);
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

   static void drawLevel(Level level)
   {
      s_app.stroke(0xffffffff);
      s_app.strokeWeight(1);
      level.getLoops().forEach(Main::drawLoop);

      for(Loop l : level.getLevel())
      {
         s_app.stroke(240);
         drawLoop(l);
      }
   }

   static void drawLoop(Loop l)
   {
      ArrayList<XY> pnts = l.facet(10);

      drawLoopPoints(pnts);
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

      s_app.scale((float)(shorter_display / larger_box));

      s_app.translate((float)-b.Min.X,(float)-b.Min.Y);
   }

   static void clear(int c)
   {
      s_app.background(c);
   }

   public static void strokeWidth(double d)
   {
      s_app.strokeWeight((float)d);
   }

   private static PApplet s_app;

   private Graph m_graph;

   private final TemplateStore m_templates = new TemplateStore1();

   @SuppressWarnings("FieldCanBeLocal")
   private final int m_reqSize = 30;

   private Expander m_expander;

   private boolean m_lay_out_running = true;
   private boolean m_level_generated = false;
   private boolean m_unions_done = false;

   // UI data
   private boolean m_step = false;
   private boolean m_go = true;
   private boolean m_auto_scale = true;
   private boolean m_labels = true;
   private boolean m_arrows = false;
   private boolean m_show_notes = true;

   @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
   private final ArrayList<Annotation> m_notes = new ArrayList<>();

   private double m_off_x = 0.0;
   private double m_off_y = 0.0;
   private double m_scale = 1.0;

   private Level m_level;

   private final Random m_union_random = new Random(1);
}
