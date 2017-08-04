package io.pdfdata;

import io.pdfdata.model.Resource;
import io.pdfdata.model.ResourcefulEntity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * API request facility providing access to the binary data contents of PDFDATA.io
 * {@link Resource}s.
 *
 * In general, using {@link Resource#get()} will be more convenient.
 *
 * @publicapi
 */
public class ResourcesRequest extends Request {
    ResourcesRequest (API pdfdata) {
        super(pdfdata);
    }

    /**
     * Retrieves a {@link Resource}'s binary data from the PDFDATA.io API, given its
     * {@link Resource#getUrl() URL}.
     */
    public InputStream byURL(URL url) throws IOException {
        return doStreamGet(url.getPath() +
                (url.getQuery() == null ? "" : "?" + url.getQuery()));
    }

    /**
     * Retrieves a {@link Resource}'s binary data from the PDFDATA.io API, given its
     * ID, available via {@link ResourcefulEntity#getResourceID()}.
     */
    public InputStream byID (String resourceID) throws IOException {
        return byURL(new URL(pdfdata.getEndpoint(), "resources/" + resourceID));
    }
}
