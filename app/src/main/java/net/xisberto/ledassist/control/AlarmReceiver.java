package net.xisberto.ledassist.control;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import net.xisberto.ledassist.R;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String ACTION_DISABLE_LED = "net.xisberto.ledassist.DISABLE_LED",
            ACTION_ENABLE_LED = "net.xisberto.ledassist.ENABLE_LED";
    private static final int NOTIFICATION_ID = 1;

    public AlarmReceiver() {
    }

    private void notifyLedDisabled(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(false)
                .setOngoing(true)
                .setTicker(context.getString(R.string.led_disabled))
                .setContentTitle(context.getString(R.string.led_disabled))
                .setSmallIcon(R.drawable.ic_stat_notification_led);
        nm.notify(NOTIFICATION_ID, builder.build());
    }

    public static void cancelNotification(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_ID);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_DISABLE_LED.equals(action)
                && Settings.isActive(context)) {
            Settings.setLedEnabled(context, false);
            notifyLedDisabled(context);
        } else {
            Settings.setLedEnabled(context, true);
            cancelNotification(context);
        }
    }
}
