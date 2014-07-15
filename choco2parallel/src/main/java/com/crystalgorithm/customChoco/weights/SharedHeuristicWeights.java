package com.crystalgorithm.customChoco.weights;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import choco.kernel.common.util.iterators.DisposableIterator;

public abstract class SharedHeuristicWeights
{
    protected ConcurrentMap<String, Integer> sharedWeights;
    private boolean hasBeenOriginallyDeviated;
    private OriginalDeviationStrategy originalDeviationStrategy;
    private WeightWeavingStrategy weightWeavingStrategy;

    public SharedHeuristicWeights(OriginalDeviationStrategy originalDeviationHeuristic,
            WeightWeavingStrategy weightWeavingStrategy)
    {
        sharedWeights = new ConcurrentHashMap<>();
        hasBeenOriginallyDeviated = false;
        this.originalDeviationStrategy = originalDeviationHeuristic;
        this.weightWeavingStrategy = weightWeavingStrategy;
    }

    public ConcurrentMap<String, Integer> shareWeights()
    {
        return sharedWeights;
    }

    public void update(Map<String, Integer> medianWeights)
    {
        weightWeavingStrategy.weave(medianWeights, sharedWeights);
    }

    public void pushWeights(Map<Object, Integer> weightedItems)
    {
        for (Map.Entry<Object, Integer> weightedItem : weightedItems.entrySet())
        {
            String key = createKey(weightedItem.getKey());

            if (sharedWeights.containsKey(key))
            {
                // could add another heuristic here, for how to mix the new values with the current ones on push
                sharedWeights.put(key, weightedItem.getValue());
            }
            else
            {
                sharedWeights.put(key, weightedItem.getValue());
            }
        }

        if (hasBeenOriginallyDeviated == false)
        {
            originalDeviationStrategy.deviate(sharedWeights);
            hasBeenOriginallyDeviated = true;
        }
    }

    public void pullWeights(DisposableIterator<?> iterator)
    {
        if (sharedWeights.isEmpty() == false)
        {
            while (iterator.hasNext())
            {
                Object weightedItem = iterator.next();

                setNewValueForWeightedItem(weightedItem);
            }
        }
    }

    protected abstract void setNewValueForWeightedItem(Object weightedItem);

    protected abstract int getExtensionNumber();

    protected abstract String createKey(Object object);
}
