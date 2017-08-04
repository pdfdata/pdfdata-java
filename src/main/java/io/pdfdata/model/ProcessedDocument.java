package io.pdfdata.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

/**
 * Entity representing a PDF document as conveyed as part of a {@link Proc} response.
 *
 * To learn more about documents as represented in the PDFDATA.io API, please visit
 * <a href="https://www.pdfdata.io/apidoc/?java#documents">their
 * dedicated section in the PDFDATA.io API reference</a>. The additional attributes provided by a
 * {@code ProcessedDocument} are discussed in the API reference in connection with
 * <a href="https://www.pdfdata.io/apidoc/?java#getting-the-results-of-a-proc">obtaining and
 * consuming {@link Proc} results</a>.
 *
 * @publicapi
 */
public class ProcessedDocument extends Document {
    private List<Operation.Result> results;

    /**
     * Returns the results of applying the
     * operations configured at proc-creation time to this document.
     * Note that these results are guaranteed to be in the same order as their corresponding
     * {@link Operation}s {@link Proc#getOperations() as indicated by the proc}.
     */
    @JsonManagedReference
    public List<Operation.Result> getResults() {
        return results;
    }
}
