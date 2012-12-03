package org.freeyourmetadata.ner.operations;

import java.io.Serializable;
import java.util.Map;

import org.freeyourmetadata.ner.services.NERService;

import com.google.refine.history.HistoryEntry;
import com.google.refine.model.AbstractOperation;
import com.google.refine.model.Cell;
import com.google.refine.model.Column;
import com.google.refine.model.Project;
import com.google.refine.model.Row;
import com.google.refine.process.LongRunningProcess;

/**
 * Process that executes named-entity recognition services
 * and aggregates their results.
 * @author Ruben Verborgh
 */
public class NERProcess extends LongRunningProcess implements Runnable {
    private final Project project;
    private final Column column;
    private final Map<String, NERService> services;
    private final AbstractOperation parentOperation;
    private final long historyEntryId;

    /**
     * Creates a new <tt>NERProcess</tt>
     * @param project The project
     * @param column The column on which named-entity recognition is performed
     * @param services The services that will be used for named-entity recognition
     * @param parentOperation The operation that creates this process
     * @param description The description of this operation
     */
    protected NERProcess(final Project project, final Column column, final Map<String, NERService> services,
                         final AbstractOperation parentOperation, final String description) {
        super(description);
        this.project = project;
        this.column = column;
        this.services = services;
        this.parentOperation = parentOperation;
        historyEntryId = HistoryEntry.allocateID();
    }

    /** {@inheritDoc} */
    @Override
    public void run() {
        final int columnIndex = project.columnModel.getColumnIndexByName(column.getName()) + 1;
        final String[] serviceNames = services.keySet().toArray(new String[services.size()]);
        final String[][][] extractedTerms = performExtraction();
        
        if (!_canceled) {
            project.history.addEntry(new HistoryEntry(historyEntryId, project, _description, parentOperation,
                                                      new NERChange(columnIndex, serviceNames, extractedTerms)));
            project.processManager.onDoneProcess(this);
        }
    }

    /**
     * Performs named-entity extraction on all rows
     * @return The extracted terms per row and service
     */
    protected String[][][] performExtraction() {
        final int rowCount = project.rows.size();
        final int cellIndex = column.getCellIndex();
        final String[][][] results = new String[rowCount][][];
        
        int rowIndex = 0;
        for (final Row row : project.rows) {
            final Cell cell = row.getCell(cellIndex);
            final Serializable cellValue = cell == null ? null : cell.value;
            final String text = cellValue == null ? "" : cellValue.toString();
            results[rowIndex++] = performExtraction(text);
            
            _progress = rowIndex * 100 / rowCount;
            if (_canceled)
                return null;
        }
        return results;
    }
    
    /**
     * Performs named-entity extraction on the specified text.
     * @param text The text
     * @return The extracted terms per service
     */
    protected String[][] performExtraction(final String text) {
        // The execution of the services happens in parallel.
        // Create the extractors and corresponding threads
        final Extractor[] extractors = new Extractor[services.size()];
        int i = 0;
        for (final NERService service : services.values()) {
            final Extractor extractor = extractors[i++] = new Extractor(text, service);
            extractor.start();
        }
        
        // Wait for all threads to finish and collect their results
        final String[][] extractions = new String[extractors.length][];
        for (i = 0; i < extractors.length; i++) {
            try {
                extractors[i].join();
            }
            catch (InterruptedException e) { }
            extractions[i] = extractors[i].getExtractedTerms();
        }
        return extractions;
    }

    /** {@inheritDoc} */
    @Override
    protected Runnable getRunnable() {
        return this;
    }
    
    /**
     * Thread that executes a named-entity recognition service
     */
    protected static class Extractor extends Thread {
        private final String text;
        private final NERService service;
        private String[] extractedTerms;
        
        /**
         * Creates a new <tt>Extractor</tt>
         * @param text The text to analyze
         * @param service The service that will analyze the text
         */
        public Extractor(final String text, final NERService service) {
            this.text = text;
            this.service = service;
        }
        
        /**
         * Gets the terms the service extracted from the text
         * @return The extracted terms
         */
        public String[] getExtractedTerms() {
            return extractedTerms;
        }
        
        /** {@inheritDoc} */
        @Override
        public void run() {
            extractedTerms = service.extractTerms(text);
        }
    }
}
