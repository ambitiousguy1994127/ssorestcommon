package com.idfconnect.ssorest.common.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

/**
 * <p>
 * GsonMessageBodyHandler
 * </p>
 * is a generic JAX-RS {@link javax.ws.rs.ext.Provider} for Google's GSon library. Visit https://github.com/google/gson for more information on GSon
 *
 * @author rsand
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonGenericMessageBodyProvider<T> implements MessageBodyReader<JsonDeserializer<T>>, MessageBodyWriter<JsonSerializer<T>> {
    /** Constant <code>UTF8</code> */
    protected static final Charset     UTF8                      = Charset.forName("UTF-8");

    private Logger                     logger                    = LoggerFactory.getLogger(this.getClass());
    private Gson                       gson;

    private static final Set<Class<?>> baseTypesForOtherHandlers = new HashSet<Class<?>>();
    private static final Set<Class<?>> handlerCache              = new HashSet<Class<?>>();

    /**
     * <p>
     * Constructor for GsonMessageBodyHandler.
     * </p>
     *
     * @since 3.0
     */
    public GsonGenericMessageBodyProvider() {
        logger.trace("Constructing a new GsonMessageBodyHandler");
        if (!getClass().equals(GsonGenericMessageBodyProvider.class)) {
            for (Class<?> nextclass : getBaseTypes())
                baseTypesForOtherHandlers.add(nextclass);
        }
    }

    /**
     * Method getGsonBuilder.
     *
     * @return GsonBuilder
     * @since 3.0
     */
    protected GsonBuilder getGsonBuilder() {
        logger.debug("Inside getGsonBuilder");
        final GsonBuilder gsonBuilder = new GsonBuilder();
        // gsonBuilder.registerTypeHierarchyAdapter(WebAgentRequestWrapper.class, new JSonHttpRequest());
        // gsonBuilder.registerTypeHierarchyAdapter(WebAgentResponseWrapper.class, new JSonHttpResponse());
        // gsonBuilder.registerTypeAdapter(JSonGatewayResponse.class, new JSonGatewayResponse());
        return gsonBuilder;
    }

    /**
     * Method getGson.
     *
     * @return Gson
     * @since 3.0
     */
    protected final Gson getGson() {
        if (gson == null) {
            logger.debug("Initializing Gson");
            gson = getGsonBuilder().create();
        }
        return gson;
    }

    /**
     * <p>isHandler.</p>
     *
     * @param type a {@link java.lang.Class} object.
     * @param genericType a {@link java.lang.reflect.Type} object.
     * @param annotations an array of java$lang$annotation$Annotation objects.
     * @param mediaType a {@link javax.ws.rs.core.MediaType} object.
     * @return a boolean.
     * @since 3.0
     */
    public final boolean isHandler(Class<?> type, Type genericType, java.lang.annotation.Annotation[] annotations, MediaType mediaType) {
        logger.trace("Evaluating class {}, genericType {}, annotations {}, mediatype {}", type, genericType, annotations, mediaType);

        if (getClass().equals(GsonGenericMessageBodyProvider.class)) {
            if (handlerCache.contains(type)) {
                logger.trace("Another handler found in cache for {}", type);
                return false;
            }
            for (Class<?> otherhandler : baseTypesForOtherHandlers) {
                if (otherhandler.isAssignableFrom(type)) {
                    logger.trace("We have another handler for {}, adding to cache", type);
                    handlerCache.add(type);
                    return false;
                }
            }
        }

        for (Class<?> nextclass : getBaseTypes())
            if (nextclass.isAssignableFrom(type))
                return true;
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * Method isReadable.
     */
    public boolean isReadable(Class<?> type, Type genericType, java.lang.annotation.Annotation[] annotations, MediaType mediaType) {
        return isHandler(type, genericType, annotations, mediaType);
    }

    /**
     * {@inheritDoc}
     *
     * Method readFrom.
     */
    @Override
    public final JsonDeserializer<T> readFrom(Class<JsonDeserializer<T>> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream) throws IOException {
        logger.debug("Reading {} from type {} with generic type {}", mediaType, type.getName(), genericType);
        InputStreamReader streamReader = new InputStreamReader(entityStream, UTF8);
        try {
            Type jsonType = (type.equals(genericType) ? type : genericType);
            return getGson().fromJson(streamReader, jsonType);
        } finally {
            streamReader.close();
        }
    }

    /**
     * {@inheritDoc}
     *
     * Method isWriteable.
     */
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isHandler(type, genericType, annotations, mediaType);
    }

    /**
     * {@inheritDoc}
     *
     * Method getSize.
     */
    @Override
    public long getSize(JsonSerializer<T> object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    /**
     * {@inheritDoc}
     *
     * Method writeTo.
     */
    @Override
    public final void writeTo(JsonSerializer<T> object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException, WebApplicationException {
        logger.debug("Writing {} to type {} with generic type {}", mediaType, type.getName(), genericType);
        OutputStreamWriter writer = new OutputStreamWriter(entityStream, UTF8);
        try {
            Type jsonType = (type.equals(genericType) ? type : genericType);
            getGson().toJson(object, jsonType, writer);
        } finally {
            writer.close();
        }
    }

    /**
     * <p>
     * getBaseTypes.
     * </p>
     *
     * @return an array of {@link java.lang.Class} objects.
     * @since 3.0
     */
    protected Class<?>[] getBaseTypes() {
        return new Class<?>[] { JsonSerializer.class };
    }
}
