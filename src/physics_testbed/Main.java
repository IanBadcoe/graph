package physics_testbed;

import engine.*;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings("WeakerAccess")
public class Main extends PApplet
{
   public static void main(String[] args) {
      PApplet.main("physics_testbed.Main", args);
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
   }

   @Override
   public void setup()
   {
      ellipseMode(RADIUS);

      UnionHelper uh = new UnionHelper();
      GeomLayout rect
            = new RectangularGeomLayout(new XY(5, height / 2 - 55), new XY(width - 5, height / 2 - 55), height / 2 - 60);
      Loop l = rect.makeBaseGeometry();

      uh.addBaseLoop(l);

      uh.unionOne(new Random(1));

      m_level = uh.makeLevel(20, 10);

      m_sim = new PhysicalSimulator(m_level);

      {
         PhysicsTestObject pto = new PhysicsTestObject(100, 100, 10, 0.3);
         pto.setPosition(new XY(width / 2, 100));
         // gravity
         pto.applyForceRelative(new XY(0, 10), new XY(0, 0));
         // kick it off centre to give some spin...
         pto.applyImpulseRelative(new XY(0, 100), new XY(50, 0));

         m_sim.addMovable(pto);
      }

      {
         PhysicsTestObject pto = new PhysicsTestObject(100, 100, 10, 0.3);
         pto.setPosition(new XY(width / 2, 300));
         // gravity
         pto.applyForceRelative(new XY(0, 10), new XY(0, 0));
         // kick it off centre to give some spin...
         pto.applyImpulseRelative(new XY(0, 110), new XY(-50, 0));

         m_sim.addMovable(pto);
      }
   }

   @Override
   public void keyPressed()
   {
   }

   @Override
   public void draw()
   {
      m_sim.step(0.1);

      drawLevel(m_level, null);
      m_sim.getMovables().forEach(x -> drawObject((PhysicsTestObject)x));
   }

   static void drawObject(PhysicsTestObject pto)
   {
      s_app.beginShape();
      for(XY pnt : pto.getTransformedCorners())
      {
         s_app.vertex((float)pnt.X, (float)pnt.Y);
      }
      s_app.endShape(CLOSE);
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

   static void drawLevel(Level level, @SuppressWarnings("SameParameterValue") XY visibility_pos)
   {
      s_app.background(0xff201010);

      s_app.stroke(0xff808080);
      s_app.strokeWeight(1);

      s_app.stroke(0xff808080);
      s_app.fill(180, 120, 120);

      Box bounds = level.getBounds();

      s_app.beginShape();
      s_app.vertex((float)bounds.Max.X + 1000, (float)bounds.Max.Y + 1000);
      s_app.vertex((float)bounds.Min.X - 1000, (float)bounds.Max.Y + 1000);
      s_app.vertex((float)bounds.Min.X - 1000, (float)bounds.Min.Y - 1000);
      s_app.vertex((float)bounds.Max.X + 1000, (float)bounds.Min.Y - 1000);

      level.getWallLoops().forEach(Main::drawWallLoop);
      s_app.endShape(CLOSE);

      s_app.stroke(0xfff0f0f0);
      s_app.strokeWeight(2);

      if (visibility_pos != null)
      {
         for (Wall w : level.getVisibleWalls(visibility_pos))
         {
            s_app.line((float) w.Start.X, (float) w.Start.Y,
                  (float) w.End.X, (float) w.End.Y);
         }
      }
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

   private Level m_level;
   @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
   private PhysicalSimulator m_sim;
}
