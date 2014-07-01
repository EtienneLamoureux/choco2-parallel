package com.crystalgorithm.customChoco.weights;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class WeightsSynchronizer implements Callable<Integer>
{
    private ConcurrentMap<String, Integer> medianWeights;
    private List<SharedHeuristicWeights> sharedHeuristicsWeights;
    private boolean noHeuristicIsFinished;
    private AveragingStrategy averagingStrategy;

    public WeightsSynchronizer(AveragingStrategy averagingStrategy)
    {
        medianWeights = new ConcurrentHashMap<>();
        sharedHeuristicsWeights = new ArrayList<>();
        noHeuristicIsFinished = true;
        this.averagingStrategy = averagingStrategy;
    }

    public void addSharedHeuristicWeights(SharedHeuristicWeights sharedHeuristicWeights)
    {
        this.sharedHeuristicsWeights.add(sharedHeuristicWeights);
    }

    public void oneOfTheHeuristicIsFinished()
    {
        noHeuristicIsFinished = false;
    }

    @Override
    public Integer call() throws Exception
    {
        int nbMedianComputations = 0;

        while (noHeuristicIsFinished)
        {
            calculateMedianWeights();
            updateSharedWeights();

            nbMedianComputations++;
        }

        return nbMedianComputations;
    }

    private void updateSharedWeights()
    {
        for (SharedHeuristicWeights sharedHeuristicWeights : sharedHeuristicsWeights)
        {
            sharedHeuristicWeights.update(medianWeights);
        }
    }

    private void calculateMedianWeights()
    {
        Map<String, Set<Integer>> allSharedWeightsOrganized = organizeSharedWeights();

        medianWeights = averagingStrategy.calculate(medianWeights, allSharedWeightsOrganized);
    }

    private Map<String, Set<Integer>> organizeSharedWeights()
    {
        Map<String, Set<Integer>> allSharedWeightsOrganized = new ConcurrentHashMap<>();

        for (SharedHeuristicWeights sharedHeuristicWeights : sharedHeuristicsWeights)
        {
            Map<String, Integer> sharedWeights = sharedHeuristicWeights.shareWeights();

            for (Entry<String, Integer> sharedWeight : sharedWeights.entrySet())
            {
                if (allSharedWeightsOrganized.containsKey(sharedWeight.getKey()))
                {
                    allSharedWeightsOrganized.get(sharedWeight.getKey()).add(sharedWeight.getValue());
                }
                else
                {
                    Set<Integer> sharedWeightsForOneEntry = new ConcurrentSkipListSet<>();
                    sharedWeightsForOneEntry.add(sharedWeight.getValue());

                    allSharedWeightsOrganized.put(sharedWeight.getKey(), sharedWeightsForOneEntry);
                }
            }
        }

        return allSharedWeightsOrganized;
    }
}
