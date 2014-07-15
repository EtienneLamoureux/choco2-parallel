package com.crystalgorithm.model;

import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;

/**
 * As opposed to an optimisable model.
 */
public interface SolvableModel
{
    IntegerVariable[] getVariables();

    Model getModel();
}
