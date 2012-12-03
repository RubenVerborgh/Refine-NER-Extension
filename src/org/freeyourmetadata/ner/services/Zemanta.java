package org.freeyourmetadata.ner.services;

import java.net.URI;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Zemanta service connector
 * @author Ruben Verborgh
 */
public class Zemanta extends NERServiceBase {
    private final static String SERVICEBASEURL = "http://papi.zemanta.com/services/rest/0.0/";
    private final static String[] PROPERTYNAMES = { "API key" };
    
    /**
     * Creates a new Zemanta service connector
     */
    public Zemanta() {
        super(PROPERTYNAMES);
    }

    /** {@inheritDoc} */
    @Override
    protected URI createExtractionRequestUrl(final String text) {
        final StringBuilder uri = new StringBuilder(SERVICEBASEURL);
        uri.append("?method=zemanta.suggest_markup")
           .append("&format=json")
           .append("&return_rdf_links=1")
           .append("&api_key=").append(urlEncode(getProperty("API key")))
           .append("&text=").append(urlEncode(text));
        return createUri(uri.toString());
    }
    
    /** {@inheritDoc} */
    @Override
    protected NamedEntity[] parseExtractionResponseEntity(final JSONTokener tokener) throws JSONException {
        // Check response status
        final JSONObject response = (JSONObject)tokener.nextValue();
        if (!"ok".equals(response.getString("status")))
            throw new IllegalArgumentException("The Zemanta request did not succeed.");
        
        // Get mark-up results
        final JSONObject markup = response.getJSONObject("markup");
        final ArrayList<NamedEntity> results = new ArrayList<NamedEntity>();
        // In the mark-up results, find the links
        final JSONArray links = markup.getJSONArray("links");
        for (int i = 0; i < links.length(); i++) {
            // In each link, find the targets
            final JSONObject link = links.getJSONObject(i);
            final JSONArray targets = link.getJSONArray("target");
            // Use the target URLs as results
            for (int j = 0; j < targets.length(); j++) {
                final JSONObject target = targets.getJSONObject(j);
                results.add(new NamedEntity(target.getString("url")));
            }
        }
        return results.toArray(new NamedEntity[results.size()]);
    }
}
