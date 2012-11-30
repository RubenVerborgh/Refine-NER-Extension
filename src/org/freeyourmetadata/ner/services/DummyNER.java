package org.freeyourmetadata.ner.services;

/**
 * DummyNER service connector
 * @author Ruben Verborgh
 */
public class DummyNER extends NERServiceBase {
    private final static String[] PROPERTYNAMES = { "API user", "API key" };
    
    /**
     * Create a new DummyNER service connector
     */
    public DummyNER() {
        super(PROPERTYNAMES);
        setProperty("API user", "ABCDEFGHIJKL");
        setProperty("API key",  "KLMNOPQRSTUV");
    }
}
