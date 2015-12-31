package engine;

public interface IDraw
{
   void line(XY from, XY to);

   void text(String text, XY pos);

   void fill(int red, int green, int blue);

   void stroke(int red, int green, int blue);
   void strokeWidth(double d);

   void circle(XY position, double radius);
}
