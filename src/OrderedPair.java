class OrderedPair<T, U>
{
   final T First;
   final U Second;

   OrderedPair(T first, U second)
   {
      First = first;
      Second = second;
   }

   @Override
   public int hashCode()
   {
      return First.hashCode() * 31 ^ Second.hashCode();
   }

   @Override
   public boolean equals(Object o)
   {
      if (!(o instanceof OrderedPair))
         return false;

      OrderedPair op = (OrderedPair)o;

      return (First.equals(op.First) && Second.equals(op.Second));
   }
}
