package org.freeyourmetadata.ner.services;

import java.util.HashMap;

public abstract class NERServiceBase implements NERService {
    private final String name;
    private final String[] propertyNames;
    private final HashMap<String, String> properties;
    
    public NERServiceBase(String name, String[] propertyNames) {
        this.name = name;
        this.propertyNames = propertyNames;
        
        properties = new HashMap<String, String>(propertyNames.length);
        for (String propertyName : propertyNames)
            this.properties.put(propertyName, "");
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getPropertyNames() {
        return propertyNames;
    }

    @Override
    public String getProperty(String name) {
        return properties.get(name);
    }

    @Override
    public void setProperty(String name, String value) {
        if (!properties.containsKey(name))
            throw new IllegalArgumentException("The property " + name
                                               + " is invalid for " + this.getName() + ".");
        properties.put(name, value == null ? "" : value);
    }
}
