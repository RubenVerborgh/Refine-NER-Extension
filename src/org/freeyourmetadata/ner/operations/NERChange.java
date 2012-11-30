package org.freeyourmetadata.ner.operations;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.google.refine.history.Change;
import com.google.refine.model.Project;
import com.google.refine.model.changes.CellAtRow;
import com.google.refine.model.changes.ColumnAdditionChange;
import com.google.refine.model.changes.ColumnRemovalChange;

/**
 * A change resulting from named-entity recognition
 * @author Ruben Verborgh
 */
public class NERChange implements Change {
    private final int columnIndex;
    private final String[] serviceNames;
    private final String[][][] extractedTerms;
    
    /**
     * Creates a new <tt>NERChange</tt>
     * @param columnIndex The index of the column used for named-entity recognition
     * @param serviceNames The names of the used services
     * @param extractedTerms The extracted terms per row and service
     */
    public NERChange(final int columnIndex, final String[] serviceNames, final String[][][] extractedTerms) {
        this.columnIndex = columnIndex;
        this.serviceNames = serviceNames;
        this.extractedTerms = extractedTerms;
    }

    /** {@inheritDoc} */
    @Override
    public void apply(final Project project) {
        createColumns(project);
    }

    /** {@inheritDoc} */
    @Override
    public void revert(final Project project) {
        deleteColumns(project);
    }

    /** {@inheritDoc} */
    @Override
    public void save(final Writer writer, final Properties options) throws IOException { }
    
    /**
     * Create the columns where the named entities will be stored
     * @param project The project
     */
    protected void createColumns(final Project project) {
        final List<CellAtRow> noCells = Collections.emptyList();
        for (int i = 0; i < serviceNames.length; i++)
            new ColumnAdditionChange(serviceNames[i], columnIndex + i, noCells).apply(project);
    }
    
    /**
     * Delete the columns where the named entities have been stored
     * @param project The project
     */
    protected void deleteColumns(final Project project) {
        for (int i = 0; i < serviceNames.length; i++)
            new ColumnRemovalChange(columnIndex).apply(project);
    }
}
