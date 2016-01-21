package models;

import engine.IDraw;
import engine.XYZ;

public class Mesh
{
   // offset exact meaning depends on shape created, but is for example base of cylinder, centre of sphere
   // elevation is rotation about Y, followed by
   // orientation, which is rotation about Z
   private Mesh(XYZ[] points, XYZ[] normals, Triangle[] triangles)
   {
      Points = points;
      Normals = normals;
      Triangles = triangles;
   }

   // base of cylinder is at (0, 0, 0) facing up X
   static public Mesh createCylinder(double radius, double length, double facetingFactor,
                                     boolean capBase, boolean capTop)
   {
      // must at least be a triangular prism
      int radius_steps = (int)(Math.PI * 2 * radius / facetingFactor + 3);
      //with at least two rings of points
      int length_steps = (int)(length / facetingFactor + 2);

      int points_size = radius_steps * length_steps;
      int normals_size = points_size;
      int triangle_size = points_size * 2;

      // need one normal, one centre point
      // and one fan of radius_steps triangles for each end
      if (capBase)
      {
         normals_size++;
         points_size++;
         triangle_size += radius_steps;
      }

      if (capTop)
      {
         normals_size++;
         points_size++;
         triangle_size += radius_steps;
      }

      XYZ[] points = new XYZ[points_size];
      XYZ[] normals = new XYZ[normals_size];
      Triangle[] triangles = new Triangle[triangle_size];

      int which = 0;
      double angle_step = 2 * Math.PI / radius_steps;
      double length_step = length / (length_steps - 1);

      double length_pos = 0;

      for(int i = 0; i < length_steps; i++)
      {
         double angle = 0;

         for(int j = 0; j < radius_steps; j++)
         {
            points[which] = new XYZ(length_pos, Math.sin(angle) * radius, Math.cos(angle) * radius);
            normals[which] = new XYZ(0, Math.sin(angle), Math.cos(angle));
            which++;

            angle += angle_step;
         }

         length_pos += length_step;
      }

      int trig_which = 0;

      if (capBase)
      {
         normals[which] = new XYZ(-1, 0, 0);
         points[which] = new XYZ(0, 0, 0);

         int prev_idx = radius_steps - 1;
         for(int i = 0; i < radius_steps; i++)
         {
            triangles[trig_which] = new Triangle(
                  i, prev_idx, which,
                  which, which, which);

            trig_which++;
            prev_idx = i;
         }

         which++;
      }

      if (capTop)
      {
         normals[which] = new XYZ(1, 0, 0);
         points[which] = new XYZ(0, 0, length);

         int prev_idx = length_steps * radius_steps - 1;
         for(int i = (length_steps - 1) * radius_steps; i < length_steps * radius_steps; i++)
         {
            triangles[trig_which] = new Triangle(
                  i, prev_idx, which,
                  which, which, which);

            trig_which++;
            prev_idx = i;
         }
      }

      which = 0;
      int next_line_which = radius_steps;

      for(int i = 0; i < length_steps - 1; i++)
      {
         int prev = (i + 1) * radius_steps - 1;
         int next_line_prev = (i + 2) * radius_steps - 1;
         for(int j = 0; j < radius_steps; j++)
         {
            triangles[trig_which] = new Triangle(
                  which, prev, next_line_which,
                  which, prev, next_line_which);

            triangles[trig_which + 1] = new Triangle(
                  next_line_prev, prev, next_line_which,
                  next_line_prev, prev, next_line_which);

            trig_which += 2;

            next_line_prev = next_line_which;
            prev = which;

            which++;
            next_line_which++;
         }
      }

      return new Mesh(points, normals, triangles);
   }

   public void draw(IDraw draw)
   {
      draw.beginTriangles();

      for(Triangle t : Triangles)
      {
         draw.triangle(
               Points[t.PointIndex1],
               Normals[t.NormalIndex1],
               Points[t.PointIndex2],
               Normals[t.NormalIndex2],
               Points[t.PointIndex3],
               Normals[t.NormalIndex3]);
      }

      draw.endTriangles();
   }

   static class Triangle
   {
      private final int PointIndex1;
      private final int PointIndex2;
      private final int PointIndex3;
      private final int NormalIndex1;
      private final int NormalIndex2;
      private final int NormalIndex3;

      Triangle(int pointIndex1, int pointIndex2, int pointIndex3,
               int normalIndex1, int normalIndex2, int normalIndex3)
      {
         PointIndex1 = pointIndex1;
         PointIndex2 = pointIndex2;
         PointIndex3 = pointIndex3;
         NormalIndex1 = normalIndex1;
         NormalIndex2 = normalIndex2;
         NormalIndex3 = normalIndex3;
      }
   }

   private final XYZ[] Points;
   private final XYZ[] Normals;
   private final Triangle[] Triangles;
}
