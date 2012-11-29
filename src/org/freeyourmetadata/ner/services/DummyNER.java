package org.freeyourmetadata.ner.services;

public class DummyNER extends NERServiceBase {
    private final static String[] PROPERTYNAMES = { "API user", "API key" };
    
    public DummyNER() {
        super("DummyNER", PROPERTYNAMES);
        setProperty("API user", "ABCDEFGHIJKL");
        setProperty("API key",  "KLMNOPQRSTUV");
    }
}
