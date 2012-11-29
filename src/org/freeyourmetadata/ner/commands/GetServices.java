package org.freeyourmetadata.ner.commands;

import javax.servlet.http.HttpServletRequest;

import org.freeyourmetadata.ner.services.NERService;
import org.freeyourmetadata.ner.services.NERServiceManager;
import org.json.JSONWriter;

public class GetServices extends NERCommand {
    private final NERServiceManager serviceManager;
    
    public GetServices(NERServiceManager serviceManager) {
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
}
