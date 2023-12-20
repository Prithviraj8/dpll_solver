package com.satya.cnf;

import com.satya.common.Atom;
import com.satya.common.Literal;
import com.satya.common.Variable;

import java.util.*;

/**
 * Represents cnf clauses with a set of literals, implicitly all literals are or together
 */
public class CNFClause {
    private final List<Literal> literals;

    HashMap<String, Atom> atomMap = new HashMap<>();
    public CNFClause() {
        literals = new ArrayList<>();
    }

    public CNFClause(HashMap<String, Atom> atomMap) {
        this.atomMap = atomMap;
        literals = new ArrayList<>();
    }

    public CNFClause addLiteral(Literal literal) {
        literals.add(literal);
        return this;
    }

    public CNFClause addLiteral(String name, boolean isNegate) {
        literals.add(new Literal(atomMap.get(name), isNegate));
        return this;
    }

    public List<Literal> getLiterals() {
        return this.literals;
    }

    public Literal returnLiteralContainingAtom(Atom atom) {
        return literals.stream().filter(l -> l.getAtom() == atom).findFirst().orElse(null);
    }

    public Literal getSingleLiteral() {
        return literals.stream().findFirst().orElse(null);
    }

    /**
     *
     * @param var variable
     * @return whether clause is satisfied using this variable
     */
    public boolean isSatisfied(Variable var) {
        Literal clauseLiteral = this.returnLiteralContainingAtom(var.getAtom());
        return (clauseLiteral != null) && ((var.getAssignmentStatus() == 1 && !clauseLiteral.isNegated()) || (var.getAssignmentStatus() == -1 && clauseLiteral.isNegated()));
    }

    /**
     *
     * @param variables list of variables
     * @return whether clause is satisfied using list of variables
     */
    public boolean isSatisfied(List<Variable> variables) {
        return this.getLiterals().stream().anyMatch(literal -> {
            Variable var = getVariable(literal.getAtom(), variables);
            return (var.getAssignmentStatus() == 1 && !literal.isNegated()) || (var.getAssignmentStatus() == -1 && literal.isNegated());
        });
    }

    /**
     *
     * @param variables list of variables
     * @return whether clause is unsatisfied using list of variables
     */
    public boolean isUnSatisfied(List<Variable> variables) {
        return literals.stream().allMatch(literal -> {
            Variable var = getVariable(literal.getAtom(), variables);
            return var.getAssignmentStatus() != 0 && !((var.getAssignmentStatus() == 1 && !literal.isNegated()) || (var.getAssignmentStatus() == -1 && literal.isNegated()));
        });
    }

    private Variable getVariable(Atom atom, List<Variable> variables) {
        return variables.stream().filter(v -> v.getAtom() == atom).findFirst().orElse(null);
    }

    public void removeLiteral(Variable var) {
        Literal clauseLiteral = returnLiteralContainingAtom(var.getAtom());
        if (clauseLiteral != null) {
            literals.remove(clauseLiteral);
        }
    }

    public CNFClause copy() {
        CNFClause newCNFClause = new CNFClause();
        for (Literal literal : this.getLiterals()) {
            newCNFClause.addLiteral(literal);
        }
        return newCNFClause;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Literal literal : literals) {
            sb.append(literal.toString()).append(" ");
        }
        return sb.toString();
    }
}
