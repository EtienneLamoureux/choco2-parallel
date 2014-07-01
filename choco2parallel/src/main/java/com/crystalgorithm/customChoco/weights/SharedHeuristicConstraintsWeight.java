package com.crystalgorithm.customChoco.weights;

import static choco.kernel.solver.constraints.AbstractSConstraint.getAbstractSConstraintExtensionNumber;
import choco.kernel.solver.constraints.SConstraint;

import com.crystalgorithm.customChoco.Utils;

@SuppressWarnings("rawtypes")
public class SharedHeuristicConstraintsWeight extends SharedHeuristicWeights
{
    public SharedHeuristicConstraintsWeight(OriginalDeviationStrategy originalDeviationHeuristic,
            WeightWeavingStrategy weightWeavingStrategy)
    {
        super(originalDeviationHeuristic, weightWeavingStrategy);
    }

    protected String createKey(Object object)
    {
        SConstraint constraint = (SConstraint) object;
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(constraint.getClass().getName());

        for (int i = 0; i < constraint.getNbVars(); i++)
        {
            stringBuilder.append(constraint.getVarQuick(i).getName());
        }

        return stringBuilder.toString();
    }

    @Override
    protected int getExtensionNumber()
    {
        return getAbstractSConstraintExtensionNumber(Utils.EXTENSION_IDENTIFIER);
    }

    @Override
    protected void setNewValueForWeightedItem(Object weightedItem)
    {
        SConstraint weightedConstraint = (SConstraint) weightedItem;
        String key = createKey(weightedConstraint);

        if (sharedWeights.containsKey(key))
        {
            weightedConstraint.getExtension(getExtensionNumber()).set(sharedWeights.get(key));
        }
    }
}
