package com.crystalgorithm.customChoco.weights;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public interface WeightWeavingStrategy
{
    void weave(Map<String, Integer> medianWeights,
            ConcurrentMap<String, Integer> sharedWeights);
}
