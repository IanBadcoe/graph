class OrderedTriplet<T, U, V>
{
   final T First;
   final U Second;
   final V Third;

   OrderedTriplet(T first, U second, V third)
   {
      First = first;
      Second = second;
      Third = third;
   }

   @Override
   public int hashCode()
   {
      int first_hash = First != null ? First.hashCode() * 31 : 0;
      int second_hash = Second != null ? Second.hashCode() : 0;
      int third_hash = Third != null ? Third.hashCode() * 17 : 0;
      return first_hash ^ second_hash ^ third_hash;
   }

   @Override
   public boolean equals(Object o)
   {
      if (!(o instanceof OrderedTriplet))
         return false;

      OrderedTriplet op = (OrderedTriplet)o;

      return First.equals(op.First) && Second.equals(op.Second) && Third.equals(op.Third);
   }
}
