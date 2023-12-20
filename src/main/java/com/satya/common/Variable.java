package com.satya.common;

/**
 * represents a variable for each atom whose assignment are stored in this variable
 */
public class Variable {

    private final Atom atom;

    private int assignmentStatus = 0; // 0 -> unassigned, 1 -> true, -1 -> false

    public Variable(Atom atom) {
        this.atom = atom;
    }

    public void assign(boolean value){
        if(value){
            this.assignmentStatus = 1;
        }else{
            this.assignmentStatus = -1;
        }
    }
    public void removeAssignment(){
        this.assignmentStatus = 0;
    }

    public Atom getAtom(){
        return this.atom;
    }
    public int getAssignmentStatus() {
        return this.assignmentStatus;
    }

    public String toString() {
        return assignmentStatus == 1 ? atom.getName() + "=true" : atom.getName() + "=false";
    }

}
