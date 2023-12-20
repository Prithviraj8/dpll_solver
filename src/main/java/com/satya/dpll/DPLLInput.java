package com.satya.dpll;

import com.satya.cnf.CNFClause;
import com.satya.common.Atom;

import java.util.*;

public class DPLLInput {

    Set<Atom> atoms;

    HashMap<String, Atom> atomMap = new HashMap<>();
    List<CNFClause> cnfClauses;

    public DPLLInput() {
        createAtoms();
        createCNFClaues();
    }

    public void createCNFClaues() {
        cnfClauses = new ArrayList<>();
        cnfClauses.add(new CNFClause(atomMap).addLiteral("C", true).addLiteral("P", false));
        cnfClauses.add(new CNFClause(atomMap).addLiteral("C", true).addLiteral("Q", false));
        cnfClauses.add(new CNFClause(atomMap).addLiteral("D", true).addLiteral("P", false));
        cnfClauses.add(new CNFClause(atomMap).addLiteral("D", true).addLiteral("Q", false));
        cnfClauses.add(new CNFClause(atomMap).addLiteral("P", true).addLiteral("Q", true).addLiteral("C", false).addLiteral("D", false));;
        cnfClauses.add(new CNFClause(atomMap).addLiteral("D", true).addLiteral("H", true));
        cnfClauses.add(new CNFClause(atomMap).addLiteral("P", true).addLiteral("Q", true));
        cnfClauses.add(new CNFClause(atomMap).addLiteral("P", true).addLiteral("E", true));
        cnfClauses.add(new CNFClause(atomMap).addLiteral("B", true).addLiteral("D", false));
        cnfClauses.add(new CNFClause(atomMap).addLiteral("C", true).addLiteral("P", true).addLiteral("E", false));
        cnfClauses.add(new CNFClause(atomMap).addLiteral("C", false).addLiteral("G", false));
        cnfClauses.add(new CNFClause(atomMap).addLiteral("D", true).addLiteral("E", false).addLiteral("G", true));
    }



    public void createAtoms() {
        atoms = new HashSet<>();
        atoms.add(new Atom("C"));
        atoms.add(new Atom("D"));
        atoms.add(new Atom("P"));
        atoms.add(new Atom("Q"));
        atoms.add(new Atom("H"));
        atoms.add(new Atom("B"));
        atoms.add(new Atom("E"));
        atoms.add(new Atom("G"));
        atoms.forEach(atom -> atomMap.put(atom.getName(), atom));
        createCNFClaues();
    }


    public Set<Atom> getAtoms() {
        return this.atoms;
    }

    public List<CNFClause> getCnfClauses(){
        return this.cnfClauses;
    }
}
