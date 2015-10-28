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
   }

   @Override
   public void setup()
   {
      ellipseMode(RADIUS);

      m_graph = MakeSeed();
      m_expander = new Expander(m_graph,
            new ExpandToSizeStepper(m_graph, m_reqSize, m_templates,
                  new Random(88)));
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

            m_lay_out_running = ret.Status == Expander.ExpandStatus.Iterate;

            print(ret.Log, "\n");
         }
      }

      double range = min(width, height);

      if (m_auto_scale)
         AutoScale(m_graph, range * 0.05, range * 0.95);

      translate((float)(range * 0.05), (float)(range * 0.05));

      scale((float)m_scale);

      translate((float)m_off_x, (float)m_off_y);

      Util.DrawGraph(this, m_graph, m_labels, m_arrows);

      if (m_show_notes)
      {
         for(Annotation a : m_notes)
         {
            a.Draw(this);
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

      start.SetPos(new XY(-100, 0));
      expander.SetPos(new XY(0, 0));
      end.SetPos(new XY(0, 100));

      ret.Connect(start, expander, 90, 110, 10);
      ret.Connect(expander, end, 90, 110, 10);

      //not expandable, which simplifies expansion as start won't need replacing
      return ret;
   }

   Graph m_graph;

   TemplateStore m_templates = new TemplateStore1();

   int m_reqSize = 100;

   Expander m_expander;

   boolean m_lay_out_running = true;

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
}
