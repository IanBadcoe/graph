package engine;

import engine.modelling.Positioner;

public interface IDraw
{
   void translate(XY offset);
   void translate(XYZ offset);
   void rotateX(double ori);
   void rotateY(double ori);
   void rotateZ(double ori);

   void pushTransform();
   void popTransform();

   void line(XY from, XY to);
   void text(String text, XY pos);
   void circle(XY position, double radius);

   void fill(int red, int green, int blue);
   void fill(int colour);

   void clear(int colour);

   void stroke(int red, int green, int blue);
   void stroke(int colour);
   void strokeWidth(double d, boolean scaling);
   void noStroke();

   // used in 2D only...
   double getScale();

   void beginTriangles();
   void triangle(XYZ p1, XYZ p2, XYZ p3, XYZ n1, XYZ n2, XYZ n3);
   void endTriangles();

   void pointLight(int r, int g, int b, XYZ pos);

   void beginShape();

   void vertex(XYZ pos);

   void endShape();

   void perspective(double angleOfView, double aspectRatio, double nearDistance, double farDistance);

   void camera(XYZ eye, XYZ target, XYZ up);

   void position(Positioner position);
}
