package net.xisberto.ledassist;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;

import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by xisberto on 18/09/14.
 */
public class Settings {
    public static final String KEY_START = "start", KEY_END = "end", DEFAULT_HOUR = "01:00";
    private static final String HOUR = "_hour", MINUTE = "_minute";

    public static void setTime(Context context, String key, int hour, int minute) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(key + HOUR, hour)
                .putInt(key + MINUTE, minute)
                .apply();
    }

    public static Calendar getTime(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int hourOfDay = preferences.getInt(key + HOUR, 1);
        int minute = preferences.getInt(key + MINUTE, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    public static String getTimeString(Context context, String key) {
        Calendar calendar = getTime(context, key);
        if (DateFormat.is24HourFormat(context)) {
            return DateFormat.format("kk:mm", calendar).toString();
        } else {
            return DateFormat.format("hh:mm aa", calendar).toString();
        }
    }

    public static String getTimeString(Context context, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        if (DateFormat.is24HourFormat(context)) {
            return DateFormat.format("kk:mm", calendar).toString();
        } else {
            return DateFormat.format("hh:mm aa", calendar).toString();
        }
    }

    public static int getHour(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key + HOUR, 1);
    }

    public static int getMinute(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key + MINUTE, 0);
    }
}
