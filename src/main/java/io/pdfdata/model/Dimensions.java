package io.pdfdata.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * Entity conveying the width and height of a region, but not its position (contrast
 * {@link Bounds}).
 * @publicapi
 */
@JsonDeserialize(using=Dimensions.DimensionsDeserializer.class)
public class Dimensions extends Entity {
    private final int width;
    private final int height;

    public Dimensions(int width, int height) {
        super();
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dimensions that = (Dimensions) o;

        if (width != that.width) return false;
        return height == that.height;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        return result;
    }

    static class DimensionsDeserializer extends StdDeserializer<Dimensions> {
        DimensionsDeserializer() {
            super(Dimensions.class);
        }

        public Dimensions deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            JsonNode array = jp.getCodec().readTree(jp);
            if (array.isArray() && array.size() == 2) {
                return new Dimensions(array.get(0).asInt(), array.get(1).asInt());
            }
            throw new IOException("Invalid dimensions data, must be array of 2 integers");
        }
    }
}
