package io.pdfdata.model.ops;

import com.fasterxml.jackson.databind.JsonNode;
import io.pdfdata.model.Operation;

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

        public JsonNode getData() {
            return data;
        }
    }
}
