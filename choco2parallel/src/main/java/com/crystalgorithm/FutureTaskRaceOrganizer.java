package com.crystalgorithm;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import com.crystalgorithm.customChoco.weights.SharedHeuristicConstraintsWeight;
import com.crystalgorithm.customChoco.weights.SharedHeuristicVariablesWeight;
import com.crystalgorithm.customChoco.weights.WeightsSynchronizer;
import com.crystalgorithm.customChoco.weights.strategy.AddWeightToFirstOriginalDeviationStrategy;
import com.crystalgorithm.customChoco.weights.strategy.AddWeightToLastOriginalDeviationStrategy;
import com.crystalgorithm.customChoco.weights.strategy.KeepMaxAveragingStrategy;
import com.crystalgorithm.customChoco.weights.strategy.KeepMaxWeightWeavingStrategy;
import com.crystalgorithm.customChoco.weights.strategy.SimpleOriginalDeviationStrategy;
import com.crystalgorithm.model.ChocoSolverFactory;

public class FutureTaskRaceOrganizer
{
    public static int raceTwoTasks(FutureTask<Integer> task1, FutureTask<Integer> task2) throws InterruptedException,
            ExecutionException
    {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(task1);
        executorService.execute(task2);

        int winnerTime = FutureTaskRaceOrganizer.determineWinner(task1, task2);

        executorService.shutdownNow();
        return winnerTime;
    }

    public static int raceTwoChocoSolvers()
    {
        FutureTask<Integer> slowTask = new FutureTask<Integer>(ChocoSolverFactory.createCallableDefaultSolver());
        FutureTask<Integer> fastTask = new FutureTask<Integer>(ChocoSolverFactory.createCallableEffectiveSolver());
        int winnerTime = 0;

        try
        {
            winnerTime = FutureTaskRaceOrganizer.raceTwoTasks(slowTask, fastTask);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return winnerTime;
    }

    public static int raceTwoChocoSolversWithSharedWeights()
    {

        WeightsSynchronizer synchronizedVariablesWeights = new WeightsSynchronizer(new KeepMaxAveragingStrategy());
        SharedHeuristicVariablesWeight sharedHeuristicVariablesWeight1 = new SharedHeuristicVariablesWeight(
                new AddWeightToFirstOriginalDeviationStrategy(), new KeepMaxWeightWeavingStrategy());
        SharedHeuristicVariablesWeight sharedHeuristicVariablesWeight2 = new SharedHeuristicVariablesWeight(
                new AddWeightToLastOriginalDeviationStrategy(), new KeepMaxWeightWeavingStrategy());
        synchronizedVariablesWeights.addSharedHeuristicWeights(sharedHeuristicVariablesWeight1);
        synchronizedVariablesWeights.addSharedHeuristicWeights(sharedHeuristicVariablesWeight2);

        WeightsSynchronizer synchronizedConstraintWeights = new WeightsSynchronizer(new KeepMaxAveragingStrategy());
        SharedHeuristicConstraintsWeight sharedHeuristicConstraintsWeight1 = new SharedHeuristicConstraintsWeight(
                new SimpleOriginalDeviationStrategy(), new KeepMaxWeightWeavingStrategy());
        SharedHeuristicConstraintsWeight sharedHeuristicConstraintsWeight2 = new SharedHeuristicConstraintsWeight(
                new SimpleOriginalDeviationStrategy(), new KeepMaxWeightWeavingStrategy());
        synchronizedConstraintWeights.addSharedHeuristicWeights(sharedHeuristicConstraintsWeight1);
        synchronizedConstraintWeights.addSharedHeuristicWeights(sharedHeuristicConstraintsWeight2);

        try
        {
            // System.out.println("Seed 0 résolu en " + ChocoSolverFactory.createChocoSolverWithDomOverWDegBranchingNew(11).call()
            // + " retours arrières.");

            // System.out.println("Solveur vanille a résolu en " + ChocoSolverFactory.createChocoSolverWithDomOverWDegBranchingNew(1).call()
            // + " retours arrières.");
            // System.out.println("Task 1 seule a résolu en "
            // + ChocoSolverFactory
            // .createCallableSharedWeightsSolver(
            // 1,
            // new SharedHeuristicVariablesWeight(new AddWeightToFirstOriginalDeviationStrategy(),
            // new KeepMaxWeightWeavingStrategy()),
            // new SharedHeuristicConstraintsWeight(new SimpleOriginalDeviationStrategy(),
            // new KeepMaxWeightWeavingStrategy())).call() + " retours arrières.");
            // System.out.println("Task 2 seule a résolu en "
            // + ChocoSolverFactory
            // .createCallableSharedWeightsSolver(
            // 1,
            // new SharedHeuristicVariablesWeight(new AddWeightToLastOriginalDeviationStrategy(),
            // new KeepMaxWeightWeavingStrategy()),
            // new SharedHeuristicConstraintsWeight(new SimpleOriginalDeviationStrategy(),
            // new KeepMaxWeightWeavingStrategy())).call() + " retours arrières.");
        } catch (Exception e1)
        {
            e1.printStackTrace();
        }

        FutureTask<Integer> slowTask = new FutureTask<>(ChocoSolverFactory.createCallableSharedWeightsSolver(1,
                sharedHeuristicVariablesWeight1, sharedHeuristicConstraintsWeight1));
        FutureTask<Integer> fastTask = new FutureTask<>(ChocoSolverFactory.createCallableSharedWeightsSolver(1,
                sharedHeuristicVariablesWeight2, sharedHeuristicConstraintsWeight2));
        FutureTask<Integer> variableSynchronizationTask = new FutureTask<>(synchronizedVariablesWeights);
        FutureTask<Integer> constraintSynchronizationTask = new FutureTask<>(synchronizedConstraintWeights);

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        executorService.execute(variableSynchronizationTask);
        executorService.execute(constraintSynchronizationTask);
        executorService.execute(slowTask);
        executorService.execute(fastTask);

        while (slowTask.isDone() == false || fastTask.isDone() == false)
        {

        }

        synchronizedVariablesWeights.oneOfTheHeuristicIsFinished();
        synchronizedConstraintWeights.oneOfTheHeuristicIsFinished();

        executorService.shutdownNow();

        try
        {
            System.out.println("Task 1 a résolu en " + slowTask.get() + " retours arrières.");
            System.out.println("Task 2 a résolu en " + fastTask.get() + " retours arrières.");
            System.out.println(variableSynchronizationTask.get() + " calculs sur les variales.");
            System.out.println(constraintSynchronizationTask.get() + " calculs sur les contraintes.");

            if (slowTask.get() < fastTask.get())
            {
                System.out.println("Task 1 a résolut le problème en " + slowTask.get() + " retours-arrière.");
                return slowTask.get();
            }
            else
            {
                System.out.println("Task 2 a résolut le problème en " + fastTask.get() + " retours-arrière.");
                return fastTask.get();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    private static int determineWinner(FutureTask<Integer> task1, FutureTask<Integer> task2)
            throws InterruptedException,
            ExecutionException
    {
        boolean isDone = false;
        int winnerTime = 0;

        while (!isDone)
        {
            if (task1.isDone())
            {
                isDone = true;
                winnerTime = task1.get();
                task2.cancel(true);
            }
            else if (task2.isDone())
            {
                isDone = true;
                winnerTime = task2.get();
                task1.cancel(true);
            }
        }

        return winnerTime;
    }
}
