package com.crystalgorithm.customChoco.weights.strategy;

import java.util.Collections;
import java.util.Map;

import com.crystalgorithm.customChoco.weights.OriginalDeviationStrategy;

public class AddWeightToLastOriginalDeviationStrategy implements OriginalDeviationStrategy
{
    public void deviate(Map<String, Integer> weights)
    {
        String maxKey = Collections.max(weights.keySet());
        int weightOfMaxKey = weights.get(maxKey);

        weights.put(maxKey, weightOfMaxKey + 10);
    }
}
