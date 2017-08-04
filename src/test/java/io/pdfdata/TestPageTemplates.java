package io.pdfdata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.pdfdata.model.Bounds;
import io.pdfdata.model.Proc;
import io.pdfdata.model.ops.PageTemplates;

import java.io.FileInputStream;
import java.io.IOException;

public class TestPageTemplates extends BaseAPITestCase {

    private PageTemplates W2Config = new PageTemplates()
            .withTemplate("2016 Form W-2", new PageTemplates.Template()
                    .withRegion("employee-ssn", new PageTemplates.Region(new Bounds(152.64988558352405, 732.4118993134997, 278.5583524027463, 748.713958810067)))
                    .withRegion("gross-wages", new PageTemplates.Region(new Bounds(331.908466819222, 708.2334096109811, 452.21510297482854, 724.9061784897004)))
                    .withRegion("W-2", new PageTemplates.Region(new Bounds(56.01830663615563, 428.1418764302055, 103.50114416475976, 453.9176201372997))
                            .containingString("W-2"))
                    .withRegion("year", new PageTemplates.Region(new Bounds(263.28604118993144, 422.54004576658974, 335.2768878718535, 454.61784897025154))
                            .matchingRegex("\\d{4}")));

    public void testRoundTrip () throws IOException {
        JsonNode cfgJson = pdfdata.json.from(
                new FileInputStream("src/test/resources/templates/w-2.js"), JsonNode.class);
        PageTemplates opCfg = pdfdata.json.from(cfgJson, PageTemplates.class);
        eq(W2Config, opCfg);

        JsonNode cfgJson2 = pdfdata.json.from(pdfdata.json.to(opCfg), JsonNode.class);
        eq(cfgJson, cfgJson2);

        // checking pagenum
        PageTemplates pageLockedConfig = new PageTemplates()
                .withTemplate("2016 Form W-2", W2Config.getTemplates().values().iterator().next()
                        .restrictToPage(0));
        ((ObjectNode)cfgJson.get("templates").get("2016 Form W-2")).put("pagenum", 0);

        opCfg = pdfdata.json.from(cfgJson, PageTemplates.class);
        eq(pageLockedConfig, opCfg);
        cfgJson2 = pdfdata.json.from(pdfdata.json.to(pageLockedConfig), JsonNode.class);
        eq(cfgJson, cfgJson2);
    }

    public void testTemplates () throws IOException {

        Proc proc = pdfdata.procs().configure()
                .withDocumentIDs("doc_" + TestDocuments.HASHES.get("W-2.pdf"))
                .withOperations(W2Config)
                .start();

        PageTemplates.Result expected = pdfdata.json.from(
                new FileInputStream("src/test/resources/templates/w-2-result.js"),
                PageTemplates.Result.class);

        PageTemplates.Result actual = (PageTemplates.Result) proc.getDocuments().get(0).getResults().get(0);

        eq(expected, actual);
    }
}
