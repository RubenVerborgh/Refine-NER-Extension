package org.freeyourmetadata.ner.services;

import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;

/**
 * A named entity with a label and URIs
 * @author Ruben Verborgh
 */
public class NamedEntity {
    private final static URI[] EMPTY_URI_SET = new URI[0];
    
    private final String label;
    private final URI[] uris;
    
    /**
     * Creates a new named entity without URIs
     * @param label The label of the entity
     */
    public NamedEntity(final String label) {
        this(label, EMPTY_URI_SET);
    }
    
    /**
     * Creates a new named entity
     * @param label The label of the entity
     * @param uris The URIs of the entity
     */
    public NamedEntity(final String label, final URI[] uris) {
        this.label = label;
        this.uris = uris;
    }
    
    /**
     * Creates a new named entity from a JSON representation
     * @param json The JSON representation of the named entity
     * @throws JSONException if the JSON is not correctly structured
     */
    public NamedEntity(final JSONObject json) throws JSONException {
        this.label = json.getString("label");
        final JSONArray urisJson = json.getJSONArray("uris");
        this.uris = new URI[urisJson.length()];
        for (int i = 0; i < uris.length; i++) {
            try { uris[i] = new URI(urisJson.getString(i)); }
            catch (URISyntaxException e) {}
        }
    }

    /**
     * Gets the entity's label
     * @return The label
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Gets the entity's URIs
     * @return The URIs
     */
    public URI[] getUris() {
        return uris;
    }
    
    /**
     * Writes the named entity in a JSON representation
     * @param json The JSON writer
     * @throws JSONException if an error occurs during writing
     */
    public void writeTo(final JSONWriter json) throws JSONException {
        json.object();
        json.key("label"); json.value(getLabel());
        json.key("uris");
        json.array();
        for (final URI uri : getUris())
            json.value(uri.toString());
        json.endArray();
        json.endObject();
    }
}
