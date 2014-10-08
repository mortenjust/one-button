package com.onebutton.util;

import android.util.Log;

/**
 * Better logging which checks if the log message is loggable before logging it.
 */
public class Logger {

    /**
     * Log a verbose message with checking if it is loggable.
     *
     * @param tag the tag.
     * @param msg the message.
     */
    public static void v(String tag, String msg) {
       // if (Log.isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, msg);
        //}
    }

    /**
     * Log a verbose message with checking if it is loggable.
     * @param tag the tag.
     * @param msg the message.
     * @param t the throwable.
     */
    public static void v(String tag, String msg, Throwable t) {
        if (Log.isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, msg, t);
        }
    }

    /**
     * Log a warn message with checking if it is loggable.
     *
     * @param tag the tag
     * @param msg the message
     */
    public static void w(String tag, String msg) {
        if (Log.isLoggable(tag, Log.WARN)) {
            Log.w(tag, msg);
        }
    }

    /**
     * Log a warn message with checking if it is loggable.
     * @param tag the tag.
     * @param msg the message.
     * @param t the throwable.
     */
    public static void w(String tag, String msg, Throwable t) {
        if (Log.isLoggable(tag, Log.VERBOSE)) {
            Log.w(tag, msg, t);
        }
    }



    /**
     * Log an error message with checking if it is loggable.
     *
     * @param tag the tag
     * @param msg the message
     */
    public static void e(String tag, String msg) {
        if (Log.isLoggable(tag, Log.ERROR)) {
            Log.e(tag, msg);
        }
    }

    /**
     * Log a verbose message with checking if it is loggable.
     * @param tag the tag.
     * @param msg the message.
     * @param t the throwable.
     */
    public static void e(String tag, String msg, Throwable t) {
        if (Log.isLoggable(tag, Log.VERBOSE)) {
            Log.e(tag, msg, t);
        }
    }
}