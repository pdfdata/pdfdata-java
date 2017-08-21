package io.pdfdata;

import io.pdfdata.model.Document;
import io.pdfdata.model.Operation;
import io.pdfdata.model.Proc;
import io.pdfdata.model.Resource;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * This is the entry point to the {@code pdfdata-java} library.
 *
 * <a href="https://www.pdfdata.io">PDFDATA.io</a> is an API providing PDF data extraction as a
 service. This library, <a href="https://www.github.com/pdfdata/pdfdata-java">pdfdata-java</a>,
 is our official Java client, providing an
 efficient, easy-to-use model for interacting with and using PDFDATA.io from Java, as well as
 from any JVM-based programming language, including Scala, Clojure, Groovy,
 Kotlin, JRuby, Jython, and others.
 *
 * This Javadoc provides useful hints and important Java-specific documentation for
 <code>pdfdata-java</code>, but <a href="https://www.pdfdata.io/apidoc/">our primary API
 reference</a> is the authoritative source for both introductory overview and advanced detailed
 information on the PDFDATA.io API itself, as well as this library.
 *
 * <h3>Example</h3>
 * <pre>Proc proc = pdfdata.procs().configure()
 *     .withFiles(
 .withOperations(new Metadata(), new Images(), new Text(), new XMPMetadata())
 .start();
 * </pre>
 * @publicapi
 */
public class API {
    /**
     * The URL of the default PDFDATA.io API endpoint, as a String.
     */
    public static final String DEFAULT_API_ENDPOINT = "https://api.pdfdata.io/v1/";
    /**
     * A URL indicating the default API endpoint that {@code API} instances will use unless
     * otherwise configured via {@link #API(URL)} or {@link #API(String, URL)}. This default is the
     * first non-null value, in order, of:
     *
     * <ol>
     *     <li>the environment variable {@code PDFDATA_ENDPOINT}</li>
     *     <li>the {@link System#getProperty(String) system property} {@code PDFDATA_ENDPOINT}</li>
     *     <li>{@link #DEFAULT_API_ENDPOINT}</li>
     * </ol>
     *
     * If either (1) or (2) above is non-null, but is not a valid URL string, then this class
     * will fail to initialize, throwing an {@link IllegalStateException}.
     */
    public static final URL API_ENDPOINT = endpoint_env();

    private final static int DEFAULT_CONNECT_TIMEOUT = 30 * 1000;
    private final static int DEFAULT_READ_TIMEOUT = 80 * 1000;
    static final DateTimeFormatter INSTANT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    // TODO load from jar
    public static String VERSION = "0.9.9";

    private final String apiKey;
    private final URL endpoint;
    private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
    private int readTimeout = DEFAULT_READ_TIMEOUT;
    private boolean captureResponseBodies = false;
    final JSON json;


    /**
     * Creates a PDFDATA.io {@code API} instance tied to the
     * {@link #API_ENDPOINT environment-sourced API endpoint}, and using an API key discovered by
     * obtaining the first non-null value, in order, of:
     *
     * <ol>
     *     <li>the environment variable {@code PDFDATA_APIKEY}</li>
     *     <li>the {@link System#getProperty(String) system property} {@code PDFDATA_APIKEY}</li>
     * </ol>
     *
     * If an API key cannot be obtained from these sources, then this constructor will throw an
     * {@link IllegalStateException}.
     */
    public API () {
        this(apikey_env(), API_ENDPOINT);
    }

    /**
     * Creates a PDFDATA.io {@code API} instance with the given API key and tied to the
     * {@link #API_ENDPOINT environment-sourced API endpoint}.
     */
    public API (String apiKey) {
        this(apiKey, API_ENDPOINT);
    }

    /**
     * Creates a PDFDATA.io {@code API} instance tied to the specified API endpoint, and using an
     * API key sourced from the environment as described {@link #API() here}.
     */
    public API (URL endpoint) {
        this(apikey_env(), endpoint);
    }

    /**
     * Creates a PDFDATA.io {@code API} instance with the given API key and tied to the specified
     * API endpoint.
     */
    public API (String apiKey, URL endpoint) {
        // api.pdfdata.io/v1 is bad, /v1/ is good
        if (!endpoint.getPath().endsWith("/")) {
            try {
                endpoint = new URL(endpoint.toString() + "/");
            } catch (MalformedURLException e) {
                // really shouldn't be possible
                throw new RuntimeException(e);
            }
        }

        this.apiKey = apiKey;
        this.endpoint = endpoint;
        json = new JSON(this);
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public String getAPIKey () {
        return apiKey;
    }

    public URL getEndpoint () {
        return endpoint;
    }

    /**
     * @nodoc
     */
    public boolean isCaptureResponseBodies() {
        return captureResponseBodies;
    }

    /**
     * @nodoc
     */
    public void setCaptureResponseBodies(boolean captureResponseBodies) {
        this.captureResponseBodies = captureResponseBodies;
    }

    /**
     * Returns an API facility for working with source PDF {@link Document}s.
     */
    public DocumentsRequest documents () {
        return new DocumentsRequest(this);
    }

    /**
     * Returns an API facility for starting new and retrieving existing {@link Proc}s, which
     * apply content and data extraction {@link Operation}s to source PDF
     * {@link Document}s.
     */
    public ProcsRequest procs () {
        return new ProcsRequest(this);
    }

    /**
     * Returns an API facility for retrieving binary data {@link Resource}s, which are produced
     * by some content and data extraction {@link Operation}s.
     */
    public ResourcesRequest resources () {
        return new ResourcesRequest(this);
    }

    /**
     * Returns an API facility corresponding to the PDFDATA.io API's root informational resource.
     */
    public InfoRequest info () {
        return new InfoRequest(this);
    }

    /**
     * Parses a {@link String} in the format used by the PDFDATA.io API (the ISO 8601 string format
     * that corresponds to <a href="http://www.ecma-international.org/ecma-262/5.1/#sec-15.9.1.15">
     *     the standard JavaScript `Date` format</a>).
     *
     * @throws java.time.format.DateTimeParseException if {@code s} is not in the expected format
     */
    public static Instant parseDate (String s) {
        return LocalDateTime.parse(s, INSTANT_FORMATTER).toInstant(ZoneOffset.UTC);
    }

    private static String apikey_env () {
        String apikey = environment("PDFDATA_APIKEY", null);
        if (apikey == null)
            throw new IllegalStateException("You must provide an API key to use this library, " +
                    "either by passing it to a `io.pdfdata.API` constructor, or by setting the " +
                    "PDFDATA_APIKEY environment variable or system property.");
        return apikey;
    }

    private static URL endpoint_env () {
        String url = environment("PDFDATA_ENDPOINT", DEFAULT_API_ENDPOINT);

        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(
                    String.format("The `PDFDATA_ENDPOINT` system property / environment variable " +
                            "is set improperly: \"%s\" is not a valid URL", url),
                    e);
        }
    }

    private static String environment (String variableName, String defaultValue) {
        String s = System.getProperty(variableName);
        if (s != null) {
            return s;
        } else {
            s = System.getenv(variableName);
            return s == null ? defaultValue : s;
        }

    }
}
