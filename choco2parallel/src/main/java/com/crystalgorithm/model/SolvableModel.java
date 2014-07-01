package com.crystalgorithm.model;

import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;

public interface SolvableModel
{

    IntegerVariable[] getVariables();

    Model getModel();
}
