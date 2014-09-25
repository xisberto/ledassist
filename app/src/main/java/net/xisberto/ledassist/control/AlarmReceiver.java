package net.xisberto.ledassist.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.xisberto.ledassist.ui.Notification;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String ACTION_DISABLE_LED = "net.xisberto.ledassist.DISABLE_LED",
            ACTION_ENABLE_LED = "net.xisberto.ledassist.ENABLE_LED";

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_DISABLE_LED.equals(action)) {
            Settings.setLedEnabled(context, false);
            //Set the alarm to end the disabled period
            Scheduler.scheduleEnd(context);
        } else if (ACTION_ENABLE_LED.equals(action)) {
            Settings.setLedEnabled(context, true);
            //Set the alarm to start the next disabled period
            Scheduler.scheduleStart(context);
        }
    }
}
