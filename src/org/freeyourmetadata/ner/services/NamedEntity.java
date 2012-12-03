package org.freeyourmetadata.ner.services;

import java.net.URI;

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
}
