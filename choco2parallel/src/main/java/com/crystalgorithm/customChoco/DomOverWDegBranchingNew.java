/**
 * Extends custom version of AbstractDomOverWDegBranching to allow children to extend the "contradictionOccured" method
 */
package com.crystalgorithm.customChoco;

import choco.cp.solver.search.integer.varselector.ratioselector.ratios.RatioFactory;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.IntBranchingDecision;
import choco.kernel.solver.search.ValIterator;
import choco.kernel.solver.variables.integer.IntDomainVar;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DomOverWDegBranchingNew extends AbstractDomOverWDegBranching
{

    private final ValIterator valIterator;

    public DomOverWDegBranchingNew(Solver s, IntDomainVar[] vars, ValIterator valHeuri, Number seed)
    {
        super(s, RatioFactory.createDomWDegRatio(vars, true), seed);
        valIterator = valHeuri;
    }

    @Override
    public String getDecisionLogMessage(IntBranchingDecision decision)
    {
        return getDefaultAssignMsg(decision);
    }

    @Override
    protected int getExpectedUpdateWeightsCount()
    {
        return solver.getSearchStrategy().getSearchLoop().getDepthCount();
    }

    @Override
    public void goDownBranch(IntBranchingDecision decision) throws ContradictionException
    {
        decision.setIntVal();
    }

    @Override
    public void goUpBranch(IntBranchingDecision decision) throws ContradictionException
    {

    }

    @Override
    public void setFirstBranch(IntBranchingDecision decision)
    {
        final IntDomainVar var = decision.getBranchingIntVar();
        decreaseVarWeights(var);
        decision.setBranchingValue(valIterator.getFirstVal(var));
    }

    @Override
    public void setNextBranch(IntBranchingDecision decision)
    {
        decision.setBranchingValue(valIterator.getNextVal(decision.getBranchingIntVar(), decision.getBranchingValue()));
    }

    @Override
    public boolean finishedBranching(IntBranchingDecision decision)
    {
        final IntDomainVar var = decision.getBranchingIntVar();
        if (valIterator.hasNextVal(var, decision.getBranchingValue()))
        {
            return false;
        }
        else
        {
            increaseVarWeights(var);
            return true;
        }
    }

}
