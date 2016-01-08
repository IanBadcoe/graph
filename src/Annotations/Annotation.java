package Annotations;

import engine.IDrawable;

@SuppressWarnings("WeakerAccess")
public abstract class Annotation implements IDrawable
{
   @SuppressWarnings("WeakerAccess")
   protected Annotation(int colour)
   {
      this.m_colour = colour;
   }

   final int m_colour;
}
