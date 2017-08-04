package io.pdfdata.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @publicapi
 */
@JsonDeserialize
public class BitmapResource extends Resource {
    private Dimensions dimensions;

    /**
     * Returns the dimensions of a bitmap image's raster, in contrast to the bounds within
     * which an embedded PDF image is rendered on a page, provided by {@link Image#getBounds()}.
     */
    public Dimensions getDimensions() {
        return dimensions;
    }
}
