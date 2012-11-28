package org.freeyourmetadata.ner.services;

public class Zemanta extends NERServiceBase {
    private final static String[] PROPERTYNAMES = { "API key" };
    
    public Zemanta() {
        super("Zemanta", PROPERTYNAMES);
    }
}
