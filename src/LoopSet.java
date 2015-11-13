import java.util.ArrayList;

class LoopSet extends ArrayList<Loop>
{
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
