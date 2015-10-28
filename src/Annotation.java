import processing.core.PApplet;

class Annotation
{
   final public XY P1;
   final public XY P2;
   final public int Red;
   final public int Green;
   final public int Blue;
   final public String Text;

   Annotation(XY p1, XY p2, int red, int green, int blue, String text)
   {
      P1 = p1;
      P2 = p2;
      Red = red;
      Green = green;
      Blue = blue;
      Text = text;
   }

   void Draw(PApplet app)
   {
      app.stroke(Red, Green, Blue);
      app.fill(Red, Green, Blue);

      Util.Line(app, P1, P2);

      XY mid = P1.Plus(P2).Divide(2);

      Util.Text(app, Text, mid);
   }
}