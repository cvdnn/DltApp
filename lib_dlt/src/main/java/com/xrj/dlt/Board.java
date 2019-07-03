package com.xrj.dlt;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.MainThread;

import com.xrj.dlt.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;

import static com.xrj.dlt.Board.Levers.OFF;
import static com.xrj.dlt.Board.Levers.ON;
import static com.xrj.dlt.io.StreamUtils.close;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Board {
    private static final String TAG = "Board";

    public static final int DELAY_MILLIS = 300;

    public static Board Impl = new Board();

    public static final Handler Loople = new Handler(Looper.getMainLooper());

    public enum Units {
        /** 补光灯 */
        LED("/dev/led_en", false),
        /** 5V电源 */
        VCC5V("/dev/vcc5v_out", false),
        /** 12V电源 */
        VCC12V("/dev/vcc12v_out", false),
        /** 继电器 */
        RELAY("/dev/relay", false),

        /**
         * GPIO
         */
        GPIO8_A7("/dev/gpio8_a7", false),
        GPIO8_B1("/dev/gpio8_b1", false),
        GPIO8_B0("/dev/gpio8_b0", true),
        GPIO8_A6("/dev/gpio8_a6", true),

        PIR_INT("/dev/pir_int", false),
        CDS_INT("/dev/cds_int", false);

        /** 单元模块路径 */
        public final String path;
        /** 是否可读，用于标记io口输入输出模式，2种模式不可混用 */
        public final boolean readable;

        Units(String p, boolean able) {
            path = p;
            readable = able;
        }

        public File path() {
            return new File(path);
        }
    }

    public enum Levers {
        ON("1"), OFF("0");

        public final String value;

        Levers(String l) {
            value = l;
        }

        public boolean valueOf() {
            return ON.value.equals(value);
        }

        public Levers reverse() {
            return ON.value.equals(value) ? OFF : ON;
        }

        public static Levers from(String l) {
            return ON.value.equals(l) ? ON : OFF;
        }
    }

    public interface OnHandlePulseListener {

        @MainThread
        void onPulsed(Units u, Levers l);
    }

    /**
     * 设置IO口状态，
     * FIXME 当Units.readable=true，表明该IO处于输入模式时，如果执行了状态输出，将会改变IO工作模式，从而改变IO输入状态。
     *
     * @param u
     * @param l
     */
    public void setStatus(Units u, Levers l) {
        write(u, l.value);
    }

    /**
     * Units.readable=true时，获取IO口状态
     * FIXME 当IO处于输入模式时，如果执行了状态输出，将会改变IO工作模式，从而改变IO输入状态。
     *
     * @param u
     * @return
     */
    public Levers getStatus(Units u) {

        return Levers.from(read(u));
    }

    public void pulse(Units u, Levers l) {
        pulse(u, l, DELAY_MILLIS, null);
    }

    public void pulse(Units u, Levers l, long delay) {
        pulse(u, l, delay, null);
    }

    public void pulse(final Units u, Levers l, long delay, OnHandlePulseListener listener) {
        setStatus(u, l);
        Loople.postDelayed(() -> {
            Levers lvs = l.reverse();
            setStatus(u, lvs);

            if (listener != null) {
                listener.onPulsed(u, l);
            }
        }, delay);
    }

    public native String read(String path);

    public native void write(String path, String text);


    public String read(Units u) {
        String text = OFF.value;

        InputStream in = null;
        try {
            byte[] buffer = new byte[4];

            in = new FileInputStream(u.path());
            int len = in.read(buffer);
            if (len == 1) {
                text = buffer[0] == 0x01 || buffer[0] == '1' ? ON.value : OFF.value;

            } else if (len > 0) {
                text = new String(buffer, 0, len);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);

        } finally {
            close(in);
        }

        return text;
    }

    public void write(Units u, String text) {
        FileWriter fw = null;

        try {
            fw = new FileWriter(u.path());
            fw.write(text);
            fw.flush();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);

        } finally {
            close(fw);
        }
    }

    private Board() {
    }
}
