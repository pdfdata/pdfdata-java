package io.pdfdata;

import junit.framework.TestCase;

public class BaseAPITestCase extends TestCase {
    protected final API pdfdata = configureAPI();

    static {
        Network.REQUIRE_SECURE_CONNECTIONS = false;
    }

    private static API configureAPI () {
        API pdfdata = new API();
        pdfdata.setCaptureResponseBodies(true);
        return pdfdata;
    }

    // keep the test runner happy
    public void testNothing () {}

    protected static void eq (Object expected, Object actual) {
        assertEquals(expected, actual);
    }
}
