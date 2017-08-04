package io.pdfdata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Set;

/**
 * Entity representing a PDF document.
 *
 * To learn more about documents as represented in the PDFDATA.io API, please visit
 * <a href="https://www.pdfdata.io/apidoc/?java#documents">their
 * dedicated section in the PDFDATA.io API reference</a>.
 *
 * Note the existence of the {@link ProcessedDocument} subclass, which provides additional
 * attributes conveyed by documents when delivered as part of a {@link Proc} response.
 * @publicapi
 */
public class Document extends ResponseEntity {
    private String id;
    private String filename;
    private Set<String> tags;
    private Instant created;
    private Instant expires;
    @JsonProperty("pagecount")
    private int pageCount;

    public String getID () {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public Set<String> getTags() {
        return tags;
    }

    public Instant getCreated() {
        return created;
    }

    public Instant getExpires() {
        return expires;
    }

    public int getPageCount() {
        return pageCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Document document = (Document) o;

        if (pageCount != document.pageCount) return false;
        if (!id.equals(document.id)) return false;
        if (!filename.equals(document.filename)) return false;
        if (tags != null ? !tags.equals(document.tags) : document.tags != null) return false;
        if (!created.equals(document.created)) return false;
        return expires.equals(document.expires);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + filename.hashCode();
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = 31 * result + created.hashCode();
        result = 31 * result + expires.hashCode();
        result = 31 * result + pageCount;
        return result;
    }
}
