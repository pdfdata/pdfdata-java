package io.pdfdata;

import io.pdfdata.model.Info;
import junit.framework.TestCase;

import java.io.IOException;

/**
 * Created by chas on 7/26/2017.
 */
public class TestInfo extends BaseAPITestCase {

    public void testInfo () throws IOException {
        Info info = pdfdata.info().get();
        assertEquals("Welcome to PDFDATA.io!", info.getMessage());
        assertTrue(info.getBuild().matches("^[\\da-f]{40}$"));
        assertTrue(info.getAPIVersion().matches("^\\d{4}-\\d\\d-\\d\\d$"));
    }
}
