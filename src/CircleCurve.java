class CircleCurve extends Curve
{
   enum RotationDirection
   {
      Forwards,
      Reverse
   }

   final public XY Position;
   final public double Radius;
   final public RotationDirection Rotation;

   CircleCurve(XY position, double radius)
   {
      // we'll only represent whole circles like this
      // so only this exact params will mean completely cyclic
      super(0.0, 2 * Math.PI);

      Position = position;
      Radius = radius;
      Rotation = RotationDirection.Forwards;
   }

   CircleCurve(XY position, double radius,
               double start_angle, double end_angle)
   {
      // we'll only represent whole circles like this
      // so only this exact params will mean completely cyclic
      super(start_angle, end_angle);

      Position = position;
      Radius = radius;
      Rotation = RotationDirection.Forwards;
   }

   CircleCurve(XY position, double radius,
         RotationDirection rotation)
   {
      // we'll only represent whole circles like this
      // so only this exact params will mean completely cyclic
      super(0.0, 2 * Math.PI);

      Position = position;
      Radius = radius;
      Rotation = rotation;
   }

   CircleCurve(XY position, double radius,
         double start_angle, double end_angle,
         RotationDirection rotation)
   {
      // we'll only represent whole circles like this
      // so only this exact params will mean completely cyclic
      super(start_angle, end_angle);

      Position = position;
      Radius = radius;
      Rotation = rotation;

      if (Radius <= 0)
         throw new IllegalArgumentException("-ve radius");
   }

   @Override
   public XY computePos(double param)
   {
      if (Rotation == RotationDirection.Forwards)
      {
         return Position.plus(new XY(Math.sin(param), Math.cos(param)).multiply(Radius));
      }

      return Position.plus(new XY(Math.sin(-param), Math.cos(-param)).multiply(Radius));
   }

   @Override
   public int hashCode()
   {
      return super.hashCode() * 17
            ^ Position.hashCode() * 31
            ^ Double.hashCode(Radius) * 11
            ^ Rotation.hashCode();
   }

   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof CircleCurve))
         return false;

      if (!super.equals(o))
         return false;

      CircleCurve cc_o = (CircleCurve)o;

      return Position.equals(cc_o.Position)
            && Radius == cc_o.Radius
            && Rotation == cc_o.Rotation;
   }

   @Override
   public Double findParamForPoint(XY pnt, double tol)
   {
      XY relative = pnt.minus(Position);

      if (Math.abs(relative.length() - Radius) > tol)
         return null;

      double ang = Math.atan2(relative.X, relative.Y);

      if (Rotation == RotationDirection.Reverse)
      {
         ang =- ang;
      }

      // atan2 returns between -pi and + pi
      // we use 0 -> 2pi
      if (ang < 0.0) ang += 2 * Math.PI;

      if (isCyclic())
      {
         return ang;
      }

      if (!withinParams(ang, tol))
         return null;

      return ang;
   }

   @Override
   public Curve cloneWithChangedParams(double start, double end)
   {
      return new CircleCurve(Position, Radius, start, end, Rotation);
   }

   @Override
   public Box boundingBox()
   {
      // use whole circle here as the use I have for the moment doesn't need anything
      // tighter
      //
      // proper solution is to union together startPos, EndPos and whichever of
      // 0, pi/2, pi and 3pi/2 points are within param range
      return new Box(Position.plus(new XY(Radius, Radius)),
            Position.minus(new XY(Radius, Radius)));
   }

   @Override
   public XY tangent(Double param)
   {
      if (Rotation == RotationDirection.Reverse)
      {
         return new XY(-Math.cos(-param), Math.sin(-param));
      }

      return new XY(Math.cos(param), -Math.sin(param));
   }

   @Override
   public boolean withinParams(double p, double tol)
   {
      if (endParam() > startParam())
         return super.withinParams(p, tol);

      // otherwise the end < start because it crosses 12:00

      // so we need to be either between 0:00 and end
      // or between start and 12:00
      return p > startParam() - tol || p < endParam() + tol;
   }

   boolean isCyclic()
   {
      return startParam() == 0.0 && endParam() == Math.PI * 2;
   }
}
