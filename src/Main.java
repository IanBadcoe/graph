import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Random;

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
            (a, b, c, d) -> new TryAllTemplatesOnOneNodeStepper(a, b, c, d));
      TryAllTemplatesOnOneNodeStepper.SetChildFactory(
            (a, b, c, d) -> new TryTemplateExpandStepper(a, b, c, d));
      ExpandToSizeStepper.SetChildFactory(
            (a, b, c) -> new TryAllNodesExpandStepper(a, b, c));
      EdgeAdjusterStepper.SetChildFactory(
            (a, b, c, d) -> new RelaxerStepper(a, b, c, d));
      TryTemplateExpandStepper.SetRelaxerFactory(
            (a, b, c, d) -> new RelaxerStepper(a, b, c, d));
      TryTemplateExpandStepper.SetAdjusterFactory(
            (a, b) -> new EdgeAdjusterStepper(a, b));
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

      Expander.ExpandRet ret = null;

      for(int i = 0; i < 1000; i++)
      {
         if ((m_step || m_go) && m_lay_out_running)
         {
            m_step = false;

            ret = m_expander.Step();

            m_lay_out_running = !ret.Complete;

//            print(ret.Log, "\n");
         }
      }

      if (!m_lay_out_running && !m_level_generated)
      {
         m_level = new Level(m_graph);

         m_level.generateGeometry();

         m_level_generated = true;
      }

      double range = min(width, height);

      if (m_auto_scale)
         AutoScale(m_graph, range * 0.05, range * 0.95);

      translate((float)(range * 0.05), (float)(range * 0.05));

      scale((float)m_scale);

      translate((float)m_off_x, (float)m_off_y);

      DrawGraph(m_graph, m_labels, m_arrows);

      if (m_level != null)
      {
         DrawLevel(m_level);
      }

      if (m_show_notes)
      {
         for(Annotation a : m_notes)
         {
            a.Draw();
         }
      }
   }

   void AutoScale(Graph g, double low, double high)
   {
      Box b = g.XYBounds();

      double sx = (high - low) / b.DX();
      double sy = (high - low) / b.DY();

      double smaller_scale = Math.min(sx, sy);

      m_off_x = -b.Min.X;
      m_off_y = -b.Min.Y;
      m_scale = smaller_scale;
   }

   Graph MakeSeed()
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


   static void Line(XY from, XY to)
   {
      s_app.line((float)from.X, (float)from.Y, (float)to.X, (float)to.Y);
   }

   static void Text(String text, XY pos)
   {
      s_app.text(text, (float)pos.X, (float)pos.Y);
   }

   static void DrawGraph(Graph g, boolean show_labels, boolean show_arrows)
   {
      for (INode n : g.AllGraphNodes())
      {
         DrawNode(n);
      }

      for (INode n : g.AllGraphNodes())
      {
         DrawConnections(n, show_arrows);
      }

      if (show_labels)
      {
         for (INode n : g.AllGraphNodes())
         {
            DrawLabel(n);
         }
      }
   }

   private static void DrawNode(INode n)
   {
      s_app.noStroke();
      s_app.fill(n.getColour());
      s_app.ellipse((float) n.getPos().X, (float) n.getPos().Y,
            (float) n.getRad(), (float) n.getRad());
   }

   private static void DrawLabel(INode n)
   {
      s_app.fill(255, 255, 255);
      s_app.text(n.getName(),
            (float) n.getPos().X, (float) n.getPos().Y);
   }

   private static void DrawConnections(INode n, boolean show_arrows)
   {
      // in connections are drawn by the other node...
      for(DirectedEdge e : n.getOutConnections())
      {
         s_app.stroke(e.GetColour());
         s_app.strokeWeight((float)(e.Width * 1.75));
         Line(e.Start.getPos(), e.End.getPos());

         if (show_arrows)
         {
            XY d = e.End.getPos().Minus(e.Start.getPos());
            d = d.Divide(10);

            XY rot = new XY(-d.Y, d.X);

            Line(e.End.getPos(), e.End.getPos().Minus(d).Minus(rot));
            Line(e.End.getPos(), e.End.getPos().Minus(d).Plus(rot));
         }
      }
   }

   private static void DrawLevel(Level level)
   {
      s_app.stroke(0xffffffff);
      s_app.strokeWeight(1);
      for(Loop l : level.getLoops())
      {
         DrawLoop(l);
      }
   }

   private static void DrawLoop(Loop l)
   {
      double r = l.paramRange();

      XY prev = l.computePos(0.0);

      for(double p = r / 20; p < r; p += r / 20)
      {
         XY curr = l.computePos(p);

         Line(prev, curr);

         prev = curr;
      }

      Line(prev, l.computePos(r));
   }

   static void Stroke(int red, int green, int blue)
   {
      s_app.stroke(red, green, blue);
   }

   static void Fill(int red, int green, int blue)
   {
      s_app.fill(red, green, blue);
   }

   static PApplet s_app;

   Graph m_graph;

   TemplateStore m_templates = new TemplateStore1();

   int m_reqSize = 30;

   Expander m_expander;

   boolean m_lay_out_running = true;
   boolean m_level_generated = false;

   // UI data
   boolean m_step = false;
   boolean m_go = false;
   boolean m_auto_scale = true;
   boolean m_labels = true;
   boolean m_arrows = true;
   boolean m_show_notes = true;

   ArrayList<Annotation> m_notes = new ArrayList<>();

   double m_off_x = 0.0;
   double m_off_y = 0.0;
   double m_scale = 1.0;

   Level m_level;
}
