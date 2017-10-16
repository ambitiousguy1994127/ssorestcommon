package com.idfconnect.ssorest.common.test.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletInputStream;

/**
 */
public class TestServletInputStream extends ServletInputStream {
    InputStream is;

    /**
     * Constructor for TestServletInputStream.
     * @param content String
     */
    public TestServletInputStream(String content) {
        super();
        try {
            is = new ByteArrayInputStream(content.getBytes("ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            is = new ByteArrayInputStream(content.getBytes()); // should never happen
        }
    }

    /**
     * Constructor for TestServletInputStream.
     * @param content String
     * @param encoding String
     * @throws UnsupportedEncodingException
     */
    public TestServletInputStream(String content, String encoding) throws UnsupportedEncodingException {
        super();
        is = new ByteArrayInputStream(content.getBytes(encoding));
    }

    /**
     * Constructor for TestServletInputStream.
     * @param is InputStream
     */
    public TestServletInputStream(InputStream is) {
        this.is = is;
    }

    /**
     * Method read.
     * @return int
     * @throws IOException
     */
    @Override
    public int read() throws IOException {
        return is.read();
    }

    /**
     * Method close.
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() {
        if (is != null)
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    /**
     * Method available.
     * @return int
     * @throws IOException
     */
    @Override
    public int available() throws IOException {
        return is.available();
    }

    /**
     * Method mark.
     * @param readlimit int
     */
    @Override
    public synchronized void mark(int readlimit) {
        is.mark(readlimit);
    }

    /**
     * Method markSupported.
     * @return boolean
     */
    @Override
    public boolean markSupported() {
        return is.markSupported();
    }

    /**
     * Method read.
     * @param b byte[]
     * @param off int
     * @param len int
     * @return int
     * @throws IOException
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        // TODO Auto-generated method stub
        return is.read(b, off, len);
    }

    /**
     * Method read.
     * @param b byte[]
     * @return int
     * @throws IOException
     */
    @Override
    public int read(byte[] b) throws IOException {
        return is.read(b);
    }

    /**
     * Method reset.
     * @throws IOException
     */
    @Override
    public synchronized void reset() throws IOException {
        is.reset();
    }

    /**
     * Method skip.
     * @param n long
     * @return long
     * @throws IOException
     */
    @Override
    public long skip(long n) throws IOException {
        return is.skip(n);
    }
}
