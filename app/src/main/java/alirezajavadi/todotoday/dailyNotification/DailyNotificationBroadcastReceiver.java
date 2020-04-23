package alirezajavadi.todotoday.dailyNotification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;


public class DailyNotificationBroadcastReceiver extends BroadcastReceiver {
    private static final int ALARM_REQUEST_CODE = 12321;

    @Override
    public void onReceive(Context context, Intent i) {
        if (i.getAction() == null) {
            AlarmManagerService.enqueueWork(context);
        } else {
            //this block will run after booting device
            startAlarm(context);
        }
    }

    public static void startAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 57);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, DailyNotificationBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, 0);
        mgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public static void stopAlarm(Context context) {
        Intent intent = new Intent(context, DailyNotificationBroadcastReceiver.class);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), ALARM_REQUEST_CODE, intent, 0);
        alarmManager.cancel(pendingIntent);

    }


}