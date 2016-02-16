package engine.controllers;

import engine.Level;
import engine.modelling.WorldObject;

public interface IController
{
   void step(double timeStep, WorldObject controlled, Level level);
}
