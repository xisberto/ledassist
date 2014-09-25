package net.xisberto.ledassist.control;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.Calendar;

public class Settings {
    public static final String KEY_START = "start", KEY_END = "end", KEY_ACTIVE = "active",
            ACTION_LED_ENABLED = "net.xisberto.ledassist.LED_ENABLED",
            EXTRA_LED_STATUS = "LED_STATUS";
    private static final String HOUR = "_hour", MINUTE = "_minute";

    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public static boolean isActive(Context context) {
        return getSharedPreferences(context)
                .getBoolean(KEY_ACTIVE, false);
    }

    public static void setActive(Context context, boolean active) {
        getSharedPreferences(context).edit()
                .putBoolean(KEY_ACTIVE, active)
                .apply();
        if (active) {
            Scheduler.startSchedule(context);
        } else {
            setLedEnabled(context, true);
            Scheduler.cancelSchedule(context);
            AlarmReceiver.cancelNotification(context);
        }
    }

    public static boolean isLedEnabled(Context context) {
        return (android.provider.Settings.System.getInt(context.getContentResolver(),
                "notification_light_pulse", 1) == 1);
    }

    public static void setLedEnabled(Context context, boolean enabled) {
        Log.d("Settings", String.format("setLedEnabled: %b", enabled));
        android.provider.Settings.System.putInt(context.getContentResolver(),
                "notification_light_pulse", enabled ? 1 : 0);
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(new Intent(ACTION_LED_ENABLED)
                        .putExtra(EXTRA_LED_STATUS, enabled));
    }

    public static void setTime(Context context, String key, int hour, int minute) {
        getSharedPreferences(context).edit()
                .putInt(key + HOUR, hour)
                .putInt(key + MINUTE, minute)
                .apply();
    }

    public static Calendar getTime(Context context, String key) {
        SharedPreferences preferences = getSharedPreferences(context);
        int hourOfDay = preferences.getInt(key + HOUR, 1);
        int minute = preferences.getInt(key + MINUTE, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
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
        return getSharedPreferences(context)
                .getInt(key + HOUR, 1);
    }

    public static int getMinute(Context context, String key) {
        return getSharedPreferences(context)
                .getInt(key + MINUTE, 0);
    }
}
