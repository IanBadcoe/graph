package engine;

import java.util.ArrayList;

public class KeyTracker
{
   private static class KeyData
   {
      public final int Id;
      // if we need key-codes then we add a second way to represent this and a bool to choose
      public int KeyCode;
      public boolean IsDown;

      KeyData(int id, int keyCode)
      {
         Id = id;
         KeyCode = keyCode;
         IsDown = false;
      }
   }

   public void addKey(int id, int keyCode)
   {
      assert !m_running;

      assert !knownId(id);

      m_data.add(new KeyData(id, keyCode));
   }

   private boolean knownId(int id)
   {
      return findById(id) != null;
   }

   private KeyData findById(int id)
   {
      for(KeyData kd : m_data)
      {
         if (kd.Id == id)
         {
            return kd;
         }
      }

      return null;
   }

   private KeyData findByKey(int keyCode)
   {
      for(KeyData kd : m_data)
      {
         if (kd.KeyCode == keyCode)
         {
            return kd;
         }
      }

      return null;
   }

   public void run()
   {
      m_running = true;
   }

   public void keyPressed(int keyCode)
   {
      KeyData kd = findByKey(keyCode);

      if (kd == null)
         return;

      kd.IsDown = true;
   }

   public void keyReleased(int keyCode)
   {
      KeyData kd = findByKey(keyCode);

      if (kd == null)
         return;

      kd.IsDown = false;
   }

   public boolean isPressed(int id)
   {
      KeyData kd = findById(id);

      assert kd != null;

      return kd.IsDown;
   }

   private ArrayList<KeyData> m_data = new ArrayList<>();
   private boolean m_running = false;
}
