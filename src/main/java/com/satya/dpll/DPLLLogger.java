package com.satya.dpll;

import com.satya.common.Variable;

import java.util.List;

public class DPLLLogger {

    private final boolean verbose;

    public DPLLLogger(boolean verbose) {
        this.verbose = verbose;
    }

    public void log(String msg) {
        if (verbose) {
            System.out.println(msg);
        }
    }

    public void logAssignments(List<Variable> variables){
        if (verbose) {
            variables.forEach(System.out::println);
        }
    }
}
