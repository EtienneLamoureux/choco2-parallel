package com.crystalgorithm;

import static choco.kernel.solver.constraints.AbstractSConstraint.getAbstractSConstraintExtensionNumber;
import static choco.kernel.solver.variables.AbstractVar.getAbstractVarExtensionNumber;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import com.crystalgorithm.model.DomOverWDegBranchingSharedWeights;

@SuppressWarnings("rawtypes")
public class SynchronizedWeights {
    private final ConcurrentMap<String, Integer> constraints;
    private final ConcurrentMap<String, Integer> variables;

    public SynchronizedWeights() {
        variables = new ConcurrentHashMap<>();
        constraints = new ConcurrentHashMap<>();
    }

    public void updateVariablesWeights(Map<IntDomainVar, Integer> intDomainVarIntegerMap) {
        for (Map.Entry<IntDomainVar, Integer> entry : intDomainVarIntegerMap.entrySet()) {
            if (variables.containsKey(entry.getKey())) {
                final Integer currentValue = variables.get(entry.getKey());
                variables.put(entry.getKey().getName(), Math.max(currentValue, entry.getValue()));
            } else {
                variables.put(entry.getKey().getName(), entry.getValue());
            }
        }
    }

    public void updateConstraintsWeights(Map<SConstraint, Integer> constraintIntegerMap) {
        for (Map.Entry<SConstraint, Integer> entry : constraintIntegerMap.entrySet()) {
            String key = createKey(entry.getKey());
            if (constraints.containsKey(key)) {
                final Integer currentValue = constraints.get(key);
                constraints.put(key, Math.max(currentValue, entry.getValue()));
            } else {
                constraints.put(key, entry.getValue());
            }
        }
    }

    private String createKey(SConstraint constraint) {
        StringBuilder sb = new StringBuilder();

        sb.append(constraint.getClass().getName());
        for (int i = 0; i < constraint.getNbVars(); i++) {
            sb.append(constraint.getVarQuick(i).getName());
        }

        return sb.toString();
    }

    public void retrieveConstraintsWeights(DisposableIterator<SConstraint> constraintIterator) {
        if (!constraints.isEmpty()) {
            while (constraintIterator.hasNext()) {
                final SConstraint constraint = constraintIterator.next();
                String key = createKey(constraint);
                constraint.getExtension(
                        getAbstractSConstraintExtensionNumber(DomOverWDegBranchingSharedWeights.EXTENSION_IDENTIFIER))
                        .set(constraints.get(key));
            }
        }
    }

    public void retrieveVariablesWeights(DisposableIterator<IntDomainVar> intVarIterator) {
        if (!variables.isEmpty()) {
            while (intVarIterator.hasNext()) {
                final IntDomainVar intDomainVar = intVarIterator.next();
                intDomainVar.getExtension(
                        getAbstractVarExtensionNumber(DomOverWDegBranchingSharedWeights.EXTENSION_IDENTIFIER)).set(
                        variables.get(intDomainVar.getName()));
            }
        }
    }
}
