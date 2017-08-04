package io.pdfdata;

import com.fasterxml.jackson.core.type.TypeReference;
import io.pdfdata.model.Document;
import io.pdfdata.model.Operation;
import io.pdfdata.model.Proc;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static io.pdfdata.Network.Method.*;

/**
 * API request facility enabling the uploading of source PDF documents in anticipation of
 * applying {@link Operation}s to them via new {@link Proc}s, and the retrieval of metadata
 * related to PDFDATA.io's (temporary) storage of those documents.
 *
 * @publicapi
 */
public class DocumentsRequest extends Request {
    private static final String base = "/documents";
    private static final TypeReference<Document> TYPEREF = new TypeReference<Document>() {};
    private static final TypeReference<List<Document>> TYPEREF_LIST =
            new TypeReference<List<Document>> () {};

    DocumentsRequest(API pdfdata) {
        super(pdfdata);
    }

    public Document byID(String docid) throws IOException {
        return doRequest(GET, base + "/" + docid, TYPEREF);
    }

    public List<Document> list () throws IOException {
        return list(Instant.ofEpochMilli(4102444799000L));
    }

    public List<Document> list (Instant createdBefore) throws IOException {
        return doRequest(GET, base, Util.kvmap("before", createdBefore), TYPEREF_LIST);
    }

    public List<Document> upload (File... files) throws IOException {
        return upload(null, Arrays.asList(files));
    }

    public List<Document> upload (Collection<File> files) throws IOException {
        return upload(null, files);
    }

    public List<Document> upload (Collection<String> tags, Collection<File> files)
            throws IOException {
        return doRequest(POST, base, Util.kvmap("tag", Util.setFrom(tags), "file", files), TYPEREF_LIST);
    }

}