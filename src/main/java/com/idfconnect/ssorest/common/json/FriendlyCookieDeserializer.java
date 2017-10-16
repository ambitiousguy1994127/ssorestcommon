/**
 * 
 */
package com.idfconnect.ssorest.common.json;

import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.idfconnect.ssorest.common.http.FriendlyCookie;

/**
 * <p>
 * FriendlyCookieDeserializer class.
 * </p>
 *
 * @author Tony
 * @since 1.4
 */
public class FriendlyCookieDeserializer implements JsonDeserializer<FriendlyCookie> {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    /** {@inheritDoc} */
    @Override
    public FriendlyCookie deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        logger.trace("Cookie to transfer {}", json);

        JsonElement name = jsonObject.get("name");
        if (name == null)
            return null;

        JsonElement value = jsonObject.get("value");
        if (value == null)
            return null;

        FriendlyCookie fc = new FriendlyCookie(name.getAsString(), value.getAsString());

        JsonElement domain = jsonObject.get("domain");
        if (domain != null)
            fc.setDomain(domain.getAsString());

        JsonElement path = jsonObject.get("path");
        fc.setPath(path == null ? null : path.getAsString());

        JsonElement maxAge = jsonObject.get("maxAge");
        fc.setMaxAge(maxAge == null ? 0 : maxAge.getAsInt());

        JsonElement version = jsonObject.get("version");
        fc.setVersion(version == null ? 0 : version.getAsInt());

        if (FriendlyCookie.is30Spec()) {
            JsonElement httpOnly = jsonObject.get("httpOnly");
            boolean isHttpOnly = (httpOnly == null) ? false : httpOnly.getAsBoolean();
            fc.setHttpOnly(isHttpOnly);
        }

        JsonElement secure = jsonObject.get("secure");
        fc.setSecure(secure == null ? false : secure.getAsBoolean());
        return fc;
    }
}
