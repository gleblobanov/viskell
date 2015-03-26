package nl.utwente.group10.haskell.catalog;

import nl.utwente.group10.haskell.type.Type;

/** A function entry in the Haskell catalog. */
public class Entry {
    /** The name of this Entry. */
    private final String name;

    /** The category this Entry belongs to. */
    private final String category;

    /** The signature of this Entry. */
    private final String signature;

    /** The documentation string for this Entry. */
    private final String documentation;

    /**
     * Creates a new Entry instance.
     * @param name The function name of this Entry.
     * @param category The category this Entry belongs to.
     * @param signature The signature of this Entry.
     * @param documentation The documentation for this Entry.
     */
    Entry(final String name, final String category, final String signature, final String documentation) {
        this.name = name;
        this.category = category;
        this.signature = signature;
        this.documentation = documentation;
    }

    /**
     * @return The function name of this Entry.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * @return The category this Entry belongs to.
     */
    public final String getCategory() {
        return this.category;
    }

    /**
     * @return The signature of this Entry.
     */
    public final String getSignature() {
        return this.signature;
    }

    /**
     * @return The documentation of this Entry.
     */
    public final String getDocumentation() {
        return this.documentation;
    }

    /**
     * Parses and returns the Type of the function in this Entry.
     * @return The Type of this Entry.
     */
    public final Type getType() {
        return null; // TODO Let this return the type of this Entry when the type parser is fixed.
    }
}
