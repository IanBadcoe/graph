package engine.controllers;

import engine.modelling.WorldObject;

public interface IController
{
   void step(double timeStep, WorldObject controlled);
}
