package com.satya.common;

/**
 * represents an atom of the CNF form
 */
public class Atom {
    private final String name;

    public Atom(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
