package io.pdfdata.model;

import com.fasterxml.jackson.annotation.*;
import io.pdfdata.model.ops.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class representing an operation.
 *
 * Complete documentation on the types of PDF operations available is available
 * in the <a href="https://www.pdfdata.io/apidoc/?java#operations">PDFDATA.io API reference</a>.
 *
 * @publicapi
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "op")
@JsonSubTypes({@JsonSubTypes.Type(value = Metadata.class, name = "metadata"),
        @JsonSubTypes.Type(value = XMPMetadata.class, name = "xmp-metadata"),
        @JsonSubTypes.Type(value = Images.class, name = "images"),
        @JsonSubTypes.Type(value = Text.class, name = "text"),
        @JsonSubTypes.Type(value = Attachments.class, name = "attachments"),
        @JsonSubTypes.Type(value = PageTemplates.class, name = "page-templates")})
public class Operation extends Entity {
    private String op;

    protected Operation(String op) {
        this.op = op;
    }

    @JsonProperty("op")
    public String getOperationName () {
        return op;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Operation operation = (Operation) o;

        return op.equals(operation.op);
    }

    @Override
    public int hashCode() {
        return op.hashCode();
    }

    /**
     * Base class representing the result of an {@link Operation} applied to a {@link Document}
     * within a {@link Proc}.
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "op")
    @JsonSubTypes({@JsonSubTypes.Type(value = Metadata.Result.class, name = "metadata"),
            @JsonSubTypes.Type(value = XMPMetadata.Result.class, name = "xmp-metadata"),
            @JsonSubTypes.Type(value = Images.Result.class, name = "images"),
            @JsonSubTypes.Type(value = Text.Result.class, name = "text"),
            @JsonSubTypes.Type(value = Attachments.Result.class, name = "attachments"),
            @JsonSubTypes.Type(value = PageTemplates.Result.class, name = "page-templates")})
    public static class Result extends Entity {
        private static Map<String, AResource> EMPTY_RESOURCES =
                Collections.unmodifiableMap(new HashMap<>());

        private String op;
        @JsonBackReference
        private ProcessedDocument document;
        private boolean failure;
        private Map<String, AResource> resources = EMPTY_RESOURCES;

        protected Result(String op) {
            this.op = op;
        }

        @JsonProperty("op")
        public String getOperationName () {
            return op;
        }

        /**
         * Returns false if the operation was applied to the document and yielded data as expected
         * without error.
         */
        public boolean isFailure () {
            return failure;
        }

        /**
         * Returns the {@link Resource} map provided as part of the operation's results. Note
         * that only some {@link Operation}s produce binary resources. Those that do will
         * manifest {@link ResourcefulEntity} instances within their results that automatically
         * look up their resources within this map. Operations that do not produce binary
         * resources will yield instances of {@code Result} types that return null or an empty
         * map from this method.
         */
        public Map<String, AResource> getResources () {
            return resources;
        }

        /**
         * Returns the entity representing the source PDF document from which this {@code
         * Result}'s data was extracted.
         */
        public ProcessedDocument getDocument () {
            return document;
        }

        /**
         * @nodoc
         */
        public void registerResources () throws IOException {}
    }
}
