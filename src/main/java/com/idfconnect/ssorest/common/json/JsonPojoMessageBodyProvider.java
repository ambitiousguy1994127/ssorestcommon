package com.idfconnect.ssorest.common.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * <p>JsonPOJOMessageBodyProvider class.</p>
 *
 * @author nghia
 * @since 3.0.4
 */
public class JsonPojoMessageBodyProvider implements MessageBodyWriter<JsonPojo>, MessageBodyReader<JsonPojo> {
    /** Constant <code>UTF8</code> */
    protected static final Charset UTF8   = Charset.forName("UTF-8");
    private                Logger  logger = LoggerFactory.getLogger(this.getClass());
    private Gson gson;

    private Gson getGson() {
        if (gson == null) {
            final GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();
        }
        return gson;
    }

    /** {@inheritDoc} */
    @Override public boolean isReadable(Class<?> type, Type genericType, java.lang.annotation.Annotation[] annotations, MediaType mediaType) {
        return JsonPojo.class.isAssignableFrom(type);
    }

    /** {@inheritDoc} */
    @Override public JsonPojo readFrom(Class<JsonPojo> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException {
        this.logger.debug("Reading {} from type {} with generic type {}", new Object[] { mediaType, type.getName(), genericType });
        InputStreamReader streamReader = new InputStreamReader(entityStream, UTF8);
        try {
            Type jsonType;
            if (type.equals(genericType)) {
                jsonType = type;
            } else {
                jsonType = genericType;
            }
            return getGson().fromJson(streamReader, jsonType);
        } finally {
            streamReader.close();
        }
    }

    /** {@inheritDoc} */
    @Override public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return JsonPojo.class.isAssignableFrom(type);
    }

    /** {@inheritDoc} */
    @Override public long getSize(JsonPojo object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    /** {@inheritDoc} */
    @Override public void writeTo(JsonPojo object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException, WebApplicationException {
        this.logger.debug("Writing {} to type {} with generic type {}", new Object[] { mediaType, type.getName(), genericType });
        OutputStreamWriter writer = new OutputStreamWriter(entityStream, UTF8);
        try {
            Type jsonType;
            if (type.equals(genericType)) {
                jsonType = type;
            } else {
                jsonType = genericType;
            }
            getGson().toJson(object, jsonType, writer);
        } finally {
            writer.close();
        }
    }
}

