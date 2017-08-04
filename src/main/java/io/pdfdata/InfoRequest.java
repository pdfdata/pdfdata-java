package io.pdfdata;

import com.fasterxml.jackson.core.type.TypeReference;
import io.pdfdata.model.Info;

import java.io.IOException;

import static io.pdfdata.Network.Method.GET;

/**
 * API request facility corresponding to the PDFDATA.io API's root informational resource.
 *
 * @publicapi
 */
public class InfoRequest extends Request {
    private static final TypeReference<Info> TYPEREF = new TypeReference<Info>() {};

    public InfoRequest (API pdfdata) {
        super(pdfdata);
    }

    public Info get () throws IOException {
        return doRequest(GET, "", TYPEREF);
    }
}
