package org.freeyourmetadata.ner.services;

import java.net.URI;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * DBpedia lookup service connector
 * @author Ruben Verborgh
 */
public class DBpediaLookup extends NERServiceBase {
    private final static String SERVICEBASEURL = "http://spotlight.dbpedia.org/rest/annotate";
    private final static String[] PROPERTYNAMES = { "Confidence", "Support" };

    /**
     * Creates a new DBpedia lookup service connector
     */
    public DBpediaLookup() {
        super(PROPERTYNAMES);
        setProperty("Confidence", "0.5");
        setProperty("Support", "30");
    }
    
    /** {@inheritDoc} */
    protected HttpUriRequest createExtractionRequest(final String text) {
        final HttpUriRequest request = new HttpGet(createExtractionRequestUrl(text));
        request.setHeader("Accept", "application/json");
        return request;
    }

    /** {@inheritDoc} */
    @Override
    protected URI createExtractionRequestUrl(final String text) {
        final StringBuilder uri = new StringBuilder(SERVICEBASEURL);
        uri.append("?confidence=").append(urlEncode(getProperty("Confidence")))
           .append("&support=").append(urlEncode(getProperty("Support")))
           .append("&text=").append(urlEncode(text));
        return createUri(uri.toString());
    }
    
    /** {@inheritDoc} */
    @Override
    protected String[] parseExtractionResponseEntity(final JSONTokener tokener) throws JSONException {
        final JSONObject response = (JSONObject)tokener.nextValue();
        final JSONArray resources = response.getJSONArray("Resources");
        final String[] results = new String[resources.length()];
        for (int i = 0; i < resources.length(); i++) {
            final JSONObject resource = resources.getJSONObject(i);
            results[i] = resource.getString("@URI");
        }
        return results;
    }
}
