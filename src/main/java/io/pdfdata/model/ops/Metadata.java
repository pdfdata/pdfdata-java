package io.pdfdata.model.ops;

import com.fasterxml.jackson.databind.JsonNode;
import io.pdfdata.API;
import io.pdfdata.model.Operation;

import java.time.Instant;

/**
 * @publicapi
 */
public class Metadata extends Operation {
    public Metadata() {
        super("metadata");
    }

    public static class Result extends Operation.Result {
        private JsonNode data;

        public Result() {
            super("metadata");
        }

        /**
         * PDF document metadata is an entirely free-form set of key-value pairs.
         *
         * This class provides accessors for the well-known metadata elements, but some documents
         * and workflows require accessing the full set of metadata. This method accomplishes
         * this by passing along the Jackson representation of the source document's entire
         * metadata as conveyed by the PDFDATA.io API.
         */
        public JsonNode getData() {
            return data;
        }

        private String value (String key) {
            JsonNode v = data.get(key);
            return v == null ? null : v.asText();
        }

        public String getTitle () {
            return value("Title");
        }

        public String getAuthor () {
            return value("Author");
        }

        public String getSubject () {
            return value("Subject");
        }

        public String getKeywords () {
            return value("Keywords");
        }

        public String getCreator () {
            return value("Creator");
        }

        public String getProducer () {
            return value("Producer");
        }

        /**
         *
         * @throws java.time.format.DateTimeParseException the creation date metadata string is
         * not in the expected format
         */
        public Instant getCreationDate () {
            String v = value("CreationDate");
            return v == null ? null : API.parseDate(v);
        }

        /**
         *
         * @throws java.time.format.DateTimeParseException the modification date metadata string is
         * not in the expected format
         */
        public Instant getModificationDate () {
            String v = value("ModDate");
            return v == null ? null : API.parseDate(v);
        }
    }
}
