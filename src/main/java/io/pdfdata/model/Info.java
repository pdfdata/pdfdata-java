package io.pdfdata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entity representing the response provided to requests to the PDFDATA.io API's root resource.
 *
 * <!-- TODO no apidoc available for the root resource yet, PDFIO-152 -->
 *
 * @publicapi
 */
public class Info extends ResponseEntity {
    private String message;
    @JsonProperty("api_version")
    private String apiVersion;
    private String build;

    public String getMessage() {
        return message;
    }

    public String getAPIVersion () {
        return apiVersion;
    }

    public String getBuild() {
        return build;
    }
}
