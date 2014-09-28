package org.freeyourmetadata.ner.services;

import static org.freeyourmetadata.util.UriUtil.createUri;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.freeyourmetadata.util.ParameterList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * WikiMeta service connector
 * 
 * Parameters:
 * <ul>
 * 	<li>span=[0-n]: the amount of word context used for disambiguation</li>
 * 	<li>lng=[fr|en|es]: the language model (French, English or Spanish)</li>
 * 	<li>semtag=[0|1]: activates semantic labeling</li>
 * </ul>
 * 
 * @author Kevin Van Raepenbusch
 */
public class WikiMeta extends NERServiceBase {
    private final static URI SERVICEBASEURL = createUri("http://www.wikimeta.com/wapi/service");
    private final static URI DOCUMENTATIONURI = createUri("http://www.wikimeta.com/api.html");
    private final static String[] SERVICESETTINGS = { "API key" };
    private final static String[] EXTRACTIONSETTINGS = { "Language", "Span", "Treshold" };
    
    /**
     * Creates a new WikiMeta service connector
     */
	public WikiMeta() {
        super(SERVICEBASEURL, DOCUMENTATIONURI, SERVICESETTINGS, EXTRACTIONSETTINGS);
        setExtractionSettingDefault("Treshold", "10");
        setExtractionSettingDefault("Span", "100");
        setExtractionSettingDefault("Language", "en");
	}
	
	/** {@inheritDoc} */
    public boolean isConfigured() {
        return getServiceSetting("API key").length() > 0;	
    }
    
    /** {@inheritDoc} */
    protected HttpEntity createExtractionRequestBody(final String text, final Map<String, String> extractionSettings)
    throws UnsupportedEncodingException {
        final ParameterList parameters = new ParameterList();
        parameters.add("api",      getServiceSetting("API key"));
        parameters.add("contenu",  text);
        parameters.add("treshold", extractionSettings.get("Treshold"));
        parameters.add("span",     extractionSettings.get("Span"));
        parameters.add("lng",      extractionSettings.get("Language").toUpperCase());
        parameters.add("semtag",   "1");
        return parameters.toEntity();
    }
    
    /**
     * Parses the named-entity recognition response
     * @param response A response of the named-entity extraction service
     * @return The extracted named entities
     * @throws Exception if the response cannot be parsed
     */
    protected NamedEntity[] parseExtractionResponse(final HttpResponse response) throws Exception {
    	final String body = EntityUtils.toString(response.getEntity());
    	
    	// An invalid response is recognized by invalid JSON
    	final JSONObject bodyJson;
    	try { bodyJson = new JSONObject(body); }
    	catch (JSONException error) { throw new Exception(body); }
    	
        return parseExtractionResponse(bodyJson);
    }
    
    /** {@inheritDoc} */
    @Override
    protected NamedEntity[] parseExtractionResponse(final JSONObject response) throws JSONException {
        final JSONArray document = response.getJSONArray("document");
        
        // Find all entities
        final JSONArray entities = document.getJSONObject(2).getJSONArray("Named Entities");
        final NamedEntity[] results = new NamedEntity[entities.length()];
        for (int i = 0; i < entities.length(); i++) {
            final JSONObject entity = entities.getJSONObject(i);
            final String entityText = entity.getString("EN");
            final String scoreString = entity.getString("confidenceScore");
            final double score = scoreString.length() == 0 ? 1.0 : Double.parseDouble(scoreString);
            // First try the "Linked Data" URI, otherwise just the URI
            final String linkedDataUri = entity.getString("LINKEDDATA");
            final String uri = !linkedDataUri.equals("null") ? linkedDataUri : entity.getString("URI");
            results[i] = new NamedEntity(entityText, createUri(uri.equals("NORDF") ? "" : uri), score);
        }
        return results;
    }
}