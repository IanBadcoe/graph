@SuppressWarnings("WeakerAccess")
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

   void Draw()
   {
      Main.Stroke(Red, Green, Blue);
      Main.Fill(Red, Green, Blue);

      Main.Line(P1, P2);

      XY mid = P1.plus(P2).divide(2);

      Main.Text(Text, mid);
   }
}