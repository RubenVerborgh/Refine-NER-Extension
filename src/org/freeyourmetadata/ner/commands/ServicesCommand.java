package org.freeyourmetadata.ner.commands;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.freeyourmetadata.ner.services.NERService;
import org.freeyourmetadata.ner.services.NERServiceManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;

import com.google.refine.util.JSONUtilities;

public class ServicesCommand extends NERCommand {
    private final NERServiceManager serviceManager;
    
    public ServicesCommand(NERServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }
    
    @Override
    public void get(HttpServletRequest request, JSONWriter response) throws Exception {
        /* Array of services */
        response.array();
        for(NERService service : serviceManager.getServices()) {
            /* Service object */
            response.object();
            response.key("name");
            response.value(service.getName());
            
            /* Service settings object */
            response.key("settings");
            response.object();
            for(String propertyName : service.getPropertyNames()) {
                response.key(propertyName);
                response.value(service.getProperty(propertyName));
            }
            response.endObject();
            
            response.endObject();
        }
        response.endArray();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void put(HttpServletRequest request, Object body, JSONWriter response) throws Exception {
        if(!(body instanceof JSONArray))
            throw new IllegalArgumentException("Body should be a JSON array.");
        final Object[] services = JSONUtilities.toArray((JSONArray)body);
        for(Object value : services) {
            if(!(value instanceof JSONObject))
                throw new IllegalArgumentException("Body should be an array of JSON objects.");
            final JSONObject serviceValue = (JSONObject)value;
            final NERService service = serviceManager.getService(serviceValue.getString("name"));
            final JSONObject settings = serviceValue.getJSONObject("settings");
            final Iterator<String> settingNames = settings.keys();
            while(settingNames.hasNext()) {
                final String settingName = settingNames.next();
                service.setProperty(settingName, settings.getString(settingName));
            }
        }
    }
}
