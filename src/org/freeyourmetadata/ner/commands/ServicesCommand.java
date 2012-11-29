package org.freeyourmetadata.ner.commands;

import javax.servlet.http.HttpServletRequest;

import org.freeyourmetadata.ner.services.NERServiceManager;
import org.json.JSONArray;
import org.json.JSONWriter;

public class ServicesCommand extends NERCommand {
    private final NERServiceManager serviceManager;
    
    public ServicesCommand(NERServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }
    
    @Override
    public void get(HttpServletRequest request, JSONWriter response) throws Exception {
        serviceManager.writeTo(response);
    }
    
    @Override
    public void put(HttpServletRequest request, Object body, JSONWriter response) throws Exception {
        if(!(body instanceof JSONArray))
            throw new IllegalArgumentException("Body should be a JSON array.");
        serviceManager.updateFrom((JSONArray)body);
    }
}
