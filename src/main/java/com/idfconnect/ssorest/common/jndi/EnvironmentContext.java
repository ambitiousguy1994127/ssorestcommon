package com.idfconnect.ssorest.common.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.NamingManager;

/**
 * This simple JNDI context is used to emulate Tomcat's handling of environment variables as a JNDI context.
 * It is useful for unit testing without a running Tomcat instance
 *
 * @author Richard Sand
 */
public class EnvironmentContext extends InitialContext {
    /** Constant <code>ENV_PREFIX="java:comp/env/"</code> */
    public static final String ENV_PREFIX = "java:comp/env/";
    
    static EnvironmentContext instance = null;

    /**
     * Calling this utility method will configure the JNDI InitialContextFactoryBuilder to provide the initial instance of EnvironmentContext
     * Call this method and set the JNDI initial factory method to any value to trigger usage of EnvironmentContext (e.g. -Djava.naming.factory.initial)
     *
     * @throws javax.naming.NamingException if any.
     */
    public static void setupInitialContext() throws NamingException {
        NamingManager.setInitialContextFactoryBuilder(new InitialContextFactoryBuilder() {
            @Override
            public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> environment) throws NamingException {
                return new InitialContextFactory() {
                    @Override
                    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
                        return EnvironmentContext.getInstance(environment);
                    }
                };
            }
        });
    }
    
    /**
     * <p>Getter for the field <code>instance</code>.</p>
     *
     * @param environment a {@link java.util.Hashtable} object.
     * @return a {@link javax.naming.InitialContext} object.
     * @throws javax.naming.NamingException if any.
     */
    public static InitialContext getInstance(Hashtable<?, ?> environment) throws NamingException {
        if (instance == null)
            instance = new EnvironmentContext(environment);
        return instance;
    }

    private EnvironmentContext(Hashtable<?, ?> environment) throws NamingException {
        super(environment);
    }

    /** {@inheritDoc} */
    @Override
    public Hashtable<?, ?> getEnvironment() throws NamingException {
        return this.myProps;
    }

    /** {@inheritDoc} */
    @Override
    public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        return this.myProps.put(propName, propVal);
    }

    /** {@inheritDoc} */
    @Override
    public Object removeFromEnvironment(String propName) throws NamingException {
        return this.myProps.remove(propName);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    protected void init(Hashtable<?, ?> environment) throws NamingException {
        this.myProps = (Hashtable<Object, Object>) environment.clone();
        this.gotDefault = true;
        this.defaultInitCtx = this;
    }

    /** {@inheritDoc} */
    @Override
    public Object lookup(String name) throws NamingException {
        if (!name.startsWith("java:comp/env/"))
            throw new NamingException("Unable to find name: " + name);
        return myProps.get(name.substring(14));
    }
}
