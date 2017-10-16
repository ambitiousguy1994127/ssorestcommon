package com.idfconnect.ssorest.common.thread;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread subclass that provides Lifecycle listener support
 *
 * @author Richard Sand
 */
public class LifecycleThread extends Thread {
    private List<LifecycleThreadListener> listeners = null;
    private boolean                       running   = false;

    /**
     * <p>
     * Constructor for LifecycleThread.
     * </p>
     */
    public LifecycleThread() {
        super();
    }

    /**
     * <p>
     * Constructor for LifecycleThread.
     * </p>
     *
     * @param target
     *            a {@link java.lang.Runnable} object.
     */
    public LifecycleThread(Runnable target) {
        super(target);
    }

    /**
     * <p>
     * Constructor for LifecycleThread.
     * </p>
     *
     * @param name
     *            a {@link java.lang.String} object.
     */
    public LifecycleThread(String name) {
        super(name);
    }

    /**
     * <p>
     * Constructor for LifecycleThread.
     * </p>
     *
     * @param group
     *            a {@link java.lang.ThreadGroup} object.
     * @param target
     *            a {@link java.lang.Runnable} object.
     */
    public LifecycleThread(ThreadGroup group, Runnable target) {
        super(group, target);
    }

    /**
     * <p>
     * Constructor for LifecycleThread.
     * </p>
     *
     * @param group
     *            a {@link java.lang.ThreadGroup} object.
     * @param name
     *            a {@link java.lang.String} object.
     */
    public LifecycleThread(ThreadGroup group, String name) {
        super(group, name);
    }

    /**
     * <p>
     * Constructor for LifecycleThread.
     * </p>
     *
     * @param target
     *            a {@link java.lang.Runnable} object.
     * @param name
     *            a {@link java.lang.String} object.
     */
    public LifecycleThread(Runnable target, String name) {
        super(target, name);
    }

    /**
     * <p>
     * Constructor for LifecycleThread.
     * </p>
     *
     * @param group
     *            a {@link java.lang.ThreadGroup} object.
     * @param target
     *            a {@link java.lang.Runnable} object.
     * @param name
     *            a {@link java.lang.String} object.
     */
    public LifecycleThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
    }

    /**
     * <p>
     * Constructor for LifecycleThread.
     * </p>
     *
     * @param group
     *            a {@link java.lang.ThreadGroup} object.
     * @param target
     *            a {@link java.lang.Runnable} object.
     * @param name
     *            a {@link java.lang.String} object.
     * @param stackSize
     *            a long.
     */
    public LifecycleThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
    }

    /**
     * <p>
     * addLifecycleThreadListener.
     * </p>
     *
     * @param listener
     *            a {@link com.idfconnect.ssorest.common.thread.LifecycleThreadListener} object.
     * @return a boolean.
     */
    public final boolean addLifecycleThreadListener(LifecycleThreadListener listener) {
        if (listeners == null)
            listeners = new ArrayList<LifecycleThreadListener>();
        LoggerFactory.getLogger(getClass()).debug("Added listener {}", listener);
        return listeners.add(listener);
    }

    /**
     * <p>
     * removeLifecycleThreadListener.
     * </p>
     *
     * @param listener
     *            a {@link com.idfconnect.ssorest.common.thread.LifecycleThreadListener} object.
     * @return a boolean.
     */
    public final boolean removeLifecycleThreadListener(LifecycleThreadListener listener) {
        Logger logger = LoggerFactory.getLogger(getClass());
        if (listeners == null)
            return false;
        boolean found = listeners.remove(listener);
        if (found)
            logger.debug("Removed listener {}", listener);
        else
            logger.debug("No listener {} is listening to this thread", listener);

        return found;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized final void start() {
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.debug("Thread starting");
        if (listeners != null)
            listeners.forEach(startInvoked -> {
                logger.debug("Notifying listener thread startup invoked {}", startInvoked);
                startInvoked.onThreadStartInvoked(this);
            });
        super.start();
        logger.debug("Thread started");
        if (listeners != null)
            listeners.forEach(startCompleted -> {
                logger.debug("Notifying listener thread startup completed {}", startCompleted);
                startCompleted.onThreadStartCompleted(this);
            });
    }

    /** {@inheritDoc} */
    @Override
    public final void run() {
        runLifecycleThread();
        Logger logger = LoggerFactory.getLogger(getClass());
        if (listeners != null)
            listeners.forEach(threadStopped -> {
                logger.debug("Notifying listener thread stopping {}", threadStopped);
                threadStopped.onThreadStopped(this);
            });
        logger.debug("Thread stopped");
    }

    /**
     * This method is typically overridden by subclasses
     */
    public void runLifecycleThread() {
        super.run();
    }

    /** {@inheritDoc} */
    @Override
    public final void interrupt() {
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.debug("Thread interrupted");
        if (listeners != null)
            listeners.forEach(interrupted -> {
                logger.debug("Notifying listener of thread interrupt {}", interrupted);
                interrupted.onThreadInterrupted(this);
            });
        super.interrupt();
    }

    /**
     * <p>
     * updateThreadListeners.
     * </p>
     */
    public final void updateThreadListeners() {
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.debug("Thread updated");
        if (listeners != null)
            listeners.forEach(update -> {
                logger.debug("Updating listener {}", update);
                update.onThreadUpdate(this);
            });
    }

    /**
     * Indicates if the thread is actively running, meaning it is initialized and ready to perform its tasks
     *
     * @return a boolean.
     */
    public final boolean isRunning() {
        return running;
    }

    /**
     * This should be called by the thread after any initialization done in its runLifecycleThread method is complete and its inner loop is started
     *
     * @param running a boolean.
     */
    protected final void setRunning(boolean running) {
        Logger logger = LoggerFactory.getLogger(getClass());
        this.running = running;
        if (running) {
            logger.debug("Thread is ready for service");
            if (listeners != null)
                listeners.forEach(update -> {
                    logger.debug("Notifying listener that thread is ready {}", update);
                    update.onThreadRunning(this);
                });
        }
        else
            logger.debug("Thread is exiting service");
    }
}
