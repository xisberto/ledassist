package net.xisberto.ledassist.control;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;

import net.xisberto.ledassist.ui.Notification;

import java.util.Calendar;

public class Scheduler {
    private static final int REQUEST_START = 1,
            REQUEST_END = 2;

    private static void logTime(Calendar calendar, String key) {
        Log.d("Scheduler", String.format("_\ntime for %s\n %d %s", key,
                calendar.getTimeInMillis(),
                DateFormat.format("yyyy-MM-dd HH:mm", calendar)));
    }

    /**
     * Set the alarm to start the disabled period
     * @param context
     */
    public static void scheduleStart(Context context) {
        Calendar startTime = Settings.getTime(context, Settings.KEY_START);
        logTime(startTime, Settings.KEY_START);
        setAlarm(context, startTime, REQUEST_START);
    }

    /**
     * Set the alarm to end the disabled period
     * @param context
     */
    public static void scheduleEnd(Context context) {
        Calendar endTime = Settings.getTime(context, Settings.KEY_END);
        logTime(endTime, Settings.KEY_END);
        setAlarm(context, endTime, REQUEST_END);
    }

    private static void setAlarm(Context context, Calendar time, int request) {
        if (Calendar.getInstance().after(time)) {
            //if the time is past, set the alarm to tomorrow
            time.add(Calendar.DAY_OF_MONTH, 1);
        }

        PendingIntent alarm = getPendingIntent(context, request);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
            am.setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), alarm);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), alarm);
        }
    }

    public static void cancelSchedule(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(getPendingIntent(context, REQUEST_START));
        am.cancel(getPendingIntent(context, REQUEST_END));
    }

    private static PendingIntent getPendingIntent(Context context, int request) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        if (request == REQUEST_START) {
            intent.setAction(AlarmReceiver.ACTION_DISABLE_LED);
        } else {
            intent.setAction(AlarmReceiver.ACTION_ENABLE_LED);
        }
        return PendingIntent.getBroadcast(context, request, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
