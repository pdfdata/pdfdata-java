package io.pdfdata.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Entity representing a page within a source PDF {@link Document}. {@code Page} subclasses
 * provide additional attributes associated with the results of different {@link Operation}s.
 *
 * @publicapi
 */
public class Page extends Entity {
    private int pagenum;
    private Dimensions dimensions;

    @JsonProperty("pagenum")
    public int getPageNumber () {
        return pagenum;
    }

    public Dimensions getDimensions() {
        return dimensions;
    }
}
