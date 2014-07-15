package com.crystalgorithm.model;

import java.util.concurrent.Callable;

import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.branching.domwdeg.DomOverWDegBranchingNew;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;

import com.crystalgorithm.customChoco.DomOverWDegBranchingSharedWeights;
import com.crystalgorithm.customChoco.weights.SharedHeuristicWeights;

public class ChocoSolver implements Callable<Integer>
{
    private CPSolver solver;
    private SolvableModel model;

    public ChocoSolver(SolvableModel model)
    {
        initModel(model);

        setHeuristic(ChocoHeuristic.DEFAULT);
    }

    public ChocoSolver(SolvableModel model, int seed)
    {
        initModel(model);

        solver.addGoal(new DomOverWDegBranchingNew(solver, solver.getVar(model.getVariables()), new IncreasingDomain(),
                seed));
    }

    public ChocoSolver(SolvableModel model, int seed, SharedHeuristicWeights sharedVariablesWeight,
            SharedHeuristicWeights sharedConstraintsWeight)
    {
        initModel(model);

        solver.addGoal(new DomOverWDegBranchingSharedWeights(solver, solver.getVar(model.getVariables()),
                new IncreasingDomain(), seed,
                sharedVariablesWeight, sharedConstraintsWeight));
    }

    public int getTimeCount()
    {
        return solver.getTimeCount();
    }

    public int getBackTrackCount()
    {
        return solver.getBackTrackCount();
    }

    public void setHeuristic(ChocoHeuristic heuristic)
    {
        switch (heuristic)
        {
            case MIN_DOMAIN: {
                solver.setVarIntSelector(new MinDomain(solver, solver.getVar(model.getVariables())));
                break;
            }
            case DOM_OVER_WDEG: {
                solver.addGoal(new DomOverWDegBranchingNew(solver, solver.getVar(model.getVariables()),
                        new IncreasingDomain(), 1));
                break;
            }
            case DEFAULT: {
                // default heuristic
                break;
            }
        }
    }

    public void setVerbosity(Verbosity verbosity)
    {
        ChocoLogging.toVerbose();
        ChocoLogging.setVerbosity(verbosity);
    }

    @Override
    public Integer call() throws Exception
    {
        solve();

        return getBackTrackCount();
    }

    private void initModel(SolvableModel model)
    {
        solver = new CPSolver();
        this.model = model;

        solver.read(model.getModel());

        setVerbosity(Verbosity.DEFAULT);
    }

    private void solve()
    {
        if (!(solver.solve()))
        {
            System.err.println("No solutions");
        }
    }

}
