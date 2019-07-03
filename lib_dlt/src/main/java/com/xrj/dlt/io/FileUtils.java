/*
 * FileHelper.java
 *
 * Copyright 2011 handyworkgroup, Inc. All rights reserved.
 * handyworkgroup PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.xrj.dlt.io;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import okio.Buffer;
import okio.ByteString;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.xrj.dlt.io.StreamUtils.close;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 文件工具
 *
 * @author handyworkgroup
 * @version 1.0.0
 * @since 1.0.0 Handy 2011-7-17
 */
public final class FileUtils {
    private static final String TAG = "FileUtils";

    private static final int BUFFER_LENGTH = 2048;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};

    public static boolean exists(File file) {

        return file != null && file.exists();
    }

    public static boolean exists(String filePath) {

        return exists(new File(filePath));
    }

    public static String checkFileName(String fileName) {
        String tmpName = fileName;

        if (!TextUtils.isEmpty(fileName)) {
            tmpName = tmpName.replaceAll("[\\\\/:\\*?\"<>|]", "");

        }

        return tmpName;
    }

    public static boolean copy(File src, File des) {
        boolean result = false;
        if (src != null & src.exists() & des != null) {
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;

            try {
                bis = new BufferedInputStream(new FileInputStream(src));
                bos = new BufferedOutputStream(new FileOutputStream(des));

                int length = -1;
                byte[] buffer = new byte[BUFFER_LENGTH];

                while ((length = bis.read(buffer, 0, BUFFER_LENGTH)) != -1) {
                    bos.write(buffer, 0, length);
                }

                bos.flush();
                result = true;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            } finally {
                try {
                    if (bos != null) {
                        bos.close();
                    }
                    if (bis != null) {
                        bis.close();
                    }
                } catch (Exception e) {
                    Log.v(TAG, e.getMessage(), e);
                }
            }
        }

        return result;
    }

    public static boolean write(File file, String text, Charset charset) {
        boolean result = false;

        if (file != null) {
            if (!TextUtils.isEmpty(text)) {
                BufferedOutputStream boutStream = null;

                try {
                    if (!file.exists()) {
                        file.getParentFile().mkdirs();

                        file.createNewFile();
                    }

                    ByteString byteString = ByteString.encodeString(text, charset != null ? charset : UTF_8);

                    boutStream = new BufferedOutputStream(new FileOutputStream(file));
                    byteString.write(boutStream);
                    boutStream.flush();

//                    file.setLastModified(System.currentTimeMillis());
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage(), e);
                } finally {
                    close(boutStream);
                }
            } else if (file.exists()) {
                file.delete();
            }
        }

        return result;
    }

    public static boolean write(InputStream in, String filePath) {

        return write(in, filePath, -1, null);
    }

    public static boolean write(InputStream in, final String filePath, final long contentLength,
                                final OnProgressChangeListener listener) {
        boolean result = false;

        if (in != null && !TextUtils.isEmpty(filePath)) {
            BufferedInputStream bin = null;
            BufferedOutputStream out = null;

            try {
                File outFile = new File(filePath);
                outFile.getParentFile().mkdirs();

                bin = new BufferedInputStream(in);
                out = new BufferedOutputStream(new FileOutputStream(outFile));

                long wroteLength = 0;
                int tempLen = -1;
                byte[] buffer = new byte[BUFFER_LENGTH];
                while ((tempLen = bin.read(buffer, 0, BUFFER_LENGTH)) != -1) {
                    out.write(buffer, 0, tempLen);

                    if (contentLength > 0 && listener != null) {
                        wroteLength += tempLen;

                        final long tempLength = wroteLength;
//                        LoopUtils.post(new Runnable() { // FIXME LOOP
//
//                            @Override
//                            public void run() {
//                                if (listener != null) {
//                                    listener.onProgressUpdate(filePath, contentLength, tempLength);
//                                }
//                            }
//                        });
                    }
                }

                out.flush();

                result = true;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            } finally {
                close(out);
                close(bin);
            }
        }

        return result;
    }

    public static String read(String absFilePath, Charset charset) {
        String entity = null;
        if (!TextUtils.isEmpty(absFilePath)) {
            entity = read(new File(absFilePath), charset);
        }

        return entity;
    }

    public static String read(File absFile, Charset charset) {
        String data = null;

        if (absFile != null && absFile.exists()) {
            BufferedInputStream br = null;

            try {
                br = new BufferedInputStream(new FileInputStream(absFile));
                Buffer tmpBuilder = new Buffer();
                tmpBuilder.readFrom(br);

                data = tmpBuilder.readString(charset != null ? charset : UTF_8);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            } finally {
                close(br);
            }
        }

        return data;
    }

    public static boolean delete(File file) {

        return delete(file, null);
    }

    public static boolean delete(File file, FileFilter filter) {
        boolean result = true;

        if (exists(file)) {
            if (file.isDirectory()) {
                File[] files = filter != null ? file.listFiles(filter) : file.listFiles();
                if (files != null && files.length > 0) {
                    for (File subFile : files) {
                        result &= delete(subFile, filter);
                    }
                }
            } else {
                try {
                    result &= file.delete();
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage(), e);
                }
            }
        }

        return result;
    }
}
