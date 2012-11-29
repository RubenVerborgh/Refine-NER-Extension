package org.freeyourmetadata.ner.services;

import java.util.HashMap;

/**
 * Abstract base class for named-entity recognition services
 * @author Ruben Verborgh
 */
public abstract class NERServiceBase implements NERService {
    private final String name;
    private final String[] propertyNames;
    private final HashMap<String, String> properties;
    
    /**
     * Creates a new named-entity recognition service base class
     * @param name The name of the service
     * @param propertyNames The names of supported properties
     */
    public NERServiceBase(final String name, final String[] propertyNames) {
        this.name = name;
        this.propertyNames = propertyNames;
        
        properties = new HashMap<String, String>(propertyNames.length);
        for (String propertyName : propertyNames)
            this.properties.put(propertyName, "");
    }
    
    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public String[] getPropertyNames() {
        return propertyNames;
    }

    /** {@inheritDoc} */
    @Override
    public String getProperty(final String name) {
        return properties.get(name);
    }

    /** {@inheritDoc} */
    @Override
    public void setProperty(final String name, final String value) {
        if (!properties.containsKey(name))
            throw new IllegalArgumentException("The property " + name
                                               + " is invalid for " + this.getName() + ".");
        properties.put(name, value == null ? "" : value);
    }
}
