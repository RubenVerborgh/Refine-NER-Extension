package org.freeyourmetadata.ner.services;

import java.util.HashMap;

public class NERServiceManager {
    private final HashMap<String, NERService> services;
    
    public NERServiceManager() {
        services = new HashMap<String, NERService>();
    }
    
    public void addService(NERService service) {
        services.put(service.getName(), service);
    }
    
    public NERService getService(String name) {
        return services.get(name);
    }
    
    public NERService[] getServices() {
        return services.values().toArray(new NERService[services.size()]);
    }
}
