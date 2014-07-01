package com.crystalgorithm.customChoco.weights;

import static choco.kernel.solver.variables.AbstractVar.getAbstractVarExtensionNumber;
import choco.kernel.solver.variables.Var;

import com.crystalgorithm.customChoco.Utils;

public class SharedHeuristicVariablesWeight extends SharedHeuristicWeights
{
    public SharedHeuristicVariablesWeight(OriginalDeviationStrategy originalDeviationHeuristic,
            WeightWeavingStrategy weightWeavingStrategy)
    {
        super(originalDeviationHeuristic, weightWeavingStrategy);
    }

    @Override
    protected String createKey(Object object)
    {
        Var variable = (Var) object;
        return variable.getName();
    }

    @Override
    protected int getExtensionNumber()
    {
        return getAbstractVarExtensionNumber(Utils.EXTENSION_IDENTIFIER);
    }

    @Override
    protected void setNewValueForWeightedItem(Object weightedItem)
    {
        Var weightedVariable = (Var) weightedItem;
        String key = createKey(weightedVariable);

        if (sharedWeights.containsKey(key))
        {
            weightedVariable.getExtension(getExtensionNumber()).set(sharedWeights.get(key));
        }
    }

}
