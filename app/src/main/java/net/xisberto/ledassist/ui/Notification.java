package net.xisberto.ledassist.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import net.xisberto.ledassist.R;
import net.xisberto.ledassist.ui.MainActivity;

/**
 * Created by xisberto on 24/09/14.
 */
public class Notification {
    private static final int NOTIFICATION_ID = 1;

    public static void cancelNotification(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_ID);
    }

    public static void showNotification(Context context) {
        Log.d("AlarmReceiver", "show notification");
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(PendingIntent.getActivity(
                        context, NOTIFICATION_ID,
                        new Intent(context, MainActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT
                ))
                .setTicker(context.getString(R.string.led_disabled))
                .setContentTitle(context.getString(R.string.led_disabled))
                .setSmallIcon(R.drawable.ic_stat_led_off);
        nm.notify(NOTIFICATION_ID, builder.build());
    }
}
