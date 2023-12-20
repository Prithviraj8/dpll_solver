package com.satya.common;

import java.util.Objects;

/**
 * represents a literal of the CNF form, which is an atom and whether it is negated
 */
public class Literal {
    private final Atom atom;
    private final boolean isNegated;

    public Literal(Atom atom, boolean isNegated) {
        this.atom = atom;
        this.isNegated = isNegated;
    }

    public Atom getAtom() {
        return atom;
    }

    public boolean isNegated() {
        return isNegated;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Literal otherLiteral = (Literal) obj;
        return isNegated == otherLiteral.isNegated && atom.equals(otherLiteral.atom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(atom, isNegated);
    }

    @Override
    public String toString() {
        return isNegated() ? "!" + atom.getName() : atom.getName();
    }
}
