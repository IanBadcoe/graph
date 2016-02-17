package engine.brep;

import java.util.ArrayList;

public class LoopSet extends ArrayList<Loop>
{
   public LoopSet()
   {
   }

   // convenience ctor
   LoopSet(Loop loop)
   {
      add(loop);
   }

   @Override
   public int hashCode()
   {
      int ret = 0;

      for(Loop l : this)
      {
         ret = ret * 3
               ^ l.hashCode();
      }

      return ret;
   }

   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof LoopSet))
         return false;

      LoopSet lso = (LoopSet)o;

      if (size() != lso.size())
         return false;

      for(int i = 0; i < size(); i++)
      {
         if (!get(i).equals(lso.get(i)))
            return false;
      }

      return true;
   }
}
