package Annotations;

import engine.IDrawable;

public abstract class Annotation implements IDrawable
{
   protected Annotation(int colour)
   {
      this.m_colour = colour;
   }

   protected final int m_colour;}
