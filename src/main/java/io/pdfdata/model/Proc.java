package io.pdfdata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Entity representing a <em>proc</em>, a process running within the PDFDATA.io API that applies
 * {@link Operation}s to source PDF {@link Document}s to extract content and data from them.
 *
 * To learn more about procs, please visit
 * <a href="https://www.pdfdata.io/apidoc/?java#procs">their
 * dedicated section in the PDFDATA.io API reference</a>.
 *
 * @see io.pdfdata.ProcsRequest to start new procs and retrieve the resulting {@code Proc} entities
 * @publicapi
 */
public class Proc extends ResponseEntity {
    public static enum Status {
        PENDING, COMPLETE, REFUSED
    }

    private String id;
    private Instant created;
    @JsonProperty("source_tags")
    private Set<String> sourceTags;
    private List<Operation> operations;
    private Status status;
    private Set<String> docIDs;
    private List<ProcessedDocument> documents;

    /**
     * @see io.pdfdata.ProcsRequest#byID(String)
     */
    public String getID() {
        return id;
    }

    public Instant getCreated() {
        return created;
    }

    public Set<String> getSourceTags() {
        return sourceTags;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public Status getStatus() {
        return status;
    }

    /**
     * Returns the set of IDs of {@link Document}s included in this proc, <em>only if the
     * proc's {@link #getStatus() status} is {@link Status#PENDING} or
     * {@link Status#REFUSED}</em>. This method will return null
     * or an empty set if this proc has been {@link Status#COMPLETE completed}.
     */
    @JsonProperty("docids")
    public Set<String> getDocIDs() {
        return docIDs;
    }

    /**
     * Returns the documents included in this proc (which carry the
     * {@link Operation.Result result}s of the {@link Operation}s applied to them by the proc via
     * {@link ProcessedDocument#getResults()}), <em>only if the
     * proc's {@link #getStatus() status} is {@link Status#COMPLETE}</em>. Otherwise, this method
     * will return null.
     */
    public List<ProcessedDocument> getDocuments() {
        return documents;
    }
}
