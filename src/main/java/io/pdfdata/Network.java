package io.pdfdata;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static io.pdfdata.Network.Method.*;

/**
 * Much of the code here was transplanted from Stripe's Java client library
 * (https://github.com/stripe/stripe-java, MIT-licensed), which contains a
 * variety of utilities and tactics for making the JDK-supplied HTTPS implementation
 * suitable/safe for secure API usage.
 * @nodoc
 */
class Network {
    static final String CHARSET_NAME = "UTF-8";
    static final Charset CHARSET = Charset.forName(CHARSET_NAME);

    private static final SSLSocketFactory socketFactory = new PDFDATASSLSocketFactory();
    static boolean REQUIRE_SECURE_CONNECTIONS = true;

    public static enum Method {
        GET, POST//, PUT
    }

    private static final Map<String, String> BASE_HEADERS =
            Collections.unmodifiableMap(new HashMap<String,String>() {{

                put("Accept-Charset", CHARSET_NAME);
                put("Accept", "application/json");
                put("User-Agent",
                        String.format("PDFDATA.io/v1 JavaBindings/%s", API.VERSION));
                put("X-API-Intent", "Y");
                /*
                put("Stripe-Version", apiVersion);
                if (options.getIdempotencyKey() != null) {
                    headers.put("Idempotency-Key", options.getIdempotencyKey());
                }
                */

                // debug headers
                String[] propertyNames = {"os.name", "os.version", "os.arch",
                        "java.version", "java.vendor", "java.vm.version",
                        "java.vm.vendor"};
                Map<String, String> propertyMap = new HashMap<String, String>();
                for (String propertyName : propertyNames) {
                    propertyMap.put(propertyName, System.getProperty(propertyName));
                }
                try {
                    put("X-PDFDATA-Client-User-Agent", JSON.to0(propertyMap));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }});

    /**
     * Merges one map of headers with the set of base headers sent with every PDFDATA.io API
     * request, returning the result. Used only in error cases, when producing
     * {@link APIException}s.
     */
    public static Map<String, String> mergeHeaders (Map<String, String> additional) {
        HashMap<String, String> headers = new HashMap<>();
        headers.putAll(BASE_HEADERS);
        if (additional != null) headers.putAll(additional);
        return headers;
    }

    private static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            // simply will never happen
            throw new RuntimeException(e);
        }
    }

    private static String urlEncodePair (String k, String v) {
        return urlEncode(k) + "=" + urlEncode(v);
    }

    static String queryString (Map<String, Object> params) {
        StringBuilder sb = new StringBuilder("?");
        for (Map.Entry<String, Object> param : params.entrySet()) {
            sb.append(urlEncodePair(param.getKey(), String.valueOf(param.getValue())));
            sb.append("&");
        }
        return sb.substring(0, sb.length() - 1);
    }

    private static String base64 (String s) {
        return DatatypeConverter.printBase64Binary(s.getBytes(CHARSET));
    }

    private static Map<String, String> getHeaders(API pdfdata) {
        Map<String, String> headers = new HashMap<String, String>(BASE_HEADERS);
        headers.put("Authorization", "Basic " + base64(pdfdata.getAPIKey() + ":"));
        return headers;
    }

    static HttpURLConnection openConnection (Method verb, URL url, Map<String, String> headers,
                                             API pdfdata) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection)conn).setSSLSocketFactory(socketFactory);
        } else if (REQUIRE_SECURE_CONNECTIONS) {
            throw new IOException("Could not establish secure connection to " + url);
        }

        conn.setRequestMethod(verb.toString());
        conn.setConnectTimeout(pdfdata.getConnectTimeout());
        conn.setReadTimeout(pdfdata.getReadTimeout());
        conn.setUseCaches(false);
        for (Map.Entry<String, String> header : getHeaders(pdfdata).entrySet()) {
            conn.setRequestProperty(header.getKey(), header.getValue());
        }
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                conn.setRequestProperty(header.getKey(), header.getValue());
            }
        }

        if (verb == POST) conn.setDoOutput(true);

        conn.connect();

        return conn;
    }
}
