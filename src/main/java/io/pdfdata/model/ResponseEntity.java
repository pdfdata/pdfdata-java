package io.pdfdata.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @nodoc -- not sure if we want to expose jackson as part of the public/documented API
 */
public class ResponseEntity extends Entity {
    @JsonIgnore
    private JsonNode responseBody;

    public JsonNode getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(JsonNode responseBody) {
        this.responseBody = responseBody;
    }
}
