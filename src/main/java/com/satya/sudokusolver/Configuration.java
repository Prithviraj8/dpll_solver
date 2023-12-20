package com.satya.sudokusolver;

public class Configuration {
    private boolean verbose = false;

    public void configure(String[] args, int[][] board) {
        for (String arg : args) {
            if (arg.equals("-v")) {
                verbose = true;
            } else {
                fillBoard(arg, board);
            }
        }
    }
    public boolean isVerbose() {
        return verbose;
    }

    private void fillBoard(String arg, int[][] board) {
        String[] split = arg.split("=");
        if (split.length != 2 || split[0].length() != 2 || split[1].length() != 1) {
            throw new IllegalArgumentException(arg + " does not match [row][col]=[num]");
        }
        int row = Character.getNumericValue(split[0].charAt(0));
        int col = Character.getNumericValue(split[0].charAt(1));
        if (row <= 0 || col <= 0) {
            throw new IllegalArgumentException(arg + " has values out of range 1-9");
        }
        int value = Integer.parseInt(split[1]);
        board[row-1][col-1] = value;
    }
}
