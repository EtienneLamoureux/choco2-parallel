package com.crystalgorithm;

import com.crystalgorithm.model.ChocoSolver;
import com.crystalgorithm.model.ChocoSolverFactory;

public class main
{

    public static void main(String[] args) throws Exception
    {
        executeRace();
        executeShared();
        executeShared();
        executeShared();
        executeShared();
        executeShared();

        System.exit(0);
    }

    private static void executeRace() throws Exception
    {
        int task1SolveTime = ((ChocoSolver) ChocoSolverFactory.createCallableDefaultSolver()).call();
        int task2SolveTime = ((ChocoSolver) ChocoSolverFactory.createCallableEffectiveSolver()).call();
        int raceWinnerSolveTime = FutureTaskRaceOrganizer.raceTwoChocoSolvers();

        System.out.println("Heuristique 1 a résolut le problème en " + task1SolveTime + " retours-arrière.");
        System.out.println("Heuristique 2 a résolut le problème en " + task2SolveTime + " retours-arrière.");
        System.out.println("Le gagnant de la course a résolut le problème en " + raceWinnerSolveTime
                + " retours-arrière.");
    }

    private static void executeShared() throws Exception
    {
        int raceWinnerSolveTime = FutureTaskRaceOrganizer.raceTwoChocoSolversWithSharedWeights();
    }

}
