package com.style.common.utils;


import org.apache.commons.codec.Charsets;

import java.io.*;
import java.nio.charset.Charset;

public class IOUtil {

    public static final int EOF = -1;

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static String toString(final InputStream input, final Charset encoding) throws IOException {
        final StringBuilderWriter sw = new StringBuilderWriter();
        copy(input, sw, encoding);
        return sw.toString();
    }

    public static void copy(final InputStream input, final Writer output, final Charset inputEncoding)
            throws IOException {
        final InputStreamReader in = new InputStreamReader(input, Charsets.toCharset(inputEncoding));
        copy(in, output);
    }

    public static int copy(final Reader input, final Writer output) throws IOException {
        final long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    public static long copyLarge(final Reader input, final Writer output) throws IOException {
        return copyLarge(input, output, new char[DEFAULT_BUFFER_SIZE]);
    }

    public static long copyLarge(final Reader input, final Writer output, final char[] buffer) throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }


}
