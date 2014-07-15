package com.crystalgorithm;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        executeShared();
        executeShared();
        executeShared();
        executeShared();
        executeShared();

        System.exit(0);
    }

    private static void executeShared() throws Exception
    {
        FutureTaskRaceOrganizer.raceTwoChocoSolversWithSharedWeights();
    }

}
