package io.pdfdata.model;

import io.pdfdata.model.ops.Images;

import java.io.IOException;

/**
 * Entity representing a bitmap image embedded in a PDF document, produced by the
 * {@link Images} op.
 *
 * To learn more about images in PDFs, the {@link Images} op, and the data it
 * provides conveyed by this class, please visit
 * <a href="https://www.pdfdata.io/apidoc/?java#images">the
 * dedicated section in the PDFDATA.io API reference</a>.
 * @publicapi
 */
public class Image extends ResourcefulEntity {
    private Bounds bounds;

    /**
     * Returns a representation of the on-page bounding box within which the embedded bitmap is
     * rendered. Note that these are <em>on-page</em> coordinates, <em>not</em> the dimensions of
     * the bitmap itself; see {@link BitmapResource#getDimensions()} via {@link #getResource()}
     * for that metric.
     */
    public Bounds getBounds() {
        return bounds;
    }

    /**
     * Returns the {@link BitmapResource} provides the raster data for this
     * {@code Image} and also describes its characteristics beyond those properties provided by
     * generic {@link Resource}s.
     */
    public BitmapResource getResource () {
        return (BitmapResource) super.getResource();
    }

    @Override
    public void registerResource (Operation.Result result) throws IOException {
        super.registerResource(result);
        if (!(getResource() instanceof BitmapResource)) {
            throw new IOException(
                    String.format("Resource %s was supposed to be a BitmapResource, but was %s instead",
                            getResourceID(),
                            getResource().getClass().getName()));
        }
    }
}
