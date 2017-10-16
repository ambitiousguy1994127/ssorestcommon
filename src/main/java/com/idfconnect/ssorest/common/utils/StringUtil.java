/*
 * Copyright 2013 IDF Connect, Inc.
 * All Rights Reserved.
 */
package com.idfconnect.ssorest.common.utils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.codec.binary.Base64;

/**
 * Utility class that contains some simple generic utilities for String objects.
 *
 * @author rsand
 * @since 1.4
 */
public final class StringUtil {

    private StringUtil() {
        super();
    }

    /**
     * Returns the specified index of the supplied string array. If the index is outside of the length of the
     * array, then <code>null</code> is returned.
     *
     * @param sa
     *            the string array to use
     * @param index
     *            the index of the string in the array to return
     * @return the specified index of the supplied string array
     * @since 1.4
     */
    public static String lookup(String[] sa, int index) {
        return lookup(sa, index, null);
    }

    /**
     * Returns the specified index of the supplied string array. If the index is outside of the length of the
     * array, then <code>def</code> is returned.
     *
     * @param sa
     *            the string array to use
     * @param index
     *            the index of the string in the array to return
     * @param def
     *            the default string to return if the index is invalid
     * @return the specified index of the supplied string array
     * @since 1.4
     */
    public static String lookup(String[] sa, int index, String def) {
        if (index >= 0 && index <= sa.length)
            return sa[index];
        else
            return def;
    }

    /**
     * Converts the supplied string into an integer and returns it. If the string can not be converted into an
     * integer, returns the value in <code>def</code>.
     *
     * @param str
     *            the string to convert to an integer
     * @param def
     *            the default value to use if the string can not be converted to an integer
     * @return an integer conversion of the supplied string
     * @since 1.4
     */
    public static int parseInt(String str, int def) {
        if (str == null)
            return def;
        try {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * Converts the supplied string into a long and returns it. If the string can not be converted into a
     * long, returns the value in <code>def</code>.
     *
     * @param str
     *            the string to convert to a long
     * @param def
     *            the default value to use if the string can not be converted to a long
     * @return a long conversion of the supplied string
     * @since 1.4
     */
    public static long parseLong(String str, long def) {
        if (str == null)
            return def;
        try {
            long i = Long.parseLong(str);
            return i;
        }
        catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * Converts the supplied string into a float and returns it. If the string can not be converted into a
     * float, returns the value in <code>def</code>.
     *
     * @param str
     *            the string to convert to a float
     * @param def
     *            the default value to use if the string can not be converted to a float
     * @return a float conversion of the supplied string
     * @since 1.4
     */
    public static float parseFloat(String str, float def) {
        if (str == null)
            return def;
        try {
            float f = Float.parseFloat(str);
            return f;
        }
        catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * Returns true if the value of the specified property is "true", false if the value is "false", or def
     * otherwise. All comparisons are case-insensitive.
     *
     * @param str
     *            the string to parse
     * @param def
     *            the default value to use if no recognized value is found in the string
     * @return true if the string is "true", false if it is "false"
     * @since 1.4
     */
    public static boolean parseTrueFalse(String str, boolean def) {
        if (str == null)
            return def;
        if (str.toLowerCase().equals("true"))
            return true;
        else if (str.toLowerCase().equals("false"))
            return false;
        else
            return def;
    }

    /**
     * Returns true if the value of the specified property is "yes", false if the value is "no", or def
     * otherwise. All comparisons are case-insensitive.
     *
     * @param str
     *            the string to parse
     * @param def
     *            the default value to use if no recognized value is found in the string
     * @return true if the string represents yes, false if it represents no
     * @since 1.4
     */
    public static boolean parseYesNo(String str, boolean def) {
        if (str == null)
            return def;
        if (str.toLowerCase().equals("yes"))
            return true;
        else if (str.toLowerCase().equals("no"))
            return false;
        else
            return def;
    }

    /**
     * Finds the first occurrence of s1 in source, replaces it with s2, and returns the result. Null values
     * are fully tolerated and handled appropriately.
     *
     * @param source
     *            the string to modify
     * @param s1
     *            the substring to replace
     * @param s2
     *            the replacement string to use
     * @return the modified string
     * @since 1.4
     */
    public static String replace(String source, String s1, String s2) {
        // Handle various null possibilities:
        if (source == null) {
            if (s1 == null)
                return s2;
            else
                return null;
        }
        if (s1 == null)
            return source;
        if (s2 == null)
            s2 = "";

        // Find s1 in source and replace it with s2:
        int loc = source.indexOf(s1);
        if (loc == -1)
            return source;
        String temp = source.substring(0, loc) + s2 + source.substring(loc + s1.length());
        return temp;
    }

    /**
     * Returns the last <code>len</code> characters of the provided string, or fewer is the string is not
     * sufficiently long.
     *
     * @param s
     *            the string to extract the tail of
     * @param len
     *            the number of characters to return
     * @return the last <code>len</code> characters of the provided string
     * @since 1.4
     */
    public static String tail(String s, int len) {
        if (s.length() <= len)
            return s;
        else if (len < 1)
            return "";
        else
            return s.substring(s.length() - len, s.length());
    }

    /**
     * Converts a delimited string of base 64 encoded strings into an array list of decoded strings.
     *
     * @param des
     *            a delimited string of base 64 encoded strings
     * @param delimiter
     *            a string delimiter that does not contain characters that appear in base 64 encoding
     *            (alphanumeric and + \ or =)
     * @param encoded
     *            true if strings are base 64 encoded false if not
     * @return an array list of decoded strings
     * @since 1.4
     */
    public static ArrayList<String> parseDelimitedEncodedString(String des, String delimiter, boolean encoded) {
        return parseDelimitedEncodedString(des, delimiter, Charset.forName("UTF-8"), encoded);
    }
    
    /**
     * Converts a delimited string of base 64 encoded strings into an array list of decoded strings.
     *
     * @param des
     *            a delimited string of base 64 encoded strings
     * @param delimiter
     *            a string delimiter that does not contain characters that appear in base 64 encoding
     *            (alphanumeric and + \ or =)
     * @param charset a {@link java.nio.charset.Charset} object.
     * @param encoded
     *            true if strings are base 64 encoded false if not
     * @return an array list of decoded strings
     * @since 1.4
     */
    public static ArrayList<String> parseDelimitedEncodedString(String des, String delimiter, Charset charset, boolean encoded) {
        ArrayList<String> al = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(des, delimiter);
        while (st.hasMoreTokens()) {
            if (encoded)
                al.add(new String(Base64.decodeBase64(st.nextToken()), charset));
            else
                al.add(st.nextToken());
        }
        return al;
    }

    /**
     * Converts a list of strings into a single delimited string. Each string in the list is base 64 encoded
     * before being added to the delimited string to ensure the delimiter does not occur in the strings in the
     * list. If the encode flag is false, the strings are added without being encoded first.
     * Uses UTF-8.
     *
     * @param strings
     *            a list of strings
     * @param delimiter
     *            any string delimiter that does not contain characters that appear in base 64 encoding
     *            (alphanumeric and + \ or =)
     * @param encode
     *            true if strings should be base 64 encoded false if not
     * @return a single delimited string of base 64 encoded strings
     * @since 1.4
     */
    public static String createDelimitedEncodedString(List<String> strings, String delimiter, boolean encode) {
        return createDelimitedEncodedString(strings, delimiter, Charset.forName("UTF-8"), encode);
    }
    
    /**
     * Converts a list of strings into a single delimited string. Each string in the list is base 64 encoded
     * before being added to the delimited string to ensure the delimiter does not occur in the strings in the
     * list. If the encode flag is false, the strings are added without being encoded first.
     *
     * @param strings
     *            a list of strings
     * @param delimiter
     *            any string delimiter that does not contain characters that appear in base 64 encoding
     *            (alphanumeric and + \ or =)
     * @param encode
     *            true if strings should be base 64 encoded false if not
     * @param charset a {@link java.nio.charset.Charset} object.
     * @return a single delimited string of base 64 encoded strings
     * @since 1.4
     */
    public static String createDelimitedEncodedString(List<String> strings, String delimiter, Charset charset, boolean encode) {
        StringBuffer sb = new StringBuffer();
        Iterator<String> iter = strings.iterator();
        while (iter.hasNext()) {
            String str = iter.next();

            if (encode)
                sb.append(Base64.encodeBase64String(str.getBytes(charset)));
            else
                sb.append(str);

            if (iter.hasNext())
                sb.append(delimiter);
        }
        return sb.toString();
    }

    /**
     * Takes a delimited string and returns an ArrayList of its elements
     *
     * @param str
     *            the String to parse
     * @param regEx
     *            the String regular expression to use as a delimiter for parsing
     * @param includeEmpty
     *            boolean to indicate whether empty elements should be included
     * @param trim
     *            trus if leading and trailing whitespace should be removed from elements
     * @return the new ArrayList. This will always return an initialized list, although it can be 0 length
     * @since 1.4
     */
    public static ArrayList<String> parseDelimitedString(String str, String regEx, boolean includeEmpty, boolean trim) {
        ArrayList<String> list = new ArrayList<String>();
        if (str != null) {
            String[] sa = str.split(regEx);
            for (int i = 0; i < sa.length; i++) {
                if (trim)
                    sa[i] = sa[i].trim();
                if (includeEmpty || sa[i].length() > 0) {
                    list.add(sa[i]);
                }
            }
        }
        return list;
    }

    /**
     * Converts a List of non-null objects into an array of String objects. The toString() method is used to
     * convert objects to strings.
     *
     * @param list
     *            a list of non-null objects to convert into an array of String objects
     * @return an array of String objects
     * @since 1.4
     */
    public static String[] toStringArray(List<?> list) {
        String[] sa = new String[list.size()];
        for (int i = 0; i < list.size(); i++)
            sa[i] = list.get(i).toString();
        return sa;
    }

    /**
     * Returns true if the provided String is non-null and greater than 0 characters long.
     *
     * @param s
     *            the string to check for characters
     * @return true if the provided String is non-null and greater than 0 characters long
     * @since 1.4
     */
    public static boolean isBlank(String s) {
        return (s == null || s.length() == 0);
    }

    /**
     * Returns true if the provided Strings are equal, with support for null values. Two nulls are considered
     * equal, a null is considered not equal to a non-null, and two non-nulls are compared using the equals()
     * method of the first provided String.
     *
     * @param a
     *            the first String to compare
     * @param b
     *            the second String to compare
     * @return true if the provided Strings are equal
     * @since 1.4
     */
    public static boolean equal(String a, String b) {
        if (a == b)
            return true;
        else if (a == null || b == null)
            return false;
        else
            return a.equals(b);
    }

    /**
     * Used to remove leading and trailing double -quotes
     *
     * @param str a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     * @since 1.4
     */
    public static String removeDoubleQuotes(String str ) {
        if (str == null)
            return null ;
        if (str .length() > 1 && str.startsWith( "\"") && str.endsWith("\"" ))
            str = str .substring(1, str.length() - 1);
        return str ;
    }
}
