package alirezajavadi.todotoday.dailyNotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import java.util.Random;

import alirezajavadi.todotoday.R;

public class AlarmManagerService extends JobIntentService {
    private static final int UNIQUE_JOB_ID = 6787;

    static void enqueueWork(Context context) {
        enqueueWork(context, AlarmManagerService.class, UNIQUE_JOB_ID, new Intent(context, AlarmManagerService.class));
    }

    @Override
    public void onHandleWork(Intent intent) {
        createNotification(getApplicationContext());
    }

    public static void createNotification(Context context) {
        //get one sentence from  "motivationalSentences" array in string.xml
        int random = new Random().nextInt(420);// this app has 420 sentence
        String randomSentence=context.getResources().getStringArray(R.array.motivationalSentences_notification)[random];

        //create notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        final int NOTIFY_ID = 1921;
        String id = "Motivational sentence";
        long[] vibrationPattern={40, 400, 200,400};

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String title = "Motivational sentence";

            NotificationChannel channel = new NotificationChannel(id, title, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("motivational sentences");
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setVibrationPattern(vibrationPattern);
            notificationManager.createNotificationChannel(channel);

            builder.setSmallIcon(R.drawable.ic_run_notification)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setLights(Color.WHITE, 1000, 1000)
                    .setContentTitle(context.getString(R.string.iCan_notification))
                    .setContentText(randomSentence)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(randomSentence))
                    .setVibrate(vibrationPattern);
        } else {
            builder.setSmallIcon(R.drawable.ic_run_notification)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(randomSentence))
                    .setContentTitle(context.getString(R.string.iCan_notification))
                    .setContentText(randomSentence)
                    .setLights(Color.WHITE, 1000, 1000)
                    .setVibrate(vibrationPattern)
                    .setPriority(Notification.PRIORITY_HIGH);
        }
        Notification notification = builder.build();
        notificationManager.notify(NOTIFY_ID, notification);
    }

}