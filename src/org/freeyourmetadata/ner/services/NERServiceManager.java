package org.freeyourmetadata.ner.services;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONWriter;

import com.google.refine.util.JSONUtilities;

/**
 * Manager that reads and stores service configurations in JSON
 * @author Ruben Verborgh
 */
public class NERServiceManager {
    private final HashMap<String, NERService> services;
    private final File settingsFile;
    
    /**
     * Creates a new <tt>NERServiceManager</tt>
     * @param settingsFile JSON file to read and store settings
     * @throws IOException if the settings file cannot be read
     * @throws JSONException if the settings file contains invalid JSON
     * @throws ClassNotFoundException if a service cannot be instantiated
     */
    public NERServiceManager(final File settingsFile) throws IOException, JSONException, ClassNotFoundException {
        this.settingsFile = settingsFile;
        services = new HashMap<String, NERService>();
        
        // Load settings from specified file if it exists, or default settings file otherwise
        final Reader settingsReader = settingsFile.exists() ? new FileReader(settingsFile)
                                      : new InputStreamReader(getClass().getResourceAsStream("DefaultServices.json"));
        final JSONTokener tokener = new JSONTokener(settingsReader);
        updateFrom((JSONArray)tokener.nextValue());
        settingsReader.close();
    }
    
    /**
     * Returns whether the manager contains the specified service
     * @param serviceName The name of the service
     * @return <tt>true</tt> if the manager contains the service
     */
    public boolean hasService(final String serviceName) {
        return services.containsKey(serviceName);
    }
    
    /**
     * Adds the service to the manager
     * @param service The service
     */
    public void addService(final NERService service) {
        services.put(service.getName(), service);
    }
    
    /**
     * Gets the specified service
     * @param name The name of the service
     * @return The service
     */
    public NERService getService(final String name) {
        if (!services.containsKey(name))
            throw new IllegalArgumentException("No service named " + name + " exists.");
        return services.get(name);
    }
    
    /**
     * Gets the service if it exists, or creates one otherwise and adds it to the manager
     * @param serviceName The name of the service
     * @param className The class name of the service to instantiate
     * @return The service
     * @throws ClassNotFoundException if the service cannot be instantiated
     */
    protected NERService getOrCreateService(final String serviceName, final String className) throws ClassNotFoundException {
        final NERService service;
        // Return the service if it exists
        if (hasService(serviceName)) {
            service = getService(serviceName);
        }
        // Create a new service otherwise
        else {
            // Create the service through reflection
            final Class<?> serviceClass = getClass().getClassLoader().loadClass(className);
            try {
                service = (NERService)serviceClass.newInstance();
            }
            // We assume instantiation and access are possible
            catch (InstantiationException error) { throw new RuntimeException(error); }
            catch (IllegalAccessException error) { throw new RuntimeException(error); }
            
            // Add the newly created service
            addService(service);
        }
        return service;
    }
    
    /**
     * Gets all services in the manager
     * @return The services
     */
    public NERService[] getServices() {
        return services.values().toArray(new NERService[services.size()]);
    }
    
    /**
     * Saves the configuration to the settings file
     * @throws IOException if the file cannot be written
     */
    public void save() throws IOException {
        final FileWriter writer = new FileWriter(settingsFile);
        writeTo(new JSONWriter(writer));
        writer.close();
    }
    
    /**
     * Writes the configuration to the specified writer
     * @param output The writer
     */
    public void writeTo(final JSONWriter output) {
        try {
            /* Array of services */
            output.array();
            for (NERService service : getServices()) {
                /* Service object */
                output.object();
                {
                    output.key("name");
                    output.value(service.getName());
                    output.key("class");
                    output.value(service.getClass().getName());
                    
                    /* Service settings object */
                    output.key("settings");
                    output.object();
                    for(String propertyName : service.getPropertyNames()) {
                        output.key(propertyName);
                        output.value(service.getProperty(propertyName));
                    }
                    output.endObject();
                }
                output.endObject();
            }
            output.endArray();
        }
        catch (JSONException e) { /* does not happen */ }
    }
    
    /**
     * Updates the manager's configuration from the JSON array
     * @param serviceValues array of service settings
     * @throws JSONException if the objects in the array are in the wrong format
     * @throws ClassNotFoundException if a service cannot be instantiated
     */
    @SuppressWarnings("unchecked")
    public void updateFrom(final JSONArray serviceValues) throws JSONException, ClassNotFoundException {
        /* Array of services */
        final Object[] services = JSONUtilities.toArray((JSONArray)serviceValues);
        for (Object value : services) {
            /* Service object */
            if (!(value instanceof JSONObject))
                throw new IllegalArgumentException("Value should be an array of JSON objects.");
            final JSONObject serviceValue = (JSONObject)value;
            final NERService service = getOrCreateService(serviceValue.getString("name"), serviceValue.getString("class"));
            
            /* Service settings object */
            if (serviceValue.has("settings")) {
                final JSONObject settings = serviceValue.getJSONObject("settings");
                final Iterator<String> settingNames = settings.keys();
                while (settingNames.hasNext()) {
                    final String settingName = settingNames.next();
                    service.setProperty(settingName, settings.getString(settingName));
                }
            }
        }
    }
}
