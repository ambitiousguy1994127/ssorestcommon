package com.idfconnect.ssorest.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.codec.binary.Base64;

import com.idfconnect.ssorest.common.SSORestException;

/**
 * A simple generator of HTML content for use in building custom server error pages using stylesheet transforms
 *
 * @author Richard Sand
 * @since 1.4
 */
public class HTMLPageGenerator {
    // TODO (77) make this configurable
    /** Constant <code>ERRORPAGE_XML_401="style/401error.xml"</code> */
    public static final String               ERRORPAGE_XML_401    = "style/401error.xml";
    /** Constant <code>ERRORPAGE_XSL_401="style/401error.xsl"</code> */
    public static final String               ERRORPAGE_XSL_401    = "style/401error.xsl";
    /** Constant <code>ERRORPAGE_XML_403="style/403error.xml"</code> */
    public static final String               ERRORPAGE_XML_403    = "style/403error.xml";
    /** Constant <code>ERRORPAGE_XML_403="style/403error.xsl"</code> */
    public static final String               ERRORPAGE_XSL_403    = "style/403error.xsl";
    /** Constant <code>PAGE_POSTPRESERVE="style/postpreserve.html"</code> */
    public static final String               PAGE_POSTPRESERVE    = "style/postpreserve.html";

    private static final String              XML_START_IDENTIFY   = "<?xml";
    private static final String              BLANK_STRING         = "";
    private static final String              NULL                 = "null";

    /** Constant <code>DO_BASE64_ENCODE=true</code> */
    public static final boolean              DO_BASE64_ENCODE     = true;
    /** Constant <code>DO_NOT_BASE64_ENCODE=false</code> */
    public static final boolean              DO_NOT_BASE64_ENCODE = false;

    private static Hashtable<String, String> files                = new Hashtable<String, String>();

    // TODO (78) make this take in an output stream directly instead of going into a string
    // TODO (78) the getJavaVersion stuff should go - make sure this is easily portable and won't conflict with other deployed apps
    /**
     * Method parseHTML.
     *
     * @param xml
     *            String
     * @param xsl
     *            String
     * @return String
     * @throws java.io.IOException
     *             if any.
     * @throws javax.xml.transform.TransformerException
     *             if any.
     * @since 1.4
     */
    public String parseHTML(String xml, String xsl) throws IOException, TransformerException {
        if ((xml == null) || (xsl == null)) {
            return null;
        }

        if (getJavaVersion() >= 1.5) {
            System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
        }
        StringWriter htmlSource = new StringWriter();
        Source xmlSource = null;
        Source xslSource = null;
        Transformer transformer = null;
        TransformerFactory factory = TransformerFactory.newInstance();

        if (xml.startsWith(XML_START_IDENTIFY)) // specified the content
            xmlSource = new StreamSource(new StringReader(xml));
        else
            // specified the FileName
            xmlSource = new StreamSource(new StringReader(getFileContent(xml)));

        if (xsl.startsWith(XML_START_IDENTIFY)) // specified the content
            xslSource = new StreamSource(new StringReader(xsl));
        else
            // specified the FileName
            xslSource = new StreamSource(new StringReader(getFileContent(xsl)));

        transformer = factory.newTransformer(xslSource);
        transformer.transform(xmlSource, new StreamResult(htmlSource));
        return htmlSource.getBuffer().toString();
    }

    // TODO (78) take in the paths instead of looking up on the classpath
    /**
     * Method getFileContent.
     * 
     * @param fileName
     *            String
     * @return String
     * @throws IOException
     */
    private String getFileContent(String fileName) throws IOException {
        if (fileName == null) {
            return BLANK_STRING;
        }

        if (files.containsKey(fileName))
            return files.get(fileName);

        InputStream is = null;
        if (fileName.startsWith("http://") || (fileName.startsWith("https://"))) {
            URL url = new URL(fileName);
            URLConnection urlConnection = url.openConnection();
            is = urlConnection.getInputStream();
        } else
            is = this.getClass().getClassLoader().getResourceAsStream(fileName);
        if (is == null)
            throw new IOException("File not found " + fileName);

        BufferedReader bf = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuffer contentBuffer = new StringBuffer();
        String s = null;
        while ((s = bf.readLine()) != null)
            contentBuffer.append(s);
        String content = contentBuffer.toString();
        files.put(fileName, content);
        bf.close();
        is.close();
        return content;
    }

    /**
     * Method getJavaVersion.
     * 
     * @return float
     */
    private float getJavaVersion() {
        String version = System.getProperty("java.version");
        version = version.substring(0, 3);
        return Float.parseFloat(version);
    }

    /**
     * Convenience method to write the generated HTML content to a response
     *
     * @param xml
     *            a {@link java.lang.String} object.
     * @param xsl
     *            a {@link java.lang.String} object.
     * @param writer
     *            PrintWriter
     * @param charset
     *            a {@link java.lang.String} object.
     * @param b64encode
     *            a boolean.
     * @return int
     * @throws com.idfconnect.ssorest.common.SSORestException
     *             if any.
     * @since 1.4
     */
    public static final int writeResponseStream(String xml, String xsl, PrintWriter writer, String charset, boolean b64encode) throws SSORestException {
        try {
            HTMLPageGenerator pg = new HTMLPageGenerator();
            String results = pg.parseHTML(xml, xsl);
            if (b64encode)
                results = Base64.encodeBase64String(results.getBytes(charset));
            writer.print(results);
            return results.length();
        } catch (IOException ioe) {
            throw new SSORestException("Error reading response content", ioe);
        } catch (TransformerException te) {
            throw new SSORestException("Error generating response content", te);
        } finally {
            writer.flush();
        }
    }

    /**
     * Writes the specified file with HTML content to a response writer, replacing all SiteMinder tokens specified in the mapping
     *
     * @param file
     *            a {@link java.lang.String} object.
     * @param tokens
     *            a {@link java.util.Map} object.
     * @param writer
     *            a {@link java.io.PrintWriter} object.
     * @param charset
     *            a {@link java.lang.String} object.
     * @param b64encode
     *            true to base64 encode the output stream
     * @return int
     * @throws com.idfconnect.ssorest.common.SSORestException
     *             if any.
     * @since 1.4
     */
    public static final int writeResponseStream(String file, Map<String, String> tokens, PrintWriter writer, String charset, boolean b64encode) throws SSORestException {
        try {
            HTMLPageGenerator pg = new HTMLPageGenerator();
            String content = pg.getFileContent(file);
            for (Entry<String, String> entry : tokens.entrySet()) {
                String tokenvalue = entry.getValue();
                if (tokenvalue == null)
                    tokenvalue = NULL;
                content = content.replace("$$" + entry.getKey() + "$$", tokenvalue);
            }
            if (b64encode)
                content = Base64.encodeBase64String(content.getBytes(charset));

            writer.print(content);
            writer.flush();
            return content.length();
        } catch (IOException ioe) {
            throw new SSORestException("Error sending response content", ioe);
        }
    }

    /**
     * Convenience method to write the default 401 Error HTML content to a response
     *
     * @param response
     *            a {@link javax.servlet.http.HttpServletResponse} object.
     * @param b64encode
     *            true to base64 encode the output stream
     * @throws com.idfconnect.ssorest.common.SSORestException
     *             if any.
     * @since 1.4
     */
    public static final void write401ResponseStream(HttpServletResponse response, boolean b64encode) throws SSORestException {
        // Send the 401 error page
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            HTMLPageGenerator pg = new HTMLPageGenerator();
            String results = pg.parseHTML(ERRORPAGE_XML_401, ERRORPAGE_XSL_401);
            if (b64encode)
                results = Base64.encodeBase64String(results.getBytes(response.getCharacterEncoding()));
            writer.print(results);
            response.setContentLength(results.length());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (IOException ioe) {
            throw new SSORestException("Error sending response content", ioe);
        } catch (TransformerException te) {
            throw new SSORestException("Error generating response content", te);
        } finally {
            if (writer != null)
                writer.flush();
        }
    }
    
    /**
     * Convenience method to write the default 403 Error HTML content to a response
     *
     * @param response a {@link javax.servlet.http.HttpServletResponse} object.
     * @param b64encode
     *            true to base64 encode the output stream
     * @throws com.idfconnect.ssorest.common.SSORestException if any.
     * @since 1.4
     */
    public static final void write403ResponseStream(HttpServletResponse response, boolean b64encode) throws SSORestException {
        // Send the 401 error page
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            HTMLPageGenerator pg = new HTMLPageGenerator();
            String results = pg.parseHTML(ERRORPAGE_XML_403, ERRORPAGE_XSL_403);
            if (b64encode)
                results = Base64.encodeBase64String(results.getBytes(response.getCharacterEncoding()));
            writer.print(results);
            response.setContentLength(results.length());
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch (IOException ioe) {
            throw new SSORestException("Error sending response content", ioe);
        } catch (TransformerException te) {
            throw new SSORestException("Error generating response content", te);
        } finally {
            if (writer != null)
                writer.flush();
        }
    }

    /**
     * Convenience method to write the 500 Error HTML content to a response
     *
     * @param response
     *            a {@link javax.servlet.http.HttpServletResponse} object.
     * @param file
     *            the error file
     * @param message
     *            the error message
     * @param t
     *            the causing Throwable
     * @param b64encode
     *            true to base64 encode the output stream
     * @throws com.idfconnect.ssorest.common.SSORestException
     *             if any.
     * @since 1.4
     */
    public static final void write500ResponseStream(HttpServletResponse response, String file, String message, Throwable t, boolean b64encode) throws SSORestException {
        try {
            // Set the response code
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            Map<String, String> tokens = new HashMap<String, String>();
            tokens.put("message", message);
            if (t != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                t.printStackTrace(pw);
                tokens.put("stackTrace", sw.toString());
            } else {
                tokens.put("stackTrace", "");
            }
            writeResponseStream(file, tokens, response.getWriter(), response.getCharacterEncoding(), b64encode);
        } catch (IOException ioe) {
            throw new SSORestException("Error generating response content", ioe);
        }
    }

    /**
     * Convenience method to write the 400 Error HTML content to a response
     *
     * @param response
     *            a {@link javax.servlet.http.HttpServletResponse} object.
     * @param file
     *            the error file
     * @param message
     *            the error message
     * @param b64encode
     *            true to base64 encode the output stream
     * @throws com.idfconnect.ssorest.common.SSORestException
     *             if any.
     * @since 1.4
     */
    public static final void write400ResponseStream(HttpServletResponse response, String file, String message, boolean b64encode) throws SSORestException {
        try {
            // Set the response code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            Map<String, String> tokens = new HashMap<String, String>();
            tokens.put("message", message);
            writeResponseStream(file, tokens, response.getWriter(), response.getCharacterEncoding(), b64encode);
        } catch (IOException ioe) {
            throw new SSORestException("Error generating response content", ioe);
        }
    }

    /**
     * Convenience method to write the default post preservation page
     *
     * @param tokens
     *            Map<String,String>
     * @param writer
     *            PrintWriter
     * @param charset
     *            a {@link java.lang.String} object.
     * @param b64encode
     *            true to base64 encode the output stream
     * @return the number of characters written
     * @throws com.idfconnect.ssorest.common.SSORestException
     *             if any.
     * @since 1.4
     */
    public static final int writePostPreservationPage(Map<String, String> tokens, PrintWriter writer, String charset, boolean b64encode) throws SSORestException {
        return writeResponseStream(PAGE_POSTPRESERVE, tokens, writer, charset, b64encode);
    }
}
