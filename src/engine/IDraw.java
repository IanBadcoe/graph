package engine;

public interface IDraw
{
   void line(XY from, XY to);

   void text(String text, XY pos);

   void fill(int red, int green, int blue);
   void fill(int colour);

   void stroke(int red, int green, int blue);
   void stroke(int colour);
   void strokeWidth(double d, boolean scaling);

   void circle(XY position, double radius);

   double getScale();

   void noStroke();
}
