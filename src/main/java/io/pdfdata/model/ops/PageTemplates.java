package io.pdfdata.model.ops;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.pdfdata.model.Bounds;
import io.pdfdata.model.Entity;
import io.pdfdata.model.Operation;
import io.pdfdata.model.TemplateMatch;

import java.util.*;

/**
 * @publicapi
 */
public class PageTemplates extends Operation {
    private Map<String, Template> templates = new HashMap<>();

    public PageTemplates () {
        super("page-templates");
    }

    PageTemplates (Map<String, Template> templates) {
        this();
        this.templates = templates;
    }

    public PageTemplates withTemplate (String name, Template t) {
        return withTemplates(Collections.singletonMap(name, t));
    }

    private PageTemplates withTemplates(Map<String, Template> templates) {
        Map<String, Template> copy = new HashMap<>(this.templates);
        copy.putAll(templates);
        return new PageTemplates(copy);
    }

    public Map<String, Template> getTemplates () {
        return Collections.unmodifiableMap(templates);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PageTemplates that = (PageTemplates) o;

        return templates.equals(that.templates);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + templates.hashCode();
        return result;
    }

    public static class Template extends Entity {
        private Map<String, Region> regions = new HashMap<>();
        private Integer pageNumber;

        public Template () {}

        Template(Map<String, Region> regions, Integer pageNumber) {
            this.regions = regions;
            this.pageNumber = pageNumber;
        }

        @JsonProperty("pagenum")
        public Integer getPageNumber () {
            return pageNumber;
        }

        public Map<String, Region> getRegions () {
            return Collections.unmodifiableMap(regions);
        }

        public Template withRegion (String name, Region region) {
            return withRegions(Collections.singletonMap(name, region));
        }

        public Template withRegions (Map<String, Region> regions) {
            HashMap<String, Region> copy = new HashMap<>(this.regions);
            copy.putAll(regions);
            return new Template(copy, pageNumber);
        }

        public Template restrictToPage (int pageNumber) {
            return new Template(regions, pageNumber);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Template template = (Template) o;

            if (!regions.equals(template.regions)) return false;
            return pageNumber != null ? pageNumber.equals(template.pageNumber) : template.pageNumber == null;
        }

        @Override
        public int hashCode() {
            int result = regions.hashCode();
            result = 31 * result + (pageNumber != null ? pageNumber.hashCode() : 0);
            return result;
        }
    }

    public static class Region extends Entity {
        private Bounds bounds;
        @JsonProperty("match")
        private String match;
        @JsonProperty("contains")
        private String contains;

        Region () {}

        public Region(Bounds bounds) {
            this.bounds = bounds;
        }

        public Region (double lx, double by, double rx, double ty) {
            this(new Bounds(lx, by, rx, ty));
        }

        Region(Bounds bounds, String match, String contains) {
            this.bounds = bounds;
            this.match = match;
            this.contains = contains;
        }

        public Region matchingRegex (String match) {
            return new Region(bounds, match, contains);
        }

        public Region containingString (String contains) {
            return new Region(bounds, match, contains);
        }

        public Bounds getBounds() {
            return bounds;
        }

        @JsonProperty("match")
        public String getMatchingRegex () {
            return match;
        }

        @JsonProperty("contains")
        public String getContainingString () {
            return contains;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Region region = (Region) o;

            if (!bounds.equals(region.bounds)) return false;
            if (match != null ? !match.equals(region.match) : region.match != null) return false;
            return contains != null ? contains.equals(region.contains) : region.contains == null;
        }

        @Override
        public int hashCode() {
            int result = bounds.hashCode();
            result = 31 * result + (match != null ? match.hashCode() : 0);
            result = 31 * result + (contains != null ? contains.hashCode() : 0);
            return result;
        }
    }

    public static class Result extends Operation.Result {
        private List<TemplateMatch> data;

        public Result() {
            super("page-templates");
        }

        public List<TemplateMatch> getData() {
            return data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Result result = (Result) o;

            return data.equals(result.data);
        }

        @Override
        public int hashCode() {
            return data.hashCode();
        }
    }

}
