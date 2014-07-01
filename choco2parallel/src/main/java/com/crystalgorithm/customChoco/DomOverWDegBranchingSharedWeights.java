package com.crystalgorithm.customChoco;

import static choco.kernel.solver.constraints.AbstractSConstraint.getAbstractSConstraintExtensionNumber;

import java.util.HashMap;
import java.util.Map;

import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.search.ValIterator;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;

import com.crystalgorithm.customChoco.weights.SharedHeuristicWeights;

@SuppressWarnings("rawtypes")
public class DomOverWDegBranchingSharedWeights extends DomOverWDegBranchingNew
{
    private final SharedHeuristicWeights sharedVariablesWeight;
    private final SharedHeuristicWeights sharedConstraintsWeight;

    public DomOverWDegBranchingSharedWeights(Solver s, IntDomainVar[] vars, ValIterator valHeuri, Number seed,
            SharedHeuristicWeights sharedVariablesWeight, SharedHeuristicWeights sharedConstraintsWeight)
    {
        super(s, vars, valHeuri, seed);

        this.sharedVariablesWeight = sharedVariablesWeight;
        this.sharedConstraintsWeight = sharedConstraintsWeight;
    }

    @Override
    protected void reinitBranching()
    {
        sharedConstraintsWeight.pullWeights(this.solver.getConstraintIterator());
        sharedVariablesWeight.pullWeights(this.solver.getIntVarIterator());

        super.reinitBranching();
    }

    @Override
    protected void increaseVarWeights(Var currentVar)
    {
        super.increaseVarWeights(currentVar);
    }

    @Override
    protected void decreaseVarWeights(Var currentVar)
    {
        super.decreaseVarWeights(currentVar);
    }

    @Override
    public void contradictionOccured(ContradictionException e)
    {
        super.contradictionOccured(e);

        updateConstraintsWeight();
        updateVariablesWeight();

        pushWeights();
    }

    private void pushWeights()
    {
        sharedVariablesWeight.pushWeights(updateVariablesWeight());
        sharedConstraintsWeight.pushWeights(updateConstraintsWeight());
    }

    private Map<Object, Integer> updateConstraintsWeight()
    {
        Map<Object, Integer> constraintsWeight = new HashMap<>();
        final DisposableIterator<SConstraint> constraintIterator = this.solver.getConstraintIterator();

        while (constraintIterator.hasNext())
        {
            final SConstraint constraint = constraintIterator.next();
            constraintsWeight.put(constraint,
                    constraint.getExtension(getAbstractSConstraintExtensionNumber(Utils.EXTENSION_IDENTIFIER))
                            .get());
        }

        return constraintsWeight;
    }

    private Map<Object, Integer> updateVariablesWeight()
    {
        Map<Object, Integer> varialbesWeight = new HashMap<>();
        final DisposableIterator<IntDomainVar> varIterator = this.solver.getIntVarIterator();

        while (varIterator.hasNext())
        {
            final IntDomainVar var = varIterator.next();
            varialbesWeight.put(var, var
                    .getExtension(getAbstractSConstraintExtensionNumber(Utils.EXTENSION_IDENTIFIER)).get());
        }

        return varialbesWeight;
    }
}
