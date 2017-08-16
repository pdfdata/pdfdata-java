package io.pdfdata;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import io.pdfdata.model.*;
import io.pdfdata.model.ops.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TestProcs extends BaseAPITestCase {

    public void setUp() {
        TestDocuments.DOCUMENTS.size();
    }

    public void testOperationDeserializationPolymorphism () throws IOException {
        ArrayList ops = new ArrayList() {{
            add(new Metadata());
            add(new XMPMetadata());
            add(new Images());
            add(new Text());
            add(new Text(Text.Layout.DECOMPOSE));
        }};
        String opsJSON = "[{\"op\":\"metadata\"}," +
                "{\"op\":\"xmp-metadata\"}," +
                "{\"op\":\"images\"}," +
                "{\"layout\":\"preserve\",\"op\":\"text\"}," +
                "{\"layout\":\"decompose\",\"op\":\"text\"}]";

        assertEquals(ops, pdfdata.json.from(opsJSON, new TypeReference<List<Operation>>() {}));
        assertEquals(opsJSON, pdfdata.json.to(ops));
    }

    public void testOne() throws IOException, InterruptedException {
        Proc proc = pdfdata.procs().configure()
                .withDocumentIDs("doc_" + TestDocuments.HASHES.get("7BECP84117T.pdf"))
                .withOperations(new Metadata())
                .start();

        assertNull(proc.getDocIDs());

        Proc proc2 = null;
        for (int wait : Arrays.asList(0, 500, 2000, 5000, 10000)) {
            Thread.sleep(wait);
            proc2 = pdfdata.procs().byID(proc.getID());
            if (proc2.getStatus() == Proc.Status.COMPLETE) break;
        }

        if (proc2.getStatus() != Proc.Status.COMPLETE) {
            fail("Timed out waiting for completed proc response");
        }

        for (Proc p : Arrays.asList(proc, proc2)) {
            Metadata.Result md = ((Metadata.Result) p.getDocuments().get(0).getResults().get(0));
            
            eq(md.getData(),
                    pdfdata.json.from("{\"Title\":\"\",\"Creator\":\"wkhtmltopdf 0.12.2.4\"," +
                                    "\"Producer\":\"Qt 4.8.6\",\"CreationDate\":\"2016-08-12T04:07:16Z\"}",
                            JsonNode.class));
            eq("", md.getTitle());
            eq("wkhtmltopdf 0.12.2.4", md.getCreator());
            eq("Qt 4.8.6", md.getProducer());
            eq(API.parseDate("2016-08-12T04:07:16Z"), md.getCreationDate());
        }
    }

    public void testPending () throws IOException {
        HashSet<String> docIDs = new HashSet<>();
        docIDs.add("doc_" + TestDocuments.HASHES.get("7BECP84117T.pdf"));
        Proc proc = pdfdata.procs().configure()
                .withDocumentIDs(docIDs)
                .withOperations(new Metadata())
                .withWait(0)
                .start();
        assertNull(proc.getDocuments());
        assertEquals(docIDs, proc.getDocIDs());
    }

    public void testAttachments () throws IOException {
        Proc proc = pdfdata.procs().configure()
                .withDocumentIDs("doc_" + TestDocuments.HASHES.get("attachments.pdf"))
                .withOperations(new Attachments())
                .start();

        Attachments.Result res = (Attachments.Result) proc.getDocuments().get(0).getResults().get(0);

        eq(proc.getDocuments().get(0), res.getDocument());
        assertFalse(res.isFailure());
        eq(2, res.getData().size());
        eq(1, res.getResources().size());

        for (Attachment a : res.getData()) {
            eq("avatar.png", a.getLocation());
            eq(0, a.getPagenum());
            eq(2085, Util.readBytes(a.getResource().get()).length);
            if (a.getBounds() == null) {
                assertNull(a.getTitle());
                eq("logo", a.getDescription());
            } else {
                eq("", a.getDescription());
                eq("somebody", a.getTitle());
                eq(new Bounds(172.615,595.586,192.615,619.586), a.getBounds());
            }
        }
    }

    public void testFailure () throws IOException {
        Proc proc = pdfdata.procs().configure()
                .withFiles(Arrays.asList("README.md", new File("pom.xml")))
                .withOperations(new Metadata())
                .start();

        eq(2, proc.getDocuments().size());
        for (ProcessedDocument d : proc.getDocuments()) {
            Operation.Result res = d.getResults().get(0);
            eq("metadata", res.getOperationName());
            assertTrue(res.isFailure());
            assertTrue(res instanceof Metadata.Result);
        }

    }

    public void testMany () throws IOException {
        HashSet<String> docIDs = new HashSet<>();
        for (String hash : TestDocuments.HASHES.values()) docIDs.add("doc_" + hash);

        Proc proc = pdfdata.procs().configure()
                .withDocumentIDs(docIDs)
                .withOperations(new Metadata(), new Images(), new Text(), new XMPMetadata())
                .start();

        assertNull(proc.getDocIDs());
        assertTrue(proc.getDocuments().size() == TestDocuments.DOCUMENTS.size());

        List<Class> expectedTypes = Arrays.asList(new Class[]
                {Metadata.Result.class, Images.Result.class, Text.Result.class,
                        XMPMetadata.Result.class});
        for (ProcessedDocument doc : proc.getDocuments()) {
            List<Class> resultTypes = new ArrayList<Class>() {{
                for (Operation.Result res : doc.getResults()) {
                    add(res.getClass());
                }
            }};
            assertEquals(expectedTypes, resultTypes);
        }

        HashSet<Dimensions> dims = new HashSet<>(Arrays.asList(
                new Dimensions(113, 44),
                new Dimensions(195, 137),
                new Dimensions(1392, 493)
        ));
        for (ProcessedDocument doc : proc.getDocuments()) {
            Images.Result result = (Images.Result) doc.getResults().get(1);
            for (Images.Page page : result.getData()) {
                for (Image img : page.getImages()) {
                    BitmapResource rsrc = (BitmapResource) img.getResource();
                    assertTrue(Util.readBytes(rsrc.get()).length > 0);
                    eq("image/png", rsrc.getMimetype());
                    assertTrue(dims.remove(rsrc.getDimensions()));
                }
            }
        }
        eq(0, dims.size());
    }
}
