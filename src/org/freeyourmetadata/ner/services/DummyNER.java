package org.freeyourmetadata.ner.services;

public class DummyNER extends NERServiceBase {
    private final static String[] PROPERTYNAMES = { "API key" };
    
    public DummyNER() {
        super("DummyNER", PROPERTYNAMES);
    }
}
