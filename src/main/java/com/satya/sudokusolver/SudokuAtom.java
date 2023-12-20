package com.satya.sudokusolver;

import com.satya.common.Atom;

/**
 * represents a Sudoku atom
 */
public class SudokuAtom extends Atom {

    public SudokuAtom(int row, int col, int num) {
        super(getName(row,col,num));
    }

    public int getRow(){
        return Character.getNumericValue(this.getName().charAt(3));
    }

    public int getColumn(){
        return Character.getNumericValue(this.getName().charAt(6));
    }

    public int getNum(){
        return Character.getNumericValue(this.getName().charAt(9));
    }

    public static String getName(int row, int col, int num){
        return "V" + "_r" + row + "_c" + col + "_n" + num;
    }
}
