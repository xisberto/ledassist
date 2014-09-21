package net.xisberto.ledassist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.Calendar;
import java.util.Set;

/**
 * Created by xisberto on 20/09/14.
 */
public class Scheduler {
    private static final int REQUEST_START = 1,
            REQUEST_END = 2;

    private static void logTime(Calendar calendar, String key) {
        Log.d("Settings", String.format("_\ntime for %s\n %d %s", key,
                calendar.getTimeInMillis(),
                DateFormat.format("yyyy-MM-dd HH:mm", calendar)));
    }

    public static void startSchedule(Context context) {
        Calendar startTime = Settings.getTime(context, Settings.KEY_START);
        logTime(startTime, Settings.KEY_START);
        Calendar endTime = Settings.getTime(context, Settings.KEY_END);
        logTime(endTime, Settings.KEY_END);

        PendingIntent alarmStart = getPendingIntent(context, REQUEST_START);
        PendingIntent alarmEnd = getPendingIntent(context, REQUEST_END);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmStart);
        am.setRepeating(AlarmManager.RTC_WAKEUP, endTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmEnd);
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
        PendingIntent result = PendingIntent.getBroadcast(context, request, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return result;
    }
}
