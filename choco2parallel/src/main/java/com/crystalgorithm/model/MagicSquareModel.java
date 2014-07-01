package com.crystalgorithm.model;

import static choco.Choco.eq;
import static choco.Choco.sum;
import choco.Choco;
import choco.cp.model.CPModel;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;

public class MagicSquareModel implements SolvableModel
{

    private int sizeOfTheMagicSquare;
    private int magicSum;

    private CPModel model;

    private IntegerVariable[] variablesDiagonal2;
    private IntegerVariable[] variablesDiagonal1;
    private IntegerVariable[][] variablesPerColumn;
    private IntegerVariable[][] variablesPerLine;
    private IntegerVariable[] allVariables;

    public MagicSquareModel(int sizeOfTheMagicSquare)
    {
        this.sizeOfTheMagicSquare = sizeOfTheMagicSquare;
        magicSum = (int) ((sizeOfTheMagicSquare * (Math.pow(sizeOfTheMagicSquare, 2) + 1)) / 2);

        this.model = new CPModel();

        createVariables(model);
        createConstraints(model);
    }

    @Override
    public IntegerVariable[] getVariables()
    {
        return allVariables;
    }

    @Override
    public Model getModel()
    {
        return model;
    }

    private void createVariables(Model model)
    {
        createLinesVariables(model);
        createColumnVariables(model);
        createDiagonalsVariables();
        createAllVariables();
    }

    private void createDiagonalsVariables()
    {
        variablesDiagonal1 = new IntegerVariable[sizeOfTheMagicSquare];
        variablesDiagonal2 = new IntegerVariable[sizeOfTheMagicSquare];
        for (int i = 0; i < sizeOfTheMagicSquare; i++)
        {
            variablesDiagonal1[i] = variablesPerLine[i][i];
            variablesDiagonal2[i] = variablesPerLine[sizeOfTheMagicSquare - i - 1][i];
        }
    }

    private void createAllVariables()
    {
        allVariables = new IntegerVariable[(int) Math.pow(sizeOfTheMagicSquare, 2)];
        for (int i = 0; i < Math.pow(sizeOfTheMagicSquare, 2); i++)
        {
            allVariables[i] = variablesPerLine[i / sizeOfTheMagicSquare][i % sizeOfTheMagicSquare];
        }
    }

    private void createColumnVariables(Model model)
    {
        variablesPerColumn = new IntegerVariable[sizeOfTheMagicSquare][sizeOfTheMagicSquare];
        for (int i = 0; i < sizeOfTheMagicSquare; i++)
        {
            for (int j = 0; j < sizeOfTheMagicSquare; j++)
            {
                variablesPerColumn[i][j] = variablesPerLine[j][i];
            }
            model.addConstraint(eq(sum(variablesPerColumn[i]), magicSum));
        }
    }

    private void createLinesVariables(Model model)
    {
        variablesPerLine = new IntegerVariable[sizeOfTheMagicSquare][sizeOfTheMagicSquare];

        for (int i = 0; i < sizeOfTheMagicSquare; i++)
        {
            for (int j = 0; j < sizeOfTheMagicSquare; j++)
            {
                variablesPerLine[i][j] = Choco.makeIntVar("x[" + i + "][" + j + "]", 1,
                        (int) Math.pow(sizeOfTheMagicSquare, 2));
                model.addVariable(variablesPerLine[i][j]);
            }
        }
    }

    private void createConstraints(Model model)
    {
        model.addConstraint(Choco.allDifferent(allVariables));

        for (int i = 0; i < sizeOfTheMagicSquare; i++)
        {
            model.addConstraint(eq(sum(variablesPerLine[i]), magicSum));
        }

        model.addConstraint(eq(sum(variablesDiagonal1), magicSum));
        model.addConstraint(eq(sum(variablesDiagonal2), magicSum));
    }
}
