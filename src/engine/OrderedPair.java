package engine;

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
      int first_hash = First != null ? First.hashCode() * 31 : 0;
      int second_hash = Second != null ? Second.hashCode() : 0;
      return first_hash ^ second_hash;
   }

   @Override
   public boolean equals(Object o)
   {
      if (!(o instanceof OrderedPair))
         return false;

      OrderedPair op = (OrderedPair)o;

      return (First.equals(op.First) && Second.equals(op.Second));
   }

   public OrderedPair<U,T> swap()
   {
      return new OrderedPair<>(Second, First);
   }
}
