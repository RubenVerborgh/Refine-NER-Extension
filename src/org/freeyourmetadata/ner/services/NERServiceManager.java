package org.freeyourmetadata.ner.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;

import com.google.refine.util.JSONUtilities;

public class NERServiceManager {
    private final HashMap<String, NERService> services;
    private final File settingsFile;
    
    public NERServiceManager(File settingsFile) {
        this.settingsFile = settingsFile;
        System.err.println(this.settingsFile.toString());
        services = new HashMap<String, NERService>();
    }
    
    public void addService(NERService service) {
        services.put(service.getName(), service);
    }
    
    public NERService getService(String name) {
        if (!services.containsKey(name))
            throw new IllegalArgumentException("No service named " + name + " exists.");
        return services.get(name);
    }
    
    public NERService[] getServices() {
        return services.values().toArray(new NERService[services.size()]);
    }
    
    public void save() throws JSONException, IOException {
        final FileWriter writer = new FileWriter(settingsFile);
        writeTo(new JSONWriter(writer));
        writer.close();
    }
    
    public void writeTo(JSONWriter output) throws JSONException {
        /* Array of services */
        output.array();
        for(NERService service : getServices()) {
            /* Service object */
            output.object();
            output.key("name");
            output.value(service.getName());
            
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
    public void updateFrom(JSONArray serviceValues) throws JSONException {
        final Object[] services = JSONUtilities.toArray((JSONArray)serviceValues);
        for(Object value : services) {
            if(!(value instanceof JSONObject))
                throw new IllegalArgumentException("Value should be an array of JSON objects.");
            final JSONObject serviceValue = (JSONObject)value;
            final NERService service = getService(serviceValue.getString("name"));
            final JSONObject settings = serviceValue.getJSONObject("settings");
            final Iterator<String> settingNames = settings.keys();
            while(settingNames.hasNext()) {
                final String settingName = settingNames.next();
                service.setProperty(settingName, settings.getString(settingName));
            }
        }
    }
}
