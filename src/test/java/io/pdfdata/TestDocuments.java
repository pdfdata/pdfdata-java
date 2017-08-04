package io.pdfdata;

import io.pdfdata.model.Document;
import junit.framework.TestCase;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class TestDocuments extends BaseAPITestCase {

    static HashMap<String, File> DOCUMENTS = new HashMap<String,File>() {{
       for (File f : new File("src/test/resources/pdfs").listFiles()) {
           if (f.getName().endsWith(".pdf")) {
               put(f.getName(), f);
           }
       }
    }};

    static HashMap<Object, String> HASHES = new HashMap<Object, String>() {{
        try {
            for (File f : DOCUMENTS.values()) {
                String hash = fileHash(f);
                put(f.getName(), hash);
                put(f, hash);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }};

    static String fileHash (File f) throws IOException {
        return DigestUtils.sha1Hex(new FileInputStream(f));
    }

    private static void checkDocument (Document d, File source, Set<String> tags) throws
            IOException {
        assertEquals("doc_" + HASHES.get(source), d.getID());
        assertEquals(source.getName(), d.getFilename());
        assertTrue(d.getTags().size() >= tags.size());
        for (String tag : tags) d.getTags().contains(tag);
        assertNotNull(d.getCreated());
        assertNotNull(d.getExpires());
        assertTrue(d.getPageCount() > 0);
    }

    public void setUp () throws IOException, InterruptedException {
        setUp(1500);
    }

    public void setUp (long delay) throws IOException, InterruptedException {
        File f = DOCUMENTS.values().iterator().next();
        List<Document> dl = pdfdata.documents().upload(f);

        assertEquals(1, dl.size());
        Document d = dl.get(0);

        Set<String> tags = new HashSet<String>() {{
           add("acquired:" + LocalDate.now(ZoneId.of("UTC")));
        }};
        checkDocument(d, DOCUMENTS.get(d.getFilename()), tags);

        Thread.sleep(delay);

        Set<File> files = new HashSet<>(DOCUMENTS.values());
        files.remove(f);

        tags.add("some_tag");
        tags.add("other_tag");

        dl = pdfdata.documents().upload(tags, files);
        for (Document d2 : dl) {
            checkDocument(d2, DOCUMENTS.get(d2.getFilename()), tags);
        }
    }

    public void testRetrieval () throws IOException {
        File f = DOCUMENTS.values().iterator().next();
        Document d = pdfdata.documents().byID("doc_" + HASHES.get(f));
        assertEquals(f.getName(), d.getFilename());
    }

    public void testListing () throws IOException {
        List<Document> listing = pdfdata.documents().list();
        listing.removeIf(document -> !document.getFilename().endsWith(".pdf"));

        assertEquals(DOCUMENTS.size(), listing.size());
        Instant before = listing.get(0).getCreated();
        while (listing.get(0).getCreated().equals(before)) listing.remove(0);

        List<Document> l2 = pdfdata.documents().list(before);
        assertEquals(listing, l2);
    }

    public void test404 () throws IOException {
        try {
            pdfdata.documents().byID("doc_XXXXX");
            fail("document should not have been found");
        } catch (APIException e) {
            // just checking sanity here, not trying to be exhaustive
            assertEquals(404, e.getResponseStatus());
            assertEquals(0, e.getRequestParams().size());
            assertEquals(Network.mergeHeaders(new HashMap<>()), e.getRequestHeaders());
        }
    }

}
