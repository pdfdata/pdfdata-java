package io.pdfdata.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.pdfdata.API;
import io.pdfdata.JSON;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Base class for binary data resources produced by some {@link Operation}s.
 *
 * @publicapi
 */
@JsonDeserialize
public class Resource extends AResource {
    @JsonDeserialize(using=ServiceRelativeURLDeserializer.class)
    private URL url;

    private String mimetype;

    @JsonIgnore
    private final API pdfdata;

    {
        pdfdata = JSON.currentAPI();
    }

    /**
     * Returns the API URL by which this resource's data may be retrieved. Generally, you'll want
     * to simply use {@link #get()}.
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Returns the MIME type associated with this resource's data.
     */
    public String getMimetype() {
        return mimetype;
    }

    /**
     * Retrieves this resource's data from the PDFDATA.io API.
     */
    public InputStream get () throws IOException {
        return pdfdata.resources().byURL(url);
    }

    private static class ServiceRelativeURLDeserializer extends StdDeserializer<URL> {
        public ServiceRelativeURLDeserializer () {
            super(URL.class);
        }

        @Override
        public URL deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new URL(JSON.currentAPI().getEndpoint(), p.getText());
        }
    }
}
