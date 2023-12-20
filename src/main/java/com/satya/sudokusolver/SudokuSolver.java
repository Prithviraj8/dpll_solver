package com.satya.sudokusolver;

import com.satya.cnf.CNFClause;
import com.satya.common.Atom;
import com.satya.common.Literal;
import com.satya.common.Variable;

import java.util.*;

public class SudokuSolver {

    private final int[][] board = new int[9][9];

    private Configuration configuration;

    public static void main(String[] args) {
        SudokuSolver sudokuSolver = new SudokuSolver();
        sudokuSolver.init(args);
        sudokuSolver.run();
        sudokuSolver.printBoard();
        //System.out.println("Violation count: " + sudokuSolver.countViolatedConstraints(sudokuSolver.board));
    }

    private void init(String[] args) {
        this.configuration = new Configuration();
        this.configuration.configure(args, board);
    }

    private void run() {
        Atom[][][] atoms = new Atom[9][9][10];
        HashSet<Atom> atomsSet = new HashSet<>();
        createAtoms(atoms, atomsSet);
        HashMap<String, Literal> literals = new HashMap<>();
//        List<CNFClause> CNFClauses = generateClauses(board, atoms, literals);
//        List<Variable> assignments = new DPLLSolver(this.configuration.isVerbose(), false).solve(atomsSet, CNFClauses);
//        if (assignments != null) {
//            assignSolutionToBoard(assignments);
//        }
    }

    private void createAtoms(Atom[][][] atoms, HashSet<Atom> atomsSet) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                for (int num = 1; num <= 9; num++) {
                    atoms[row][col][num] = new SudokuAtom(row, col, num);
                    atomsSet.add(atoms[row][col][num]);
                }
            }
        }
    }

    private Set<CNFClause> generateClauses(int[][] board, Atom[][][] atoms, HashMap<String, Literal> literals) {
        Set<CNFClause> CNFClauses = new HashSet<>();
        CNFClauses.addAll(generateCellConstraintClauses(atoms, literals));
        CNFClauses.addAll(generateRowConstraintClauses(atoms, literals));
        CNFClauses.addAll(generateColumnConstraintClauses(atoms, literals));
        CNFClauses.addAll(generateBoxConstraintClauses(atoms, literals));
        CNFClauses.addAll(generatePreFilledBoardClauses(board, atoms, literals));
        return CNFClauses;
    }

    private Set<CNFClause> generatePreFilledBoardClauses(int[][] board, Atom[][][] atoms, HashMap<String, Literal> literals) {
        Set<CNFClause> prefilledCNFClauses = new HashSet<>();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] != 0) {
                    int num = board[row][col];
                    CNFClause assignCNFClause = new CNFClause();
                    assignCNFClause.addLiteral(getLiteral(row, col, num, false, literals, atoms));
                    prefilledCNFClauses.add(assignCNFClause);
                }
            }
        }
        return prefilledCNFClauses;
    }

    private Collection<? extends CNFClause> generateCellConstraintClauses(Atom[][][] atoms, HashMap<String, Literal> literals) {
        Set<CNFClause> cellCNFClauses = new HashSet<>();
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                CNFClause atLeastOneDigitInCell = new CNFClause();
                for (int z = 1; z <= 9; z++) {
                    atLeastOneDigitInCell.addLiteral(getLiteral(x, y, z, false, literals, atoms));
                }
                cellCNFClauses.add(atLeastOneDigitInCell);
            }
        }
        return cellCNFClauses;
    }

    private Collection<? extends CNFClause> generateRowConstraintClauses(Atom[][][] atoms, HashMap<String, Literal> literals) {
        Set<CNFClause> rowCNFClauses = new HashSet<>();
        for (int y = 0; y < 9; y++) {
            for (int z = 1; z <= 9; z++) {
                for (int x = 0; x < 8; x++) {
                    for (int i = x + 1; i < 9; i++) {
                        rowCNFClauses.add(createBinaryClause(getLiteral(x, y, z, true, literals, atoms), getLiteral(i, y, z, true, literals, atoms)));
                    }
                }
            }
        }
        return rowCNFClauses;
    }

    private Collection<? extends CNFClause> generateColumnConstraintClauses(Atom[][][] atoms, HashMap<String, Literal> literals) {
        Set<CNFClause> colCNFClauses = new HashSet<>();
        for (int x = 0; x < 9; x++) {
            for (int z = 1; z <= 9; z++) {
                for (int y = 0; y < 8; y++) {
                    for (int i = y + 1; i < 9; i++) {
                        colCNFClauses.add(createBinaryClause(getLiteral(x, y, z, true, literals, atoms), getLiteral(x, i, z, true, literals, atoms)));
                    }
                }
            }
        }
        return colCNFClauses;
    }

    private Collection<? extends CNFClause> generateBoxConstraintClauses(Atom[][][] atoms, HashMap<String, Literal> literals) {
        Set<CNFClause> boxCNFClauses = new HashSet<>();
        for (int z = 1; z <= 9; z++) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    for (int x = 0; x < 3; x++) {
                        for (int y = 0; y < 3; y++) {
                            for (int k = y + 1; k < 3; k++) {
                                int rowVal = i * 3 + x;
                                int colVal1 = j * 3 + y;
                                int colVal2 = j * 3 + k;
                                boxCNFClauses.add(createBinaryClause(getLiteral(rowVal, colVal1, z, true, literals, atoms), getLiteral(rowVal, colVal2, z, true, literals, atoms)));
                            }
                        }
                    }
                }
            }
        }
        for (int z = 1; z <= 9; z++) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    for (int x = 0; x < 3; x++) {
                        for (int y = 0; y < 3; y++) {
                            for (int k = x + 1; k < 3; k++) {
                                for (int l = 0; l < 3; l++) {
                                    int rowVal1 = i * 3 + x;
                                    int colVal1 = j * 3 + y;
                                    int rowVal2 = i * 3 + k;
                                    int colVal2 = j * 3 + l;
                                    boxCNFClauses.add(createBinaryClause(getLiteral(rowVal1, colVal1, z, true, literals, atoms), getLiteral(rowVal2, colVal2, z, true, literals, atoms)));
                                }
                            }
                        }
                    }
                }
            }
        }
        return boxCNFClauses;
    }

    private void assignSolutionToBoard(List<Variable> variables) {
        variables.stream().filter(var -> var.getAssignmentStatus() == 1)
                .map(var -> (SudokuAtom) var.getAtom())
                .forEach(sudokuAtom -> board[sudokuAtom.getRow()][sudokuAtom.getColumn()] = sudokuAtom.getNum());
    }

    private void printBoard() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                System.out.print(board[row][col] + " ");
            }
            System.out.println();
        }
    }

    private CNFClause createBinaryClause(Literal literal1, Literal literal2) {
        CNFClause binaryCNFClause = new CNFClause();
        binaryCNFClause.addLiteral(literal1);
        binaryCNFClause.addLiteral(literal2);
        return binaryCNFClause;
    }

    private Literal getLiteral(int row, int col, int num, boolean negate, HashMap<String, Literal> literals, Atom[][][] atoms) {
        String literalName = SudokuAtom.getName(row, col, num);
        if (negate) {
            literalName = literalName + "N";
        }
        return literals.computeIfAbsent(literalName, key -> new Literal(atoms[row][col][num], negate));
    }

    public int countViolatedConstraints(int[][] board) {
        int violatedConstraints = 0;
        // Check row constraints
        for (int[] ints : board) {
            Set<Integer> rowSet = new HashSet<>();
            for (int col = 0; col < 9; col++) {
                int value = ints[col];
                if (value != 0) {
                    if (rowSet.contains(value)) {
                        violatedConstraints++;
                    } else {
                        rowSet.add(value);
                    }
                }
            }
        }
        // Check column constraints
        for (int col = 0; col < 9; col++) {
            Set<Integer> colSet = new HashSet<>();
            for (int[] ints : board) {
                int value = ints[col];
                if (value != 0) {
                    if (colSet.contains(value)) {
                        violatedConstraints++;
                    } else {
                        colSet.add(value);
                    }
                }
            }
        }
        // Check box constraints
        int regionSize = (int) Math.sqrt(9);
        for (int startRow = 0; startRow < 9; startRow += regionSize) {
            for (int startCol = 0; startCol < 9; startCol += regionSize) {
                Set<Integer> boxSet = new HashSet<>();
                for (int row = startRow; row < startRow + regionSize; row++) {
                    for (int col = startCol; col < startCol + regionSize; col++) {
                        int value = board[row][col];
                        if (value != 0) {
                            if (boxSet.contains(value)) {
                                violatedConstraints++;
                            } else {
                                boxSet.add(value);
                            }
                        }
                    }
                }
            }
        }
        return violatedConstraints;
    }
}
