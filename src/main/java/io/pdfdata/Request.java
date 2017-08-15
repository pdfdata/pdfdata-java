package io.pdfdata;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import io.pdfdata.model.Operation;
import io.pdfdata.model.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static io.pdfdata.Network.Method.*;

/**
 * @nodoc
 */
public class Request {
    private final static int DEFAULT_CONNECT_TIMEOUT = 30 * 1000;
    private final static int DEFAULT_READ_TIMEOUT = 80 * 1000;

    final API pdfdata;
    private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
    private int readTimeout = DEFAULT_READ_TIMEOUT;

    Request (API pdfdata) {
        this.pdfdata = pdfdata;
    }

    <T> T doRequest (Network.Method verb, String path, TypeReference<T> resultType)
            throws IOException {
        return (T) doRequest(verb, path, new HashMap(), resultType);
    }

    <T> T doRequest (Network.Method verb, String path, Map<String,
            Object> params, TypeReference<T> resultType) throws IOException {
        return (T) doRequest(verb, path, params, new HashMap(), resultType);
    }

    // this is pretty nasty
    // TODO why aren't we just accepting json request objects for each POST in the API?
    private static boolean isOperationsList (Object x) {
        if (x instanceof List) {
            for (Object y : (List)x) {
                if (!(y instanceof Operation)) return false;
            }

            return true;
        }

        return false;
    }

    InputStream doStreamGet (String path) throws IOException {
        URL url = new URL(pdfdata.getEndpoint(), path);
        HttpURLConnection conn = Network.openConnection(GET, url, null, pdfdata);
        int status = conn.getResponseCode();
        String contentType = String.valueOf(conn.getHeaderField("Content-Type"));
        if (status >= 200 && status < 300) {
            return conn.getInputStream();
        } else if (contentType.equals("application/json")) {
            InputStream is = conn.getErrorStream();
            throw new APIException(pdfdata.json.from(is, JsonNode.class),
                    status,
                    GET,
                    url,
                    null,
                    Network.mergeHeaders(null));
        } else {
            throw new APIException(String.format("%s request to %s produced a failure response " +
                    "with an unparseable Content-Type: %s.", GET, url.toExternalForm(), contentType),
                    status,
                    GET,
                    url,
                    null,
                    Network.mergeHeaders(null));
        }
    }

    <T> T doRequest (Network.Method verb, String path, Map<String, Object> params, Map<String, String> headers,
                     TypeReference<T> resultType) throws IOException {
        String queryString = verb == GET ? Network.queryString(params) : "";

        URL url = new URL(pdfdata.getEndpoint().toExternalForm() + path + queryString);

        String boundary = null;

        if (verb == POST) {
            boundary = MultipartProcessor.getBoundary();
            headers.put("Content-Type", "multipart/form-data; boundary=" + boundary);
        }

        HttpURLConnection conn = Network.openConnection(verb, url, headers, pdfdata);

        // send request body
        if (verb == POST) {
            MultipartProcessor mpp = new MultipartProcessor(conn, boundary, Network.CHARSET_NAME);
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (isOperationsList(param.getValue())) {
                    mpp.addFormField(param.getKey(), JSON.to0(param.getValue()));
                } else {
                    for (Object v : (Collection) (param.getValue() instanceof Collection ?
                            param.getValue() :
                            Collections.singleton(param.getValue()))) {
                        if (v == null) {
                            // no op
                        } else if (v instanceof String) {
                            mpp.addFormField(param.getKey(), (String) v);
                        } else if (v instanceof File) {
                            mpp.addFileField(param.getKey(), (File) v);
                        } else if (v instanceof Number) {
                            mpp.addFormField(param.getKey(), v.toString());
                        } else {
                            throw new IllegalArgumentException(String.format(
                                    "Illegal param value type %s provided for %s",
                                    v.getClass().getName(), param.getKey()));
                        }
                    }
                }
            }
            mpp.finish();
        }

        // receive response
        int status = conn.getResponseCode();
        String contentType = String.valueOf(conn.getHeaderField("Content-Type"));
        if (contentType.equals("application/json")) {
            if (status >= 200 && status < 300) {

                InputStream is = conn.getInputStream();
                JsonNode body = pdfdata.json.from(is, JsonNode.class);
                T response = pdfdata.json.from(body, resultType);
                if (response instanceof List) {
                    List listResponse = (List)response;
                    for (int i = 0, len = listResponse.size(); i < len; i++) {
                        Object resp = listResponse.get(i);
                        if (pdfdata.isCaptureResponseBodies()) {
                            ((ResponseEntity)resp).setResponseBody(body.get(i));
                        }
                    }
                } else if (response instanceof Collection) {
                    throw new IOException("Unexpected response type " + response.getClass());
                } else {
                    if (pdfdata.isCaptureResponseBodies()) {
                        ((ResponseEntity)response).setResponseBody(body);
                    }
                }

                return response;
            } else {
                InputStream is = conn.getErrorStream();
                throw new APIException(pdfdata.json.from(is, JsonNode.class),
                        status,
                        verb,
                        url,
                        params,
                        Network.mergeHeaders(headers));
            }
        } else {
            throw new APIException(String.format("%s request to %s produced a response with an " +
                    "unexpected Content-Type: %s.", verb, url.toExternalForm(), contentType),
                    status,
                    verb,
                    url,
                    params,
                    Network.mergeHeaders(headers));
        }
    }
}
