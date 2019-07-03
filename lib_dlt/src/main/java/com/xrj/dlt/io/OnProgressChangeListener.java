package com.xrj.dlt.io;

/**
 * Created by handy on 16-12-19.
 */

public interface OnProgressChangeListener {

    void onProgressUpdate(String filePath, long contentLength, long wroteLength);
}
