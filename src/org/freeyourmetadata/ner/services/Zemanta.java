package org.freeyourmetadata.ner.services;

/**
 * Zemanta service connector
 * @author Ruben Verborgh
 */
public class Zemanta extends NERServiceBase {
    private final static String[] PROPERTYNAMES = { "API key" };
    
    /**
     * Creates a new Zemanta service connector
     */
    public Zemanta() {
        super("Zemanta", PROPERTYNAMES);
    }
}
