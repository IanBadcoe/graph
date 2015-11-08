// a very simple approach to doors, just renaming door/key pairs to match one-another
class DoorPostExpand implements Template.IPostExpand
{
   @Override
   public void AfterExpand(INode n)
   {
      if (n.GetName() == "key")
      {
         n.SetName("Key: " + m_door_count);
         n.SetColour(s_colours[m_door_count % 3]);
      }
      else if (n.GetName() == "door")
      {
         n.SetName("Door: " + m_door_count);
         n.SetColour(s_colours[m_door_count % 3]);
      }
      else if (n.GetName() == "obstacle")
      {
         n.SetColour(0xff404040);
      }
   }

   @Override
   public void Done()
   {
      m_door_count++;
   }

   private int m_door_count = 1;

   private static int[] s_colours = new int[] { 0xff800000, 0xff008000, 0xff000080 };
}
