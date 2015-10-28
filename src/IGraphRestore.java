import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

interface IGraphRestore
{
   boolean Restore();
   boolean IsRestored();
   // erase the ability to restore this restore
   boolean Commit();
}
