package com.idfconnect.ssorest.common.test.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

/**
 */
public class TestServletOutputStream extends ServletOutputStream {
    ByteArrayOutputStream baos;
    
    public TestServletOutputStream() {
        baos = new ByteArrayOutputStream();
    }

    /**
     * Method write.
     * @param b int
     * @throws IOException
     */
    @Override
    public void write(int b) throws IOException {
        baos.write(b);
    }

    /**
     * Method flush.
     * @throws IOException
     * @see java.io.Flushable#flush()
     */
    @Override
    public void flush() throws IOException {
        baos.flush();
    }

    /**
     * Method write.
     * @param b byte[]
     * @param off int
     * @param len int
     * @throws IOException
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        baos.write(b, off, len);
    }

    /**
     * Method write.
     * @param b byte[]
     * @throws IOException
     */
    @Override
    public void write(byte[] b) throws IOException {
        baos.write(b);
    }

    /**
     * Method toString.
     * @return String
     */
    @Override
    public String toString() {
        return baos.toString();
    }
}
