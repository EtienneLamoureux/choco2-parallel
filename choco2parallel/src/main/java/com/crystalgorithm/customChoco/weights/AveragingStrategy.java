package com.crystalgorithm.customChoco.weights;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public interface AveragingStrategy
{

    ConcurrentMap<String, Integer> calculate(ConcurrentMap<String, Integer> medianWeights,
            Map<String, Set<Integer>> allSharedWeights);

}
