package com.idfconnect.ssorest.common.utils;

import java.io.UnsupportedEncodingException;

import java.text.StringCharacterIterator;

import com.idfconnect.ssorest.common.SSORestException;

/**
 * <p>SmEncodingUtil class</p>
 *
 * @author rsand
 * @since 1.4
 */
public class SmEncodingUtil {
    /** Constant <code>OLD_SM_URL_ENCODING_STRING="$SM$"</code> */
    public static final String OLD_SM_URL_ENCODING_STRING    = "$SM$";
    
    /** Constant <code>SM_URL_ENCODING_STRING_LENGTH=4</code> */
    public static final int    SM_URL_ENCODING_STRING_LENGTH = 4;
    
    /** Constant <code>OLD_SM_URL_ENCODING_DELIMITER='$'</code> */
    public static final char   OLD_SM_URL_ENCODING_DELIMITER = '$';
    
    /** Constant <code>NEW_SM_URL_ENCODING_STRING="-SM-"</code> */
    public static final String NEW_SM_URL_ENCODING_STRING    = "-SM-";
    
    /** Constant <code>NEW_SM_URL_ENCODING_DELIMITER='-'</code> */
    public static final char   NEW_SM_URL_ENCODING_DELIMITER = '-';
    
    /** Constant <code>DEFAULT_ENCODING="UTF-8"</code> */
    public static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * Method encode.
     *
     * @param inString String
     * @param legacy boolean
     * @return String
     * @since 1.4
     */
    public static String encode(String inString, boolean legacy) {
        if (inString == null || inString.length() == 0) {
            return inString;
        }
        char delimiter = OLD_SM_URL_ENCODING_DELIMITER;
        if (!legacy) {
            // use new delimiter if not legacy
            delimiter = NEW_SM_URL_ENCODING_DELIMITER;
        }

        StringCharacterIterator iterator = new StringCharacterIterator(inString);
        StringBuffer sb = new StringBuffer();
        boolean encoded = false;
        for (char c = iterator.first(); c != StringCharacterIterator.DONE; c = iterator.next()) {
            if (c == ' ') {
                sb.append("%20");
                encoded = true;
            } else if (c == '+') {
                sb.append("%2b");
                encoded = true;
            } else if (c == '&') {
                sb.append("%26");
                encoded = true;
            } else if (c == '?') {
                sb.append("%3f");
                encoded = true;
            } else if (c == '.') {
                sb.append("%2e");
                encoded = true;
            } else if (c == ';') {
                sb.append("%3b");
                encoded = true;
            } else if (c == '/') {
                sb.append("%2f");
                encoded = true;
            } else if (c == ':') {
                sb.append("%3a");
                encoded = true;
            } else if (c == '@') {
                sb.append("%40");
                encoded = true;
            } else if (c == '=') {
                sb.append("%3d");
                encoded = true;
            } else if (c == ',') {
                sb.append("%2c");
                encoded = true;
            } else if (c == delimiter) {
                sb.append(delimiter);
                sb.append(delimiter);
                encoded = true;
            } else if (c == '%') {
                sb.append(delimiter);
                sb.append('%');
                encoded = true;
            } else {
                sb.append(c);
            }
        }

        if (legacy) {
            return (encoded ? OLD_SM_URL_ENCODING_STRING : "") + sb.toString();
        } else {
            return (encoded ? NEW_SM_URL_ENCODING_STRING : "") + sb.toString();
        }
    }

    /**
     * Method decode.
     *
     * @param inString String
     * @return String
     * @throws com.idfconnect.ssorest.common.SSORestException if any.
     * @since 1.4
     */
    public static String decode(String inString) throws SSORestException {
        char decodingDelimiter;
        if (inString.startsWith(OLD_SM_URL_ENCODING_STRING)) {
            decodingDelimiter = OLD_SM_URL_ENCODING_DELIMITER;
        } else if (inString.startsWith(NEW_SM_URL_ENCODING_STRING)) {
            decodingDelimiter = NEW_SM_URL_ENCODING_DELIMITER;
        } else
            return inString;

        StringCharacterIterator iterator = new StringCharacterIterator(inString, SM_URL_ENCODING_STRING_LENGTH);
        StringBuffer sb = new StringBuffer();
        // Current -> because we started the iterator past the SM_URL_ENCODING_STRING
        for (char c = iterator.current(); c != StringCharacterIterator.DONE; c = iterator.next()) {
            if (c == decodingDelimiter) {
                // If the next character is a DELIMITER ->
                // add a delimiter
                c = iterator.next();
                if (c == StringCharacterIterator.DONE) {
                    throw new SSORestException("Invalid SiteMinder Encoded String -> " + inString);
                } else if (c == decodingDelimiter) {
                    // $$ -> $
                    // -- -> -
                    sb.append(decodingDelimiter);
                } else if (c == '%') {
                    sb.append(c);
                } else {
                    // No valid token $*
                    // throw new Exception("Invalid SiteMinder Encoded String ->
                    // "+inString);
                    // should not throw an exception
                    // just append
                    sb.append(c);
                }
            } else if (c == '%') {
                // build a char from the next two tokens
                if (iterator.getIndex() + 2 < iterator.getEndIndex()) {
                    sb.append((char) ((16 * Character.digit((c = iterator.next()), 16)) + (Character.digit((c = iterator.next()), 16))));
                }
            } else {
                sb.append(c);
            }

        }

        // It's valid
        return sb.toString();
    }
    
    /**
     * Fixes a problem with SiteMinder handling of a twice-url-encoded string
     *
     * @param str a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     * @throws java.io.UnsupportedEncodingException if any.
     * @since 1.4
     */
    public static String undoSiteMinderEncoding(String str) throws UnsupportedEncodingException {
        if (!(str.startsWith("-SM-") || (str.startsWith("$SM$"))))
            return str;

        while (str.startsWith("--"))
            str = fixSiteMinderDoubleEncodingChars(str);

        // Now if there is a query string with content after it, we need to re-encode that content
        int begin = 0;
        if (str.startsWith("$SM$") || str.startsWith("-SM-"))
            begin = 4;

        int idx = str.indexOf('?');
        if ((idx > 0) && ((idx + 1) < str.length())) {
            String start = str.substring(begin, idx + 1);
            String end = str.substring(idx + 1);
            str = fixSiteMinderDoubleEncodingChars(start + fixAndReEncodeSiteMinderEncoding(end));
        } else
            str = fixSiteMinderDoubleEncodingChars(str.substring(begin));

        // Undo encoding of dashes
        str = str.replace("%2D", "-");

        return str;
    }

    private static String fixSiteMinderDoubleEncodingChars(String str) {
        // Sanity cases
        if (str == null)
            return null;

        if (str.length() < 2)
            return str;

        // Iterate over the string up to but precluding the last character

        StringBuffer buffer = new StringBuffer();

        int i = 0;
        for (i = 0; i < str.length() - 1; i++) {

            if (str.charAt(i) == '-') {
                // Fix SiteMinder target encoding strangeness by skipping the extra -
                switch (str.charAt(i + 1)) {

                case '+':
                    i++;
                    break;
                case '%':
                    i++;
                    break;
                case '-':
                    i++;
                    break;
                case '.':
                    i++;
                    break;
                case '/':
                    i++;
                    break;
                case ':':
                    i++;
                    break;
                case ';':
                    i++;
                    break;
                default:
                    break;
                }
            }
            buffer.append(str.charAt(i));
        }
        if (i < str.length())
            buffer.append(str.charAt(str.length() - 1));

        return buffer.toString();
    }

    private static String fixAndReEncodeSiteMinderEncoding(String str) {
        str = str.replace("-#", "%23");
        str = str.replace("-%", "%25");
        str = str.replace("-+", "%2B");
        str = str.replace("--", "%2D");
        str = str.replace("-.", "%2E");
        str = str.replace("-/", "%2F");
        str = str.replace("-:", "%3A");
        str = str.replace("-;", "%3B");
        str = str.replace("-=", "%3D");

        return str;
    }
    
    /**
     * <p>toByteString.</p>
     *
     * @param bytes an array of byte.
     * @return a {@link java.lang.String} object.
     * @since 1.4
     */
    public static String toByteString(byte[] bytes){
    	StringBuffer sb = new StringBuffer();
    	for (byte b : bytes) {
			sb.append(b);
			sb.append(",");
		}
    	return sb.toString();
    }
}
