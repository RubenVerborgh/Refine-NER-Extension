package org.freeyourmetadata.ner.services;

import static org.freeyourmetadata.util.UriUtil.createUri;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.common.base.Charsets;

/**
 * DummyNER service connector
 * @author Ruben Verborgh
 */
public class DummyNER extends NERServiceBase {
    private final static URI SERVICEURL = createUri("http://dummyner.freeyourmetadata.org/");
    private final static String[] SERVICESETTINGS = { "API user", "API key" };
    private final static String[] EXTRACTIONSETTINGS = { "Confidence" };

    /**
     * Create a new DummyNER service connector
     */
    public DummyNER() {
        super(SERVICEURL, null, SERVICESETTINGS, EXTRACTIONSETTINGS);
        setServiceSetting("API user", "ABCDEFGHIJKL");
        setServiceSetting("API key",  "KLMNOPQRSTUV");
        setExtractionSettingDefault("Confidence", "0.5");
    }
    
    /** {@inheritDoc} */
    public boolean isConfigured() {
        return getServiceSetting("API user").length() > 0 && getServiceSetting("API key").length() > 0;
    }
    
    /** {@inheritDoc} */
    @Override
    protected HttpEntity createExtractionRequestBody(final String text, final Map<String, String> extractionSettings) {
        final byte[] textBytes = text.getBytes(Charsets.UTF_8);
        final ByteArrayInputStream textStream = new ByteArrayInputStream(textBytes);
        return new InputStreamEntity(textStream, textBytes.length);
    }
    
    /** {@inheritDoc} */
    @Override
    protected NamedEntity[] parseExtractionResponseEntity(final JSONTokener tokener) throws JSONException {
        final JSONArray resultsJson = (JSONArray)tokener.nextValue();
        final NamedEntity[] results = new NamedEntity[resultsJson.length()];
        for (int i = 0; i < results.length; i++)
            results[i] = new NamedEntity(resultsJson.getString(i));
        return results;
    }
    
    /** {@inheritDoc} */
    @Override
    protected Exception extractError(final JSONTokener tokener) throws JSONException {
    	final JSONObject response = (JSONObject)tokener.nextValue();
    	return new Exception(response.getString("message"));
    }
}
