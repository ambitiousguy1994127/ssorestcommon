package com.idfconnect.ssorest.common.utils;

/**
 * StringBuilder utility methods, adapted from JAX-RS internal classes
 *
 * @author Richard Sand
 * @since 1.4
 */
public class StringBuilderUtil {
    /** Array of chars representing white spaces */
    private static final char[]    WHITE_SPACE    = { '\t', '\r', '\n', ' ' };

    /** Array of chars representing separators */
    private static final boolean[] IS_WHITE_SPACE = createWhiteSpaceTable();

    /**
     * Method createWhiteSpaceTable.
     * 
     * @return boolean[]
     */
    private static boolean[] createWhiteSpaceTable() {
        boolean[] table = new boolean[128];

        for (char c : WHITE_SPACE) {
            table[c] = true;
        }

        return table;
    }

    /**
     * Returns {@code true} if the provided char is a white space.
     *
     * @param c
     *            char to check.
     * @return a boolean.
     * @since 1.4
     */
    public static boolean isWhiteSpace(final char c) {
        return (c < 128 && IS_WHITE_SPACE[c]);
    }

    /**
     * Returns {@code true} if string s contains a white space char.
     *
     * @param s
     *            string to check for white spaces.
     * @return a boolean.
     * @since 1.4
     */
    public static boolean containsWhiteSpace(final String s) {
        for (char c : s.toCharArray()) {
            if (isWhiteSpace(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Append a new value to the string builder.
     *
     * If the value contains white-space characters, the appended value is quoted and all the quotes in the value are escaped.
     *
     * @param b
     *            string builder to be updated.
     * @param value
     *            value to be appended.
     * @since 1.4
     */
    public static void appendQuotedIfWhitespace(StringBuilder b, String value) {
        if (value == null) {
            return;
        }
        boolean quote = containsWhiteSpace(value);
        if (quote) {
            b.append('"');
        }
        appendEscapingQuotes(b, value);
        if (quote) {
            b.append('"');
        }
    }

    /**
     * Append a new quoted value to the string builder.
     *
     * The appended value is quoted and all the quotes in the value are escaped.
     *
     * @param b
     *            string builder to be updated.
     * @param value
     *            value to be appended.
     * @since 1.4
     */
    public static void appendQuoted(StringBuilder b, String value) {
        b.append('"');
        appendEscapingQuotes(b, value);
        b.append('"');
    }

    /**
     * Append a new value to the string builder.
     *
     * All the quotes in the value are escaped before appending.
     *
     * @param b
     *            string builder to be updated.
     * @param value
     *            value to be appended.
     * @since 1.4
     */
    public static void appendEscapingQuotes(StringBuilder b, String value) {
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '"') {
                b.append('\\');
            }
            b.append(c);
        }
    }

    private StringBuilderUtil() {
    }
}
