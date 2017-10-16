package com.idfconnect.ssorest.common.test.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import com.idfconnect.ssorest.common.logging.Loggable;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;

@RunWith(MockitoJUnitRunner.class)
public class LoggableTest {
    @Loggable
    class ClassAnnotatedWithLoggable {
        public double power(int x, int y) {
            return Math.pow(x, y);
        }

        protected double powerProt(int x, int y) {
            return Math.pow(x, y);
        }

        double powerPkg(int x, int y) {
            return Math.pow(x, y);
        }
    }

    class MethodsAnnotatedWithLoggable {
        @Loggable(prepend = true)
        double power(int x, int y) {
            return Math.pow(x, y);
        }
    }

    @Mock
    private Appender<ILoggingEvent>       mockAppender;

    @Captor
    private ArgumentCaptor<ILoggingEvent> captorLoggingEvent;

    @Before
    public void setup() {
        final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(mockAppender);
    }

    @Test
    public void test_ClassAnnotatedWithLoggable_publicMethod() {
        // Make some logging happen
        ClassAnnotatedWithLoggable wl = new ClassAnnotatedWithLoggable();
        double result = wl.power(2, 3);
        assertEquals(8, result, 0);

        // Now verify our logging interactions
        verify(mockAppender).doAppend(captorLoggingEvent.capture());
        final ILoggingEvent loggingEvent = captorLoggingEvent.getValue();

        // Check log level is correct
        assertEquals(Level.INFO, loggingEvent.getLevel());

        // Check the message being logged is correct
        assertTrue(loggingEvent.getMessage().contains("[power(2,3)]"));
        assertTrue(loggingEvent.getMessage().contains("[returned 8.0]"));
    }

    @Test
    public void test_ClassAnnotatedWithLoggable_protectedMethod() {
        // Make some logging happen
        ClassAnnotatedWithLoggable wl = new ClassAnnotatedWithLoggable();
        double result = wl.powerProt(2, 3);
        assertEquals(8, result, 0);

        // Now verify our logging interactions
        verify(mockAppender).doAppend(captorLoggingEvent.capture());
        final ILoggingEvent loggingEvent = captorLoggingEvent.getValue();

        // Check log level is correct
        assertEquals(Level.INFO, loggingEvent.getLevel());

        // Check the message being logged is correct
        assertTrue(loggingEvent.getMessage().contains("[powerProt(2,3)]"));
        assertTrue(loggingEvent.getMessage().contains("[returned 8.0]"));
    }

    @Test
    public void test_methodsAnnotatedWithLoggable() {
        // Make some logging happen
        MethodsAnnotatedWithLoggable wl = new MethodsAnnotatedWithLoggable();
        double result = wl.power(2, 3);
        assertEquals(8, result, 0);

        // Now verify our logging interactions
        verify(mockAppender, times(2)).doAppend(captorLoggingEvent.capture());
        final ILoggingEvent loggingEvent = captorLoggingEvent.getValue();

        // Check log level is correct
        assertEquals(Level.INFO, loggingEvent.getLevel());

        // Check the message being logged is correct
        assertTrue(loggingEvent.getMessage().contains("[power(2,3)]"));
        assertTrue(loggingEvent.getMessage().contains("[returned 8.0]"));
    }
}
