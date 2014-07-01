package com.crystalgorithm.model;

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

import com.crystalgorithm.DomOverWDegBranchingNew;
import com.crystalgorithm.SynchronizedWeights;

@SuppressWarnings("rawtypes")
public class DomOverWDegBranchingSharedWeights extends DomOverWDegBranchingNew {

    public static final String EXTENSION_IDENTIFIER = "choco.cp.cpsolver.search.integer.varselector.DomOverWDeg";

    private final SynchronizedWeights synchronizedWeights;

    public DomOverWDegBranchingSharedWeights(Solver s, IntDomainVar[] vars, ValIterator valHeuri, Number seed,
            SynchronizedWeights synchronizedWeights) {
        super(s, vars, valHeuri, seed);

        this.synchronizedWeights = synchronizedWeights;
    }

    @Override
    protected void reinitBranching() {
        //synchronizedWeights.retrieveConstraintsWeights(this.solver.getConstraintIterator());
        //synchronizedWeights.retrieveVariablesWeights(this.solver.getIntVarIterator());

        super.reinitBranching();
    }

    @Override
    protected void increaseVarWeights(Var currentVar) {
        super.increaseVarWeights(currentVar);
    }

    @Override
    protected void decreaseVarWeights(Var currentVar) {
        super.decreaseVarWeights(currentVar);
    }

    @Override
    public void contradictionOccured(ContradictionException e) {
        super.contradictionOccured(e);
        updateConstraintsWeigth();
        updateVariablesWeigth();
        sendWeigthsToMaster();
    }

    private void sendWeigthsToMaster() {
        synchronizedWeights.updateVariablesWeights(updateVariablesWeigth());
        synchronizedWeights.updateConstraintsWeights(updateConstraintsWeigth());
    }

    private Map<SConstraint, Integer> updateConstraintsWeigth() {
        Map<SConstraint, Integer> constraintsWeight = new HashMap<>();
        final DisposableIterator<SConstraint> constraintIterator = this.solver.getConstraintIterator();
        while (constraintIterator.hasNext()) {
            final SConstraint constraint = constraintIterator.next();
            constraintsWeight.put(constraint,
                    constraint.getExtension(getAbstractSConstraintExtensionNumber(EXTENSION_IDENTIFIER)).get());
        }
        return constraintsWeight;
    }

    private Map<IntDomainVar, Integer> updateVariablesWeigth() {
        Map<IntDomainVar, Integer> varialbesWeight = new HashMap<>();
        final DisposableIterator<IntDomainVar> varIterator = this.solver.getIntVarIterator();
        while (varIterator.hasNext()) {
            final IntDomainVar var = varIterator.next();
            varialbesWeight.put(var, var.getExtension(getAbstractSConstraintExtensionNumber(EXTENSION_IDENTIFIER))
                    .get());
        }

        return varialbesWeight;
    }
}
