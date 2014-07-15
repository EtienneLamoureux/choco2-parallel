package com.crystalgorithm.customChoco.weights.strategy;

import java.util.Collections;
import java.util.Map;

import com.crystalgorithm.customChoco.weights.OriginalDeviationStrategy;

public class AddWeightToFirstOriginalDeviationStrategy implements OriginalDeviationStrategy
{
    public void deviate(Map<String, Integer> weights)
    {
        String minKey = Collections.min(weights.keySet());
        int weightOfMainKey = weights.get(minKey);

        weights.put(minKey, weightOfMainKey + 10);
    }
}
