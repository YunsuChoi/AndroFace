/**
 * @version $Id: StringUtils.java 168 2012-08-09 09:14:39Z mroland $
 *
 * @author Michael Roland <mi.roland@gmail.com>
 *
 * Copyright (c) 2009-2012 Michael Roland
 *
 * ALL RIGHTS RESERVED.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name Michael Roland nor the names of any contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL MICHAEL ROLAND BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.tigris.adk.androface.nfc.utils;

import java.nio.charset.Charset;

/**
 * Utilities for conversions between byte-arrays and strings.
 */
public class StringUtils {

    private StringUtils() {
    }

    /**
     * Convert a byte array into its hexadecimal string representation.
     * @param b  Byte array.
     * @return   Hexadecimal string representation.
     */
    public static String convertByteArrayToHexString(byte[] b) {
        if (b != null) {
            StringBuilder s = new StringBuilder(2 * b.length);

            for (int i = 0; i < b.length; ++i) {
                final String t = Integer.toHexString(b[i]);
                final int l = t.length();
                if (l > 2) {
                    s.append(t.substring(l - 2));
                } else {
                    if (l == 1) {
                        s.append("0");
                    }
                    s.append(t);
                }
            }

            return s.toString();
        } else {
            return "";
        }
    }

    /**
     * Reverse a byte array and convert it into its hexadecimal string representation.
     * @param b  Byte array.
     * @return   Reverse hexadecimal string representation.
     */
    public static String convertByteArrayToReverseHexString(byte[] b) {
        if (b != null) {
            StringBuilder s = new StringBuilder(2 * b.length);

            for (int i = (b.length - 1); i >= 0; --i) {
                final String t = Integer.toHexString(b[i]);
                final int l = t.length();
                if (l > 2) {
                    s.append(t.substring(l - 2));
                } else {
                    if (l == 1) {
                        s.append("0");
                    }
                    s.append(t);
                }
            }

            return s.toString();
        } else {
            return "";
        }
    }

    /**
     * Convert a byte array into a character string using US-ASCII encoding.
     * @param b  Byte array.
     * @return   US-ASCII string representation.
     */
    public static String convertByteArrayToASCIIString(byte[] b) {
        String s = "";

        try {
            s = new String(b, Charset.forName("US-ASCII"));
        } catch (Exception e) {
        }

        return s;
    }

    /**
     * Convert a character string into a byte array using ASCII encoding.
     * @param s  Character string.
     * @return   ASCII byte array representation.
     */
    public static byte[] convertASCIIStringToByteArray(String s) {
        byte[] b = new byte[0];

        try {
            b = s.getBytes(Charset.forName("US-ASCII"));
        } catch (Exception e) {
        }

        return b;
    }

    /**
     * Convert a byte array into a character string using UTF-8 encoding.
     * @param b  Byte array.
     * @return   UTF-8 string representation.
     */
    public static String convertByteArrayToUTF8String(byte[] b) {
        String s = "";

        try {
            s = new String(b, Charset.forName("UTF-8"));
        } catch (Exception e) {
        }

        return s;
    }

     /**
     * Convert a character string into a byte array using UTF-8 encoding.
     * @param s  Character string.
     * @return   UTF-8 byte array representation.
     */
   public static byte[] convertUTF8StringToByteArray(String s) {
        byte[] b = new byte[0];

        try {
            b = s.getBytes(Charset.forName("UTF-8"));
        } catch (Exception e) {
        }

        return b;
    }

    /**
     * Convert a byte array into a character string using UTF-16 encoding.
     * @param b  Byte array.
     * @return   UTF-16 string representation.
     */
    public static String convertByteArrayToUTF16String(byte[] b) {
        String s = "";

        try {
            s = new String(b, Charset.forName("UTF-16"));
        } catch (Exception e) {
        }

        return s;
    }

    /**
     * Convert a character string into a byte array using UTF-16 encoding.
     * @param s  Character string.
     * @return   UTF-16 byte array representation.
     */
    public static byte[] convertUTF16StringToByteArray(String s) {
        byte[] b = new byte[0];

        try {
            b = s.getBytes(Charset.forName("UTF-16"));
        } catch (Exception e) {
        }

        return b;
    }

    /**
     * Convert a string of hexadecimal encoded octets into its byte array representation.
     * @param s  String of hexadecimal encoded octets.
     * @return   Byte array representation.
     */
    public static byte[] convertHexStringToByteArray(String s) {
        final int len = s.length();
        final int rem = len % 2;

        byte[] ret = new byte[len / 2 + rem];

        if (rem != 0) {
            try {
                ret[0] = (byte) (Integer.parseInt(s.substring(0, 1), 16) & 0x00F);
            } catch (Exception e) {
                ret[0] = 0;
            }
        }

        for (int i = rem; i < len; i += 2) {
            try {
                ret[i / 2 + rem] = (byte) (Integer.parseInt(s.substring(i, i + 2), 16) & 0x0FF);
            } catch (Exception e) {
                ret[i / 2 + rem] = 0;
            }
        }

        return ret;
    }
}
