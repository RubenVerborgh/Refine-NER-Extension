package org.freeyourmetadata.ner.services;

/**
 * Interface for named-entity recognition services
 * @author Ruben Verborgh
 */
public interface NERService {
    /**
     * Gets the names of supported properties of the service
     * @return The property names
     */
    public String[] getPropertyNames();
    
    /**
     * Gets the value of the specified property
     * @param name The property name
     * @return The property value
     */
    public String getProperty(String name);
    
    /**
     * Sets the value of the specified property
     * @param name The property name
     * @param value The property value
     */
    public void setProperty(String name, String value);
}
