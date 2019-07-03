/*
 * StreamHelper.java
 *
 * Copyright 2011 sillar team, Inc. All rights reserved.
 *
 * SILLAR PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xrj.dlt.io;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.FileChannel;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author sillar team
 * @version 1.0.0
 * @since 1.0.0 Handy 2013-10-20
 */
public class StreamUtils {
    private static String TAG = "StreamUtils";
    private static final int BUFFER_LENGTH = 2048;

    public static String getContent(InputStream inStream) {

        return getContent(inStream, UTF_8.toString());
    }

    public static String getContent(InputStream inStream, String charSet) {
        String text = null;

        if (inStream != null) {
            BufferedInputStream binStream = null;
            ByteArrayOutputStream byteOutStream = null;

            try {
                binStream = new BufferedInputStream(inStream);
                byteOutStream = new ByteArrayOutputStream();

                int len = BUFFER_LENGTH;
                byte[] buff = new byte[BUFFER_LENGTH];
                while ((len = binStream.read(buff, 0, BUFFER_LENGTH)) != -1) {
                    byteOutStream.write(buff, 0, len);
                }

                byteOutStream.flush();
                text = byteOutStream.toString(charSet != null ? charSet : UTF_8.toString());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            } finally {
                close(byteOutStream);
                close(binStream);
            }
        }

        return text;
    }

    /**
     * 获取流,获取完关闭流
     */
    public static byte[] getByteArray(InputStream inStream) {
        byte[] byteArray = null;

        if (inStream != null) {
            BufferedInputStream binStream = null;
            ByteArrayOutputStream byteOutStream = null;

            try {
                binStream = new BufferedInputStream(inStream);
                byteOutStream = new ByteArrayOutputStream();

                int len = BUFFER_LENGTH;
                byte[] buff = new byte[BUFFER_LENGTH];
                while ((len = binStream.read(buff, 0, BUFFER_LENGTH)) != -1) {
                    byteOutStream.write(buff, 0, len);
                }

                byteOutStream.flush();
                byteArray = byteOutStream.toByteArray();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            } finally {
                close(byteOutStream);
                close(binStream);
            }
        }

        return byteArray;
    }

    public static void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (Exception e) {
                Log.v(TAG, e.getMessage(), e);
            }
        }
    }

    public static void close(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (Exception e) {
                Log.v(TAG, e.getMessage(), e);
            }
        }
    }

    public static void close(Reader r) {
        if (r != null) {
            try {
                r.close();
            } catch (Exception e) {
                Log.v(TAG, e.getMessage(), e);
            }
        }
    }

    public static void close(Writer w) {
        if (w != null) {
            try {
                w.close();
            } catch (Exception e) {
                Log.v(TAG, e.getMessage(), e);
            }
        }
    }

    public static void close(FileChannel chn) {
        if (chn != null) {
            try {
                chn.close();
            } catch (Exception e) {
                Log.v(TAG, e.getMessage(), e);
            }
        }
    }
}
