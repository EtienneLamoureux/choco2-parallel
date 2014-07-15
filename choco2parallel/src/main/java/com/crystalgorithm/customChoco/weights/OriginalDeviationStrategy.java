package com.crystalgorithm.customChoco.weights;

import java.util.Map;

/**
 * Ensures the weights are deviated from their original values once before starting the solver. This is done so the
 * multiple threads can explore different paths in the solution-tree.
 */
public interface OriginalDeviationStrategy
{
    public void deviate(Map<String, Integer> weights);
}
