package engine;

class TemplateStore1 extends TemplateStore
{
   public TemplateStore1()
   {
      {
         TemplateBuilder tb = new TemplateBuilder("Extend", "e");
         tb.AddNode(Template.NodeType.In, "i");
         tb.AddNode(Template.NodeType.Out, "o");
         tb.AddNode(Template.NodeType.Internal, "e1", false, "<target>", "i", null, "e", 55f);
         tb.AddNode(Template.NodeType.Internal, "e2", false, "<target>", "o", null, "e", 55f);

         tb.Connect("i", "e1", 120, 120, 10);
         tb.Connect("e1", "e2", 120, 120, 10);
         tb.Connect("e2", "o", 120, 120, 10);

         AddTemplate(tb.Build());
      }

      {
         TemplateBuilder tb = new TemplateBuilder("Tee", "e");
         tb.AddNode(Template.NodeType.In, "i");
         tb.AddNode(Template.NodeType.Out, "o");
         tb.AddNode(Template.NodeType.Internal, "j", false, "<target>", null, null, "", 20f,
               0xff808040, a -> new CircularGeomLayout(a.getPos(), 10));
         tb.AddNode(Template.NodeType.Internal, "side", true, "<target>", null, null, "e", 55f);

         tb.Connect("i", "j", 120, 120, 10);
         tb.Connect("j", "o", 120, 120, 10);
         // just try a different distance
         tb.Connect("j", "side", 120, 120, 10);

         AddTemplate(tb.Build());
      }

//      {
//         engine.TemplateBuilder tb = new engine.TemplateBuilder("Space", "");
//         tb.addNode(engine.Template.NodeType.In, "i");
//         tb.addNode(engine.Template.NodeType.Out, "o");
//         tb.addNode(engine.Template.NodeType.Internal, "j", false, "<target>", null, null, "", 55f);
//         tb.addNode(engine.Template.NodeType.Internal, "void", true, "<target>", null, null, "", 200f, 0x80808080);
//
//         tb.connect("i", "j", 90, 110, 10);
//         tb.connect("j", "o", 90, 110, 10);
//         // just try a different distance
//         tb.connect("j", "void", 140, 150, 10,  0x80808080);
//
//         AddTemplate(tb.Build());
//      }

//      {
//         engine.TemplateBuilder tb = new engine.TemplateBuilder("Rotunda", "e");
//         tb.addNode(engine.Template.NodeType.In, "i");
//         tb.addNode(engine.Template.NodeType.Out, "o1");
//         tb.addNode(engine.Template.NodeType.Out, "o2");
//         tb.addNode(engine.Template.NodeType.Internal, "ji", false, "<target>", "i", null, "", 55f);
//         tb.addNode(engine.Template.NodeType.Internal, "jo1", false, "<target>", "o1", null, "", 55f);
//         tb.addNode(engine.Template.NodeType.Internal, "jo2", false, "<target>", "o2", null, "", 55f);
//         tb.addNode(engine.Template.NodeType.Internal, "eio1", false, "ji", "jo1", null, "e", 20f);
//         tb.addNode(engine.Template.NodeType.Internal, "eio2", false, "ji", "jo2", null, "e", 20f);
//         tb.addNode(engine.Template.NodeType.Internal, "eo1o2", false, "jo2", "jo1", null, "e", 20f);
//         tb.addNode(engine.Template.NodeType.Internal, "void", false, "<target>", null, null, "", 100f, 0x80808080);
//
//         tb.connect("i", "ji", 90, 110, 10);
//         tb.connect("o1", "jo1", 90, 110, 10);
//         tb.connect("o2", "jo2", 90, 110, 10);
//         tb.connect("ji", "eio1", 90, 110, 10);
//         tb.connect("eio1", "jo1", 90, 110, 10);
//         tb.connect("jo1", "eo1o2", 90, 110, 10);
//         tb.connect("eo1o2", "jo2", 90, 110, 10);
//         tb.connect("jo2", "eio2", 90, 110, 10);
//         tb.connect("eio2", "ji", 90, 110, 10);
//
//         AddTemplate(tb.Build());
//      }

//      {
//         engine.TemplateBuilder tb = new engine.TemplateBuilder("Split", "e");
//         tb.addNode(engine.Template.NodeType.In, "i");
//         tb.addNode(engine.Template.NodeType.Out, "o");
//         tb.addNode(engine.Template.NodeType.Internal, "e1", true, "<target>", null, null, "e", 55f);
//         tb.addNode(engine.Template.NodeType.Internal, "e2", true, "<target>", null, null, "e", 55f);
//
//         tb.connect("i", "e1", 70, 90, 15);
//         tb.connect("i", "e2", 70, 90, 15);
//         tb.connect("e1", "o", 70, 90, 15);
//         tb.connect("e2", "o", 70, 90, 15);
//
//         AddTemplate(tb.Build());
//      }

      {
         DoorPostExpand dh = new DoorPostExpand();

         TemplateBuilder tb = new TemplateBuilder("Door", "e", dh);
         tb.AddNode(Template.NodeType.In, "i");
         tb.AddNode(Template.NodeType.Out, "o");
         tb.AddNode(Template.NodeType.Internal, "j", false, "<target>", null, null, "e",  20f,
               0xff808040, a -> new CircularGeomLayout(a.getPos(), 10));
         tb.AddNode(Template.NodeType.Internal, "e", true, "<target>", null, null, "e", 55f);
         tb.AddNode(Template.NodeType.Internal, "obstacle", true, "e", null, null, "", 55f);
         tb.AddNode(Template.NodeType.Internal, "key", true, "obstacle", null, null, "", 30f);
         tb.AddNode(Template.NodeType.Internal, "door", false, "<target>", "o", null, "", 30f);

         tb.Connect("i", "j", 120, 120, 10);
         tb.Connect("j", "e", 120, 120, 10);
         tb.Connect("e", "obstacle", 120, 120, 10);
         tb.Connect("obstacle", "key", 70, 90, 10);
         tb.Connect("j", "door", 120, 120, 10);
         tb.Connect("door", "o", 70, 90, 10);

         AddTemplate(tb.Build());
      }

//      {
//         engine.TemplateBuilder tb = new engine.TemplateBuilder("Cluster", "e");
//         tb.addNode(engine.Template.NodeType.In, "i");
//         tb.addNode(engine.Template.NodeType.Out, "o");
//         tb.addNode(engine.Template.NodeType.Internal, "a", false, "<target>", "i", null, "", 55f);
//         tb.addNode(engine.Template.NodeType.Internal, "b", false, "<target>", "o", null, "", 55f);
//         tb.addNode(engine.Template.NodeType.Internal, "c", true, "a", "b", null, "", 55f);
//
//         tb.connect("i", "a", 70, 90, 10);
//         tb.connect("a", "b", 70, 90, 10);
//         tb.connect("b", "o", 70, 90, 10);
//         tb.connect("a", "c", 70, 90, 10);
//         tb.connect("c", "b", 70, 90, 10);
//
//         AddTemplate(tb.Build());
//      }

//      {
//         engine.TemplateBuilder tb = new engine.TemplateBuilder("EndLoop", "e");
//         tb.addNode(engine.Template.NodeType.In, "i");
//         tb.addNode(engine.Template.NodeType.Internal, "j", true, "<target>", null, null, "", 55f);
//         // "-i" means in the opposite direction to "i" :-)
//         tb.addNode(engine.Template.NodeType.Internal, "e1", true, "<target>", null, "i", "e", 100f);
//         tb.addNode(engine.Template.NodeType.Internal, "e2", true, "<target>", null, "i", "e", 75f);
//
//         tb.connect("i", "j", 90, 110, 10);
//         tb.connect("j", "e1", 90, 110, 10);
//         tb.connect("j", "e2", 90, 110, 10);
//         tb.connect("e1", "e2", 90, 110, 10);
//
//         AddTemplate(tb.Build());
//      }


      {
         DoorPostExpand dh = new DoorPostExpand();

         TemplateBuilder tb = new TemplateBuilder("Rotunda", "", dh);
         tb.AddNode(Template.NodeType.In, "i");
         tb.AddNode(Template.NodeType.Internal, "rotunda", false, "<target>",
               null, null, "e", 70f, 0xff802080,
               CircularPillarGeomLayout::createFromNode);

         tb.Connect("i", "rotunda", 120, 120, 10);

         AddTemplate(tb.Build());
      }
   }
}
