package io.pdfdata.model;

import io.pdfdata.model.ops.Attachments;

/**
 * Entity representing a binary attachment to a PDF document, produced by the {@link Attachments}
 * op.
 *
 * To learn more about PDF document attachments, the {@link Attachments} op, and the data it
 * provides conveyed by this class, please visit
 * <a href="https://www.pdfdata.io/apidoc/?java#document-attachments">the
 * dedicated section in the PDFDATA.io API reference</a>.
 * @publicapi
 */
public class Attachment extends ResourcefulEntity {
    private String description;
    private String location;

    private String title;
    private int pagenum;
    private Bounds bounds;

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getTitle() {
        return title;
    }

    public int getPagenum() {
        return pagenum;
    }

    public Bounds getBounds() {
        return bounds;
    }
}
