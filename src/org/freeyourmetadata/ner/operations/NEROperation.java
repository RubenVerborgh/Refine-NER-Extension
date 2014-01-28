package org.freeyourmetadata.ner.operations;

import java.util.HashMap;
import java.util.Properties;
import java.util.SortedMap;
import java.util.Map;
import java.util.TreeMap;

import org.freeyourmetadata.ner.services.NERService;
import org.freeyourmetadata.ner.services.NERServiceManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;

import com.google.refine.model.AbstractOperation;
import com.google.refine.model.Column;
import com.google.refine.model.Project;
import com.google.refine.operations.EngineDependentOperation;
import com.google.refine.operations.OperationRegistry;
import com.google.refine.process.Process;
import com.google.refine.util.JSONUtilities;

/**
 * Operation that starts a named-entity recognition process
 * @author Ruben Verborgh
 */
public class NEROperation extends EngineDependentOperation {
    private final Column column;
    private final SortedMap<String, NERService> services;
    private final Map<String, Map<String, String>> settings;

    /**
     * Creates a new <tt>NEROperation</tt>
     * @param column The column on which named-entity recognition is performed
     * @param services The services that will be used for named-entity recognition
     * @param settings The settings of the individual services
     * @param engineConfig The faceted browsing engine configuration
     */
    public NEROperation(final Column column, final SortedMap<String, NERService> services,
                        final Map<String, Map<String, String>> settings, final JSONObject engineConfig) {
        super(engineConfig);
        this.column = column;
        this.services = services;
        this.settings = settings;
    }

    /**
     * Recreates a <tt>NEROperation</tt> from a JSON object
     * @param project The project for which the operation is created
     * @param operation A JSON representation of the operation
     * @return A new <tt>NEROperation</tt>
     * @throws Exception when the operation cannot be created
     */
    static public AbstractOperation reconstruct(final Project project, final JSONObject operation) throws Exception {
        final JSONObject engineConfig = operation.getJSONObject("engineConfig");
        final JSONObject parameters = operation.getJSONObject("parameters");
        final NERServiceManager serviceManager = new NERServiceManager();
        final TreeMap<String, NERService> services = new TreeMap<String, NERService>();
        final Map<String, Map<String, String>> settings = new HashMap<String, Map<String, String>>();
        
        // Instantiate all needed services
        for (final String serviceName : JSONUtilities.getStringArray(operation, "services")) {
            // Create the service
            final NERService service = serviceManager.getService(serviceName);
            services.put(serviceName, service);
            
            // Apply the service settings
            final Map<String, String> serviceSettings = new TreeMap<String, String>();
            final JSONObject serviceSetttingsJson = parameters.getJSONObject(serviceName);
            for (final String settingName : service.getExtractionSettings()) {
                try {
                    serviceSettings.put(settingName, serviceSetttingsJson.getString(settingName));
                }
                catch (JSONException e) { }
            }
            settings.put(serviceName, serviceSettings);
        }

        return new NEROperation(project.columnModel.getColumnByName(operation.getString("column")),
                                services, settings, engineConfig);
    }

    /** {@inheritDoc} */
    @Override
    public void write(final JSONWriter writer, final Properties options) throws JSONException {
        writer.object();
        writer.key("op"); writer.value(OperationRegistry.s_opClassToName.get(getClass()));
        writer.key("description"); writer.value(getBriefDescription(null));
        writer.key("engineConfig"); writer.value(getEngineConfig());
        writer.key("column"); writer.value(column.getName());
        writer.key("services");
        JSONUtilities.writeStringArray(writer, services.keySet().toArray(new String[services.size()]));

        writer.key("parameters");
        writer.object();
        for (Map.Entry<String, Map<String,String>> serviceSettings : settings.entrySet()) {
            writer.key(serviceSettings.getKey());
            writer.object();
            for (Map.Entry<String, String> setting : serviceSettings.getValue().entrySet()) {
                writer.key(setting.getKey());
                writer.value(setting.getValue());
            }
            writer.endObject();
        }
        writer.endObject();

        writer.endObject();
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getBriefDescription(final Project project) {
        return String.format("Recognize named entities in column %s", column.getName());
    }
    
    /** {@inheritDoc} */
    @Override
    public Process createProcess(final Project project, final Properties options) throws Exception {
        return new NERProcess(project, column, services, settings, this, getBriefDescription(project), getEngineConfig());
    }
}
