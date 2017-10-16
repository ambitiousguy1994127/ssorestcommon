package com.idfconnect.ssorest.common.thread;

import java.util.EventListener;

/**
 * Interface for classes that want to listen for lifecycle events from a {@link LifecycleThread}
 *
 */
public interface LifecycleThreadListener extends EventListener {
    /**
     * Handle when Thread.start() is called
     * 
     * @param thread
     */
    default void onThreadStartInvoked(LifecycleThread thread) {
    }

    /**
     * Handle when Thread.start() is completed
     * 
     * @param thread
     */
    default void onThreadStartCompleted(LifecycleThread thread) {
    }

    /**
     * Handle when Thread.stop() is completed
     * 
     * @param thread
     */
    default void onThreadStopped(LifecycleThread thread) {
    }

    /**
     * Handle when Thread.interrupt() is completed
     * 
     * @param thread
     */
    default void onThreadInterrupted(LifecycleThread thread) {
    }

    /**
     * Handle when LifecycleThread.update() is called
     * 
     * @param thread
     */
    default void onThreadUpdate(LifecycleThread thread) {
    }

    /**
     * Handle when the LifecycleThread is running and ready to do its job. This event is typically used by threads that have an inner loop inside their run()
     * method to indicate that the thread is completed initialization and ready to do its job, whatever it may be
     * 
     * @param thread
     */
    default void onThreadRunning(LifecycleThread thread) {
    }
}
