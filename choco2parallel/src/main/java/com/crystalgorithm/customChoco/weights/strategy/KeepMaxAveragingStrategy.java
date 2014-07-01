package com.crystalgorithm.customChoco.weights.strategy;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import com.crystalgorithm.customChoco.weights.AveragingStrategy;

public class KeepMaxAveragingStrategy implements AveragingStrategy
{
    public ConcurrentMap<String, Integer> calculate(ConcurrentMap<String, Integer> medianWeights,
            Map<String, Set<Integer>> allSharedWeights)
    {
        for (Entry<String, Set<Integer>> sharedWeights : allSharedWeights.entrySet())
        {
            medianWeights.put(sharedWeights.getKey(), Collections.max(sharedWeights.getValue()));
        }

        return medianWeights;
    }
}
