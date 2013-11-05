package org.freeyourmetadata.ner.services;

import java.net.URI;
import java.util.Set;
import java.util.Map;

/**
 * Interface for named-entity recognition services
 * @author Ruben Verborgh
 */
public interface NERService {
    /**
     * Extracts named entities from the specified text
     * @param text The text
     * @param settings The settings for the extraction
     * @return The extracted named entities
     * @throws Exception if the extraction fails
     */
    public NamedEntity[] extractNamedEntities(String text, final Map<String, String> settings) throws Exception;
    
    /**
     * Gets the names of supported settings of the service
     * @return The setting names
     */
    public Set<String> getServiceSettings();
    
    /**
     * Gets the value of the specified setting
     * @param name The setting name
     * @return The setting value
     */
    public String getServiceSetting(String name);
    
    /**
     * Sets the value of the specified setting
     * @param name The setting name
     * @param value The setting value
     */
    public void setServiceSetting(String name, String value);
    
    /**
     * Gets the names of supported extraction settings of the service
     * @return The runtime setting names
     */
    public Set<String> getExtractionSettings();
    
    /**
     * Gets the default value of the specified extraction setting
     * @param name The setting name
     * @return The setting value
     */
    public String getExtractionSettingDefault(String name);

    /**
     * Sets the default value of the specified extraction setting
     * @param name The setting name
     * @param value The setting value
     */
    public void setExtractionSettingDefault(String name, String value);
    
    /**
     * Indicates whether all necessary service settings have been set
     * @return <tt>true</tt> if all settings are set
     */
    public boolean isConfigured();
    
    /**
     * Gets a URI with documentation about the service
     * @return A documentation URI
     */
    public URI getDocumentationUri();
}
