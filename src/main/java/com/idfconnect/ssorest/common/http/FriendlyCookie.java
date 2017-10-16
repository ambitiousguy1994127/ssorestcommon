package com.idfconnect.ssorest.common.http;

import java.io.Serializable;

import javax.servlet.http.Cookie;

import org.slf4j.LoggerFactory;

import com.idfconnect.ssorest.common.utils.StringBuilderUtil;

/**
 *
 * Extension to Cookie that adds friendly toString() and equals implementations
 *
 * @author Richard Sand
 * @since 1.4
 */
public class FriendlyCookie extends Cookie implements Serializable {
    private static final long serialVersionUID = -4291086789409667996L;

    transient static boolean  is30spec         = false;

    static {
        try {
            is30spec = (Cookie.class.getDeclaredMethod("setHttpOnly", boolean.class) != null);
        } catch (SecurityException e) {
            LoggerFactory.getLogger(FriendlyCookie.class).warn("SecurityException reading Cookie class, Servlet API compliance set to 2.x");
        } catch (NoSuchMethodException e) {
            LoggerFactory.getLogger(FriendlyCookie.class).warn("No HttpOnly support in this container. Servlet API compliance set to 2.x");
        }
    }

    /**
     * Returns true if the Servlet API supports 3.0 or later. This is used to determine support for <code>HttpOnly</code>
     *
     * @return a boolean.
     */
    public static final boolean is30Spec() {
        return is30spec;
    }

    /**
     * Constructor for FriendlyCookie.
     *
     * @param name
     *            String
     * @param value
     *            String
     * @since 1.4
     */
    public FriendlyCookie(String name, String value) {
        super(name, value);
        this.setVersion(0);
    }

    /**
     * Constructor for FriendlyCookie.
     *
     * @param c
     *            Cookie
     * @since 1.4
     */
    public FriendlyCookie(Cookie c) {
        super(c.getName(), c.getValue());
        if (c.getComment() != null)
            setComment(c.getComment());
        if (c.getDomain() != null)
            setDomain(c.getDomain());
        setMaxAge(c.getMaxAge());
        if (c.getPath() != null)
            setPath(c.getPath());
        setSecure(c.getSecure());
        if (is30spec)
            setHttpOnly(c.isHttpOnly());
        if (c.getVersion() > -1)
            setVersion(c.getVersion());
        else
            setVersion(0);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FriendlyCookie [");
        if (getName() != null) {
            builder.append("getName()=");
            builder.append(getName());
            builder.append(", ");
        }
        if (getValue() != null) {
            builder.append("getValue()=");
            builder.append(getValue());
            builder.append(", ");
        }
        if (getDomain() != null) {
            builder.append("getDomain()=");
            builder.append(getDomain());
            builder.append(", ");
        }
        if (getPath() != null) {
            builder.append("getPath()=");
            builder.append(getPath());
            builder.append(", ");
        }
        builder.append("getMaxAge()=");
        builder.append(getMaxAge());
        if (getSecure() == true) {
            builder.append(", getSecure()=");
            builder.append(getSecure());
        }
        if (is30spec) {
            if (isHttpOnly()) {
                builder.append(", isHttpOnly()=");
                builder.append(isHttpOnly());
            }
        }
        builder.append(", ");
        if (getComment() != null) {
            builder.append("getComment()=");
            builder.append(getComment());
            builder.append(", ");
        }
        builder.append("getVersion()=");
        builder.append(getVersion());
        builder.append(']');
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     *
     * Method equals.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Cookie))
            return false;
        FriendlyCookie cookie2 = null;
        if (obj instanceof FriendlyCookie)
            cookie2 = (FriendlyCookie) obj;
        else
            cookie2 = new FriendlyCookie((Cookie) obj);
        return cookie2.toString().equals(this.toString());
    }

    /**
     * {@inheritDoc}
     *
     * Method hashCode.
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Method toHeaderString.
     *
     * @param cookie
     *            Cookie
     * @return String
     * @since 1.4
     */
    public static String toHeaderString(Cookie cookie) {
        StringBuilder sb = new StringBuilder();

        // name
        sb.append(cookie.getName()).append('=');
        StringBuilderUtil.appendQuotedIfWhitespace(sb, cookie.getValue());

        // version
        sb.append(';').append("Version=").append(cookie.getVersion());

        // comment
        if (cookie.getComment() != null) {
            sb.append("; Comment=");
            StringBuilderUtil.appendQuotedIfWhitespace(sb, cookie.getComment());
        }

        // domain
        if (cookie.getDomain() != null) {
            sb.append("; Domain=");
            StringBuilderUtil.appendQuotedIfWhitespace(sb, cookie.getDomain());
        }

        // path
        if (cookie.getPath() != null) {
            sb.append("; Path=");
            StringBuilderUtil.appendQuotedIfWhitespace(sb, cookie.getPath());
        }

        // max-age
        if (cookie.getMaxAge() != -1) {
            sb.append("; Max-Age=");
            sb.append(cookie.getMaxAge());
        }

        // secure
        if (cookie.getSecure()) {
            sb.append("; Secure");
        }

        // httponly
        if (is30spec)
            if (cookie.isHttpOnly()) {
                sb.append("; HttpOnly");
            }

        return sb.toString();
    }
}
