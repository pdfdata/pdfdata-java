package io.pdfdata.model.ops;

import com.fasterxml.jackson.databind.JsonNode;
import io.pdfdata.model.Operation;

/**
 * @publicapi
 */
public class XMPMetadata extends Operation {
    public XMPMetadata() {
        super("xmp-metadata");
    }

    public static class Result extends Operation.Result {
        private JsonNode data;

        public Result() {
            super("xmp-metadata");
        }

        public JsonNode getData() {
            return data;
        }
    }
}
