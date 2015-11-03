public class TemplateStore1 extends TemplateStore
{
   public TemplateStore1()
   {
      {
         TemplateBuilder tb = new TemplateBuilder("Extend", "e");
         tb.AddNode(Template.NodeType.In, "i");
         tb.AddNode(Template.NodeType.Out, "o");
         tb.AddNode(Template.NodeType.Internal, "e1", false, "<target>", "i", null, "e", 55f);
         tb.AddNode(Template.NodeType.Internal, "e2", false, "<target>", "o", null, "e", 55f);

         tb.Connect("i", "e1", 90, 110, 10);
         tb.Connect("e1", "e2", 90, 110, 10);
         tb.Connect("e2", "o", 90, 110, 10);

         AddTemplate(tb.Build());
      }

      {
         TemplateBuilder tb = new TemplateBuilder("Tee", "e");
         tb.AddNode(Template.NodeType.In, "i");
         tb.AddNode(Template.NodeType.Out, "o");
         tb.AddNode(Template.NodeType.Internal, "j", false, "<target>", null, null, "", 55f);
         tb.AddNode(Template.NodeType.Internal, "side", true, "<target>", null, null, "e", 55f);

         tb.Connect("i", "j", 90, 110, 10);
         tb.Connect("j", "o", 90, 110, 10);
         // just try a different distance
         tb.Connect("j", "side", 140, 150, 10);

         AddTemplate(tb.Build());
      }

      {
         TemplateBuilder tb = new TemplateBuilder("Space", "e");
         tb.AddNode(Template.NodeType.In, "i");
         tb.AddNode(Template.NodeType.Out, "o");
         tb.AddNode(Template.NodeType.Internal, "j", false, "<target>", null, null, "", 55f);
         tb.AddNode(Template.NodeType.Internal, "void", true, "<target>", null, null, "", 200f, 0x80808080);

         tb.Connect("i", "j", 90, 110, 10);
         tb.Connect("j", "o", 90, 110, 10);
         // just try a different distance
         tb.Connect("j", "void", 140, 150, 10,  0x80808080);

         AddTemplate(tb.Build());
      }

      {
         TemplateBuilder tb = new TemplateBuilder("Split", "e");
         tb.AddNode(Template.NodeType.In, "i");
         tb.AddNode(Template.NodeType.Out, "o");
         tb.AddNode(Template.NodeType.Internal, "e1", true, "<target>", null, null, "e", 55f);
         tb.AddNode(Template.NodeType.Internal, "e2", true, "<target>", null, null, "e", 55f);

         tb.Connect("i", "e1", 70, 90, 15);
         tb.Connect("i", "e2", 70, 90, 15);
         tb.Connect("e1", "o", 70, 90, 15);
         tb.Connect("e2", "o", 70, 90, 15);

         AddTemplate(tb.Build());
      }

      {
         TemplateBuilder tb = new TemplateBuilder("EndLoop", "e");
         tb.AddNode(Template.NodeType.In, "i");
         tb.AddNode(Template.NodeType.Internal, "j", true, "<target>", null, null, "", 55f);
         // "-i" means in the opposite direction to "i" :-)
         tb.AddNode(Template.NodeType.Internal, "e1", true, "<target>", null, "i", "e", 100f);
         tb.AddNode(Template.NodeType.Internal, "e2", true, "<target>", null, "i", "e", 75f);

         tb.Connect("i", "j", 90, 110, 10);
         tb.Connect("j", "e1", 90, 110, 10);
         tb.Connect("j", "e2", 90, 110, 10);
         tb.Connect("e1", "e2", 90, 110, 10);

         AddTemplate(tb.Build());
      }
   }
}
