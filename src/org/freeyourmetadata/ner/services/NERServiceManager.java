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

public class NERServiceManager {
    private final HashMap<String, NERService> services;
    private final File settingsFile;
    
    public NERServiceManager(final File settingsFile) throws IOException, JSONException, ClassNotFoundException {
        this.settingsFile = settingsFile;
        services = new HashMap<String, NERService>();
        
        final Reader settingsReader = settingsFile.exists() ? new FileReader(settingsFile)
                                      : new InputStreamReader(getClass().getResourceAsStream("DefaultServices.json"));
        final JSONTokener tokener = new JSONTokener(settingsReader);
        updateFrom((JSONArray)tokener.nextValue());
        settingsReader.close();
    }
    
    public boolean hasService(final String serviceName) {
        return services.containsKey(serviceName);
    }
    
    public void addService(final NERService service) {
        services.put(service.getName(), service);
    }
    
    public NERService getService(final String name) {
        if (!services.containsKey(name))
            throw new IllegalArgumentException("No service named " + name + " exists.");
        return services.get(name);
    }
    
    protected NERService getOrCreateService(final String serviceName, final String className) throws ClassNotFoundException {
        final NERService service;
        if (hasService(serviceName)) {
            service = getService(serviceName);
        }
        else {
            final Class<?> serviceClass = getClass().getClassLoader().loadClass(className);
            try {
                service = (NERService)serviceClass.newInstance();
            }
            catch (InstantiationException error) { throw new RuntimeException(error); }
            catch (IllegalAccessException error) { throw new RuntimeException(error); }
            addService(service);
        }
        return service;
    }
    
    public NERService[] getServices() {
        return services.values().toArray(new NERService[services.size()]);
    }
    
    public void save() throws JSONException, IOException {
        final FileWriter writer = new FileWriter(settingsFile);
        writeTo(new JSONWriter(writer));
        writer.close();
    }
    
    public void writeTo(final JSONWriter output) throws JSONException {
        /* Array of services */
        output.array();
        for (NERService service : getServices()) {
            /* Service object */
            output.object();
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
            
            output.endObject();
        }
        output.endArray();
    }
    
    @SuppressWarnings("unchecked")
    public void updateFrom(final JSONArray serviceValues) throws JSONException, ClassNotFoundException {
        final Object[] services = JSONUtilities.toArray((JSONArray)serviceValues);
        for (Object value : services) {
            if (!(value instanceof JSONObject))
                throw new IllegalArgumentException("Value should be an array of JSON objects.");
            final JSONObject serviceValue = (JSONObject)value;
            final NERService service = getOrCreateService(serviceValue.getString("name"), serviceValue.getString("class"));
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
