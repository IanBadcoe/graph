package engine.controllers;

import engine.level.Level;
import engine.modelling.WorldObject;

public interface IController
{
   @SuppressWarnings({"EmptyMethod", "UnusedParameters"})
   void timeStep(double timeStep, WorldObject controlled, Level level);
}
