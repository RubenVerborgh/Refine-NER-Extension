package org.freeyourmetadata.ner.services;

import static org.freeyourmetadata.util.UriUtil.createUri;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.freeyourmetadata.util.ParameterList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Zemanta service connector
 * @author Ruben Verborgh
 */
public class Zemanta extends NERServiceBase {
    private final static URI SERVICEBASEURL = createUri("http://papi.zemanta.com/services/rest/0.0/");
    private final static URI DOCUMENTATIONURI = createUri("http://freeyourmetadata.org/named-entity-extraction/zemanta/");
    private final static String[] SERVICESETTINGS = { "API key" };
    private final static String[] EXTRACTIONSETTINGS = {};
    
    /**
     * Creates a new Zemanta service connector
     */
    public Zemanta() {
        super(SERVICEBASEURL, DOCUMENTATIONURI, SERVICESETTINGS, EXTRACTIONSETTINGS);
    }
    
    /** {@inheritDoc} */
    public boolean isConfigured() {
        return getServiceSetting("API key").length() > 0;
    }
    
    /** {@inheritDoc} */
    protected HttpEntity createExtractionRequestBody(final String text, final Map<String, String> extractionSettings)
    throws UnsupportedEncodingException {
        final ParameterList parameters = new ParameterList();
        parameters.add("method", "zemanta.suggest_markup");
        parameters.add("format", "json");
        parameters.add("return_rdf_links", "1");
        parameters.add("api_key", getServiceSetting("API key"));
        parameters.add("text", text);
        return parameters.toEntity();
    }
    
    /** {@inheritDoc} */
    @Override
    protected NamedEntity[] parseExtractionResponse(final JSONObject response) throws JSONException {
        // Check response status
    	final String status = response.getString("status");
        if (!"ok".equals(status))
            throw new RuntimeException(status);
        
        // Get mark-up results
        final JSONObject markup = response.getJSONObject("markup");
        final ArrayList<NamedEntity> results = new ArrayList<NamedEntity>();
        // In the mark-up results, find the links
        final JSONArray links = markup.getJSONArray("links");
        for (int i = 0; i < links.length(); i++) {
            // In each link, find the targets
            final JSONObject link = links.getJSONObject(i);
            final JSONArray targets = link.getJSONArray("target");
            final String label = targets.getJSONObject(0).getString("title");
            // Make a disambiguation from each target
            final Disambiguation[] disambiguations = new Disambiguation[targets.length()];
            for (int j = 0; j < targets.length(); j++) {
                final JSONObject target = targets.getJSONObject(j);
                disambiguations[j] = new Disambiguation(target.getString("title"), createUri(target.getString("url")));
            }
            results.add(new NamedEntity(label, disambiguations));
        }
        return results.toArray(new NamedEntity[results.size()]);
    }
    
    /** {@inheritDoc} */
    @Override
    protected Exception extractError(final String response) throws Exception {
        return new Exception(response);
    }
}
