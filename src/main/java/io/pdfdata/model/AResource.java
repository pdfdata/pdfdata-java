package io.pdfdata.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * @nodoc
 */
@JsonDeserialize(using=AResource.ResourceDeserializer.class)
public abstract class AResource extends Entity {
    public static class ResourceDeserializer extends StdDeserializer<AResource> {
        ResourceDeserializer() {
            super(AResource.class);
        }

        public AResource deserialize(JsonParser jp, DeserializationContext deserializationContext)
                throws IOException {
            ObjectReader reader;
            ObjectCodec codec = jp.getCodec();
            if (codec instanceof ObjectReader) {
                reader = (ObjectReader) codec;
            } else {
                reader = ((ObjectMapper)codec).reader().with(deserializationContext.getConfig());
            }

            JsonNode k = reader.readTree(jp);
            if (k.has("dimensions")) {
                return reader.treeToValue(k, BitmapResource.class);
            } else {
                return reader.treeToValue(k, Resource.class);
            }
        }
    }
}
