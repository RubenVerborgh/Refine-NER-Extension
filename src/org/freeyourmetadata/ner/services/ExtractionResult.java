package org.freeyourmetadata.ner.services;

import com.google.refine.expr.EvalError;

/**
 * A result of a single named-entity extraction service call.
 * @author Giuliano Tortoreto
 * @author Ruben Verborgh
 */
public class ExtractionResult {
    private final NamedEntity[] namedEntities;
    private final EvalError extractionError;

    /**
     * Creates a successful named-entity extraction result
     * @param namedEntities The list of extracted named entities
     */
    public ExtractionResult(final NamedEntity[] namedEntities) {
        this.namedEntities = namedEntities;
        this.extractionError = null;
    }
    
    /**
     * Creates an unsuccessful named-entity extraction result
     * @param extractionError The error that occurred during extraction
     */
    public ExtractionResult(final Throwable extractionError) {
        this.namedEntities = null;
        this.extractionError = new EvalError(extractionError.getMessage());
    }

    /**
     * Gets the extracted named entities
     * @return The extracted named entities
     */
    public NamedEntity[] getNamedEntities() {
        return namedEntities == null ? new NamedEntity[0] : namedEntities;
    }
    
    /**
     * Gets the error that occurred during the extraction
     * @return The extracted named entities
     */
    public EvalError getExtractionError() {
        return extractionError;
    }

    /**
     * Indicates whether an error occurred during extraction.
     * @return <tt>true</tt> if an error occurred
     */
    public boolean hasError() {
        return extractionError != null;
    }
}
