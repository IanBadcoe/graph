package engine;

public interface IDrawable
{
   void draw2D(IDraw draw);
   void draw3D(IDraw draw, XYZ eye);
}
