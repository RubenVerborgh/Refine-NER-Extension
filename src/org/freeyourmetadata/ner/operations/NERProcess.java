package org.freeyourmetadata.ner.operations;

import java.util.Map;

import org.freeyourmetadata.ner.services.NERService;

import com.google.refine.history.HistoryEntry;
import com.google.refine.model.AbstractOperation;
import com.google.refine.model.Column;
import com.google.refine.model.Project;
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
     * Performs named-entity extraction
     * @return The extracted terms per row and service
     */
    protected String[][][] performExtraction() {
        final int rowCount = project.rows.size();
        final String[][][] results = new String[rowCount][][];
        
        int rowIndex = 0;
        while (!_canceled && rowIndex < rowCount) {
            _progress = rowIndex * 100 / rowCount;
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {}
            rowIndex++;
        }
        return results;
    }

    /** {@inheritDoc} */
    @Override
    protected Runnable getRunnable() {
        return this;
    }
}
