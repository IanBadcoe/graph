import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

@SuppressWarnings("WeakerAccess")
public class TemplateStore
{
   public boolean AddTemplate(Template t)
   {
      if (Contains(t.GetName()))
         return false;

      m_templates.put(t.GetName(), t);

      return true;
   }

   public int NumTemplates()
   {
      return m_templates.size();
   }

   public Collection<Template> GetTemplatesCopy()
   {
      return new ArrayList<>(m_templates.values());
   }

   public Template FindByName(String name)
   {
      return m_templates.get(name);
   }

   public boolean Contains(String name)
   {
      return m_templates.containsKey(name);
   }

   private final HashMap<String, Template> m_templates = new HashMap<>();
}
