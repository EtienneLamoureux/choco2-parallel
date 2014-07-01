package com.crystalgorithm;

import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.addConstraintExtension;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.addConstraintToVarWeights;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.addFailure;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.addIncFailure;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.computeWeightedDegreeFromScratch;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.getConstraintExtension;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.getConstraintFailures;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.getVarExtension;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.getVariableIncWDeg;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.initConstraintExtensions;
import static choco.cp.solver.search.integer.branching.domwdeg.DomWDegUtils.initVarExtensions;
import choco.cp.solver.search.integer.branching.IRandomBreakTies;
import choco.cp.solver.search.integer.varselector.ratioselector.IntVarRatioSelector;
import choco.cp.solver.search.integer.varselector.ratioselector.MinRatioSelector;
import choco.cp.solver.search.integer.varselector.ratioselector.RandMinRatioSelector;
import choco.cp.solver.search.integer.varselector.ratioselector.ratios.IntRatio;
import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.branch.AbstractLargeIntBranchingStrategy;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.SConstraintType;
import choco.kernel.solver.propagation.listener.PropagationEngineListener;
import choco.kernel.solver.variables.AbstractVar;
import choco.kernel.solver.variables.Var;

@SuppressWarnings("rawtypes")
public abstract class AbstractDomOverWDegBranching extends
        AbstractLargeIntBranchingStrategy implements PropagationEngineListener, IRandomBreakTies {

    protected final Solver solver;

    protected final IntRatio[] varRatios;

    private IntVarRatioSelector ratioSelector;

    //helps to synchronize incremental weights
    protected int updateWeightsCount;

    public AbstractDomOverWDegBranching(Solver solver, IntRatio[] varRatios, Number seed) {
        super();
        this.solver = solver;
        this.varRatios = varRatios;
        initConstraintExtensions(this.solver);
        initVarExtensions(this.solver);
        this.solver.getPropagationEngine().addPropagationEngineListener(this);
        if (seed == null)
            cancelRandomBreakTies();
        else
            setRandomBreakTies(seed.longValue());
    }

    public final Solver getSolver() {
        return solver;
    }

    public final IntVarRatioSelector getRatioSelector() {
        return ratioSelector;
    }

    @Override
    public void cancelRandomBreakTies() {
        ratioSelector = new MinRatioSelector(solver, varRatios);
    }

    @Override
    public void setRandomBreakTies(long seed) {
        ratioSelector = new RandMinRatioSelector(solver, varRatios, seed);

    }

    //*****************************************************************//
    //*******************  Weighted degrees and failures managment ***//
    //***************************************************************//

    @Override
    public void initConstraintForBranching(SConstraint c) {
        addConstraintExtension(c);
        addConstraintToVarWeights(c);
    }

    protected abstract int getExpectedUpdateWeightsCount();

    @Override
    public void initBranching() {
        final int n = solver.getNbIntVars();
        for (int i = 0; i < n; i++) {
            final Var v = solver.getIntVar(i);
            getVarExtension(v).set(computeWeightedDegreeFromScratch(v));
        }
        updateWeightsCount = getExpectedUpdateWeightsCount();
    }

    protected void reinitBranching() {
        if (updateWeightsCount != getExpectedUpdateWeightsCount())
            initBranching();
    }

    private void updateVarWeights(final Var currentVar, final SConstraint<?> cstr, final int delta) {
        if (delta != 0) {
            final int n = cstr.getNbVars();
            for (int k = 0; k < n; k++) {
                final AbstractVar var = (AbstractVar) cstr.getVarQuick(k);
                if (var != currentVar && !var.isInstantiated()) {
                    getVarExtension(var).add(delta);
                    //check robustness of the incremental weights
                    assert getVarExtension(var).get() >= 0 : "" + var.getName() + " weight is negative ("
                            + getVarExtension(var).get()
                            + "). This is due to incremental computation of weights in Dom/WDeg.";
                }
            }
        }
    }

    protected static boolean hasTwoNotInstVars(SConstraint<?> c) {
        final int n = c.getNbVars();
        int cpt = -2;
        for (int i = 0; i < n; i++) {
            if (!c.getVarQuick(i).isInstantiated()) {
                cpt++;
                if (cpt > 0)
                    return false;
            }
        }
        return cpt == 0;
    }

    private boolean isDisconnected(SConstraint<?> cstr) {
        return SConstraintType.INTEGER.equals(cstr.getConstraintType()) && hasTwoNotInstVars(cstr);
    }

    protected void increaseVarWeights(final Var currentVar) {
        updateWeightsCount--;
        final DisposableIterator<SConstraint> iter = currentVar.getConstraintsIterator();
        while (iter.hasNext()) {
            final SConstraint cstr = iter.next();
            if (isDisconnected(cstr)) {
                updateVarWeights(currentVar, cstr, getConstraintExtension(cstr).get());
            }
        }
        iter.dispose();
    }

    protected void decreaseVarWeights(final Var currentVar) {
        updateWeightsCount++;
        final DisposableIterator<SConstraint> iter = currentVar.getConstraintsIterator();
        while (iter.hasNext()) {
            final SConstraint cstr = iter.next();
            if (isDisconnected(cstr)) {
                updateVarWeights(currentVar, cstr, -getConstraintExtension(cstr).get());
            }
        }
        iter.dispose();
    }

    @Override
    public void contradictionOccured(ContradictionException e) {
        if (updateWeightsCount == getExpectedUpdateWeightsCount()) {
            addIncFailure(e.getDomOverDegContradictionCause());
        } else {
            //weights are already out-of-date
            addFailure(e.getDomOverDegContradictionCause());
        }
    }

    @Override
    public void safeDelete() {
        solver.getPropagationEngine().removePropagationEngineListener(this);
    }

    //*****************************************************************//
    //*******************  Variable Selection *************************//
    //***************************************************************//

    public Object selectBranchingObject() throws ContradictionException {
        reinitBranching();
        return ratioSelector.selectVar();
    }

    @Override
    public String toString() {
        return "nbUpdates: " + updateWeightsCount + "\n" + getVariableIncWDeg(solver) + "\n"
                + getConstraintFailures(solver);
    }

}
