package org.freeyourmetadata.ner.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;

/**
 * Abstract base class for named-entity recognition services
 * with default support for JSON communication (but others are possible)
 * @author Ruben Verborgh
 */
public abstract class NERServiceBase implements NERService {
    /** The empty extraction result, containing no entities. */
    protected final static NamedEntity[] EMPTY_EXTRACTION_RESULT = new NamedEntity[0];
    
    private final static Charset UTF8 = Charset.forName("UTF-8");
    
    private final URI serviceUrl;
    private final HashMap<String, String> serviceSettings;
    private final HashMap<String, String> extractionSettingsDefault;
    private final URI documentationUri;

    /**
     * Creates a new named-entity recognition service base class
     * @param serviceUrl The URL of the service (can be null if not fixed)
     * @param serviceSettings The names of supported service settings
     * @param extractionSettings The names of supported extraction settings
     * @param documentationUri The URI of the service's documentation
     */
    public NERServiceBase(final URI serviceUrl, final URI documentationUri,
    					  final String[] serviceSettings, final String[] extractionSettings) {
        this.serviceUrl = serviceUrl;
        this.documentationUri = documentationUri;
        
        this.serviceSettings = new HashMap<String, String>(serviceSettings.length);
        for (String serviceSetting : serviceSettings)
            this.serviceSettings.put(serviceSetting, "");

        extractionSettingsDefault = new HashMap<String, String>(extractionSettings.length);
        for (String extractionSetting : extractionSettings)
        	extractionSettingsDefault.put(extractionSetting, "");
    }
    
    /** {@inheritDoc} */
    @Override
    public Set<String> getServiceSettings() {
        return serviceSettings.keySet();
    }

    /** {@inheritDoc} */
    @Override
    public String getServiceSetting(final String name) {
        return serviceSettings.get(name);
    }

    /** {@inheritDoc} */
    @Override
    public void setServiceSetting(final String name, final String value) {
        if (!serviceSettings.containsKey(name))
        	throw new IllegalArgumentException("The service setting " + name
                                               + " is invalid for " + getClass().getName() + ".");
        serviceSettings.put(name, value == null ? "" : value);
    }
    
    
    /** {@inheritDoc} */
    @Override
    public Set<String> getExtractionSettings() {
        return extractionSettingsDefault.keySet();
    }

    /** {@inheritDoc} */
    @Override
    public String getExtractionSettingDefault(final String name) {
        return extractionSettingsDefault.get(name);
    }

    /** {@inheritDoc} */
    @Override
    public void setExtractionSettingDefault(final String name, final String value) {
        if (!extractionSettingsDefault.containsKey(name))
            throw new IllegalArgumentException("The extraction setting " + name
                                               + " is invalid for " + getClass().getName() + ".");
        extractionSettingsDefault.put(name, value == null ? "" : value);
    }
    
    /** {@inheritDoc} */
    @Override
    public NamedEntity[] extractNamedEntities(final String text, final Map<String, String> settings) throws Exception {
        final HttpUriRequest request = createExtractionRequest(text, settings);
        return performExtractionRequest(request);
    }
    
    /** {@inheritDoc} */
    public boolean isConfigured() {
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    public URI getDocumentationUri() {
        return documentationUri;
    }
    
    /**
     * Performs the named-entity recognition request
     * @param request The request
     * @return The extracted named entities
     * @throws Exception if the request fails
     */
    protected NamedEntity[] performExtractionRequest(final HttpUriRequest request) throws Exception {
        final DefaultHttpClient httpClient = new DefaultHttpClient();
        final HttpResponse response;
        try { response = httpClient.execute(request); }
        catch (IOException error) { throw new RuntimeException("Could not execute HTTP request", error); }
        return parseExtractionResponse(response);
    }

    /**
     * Creates a named-entity recognition request on the specified text
     * @param text The text to analyze
     * @param settings The settings for the extraction
     * @return The created request
     * @throws Exception if the request cannot be created
     */
    protected HttpUriRequest createExtractionRequest(final String text, final Map<String, String> settings) throws Exception {
        final URI requestUrl = createExtractionRequestUrl(text);
        final HttpEntity body = createExtractionRequestBody(text, settings);
        final HttpPost request = new HttpPost(requestUrl);
        request.setHeader("Accept", "application/json");
        request.setHeader("User-Agent", "Refine NER Extension");
        request.setEntity(body);
        return request;
    }
    
    /**
     * Creates the URL for a named-entity recognition request on the specified text
     * @param text The text to analyze
     * @return The created URL
     */
    protected URI createExtractionRequestUrl(final String text) {
        return serviceUrl;
    }

    /**
     * Creates the body for a named-entity recognition request on the specified text
     * @param text The text to analyze
     * @param settings The settings for the extraction
     * @return The created body entity
     * @throws Exception if the request body cannot be created
     */
    protected HttpEntity createExtractionRequestBody(final String text, final Map<String, String> settings) throws Exception {
        final ByteArrayOutputStream bodyOutput = new ByteArrayOutputStream();
        final JSONWriter bodyWriter = new JSONWriter(new OutputStreamWriter(bodyOutput, UTF8));
        try {
            writeExtractionRequestBody(text, bodyWriter);
        }
        catch (JSONException error) {
            throw new RuntimeException(error);
        }
        try {
            bodyOutput.close();
        }
        catch (IOException e) { }
        final byte[] bodyBytes = bodyOutput.toByteArray();
        final ByteArrayInputStream bodyInput = new ByteArrayInputStream(bodyBytes);
        final HttpEntity body = new InputStreamEntity(bodyInput, bodyBytes.length);
        return body;
    }
    
    /**
     * Writes the body JSON for a named-entity recognition request on the specified text
     * @param text The text to analyze
     * @param body The body writer
     * @throws JSONException if writing the body goes wrong
     */
    protected void writeExtractionRequestBody(final String text, final JSONWriter body) throws JSONException { }
    
    /**
     * Parses the named-entity recognition response
     * @param response A response of the named-entity extraction service
     * @return The extracted named entities
     * @throws Exception if the extraction was not successful
     */
    protected NamedEntity[] parseExtractionResponse(final HttpResponse response) throws Exception {
        final Exception error = parseErrorResponse(response);
        if (error != null) throw error;
        return parseExtractionResponse(new JSONObject((EntityUtils.toString(response.getEntity()))));
    }
    
    /**
     * Parse the named-entity recognition response
     * @param response The response body
     * @return The extracted named entities
     * @throws Exception if the extraction was not successful
     */
    protected NamedEntity[] parseExtractionResponse(final JSONObject response) throws Exception {
        return EMPTY_EXTRACTION_RESULT;
    }
    
    /**
     * Encodes the specified text for use in an URL.
     * @param text The text to encode
     * @return The encoded text
     */
    protected static String urlEncode(String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        }
        catch (UnsupportedEncodingException error) {
            throw new RuntimeException(error);
        }
    }
    
    /**
     * Parses a possible error in the HTTP response
     * @param response The response
     * @return The extracted error, or <tt>null</tt> if none exists
     */
    protected Exception parseErrorResponse(final HttpResponse response) throws Exception {
        if (response.getStatusLine().getStatusCode() < 300) return null;
        try {
            return parseErrorResponse((EntityUtils.toString(response.getEntity())));
        }
        catch (Exception error) {
        	return new Exception(String.format("HTTP error %d", response.getStatusLine().getStatusCode()));
        }
    }
    
    /**
     * Parses a possible error in the HTTP response
     * @param response The response body
     * @return The extracted error, or <tt>null</tt> if none exists
     */
    protected Exception parseErrorResponse(final String response) throws Exception {
        return parseErrorResponse(new JSONObject(response));
    }
    
    /**
     * Parses a possible error in the HTTP response
     * @param response The response body
     * @return The extracted error, or <tt>null</tt> if none exists
     */
    protected Exception parseErrorResponse(final JSONObject response) throws JSONException {
    	throw new UnsupportedOperationException();
    }
}
