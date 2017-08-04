package io.pdfdata;

import com.fasterxml.jackson.core.type.TypeReference;
import io.pdfdata.model.*;
import io.pdfdata.model.ops.Images;
import io.pdfdata.model.ops.Metadata;
import io.pdfdata.model.ops.Text;
import junit.framework.TestCase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static io.pdfdata.model.Proc.Status.COMPLETE;
import static io.pdfdata.model.Proc.Status.PENDING;
import static java.util.Collections.*;

import static io.pdfdata.JSON.parseDate;

public class TestResponseMapping extends BaseAPITestCase {
    private <T> T slurpResponse (String jsonPath, Class<T> cls) throws IOException {
        byte[] json = Files.readAllBytes(Paths.get("src/test/resources/responses", jsonPath));
        return pdfdata.json.from(new String(json, Network.CHARSET), cls);
    }

    public void testPendingProc () throws IOException {
        Proc p = slurpResponse("pending-proc.js", Proc.class);
        eq("proc_1555580e8ff", p.getID());
        eq(parseDate("2016-06-15T19:19:19Z"), p.getCreated());
        eq(emptySet(), p.getSourceTags());
        eq(singletonList(new Metadata()), p.getOperations());
        eq(PENDING, p.getStatus());
        eq(new HashSet<>(Arrays.asList("doc_8e9600cd7db5baf2fad83e4d8b48359678b24322",
                "doc_8e96ec0533ac3e1e988b7d1ca27bfdc096b82ddc",
                "doc_a5d8e5d0b99ac891226acb35f24a9f8f8eda50df")),
                p.getDocIDs());
        assertNull(p.getDocuments());
    }

    public void testCompletedProc () throws IOException {
        Proc p = slurpResponse("completed-proc.js", Proc.class);
        eq("proc_1555580e8ff", p.getID());
        eq(parseDate("2016-06-15T19:19:19Z"), p.getCreated());
        eq(emptySet(), p.getSourceTags());
        eq(Arrays.asList(new Images(), new Text(), new Text(Text.Layout.DECOMPOSE)), p.getOperations());
        eq(COMPLETE, p.getStatus());
        assertNull(p.getDocIDs());

    }

    public void testBoundsFailure () throws IOException {
        Proc p = slurpResponse("completed-proc-2.js", Proc.class);
        List<Bounds> bounds = pdfdata.json.from("[[422.63,784.2718,490.4489,810.67914]," +
                "[39.123013,667.83936,156.15565,750.06226]," +
                "[18.909943, 733.7, 259.80994, 819.2]]", new TypeReference<List<Bounds>>(){});
        for (ProcessedDocument d : p.getDocuments()) {
            for (Operation.Result res : d.getResults()) {
                for (Images.Page page : ((Images.Result)res).getData()) {
                    for (Image img : page.getImages()) {
                        eq(bounds.get(0), img.getBounds());
                        bounds.remove(0);
                    }
                }
            }
        }
        eq(0, bounds.size());
    }
}
