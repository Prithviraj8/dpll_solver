package com.satya.dpll;

import com.satya.common.Atom;
import com.satya.cnf.CNFClause;
import com.satya.common.Literal;
import com.satya.common.Variable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DPLL solver given a List of cnf clauses return as list of variables with assignments to atoms
 * such that all the cnf clauses are satisfied.
 */
public class DPLLSolver {

    private final DPLLLogger logger;

    private final boolean defaultAssignment;

    public DPLLSolver(boolean verbose, boolean defaultAssignment) {
        logger = new DPLLLogger(verbose);
        this.defaultAssignment = defaultAssignment;
    }

    public static void main(String[] args) {
        DPLLInput dpllInput = new DPLLInput();
        Set<Atom> atomsList = dpllInput.getAtoms();
        List<CNFClause> cnfClauses = dpllInput.getCnfClauses();
        new DPLLSolver(true, false).solve(atomsList, cnfClauses);
    }

    public List<Variable> solve(Set<Atom> atoms, List<CNFClause> cnfClauses) {
        List<Variable> variables = atoms.stream().map(Variable::new).collect(Collectors.toList());
        variables = solve(cnfClauses, variables, new ArrayList<>());
        if (variables == null) {
            System.out.println("NO VALID ASSIGNMENT");
            return null;
        }
        return variables;
    }

    private List<Variable> solve(List<CNFClause> cnfClauses, List<Variable> variables, List<Variable> trackAssignedVariables) {
        do {
            if (allClausesSatisfied(cnfClauses, variables)) {
                assignUnboundedVariables(variables, this.defaultAssignment);
                logger.logAssignments(variables);
                return variables;
            }
            if (conflictDetected(cnfClauses, variables)) {
                return null;
            }
            Variable assignedVariable = findAndPropagatePureLiteralClause(cnfClauses, variables);
            if (assignedVariable != null) {
                trackAssignedVariables.add(assignedVariable);
                continue;
            }
            assignedVariable = findAndPropagateUnitLiteralClause(cnfClauses, variables);
            if (assignedVariable != null) {
                trackAssignedVariables.add(assignedVariable);
                continue;
            }
            break; // no easy case found
        } while (true);
        Variable decisionVariable = selectUnassignedVariable(variables);
        // Guess true
        cnfClauses.forEach(System.out::println);
        logger.log("hard case: guess " + decisionVariable.getAtom().getName() + "=true");
        List<Variable> newAssignments = guessAndPropagate(decisionVariable, true, cnfClauses, variables);
        if (newAssignments != null) {
            return newAssignments;
        }
        // Guess false
        cnfClauses.forEach(System.out::println);
        logger.log("contradiction: backtrack guess " + decisionVariable.getAtom().getName() + "=false");
        return guessAndPropagate(decisionVariable, false, cnfClauses, variables);
    }

    private List<Variable> guessAndPropagate(Variable decisionVariable, boolean guess, List<CNFClause> cnfClauses, List<Variable> variables) {
        decisionVariable.assign(guess);
        List<CNFClause> newCNFClauses = copyClauses(cnfClauses);
        propagate(decisionVariable, newCNFClauses);
        // this list contains all the assignments for this decision
        List<Variable> trackAssignVariablesForThisDecision = new ArrayList<>();
        List<Variable> newAssignments = this.solve(newCNFClauses, variables, trackAssignVariablesForThisDecision);
        if (newAssignments == null) {
            decisionVariable.removeAssignment();
            // if decision was wrong, remove assignment for all the variables assigned after the decision
            trackAssignVariablesForThisDecision.forEach(Variable::removeAssignment);
        }
        return newAssignments;
    }

    private Variable findAndPropagatePureLiteralClause(List<CNFClause> CNFClauses, List<Variable> variables) {
        Literal pureLiteral = getPureLiteral(CNFClauses);
        if (pureLiteral != null) {
            Variable assignedVariable = obviousAssign(pureLiteral, variables);
            CNFClauses.forEach(System.out::println);
            logger.log("easy case: pure literal " + pureLiteral);
            propagate(assignedVariable, CNFClauses);
            return assignedVariable;
        }
        return null;
    }

    private Variable findAndPropagateUnitLiteralClause(List<CNFClause> CNFClauses, List<Variable> variables) {
        CNFClause singleLiteralCNFClause = CNFClauses.stream().filter(c -> c.getLiterals().size() == 1).sorted((a, b) -> {
            if (!a.toString().startsWith("!") && !b.toString().startsWith("!")) {
                return a.toString().compareTo(b.toString());
            } else if (!a.toString().startsWith("!")) {
                return -1;
            } else if (!b.toString().startsWith("!")) {
                return 1;
            } else {
                return a.toString().substring(1).compareTo(b.toString().substring(1));
            }
        }).findFirst().orElse(null);
        if (singleLiteralCNFClause != null) {
            Literal singleLiteral = singleLiteralCNFClause.getSingleLiteral();
            Variable assignedVariable = obviousAssign(singleLiteral, variables);
            CNFClauses.forEach(System.out::println);
            logger.log("easy case: unit literal " + singleLiteral);
            propagate(assignedVariable, CNFClauses);
            return assignedVariable;
        }
        return null;
    }

    private boolean allClausesSatisfied(List<CNFClause> CNFClauses, List<Variable> variables) {
        return CNFClauses.isEmpty() || CNFClauses.stream().allMatch(CNFClause -> CNFClause.isSatisfied(variables));
    }

    private Variable obviousAssign(Literal literal, List<Variable> variables) {
        Variable var = getVariable(literal.getAtom(), variables);
        if (var != null) {
            var.assign(!literal.isNegated());
        }
        return var;
    }

    private List<CNFClause> copyClauses(List<CNFClause> cnfClauses) {
        List<CNFClause> copiedCNFClauses = new ArrayList<>();
        cnfClauses.forEach(clause -> copiedCNFClauses.add(clause.copy()));
        return copiedCNFClauses;
    }

    private void propagate(Variable var, List<CNFClause> CNFClauses) {
        CNFClauses.removeIf(CNFClause -> CNFClause.isSatisfied(var));
        CNFClauses.forEach(CNFClause -> CNFClause.removeLiteral(var));
    }

    private Literal getPureLiteral(List<CNFClause> CNFClauses) {
        List<Literal> literals = CNFClauses.stream()
                .flatMap(CNFClause -> CNFClause.getLiterals().stream())
                .collect(Collectors.toList());
        return literals.stream().sorted((a, b) -> {
            if (!a.toString().startsWith("!") && !b.toString().startsWith("!")) {
                return a.toString().compareTo(b.toString());
            } else if (!a.toString().startsWith("!")) {
                return -1;
            } else if (!b.toString().startsWith("!")) {
                return 1;
            } else {
                return a.toString().substring(1).compareTo(b.toString().substring(1));
            }
        }).filter(literal -> !literals.contains(ComplimentLiteral(literal))).findFirst().orElse(null);
    }

    private Literal ComplimentLiteral(Literal literal) {
        return new Literal(literal.getAtom(), !literal.isNegated());
    }

    private void assignUnboundedVariables(List<Variable> variables, boolean value) {
        variables.stream().filter(variable -> variable.getAssignmentStatus() == 0).forEach(variable ->
        {
            variable.assign(value);
            System.out.println("unbounded:" + variable);
        });
    }

    private Variable selectUnassignedVariable(List<Variable> variables) {
        // lexicographical ordering based on variable name
        variables.sort(Comparator.comparing(v -> v.getAtom().getName()));
        return variables.stream().filter(v -> v.getAssignmentStatus() == 0).findFirst().orElse(null);
    }

    private boolean conflictDetected(List<CNFClause> CNFClauses, List<Variable> variables) {
        return CNFClauses.stream().anyMatch(CNFClause -> CNFClause.isUnSatisfied(variables));
    }

    private Variable getVariable(Atom atom, List<Variable> variables) {
        return variables.stream().filter(v -> v.getAtom() == atom).findFirst().orElse(null);
    }
}
