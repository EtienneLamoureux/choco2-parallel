package com.crystalgorithm.customChoco.weights.strategy;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.crystalgorithm.customChoco.weights.WeightWeavingStrategy;

public class KeepMaxWeightWeavingStrategy implements WeightWeavingStrategy
{
    public void weave(Map<String, Integer> medianWeights,
            ConcurrentMap<String, Integer> sharedWeights)
    {
        for (Map.Entry<String, Integer> entry : medianWeights.entrySet())
        {
            if (sharedWeights.containsKey(entry.getKey()))
            {
                final Integer currentValue = sharedWeights.get(entry.getKey());
                sharedWeights.put(entry.getKey(), Math.max(currentValue, entry.getValue()));
            }
            else
            {
                sharedWeights.put(entry.getKey(), entry.getValue());
            }
        }
    }
}
