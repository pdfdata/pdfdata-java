package io.pdfdata.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.pdfdata.model.ops.PageTemplates;

import java.util.Map;


/**
 * Entity representing a match of a {@link PageTemplates.Template template} to a page from a
 * source PDF document, produced by the {@link PageTemplates} operation.
 *
 * To learn more about images in PDFs, the {@link PageTemplates} operation, and the data it
 * provides conveyed by this class, please visit
 * <a href="https://www.pdfdata.io/apidoc/?java#page-templates">the
 * dedicated section in the PDFDATA.io API reference</a>.
 *
 * @publicapi
 */
public class TemplateMatch extends Entity {
    private int pageNumber;
    private String templateName;
    private Map<String, String> regions;

    @JsonProperty("pagenum")
    public int getPageNumber() {
        return pageNumber;
    }

    @JsonProperty("template")
    public String getTemplateName() {
        return templateName;
    }

    /**
     * Returns the map of region names (as defined in the submitted
     * {@link PageTemplates.Template} configuration) to their values as extracted from the
     * {@link #getPageNumber() page} of the source PDF.
     */
    public Map<String, String> getRegions() {
        return regions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TemplateMatch that = (TemplateMatch) o;

        if (pageNumber != that.pageNumber) return false;
        if (!templateName.equals(that.templateName)) return false;
        return regions.equals(that.regions);
    }

    @Override
    public int hashCode() {
        int result = pageNumber;
        result = 31 * result + templateName.hashCode();
        result = 31 * result + regions.hashCode();
        return result;
    }
}
