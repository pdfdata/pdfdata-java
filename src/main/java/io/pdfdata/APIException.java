package io.pdfdata;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * @publicapi
 */
public class APIException extends IOException {

    private final Network.Method verb;
    private final int responseStatus;
    private final URL url;
    private final Map<String, Object> params;
    private final Map<String, String> headers;
    private final JsonNode errorResponse;

    APIException(String message, int responseStatus, Network.Method verb, URL url,
                 Map<String, Object> params, Map<String, String> headers) {
        super(message + " Use the accessors in " +
                "`io.pdfdata.APIException` (e.g. `.getParams()`) to examine the request that was " +
                "sent, and thereby identify why the request might have failed.");
        errorResponse = null;
        this.responseStatus = responseStatus;
        this.verb = verb;
        this.url = url;
        this.params = params;
        this.headers = headers;
    }

    APIException(JsonNode response, int responseStatus, Network.Method verb, URL url,
                 Map<String, Object> params, Map<String, String> headers) throws IOException {
        super("The PDFDATA.io API responded to this request with an error. Use the accessors in " +
                "`io.pdfdata.APIException` (e.g. `.getErrorResponse()`) for details on the " +
                "problem.");
        this.errorResponse = response;
        this.responseStatus = responseStatus;
        this.verb = verb;
        this.url = url;
        this.params = params;
        this.headers = headers;
    }

    public int getResponseStatus () {
        return responseStatus;
    }

    public JsonNode getErrorResponse () {
        return errorResponse;
    }

    public String getRequestMethod () {
        return verb.toString();
    }

    public URL getUrl() {
        return url;
    }

    public Map<String, Object> getRequestParams() {
        return params;
    }

    public Map<String, String> getRequestHeaders() {
        return headers;
    }
}
