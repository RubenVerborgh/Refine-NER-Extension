package org.freeyourmetadata.ner.services;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Alchemy service connector
 * @author Ruben Verborgh
 */
public class Alchemy extends NERServiceBase {
    private final static String SERVICEBASEURL = "http://access.alchemyapi.com/calls/text/TextGetRankedNamedEntities";
    private final static String[] PROPERTYNAMES = { "API key" };
    private final static HashSet<String> NONURIFIELDS = new HashSet<String>(
            Arrays.asList(new String[]{ "subType", "name", "website" }));
    
    /**
     * Creates a new Alchemy service connector
     */
    public Alchemy() {
        super(PROPERTYNAMES);
    }
    
    /** {@inheritDoc} */
    protected HttpUriRequest createExtractionRequest(final String text) {
        return new HttpGet(createExtractionRequestUrl(text));
    }
    
    /** {@inheritDoc} */
    @Override
    protected URI createExtractionRequestUrl(final String text) {
        final StringBuilder uri = new StringBuilder(SERVICEBASEURL);
        uri.append("?apikey=").append(urlEncode(getProperty("API key")))
           .append("&outputMode=json")
           .append("&text=").append(urlEncode(text));
        return createUri(uri.toString());
    }
    
    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    protected NamedEntity[] parseExtractionResponseEntity(final JSONTokener tokener) throws JSONException {
        // Check response status
        final JSONObject response = (JSONObject)tokener.nextValue();
        if (!"OK".equals(response.getString("status")))
            throw new IllegalArgumentException("The Alchemy request did not succeed.");
        // Find all entities
        final JSONArray entities = response.getJSONArray("entities");
        final NamedEntity[] results = new NamedEntity[entities.length()];
        for (int i = 0; i < results.length; i++) {
            final JSONObject entity = entities.getJSONObject(i);
            final String label = entity.getString("text");
            // Find possible URLs in the entities
            final HashSet<URI> uris = new HashSet<URI>();
            if (entity.has("disambiguated")) {
                final JSONObject disambiguated = entity.getJSONObject("disambiguated");
                final Iterator<String> keyIterator = disambiguated.keys();
                while (keyIterator.hasNext()) {
                    final String key = keyIterator.next();
                    if (!NONURIFIELDS.contains(key))
                        uris.add(createUri(disambiguated.getString(key)));
                }
            }
            // Create new named entity for the result
            results[i] = new NamedEntity(label, uris.toArray(new URI[uris.size()]));
        }
        return results;
    }
}
