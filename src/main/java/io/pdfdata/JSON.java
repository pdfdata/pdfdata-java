package io.pdfdata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneOffset;

/**
 * @nodoc
 */
public class JSON {
    private static final ObjectMapper MAPPER = configureMapper(new ObjectMapper());
    private final API pdfdata;
    private final ObjectMapper mapper;
    private final ObjectReader reader;

    private static final ThreadLocal<API> TL_API = new ThreadLocal<>();

    public static API currentAPI () {
        API pdfdata = TL_API.get();
        assert pdfdata != null;
        return pdfdata;
    }

    public JSON (API pdfdata) {
        this.pdfdata = pdfdata;
        mapper = configureMapper(new ObjectMapper());
        reader = mapper.reader();
        // left here so we can remember how to configure the ObjectCodec
        // .withAttribute(API_BASE_URL, pdfdata.getEndpoint());
    }

    private static ObjectMapper configureMapper (ObjectMapper mapper) {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SimpleModule m = new SimpleModule();
        // support for instants w/o the additional dependency
        m.addSerializer(new InstantSerializer());
        m.addDeserializer(Instant.class, new InstantDeserializer());
        // case-converting enum mapping, from https://stackoverflow.com/a/24173645
        m.setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<Enum> modifyEnumDeserializer(DeserializationConfig config,
                                                                 final JavaType type,
                                                                 BeanDescription beanDesc,
                                                                 final JsonDeserializer<?> deserializer) {
                return new JsonDeserializer<Enum>() {
                    @Override
                    public Enum deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
                        Class<? extends Enum> rawClass = (Class<Enum<?>>) type.getRawClass();
                        return Enum.valueOf(rawClass, jp.getValueAsString().toUpperCase());
                    }
                };
            }
        });
        m.addSerializer(Enum.class, new StdSerializer<Enum>(Enum.class) {
            @Override
            public void serialize(Enum value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                jgen.writeString(value.name().toLowerCase());
            }
        });
        mapper.registerModule(m);
        return mapper;
    }

    private void startMapping () {
        TL_API.set(pdfdata);
    }

    public String to (Object obj) throws IOException {
        return to(obj, mapper);
    }

    public static String to0 (Object obj) throws IOException {
        return to(obj, MAPPER);
    }

    public static String to(Object obj, ObjectMapper mapper) throws IOException {
        StringWriter out = new StringWriter();
        mapper.writeValue(out, obj);
        return out.toString();
    }

    public <T> T from(InputStream is, TypeReference<T> resultType) throws IOException {
        return from(Util.readString(is), resultType);
    }

    public <T> T from(InputStream is, Class<T> cls) throws IOException {
        return from(Util.readString(is), cls);
    }

    public <T> T from(String data, Class<T> cls) throws IOException {
        startMapping();
        return reader.readValue(reader.getFactory().createParser(data), cls);
    }

    public <T> T from(String data, TypeReference<T> resultType) throws IOException {
        startMapping();
        return reader.readValue(reader.getFactory().createParser(data), resultType);
    }

    public <T> T from(JsonNode responseBody, TypeReference<T> resultType) throws IOException {
        startMapping();
        return reader.readValue(new TreeTraversingParser(responseBody, reader), resultType);
    }

    public <T> T from(JsonNode responseBody, Class<T> resultType) throws IOException {
        startMapping();
        return reader.readValue(new TreeTraversingParser(responseBody, reader), resultType);
    }


    static class InstantSerializer extends StdSerializer<Instant> {
        InstantSerializer() {
            super(Instant.class);
        }

        public void serialize(Instant t, JsonGenerator jg, SerializerProvider serializerProvider)
                throws IOException {
            jg.writeString(API.INSTANT_FORMATTER.format(t.atOffset(ZoneOffset.UTC)));
        }
    }

    static class InstantDeserializer extends StdDeserializer<Instant> {
        InstantDeserializer() {
            super(Instant.class);
        }

        public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            return API.parseDate(jsonParser.getText());
        }
    }
}
