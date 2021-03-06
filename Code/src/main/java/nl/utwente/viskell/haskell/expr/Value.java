package nl.utwente.viskell.haskell.expr;

import nl.utwente.viskell.haskell.type.HaskellTypeError;
import nl.utwente.viskell.haskell.type.Type;

/**
 * Value in Haskell. Haskell values are always defined as String in Java. The responsibility of inputting a valid value,
 * e.g. wrapping a String in quotes, is the responsibility of the user.
 */
public class Value extends Expression {
    /**
     * Type of this value.
     */
    private final Type type;

    /**
     * Haskell representation of the value.
     */
    private final String value;

    /**
     * @param type Type of this value.
     * @param value Haskell representation of the value that is valid for the given type. An empty String is not
     *              allowed.
     */
    public Value(final Type type, final String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public final Type inferType() throws HaskellTypeError {
        return this.type;
    }

    /**
     * @return Haskell representation of the value.
     */
    public final String getValue() {
        return this.value;
    }

    @Override
    public final String toHaskell() {
        return "(" + this.value + ")";
    }

    @Override
    public final String toString() {
        return this.value;
    }
}
