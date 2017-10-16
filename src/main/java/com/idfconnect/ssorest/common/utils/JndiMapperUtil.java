package com.idfconnect.ssorest.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>JndiMapperUtil class.</p>
 *
 * @author rsand
 * @since 1.4.5
 */
public class JndiMapperUtil {
    static Logger logger = LoggerFactory.getLogger(JndiMapperUtil.class);

    /**
     * <p>Constructor for JndiMapperUtil.</p>
     */
    public JndiMapperUtil() {
    }

    /**
     * Recursively list the JNDI namespace
     *
     * @param ctx a {@link javax.naming.Context} object.
     * @param parent a {@link java.lang.String} object.
     * @param processed a {@link java.util.List} object.
     * @param base a {@link java.util.Map} object.
     * @return a {@link java.util.Map} object.
     * @throws javax.naming.NamingException if any.
     */
    public static final Map<String, Object> mapContext(final Context ctx, final String parent, List<Object> processed, Map<String, Object> base) throws NamingException {
        NamingEnumeration<Binding> list = ctx.listBindings("");
        String prefix = ((parent != null && !"".equals(parent)) ? parent + "." : "");
        //int children = 0;
        while (list.hasMore()) {
            //children++;
            Binding nextBinding = (Binding) list.next();
            String name = nextBinding.getName();
            //String className = nextBinding.getClassName();
            Object nextBindingObject = nextBinding.getObject();

            if (processed.contains(nextBindingObject))
                continue;
            if (nextBindingObject instanceof Context) {
                Context nextCtx = (Context) nextBindingObject;
                String nns = nextCtx.getNameInNamespace();
                if (processed.contains(nns))
                    continue;
                processed.add(nns);
            } else
                processed.add(nextBindingObject);

            if (nextBindingObject instanceof Context) {
                HashMap<String, Object> node = new HashMap<String, Object>();
                base.put(prefix + name, node);
                mapContext((Context) nextBindingObject, prefix + name, processed, node);
                //mapContext((Context) nextBindingObject, prefix + name, processed, node).size();
                //int nextChildren = mapContext((Context) nextBindingObject, prefix + name, processed, node).size();
                //logger.trace("JNDI context [{}]: {}{} of type {} with {} children", children, prefix, name, className, nextChildren);
            } else {
                base.put(prefix + name, nextBindingObject);
                //logger.trace("JNDI object [{}]: {}{} of type {}", children, prefix, name, className);
            }
        }

        return base;
    }

    /**
     * <p>dumpMap.</p>
     *
     * @param map a {@link java.util.Map} object.
     * @param sb a {@link java.lang.StringBuffer} object.
     */
    @SuppressWarnings("unchecked")
    public static void dumpMap(Map<String, Object> map, StringBuffer sb) {
        for (Entry<String, Object> entry : map.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                dumpMap((Map<String, Object>) value, sb);
            } else {
                sb.append(name).append(" of type ").append(value.getClass().getName());
                sb.append(System.getProperty("line.separator"));
            }
        }
    }

    /**
     * Dumps the mapping of this context into a StringBuffer for logging purposes
     *
     * @param ctx a {@link javax.naming.Context} object.
     * @return a {@link java.lang.StringBuffer} object.
     */
    public static final StringBuffer dumpContext(Context ctx) {
        StringBuffer sb = new StringBuffer();
        try {
            Map<String, Object> map = mapContext(ctx, "", new ArrayList<Object>(), new HashMap<String, Object>());
            dumpMap(map, sb);
            return sb;
        } catch (NamingException ne) {
            logger.warn("Caught exception in dumpContext", ne);
        }
        return sb;
    }
    
    
    /**
     * Maps the initialContext
     *
     * @return a {@link java.util.Map} object.
     * @throws javax.naming.NamingException if any.
     */
    public static Map<String, Object> mapInitialContext() throws NamingException {
        return mapInitialContext(null);
    }
    
    /**
     * Maps the initialContext
     *
     * @param env a {@link java.util.Hashtable} object.
     * @return a {@link java.util.Map} object.
     * @throws javax.naming.NamingException if any.
     */
    public static Map<String, Object> mapInitialContext(Hashtable<?,?> env) throws NamingException {
        return mapContext(new InitialContext(env), "", new ArrayList<Object>(), new HashMap<String, Object>());
    }
}
