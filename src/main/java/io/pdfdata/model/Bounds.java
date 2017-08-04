package io.pdfdata.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * A rectangular region, represented by its bottom-left ({@link #lx()} and {@link #by()}) and
 * top-right ({@link #rx()} and {@link #ty()}) coordinates.
 * @publicapi
 */
@JsonDeserialize(using=Bounds.BoundsDeserializer.class)
@JsonSerialize(using=Bounds.BoundsSerializer.class)
public class Bounds extends Entity {
    private final double lx, by, rx, ty;

    public Bounds(double lx, double by, double rx, double ty) {
        this.lx = lx;
        this.by = by;
        this.rx = rx;
        this.ty = ty;
    }

    public double lx () {
        return lx;
    }

    public double by () {
        return by;
    }

    public double rx () {
        return rx;
    }

    public double ty () {
        return ty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bounds bounds = (Bounds) o;

        if (Double.compare(bounds.lx, lx) != 0) return false;
        if (Double.compare(bounds.by, by) != 0) return false;
        if (Double.compare(bounds.rx, rx) != 0) return false;
        return Double.compare(bounds.ty, ty) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(lx);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(by);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(rx);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(ty);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    static class BoundsDeserializer extends StdDeserializer<Bounds> {
        BoundsDeserializer() {
            super(Bounds.class);
        }

        public Bounds deserialize(JsonParser jp, DeserializationContext deserializationContext)
                throws IOException {
            JsonNode array = jp.getCodec().readTree(jp);
            if (array.isArray() && array.size() == 4) {
                return new Bounds(array.get(0).doubleValue(),
                        array.get(1).doubleValue(),
                        array.get(2).doubleValue(),
                        array.get(3).doubleValue());
            }
            throw new IOException("Invalid bounds data, must be array of 4 numbers");
        }
    }

    static class BoundsSerializer extends StdSerializer<Bounds> {
        BoundsSerializer() {
            super(Bounds.class);
        }

        public void serialize(Bounds t, JsonGenerator jg, SerializerProvider serializerProvider)
                throws IOException {
            jg.writeStartArray();
            jg.writeNumber(t.lx());
            jg.writeNumber(t.by());
            jg.writeNumber(t.rx());
            jg.writeNumber(t.ty());
            jg.writeEndArray();
        }
    }
}
