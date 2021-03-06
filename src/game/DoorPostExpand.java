package game;

import engine.graph.INode;
import engine.graph.Template;

// a very simple approach to doors, just renaming door/key pairs to match one-another
public class DoorPostExpand implements Template.IPostExpand
{
   @Override
   public void AfterExpand(INode n)
   {
      if (n.getName().equals("key"))
      {
         n.setName("Key: " + m_door_count);
         n.setColour(s_colours[m_door_count % 3]);
      }
      else if (n.getName().equals("door"))
      {
         n.setName("Door: " + m_door_count);
         n.setColour(s_colours[m_door_count % 3]);
      }
      else if (n.getName().equals("obstacle"))
      {
         n.setColour(0xff404040);
      }
   }

   @Override
   public void Done()
   {
      m_door_count++;
   }

   private int m_door_count = 1;

   private static final int[] s_colours = new int[] { 0xff800000, 0xff008000, 0xff000080 };
}
