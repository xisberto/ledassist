package net.xisberto.ledassist;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by xisberto on 18/09/14.
 */
public class Settings {
    public static final String KEY_START = "start", KEY_END = "end", KEY_ACTIVE = "active",
            DEFAULT_HOUR = "01:00";
    private static final String HOUR = "_hour", MINUTE = "_minute";

    public static boolean isActive(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_ACTIVE, false);
    }

    public static void setActive(Context context, boolean active) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(KEY_ACTIVE, active)
                .apply();
        if (active) {
            Scheduler.startSchedule(context);
        } else {
            setLedEnabled(context, false);
            Scheduler.cancelSchedule(context);
        }
    }

    public static boolean isLedEnabled(Context context) {
        return (android.provider.Settings.System.getInt(context.getContentResolver(),
                "notification_light_pulse", 1) == 1);
    }

    public static void setLedEnabled(Context context, boolean enabled) {
        Log.d("Settings", String.format("setLedEnabled: %b", enabled));
        android.provider.Settings.System.putInt(context.getContentResolver(),
                "notification_light_pulse", enabled ? 1: 0);
    }

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
        calendar.set(Calendar.MILLISECOND, 0);
        Log.d("Settings", String.format("_\ntime for %s\n %d %s", key,
                calendar.getTimeInMillis(),
                DateFormat.format("yyyy-MM-dd HH:mm", calendar)));
        return calendar;
    }

    public static String getTimeString(Context context, String key) {
        Calendar calendar = getTime(context, key);
        if (DateFormat.is24HourFormat(context)) {
            return DateFormat.format("HH:mm", calendar).toString();
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
