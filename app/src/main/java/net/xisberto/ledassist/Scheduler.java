package net.xisberto.ledassist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Set;

/**
 * Created by xisberto on 20/09/14.
 */
public class Scheduler {
    private static final int REQUEST_START = 1,
            REQUEST_END = 2;

    public static void startSchedule(Context context) {
        Calendar startTime = Settings.getTime(context, Settings.KEY_START);
        Calendar endTime = Settings.getTime(context, Settings.KEY_END);

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
