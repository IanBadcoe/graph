package engine.controllers;

import engine.Level;
import engine.modelling.WorldObject;

public interface IController
{
   void timeStep(double timeStep, WorldObject controlled, Level level);
}
