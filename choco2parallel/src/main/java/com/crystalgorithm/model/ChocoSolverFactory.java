package com.crystalgorithm.model;

import java.util.concurrent.Callable;

import choco.kernel.common.logging.Verbosity;

import com.crystalgorithm.customChoco.weights.SharedHeuristicWeights;

public class ChocoSolverFactory
{

    private static final int SIZE_OF_THE_MAGIC_SQUARE = 5;

    public static Callable<Integer> createCallableEffectiveSolver()
    {
        ChocoSolver solver = createChocoSolver();
        solver.setHeuristic(ChocoHeuristic.DOM_OVER_WDEG);

        return solver;
    }

    public static Callable<Integer> createCallableDefaultSolver()
    {
        ChocoSolver solver = createChocoSolver();
        solver.setHeuristic(ChocoHeuristic.DEFAULT);

        return solver;
    }

    public static Callable<Integer> createCallableSharedWeightsSolver(int seed,
            SharedHeuristicWeights sharedVariablesWeight,
            SharedHeuristicWeights sharedConstraintsWeight)
    {
        MagicSquareModel model = createModel();
        ChocoSolver solver = new ChocoSolver(model, seed, sharedVariablesWeight, sharedConstraintsWeight);
        solver.setVerbosity(Verbosity.QUIET);

        return solver;
    }

    public static ChocoSolver createChocoSolverWithDomOverWDegBranchingNew(int seed)
    {
        MagicSquareModel model = createModel();
        ChocoSolver solver = new ChocoSolver(model, seed);
        solver.setVerbosity(Verbosity.QUIET);

        return solver;
    }

    public static ChocoSolver createChocoSolver()
    {
        MagicSquareModel model = createModel();
        ChocoSolver solver = new ChocoSolver(model);
        solver.setVerbosity(Verbosity.QUIET);

        return solver;
    }

    private static MagicSquareModel createModel()
    {
        return new MagicSquareModel(SIZE_OF_THE_MAGIC_SQUARE);
    }
}
