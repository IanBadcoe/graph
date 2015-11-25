import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

public class LevelTest
{
   private final HashSet<Object> m_seen_things = new HashSet<>();

   private class ThingNoticer extends GeomLayout
   {
      ThingNoticer(INode n)
      {
         m_seen_things.add(n);
      }

      ThingNoticer(DirectedEdge de)
      {
         m_seen_things.add(de);
      }

      @Override
      Loop makeBaseGeometry()
      {
         return null;
      }

      @Override
      LoopSet makeDetailGeometry()
      {
         return null;
      }
   }

   @Test
   public void testGenerateGeometry() throws Exception
   {
      m_seen_things.clear();

      Graph g = new Graph();

      INode n1 = g.addNode("", "", "", 0, ThingNoticer::new);
      INode n2 = g.addNode("", "", "", 0, ThingNoticer::new);

      DirectedEdge de = g.connect(n1, n2, 0, 0, 0, ThingNoticer::new);

      Level l = new Level(g, 10, 5);

      l.generateGeometry();

      assertEquals(3, m_seen_things.size());
      assertTrue(m_seen_things.contains(n1));
      assertTrue(m_seen_things.contains(n2));
      assertTrue(m_seen_things.contains(de));
   }

   @Test
   public void testUnionOne() throws Exception
   {

   }

   @Test
   public void testGetWallLoops() throws Exception
   {

   }

   @Test
   public void testGetBounds() throws Exception
   {

   }

   @Test
   public void testNearestWall() throws Exception
   {

   }
}