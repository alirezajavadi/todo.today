package alirezajavadi.todotoday.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import alirezajavadi.todotoday.dailyNotification.DailyNotificationBroadcastReceiver;
import alirezajavadi.todotoday.Prefs;
import alirezajavadi.todotoday.R;
import alirezajavadi.todotoday.Reminder;
import alirezajavadi.todotoday.widget.MainWidgetAppWidgetProvider;

import static alirezajavadi.todotoday.Reminder.PERMISSION_REQUEST_CODE_CALENDAR;

public class SettingsActivity extends AppCompatActivity {
    private Switch sw_reminder;
    private Switch sw_dailyNotification;
    private TextView txv_saveSetting;
    private RadioButton rdb_grayTheme;
    private RadioButton rdb_darkTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set theme to activity
        Prefs.initial(getApplicationContext());
        if (Prefs.read(Prefs.THEME_IS_GRAY,true))
            this.setTheme(R.style.GrayTheme);
        else
            this.setTheme(R.style.DarkTheme);
        setContentView(R.layout.activity_settings);
        init();

        //reminder is on or off? set it to Switch
        sw_reminder.setChecked(Prefs.read(Prefs.IS_ENABLE_REMINDER, false));
        //request calender permissions
        sw_reminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    if (Reminder.isPermissionAllowed()) {
                        if (Reminder.getCalenderIdList().size() == 0) {
                            Toast.makeText(SettingsActivity.this, getString(R.string.errorNoCalender_settings), Toast.LENGTH_LONG).show();
                            sw_reminder.setChecked(false);
                        }
                    } else {
                        Reminder.requestCalendarReadWritePermission(SettingsActivity.this);
                        sw_reminder.setChecked(false);
                    }

            }
        });


        //dailyNotification is on or off? set it to Switch
        sw_dailyNotification.setChecked(Prefs.read(Prefs.IS_ENABLE_DAILY_NOTIFICATION, false));

        //theme is gray or dark? set it to radioButtons
        if (Prefs.read(Prefs.THEME_IS_GRAY, true))
            rdb_grayTheme.setChecked(true);
        else
            rdb_darkTheme.setChecked(true);


        //save settings in sharedPrefs and close activity
        txv_saveSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //reminder
                //save reminder in sharedPrefs
                Prefs.write(Prefs.IS_ENABLE_REMINDER, sw_reminder.isChecked());

                //dailyNotification
                //start the alarmManager and set it to show notification every day or stop
                if (sw_dailyNotification.isChecked() && !Prefs.read(Prefs.IS_ENABLE_DAILY_NOTIFICATION, false))
                    DailyNotificationBroadcastReceiver.startAlarm(SettingsActivity.this);
                //stop the alarmManager
                if (!sw_dailyNotification.isChecked() && Prefs.read(Prefs.IS_ENABLE_DAILY_NOTIFICATION, false))
                    DailyNotificationBroadcastReceiver.stopAlarm(SettingsActivity.this);
                //save dailyNotification status in sharedPrefs
                Prefs.write(Prefs.IS_ENABLE_DAILY_NOTIFICATION, sw_dailyNotification.isChecked());

                //save theme changes in sharedPrefs
                Prefs.write(Prefs.THEME_IS_GRAY, rdb_grayTheme.isChecked());

                Toast.makeText(SettingsActivity.this, getString(R.string.toastSettingsSave), Toast.LENGTH_SHORT).show();

                //close Activity
                onBackPressed();
            }
        });
    }

    private void init() {
        Prefs.initial(SettingsActivity.this);
        Reminder.initial(SettingsActivity.this);
        sw_reminder = findViewById(R.id.sw_reminder_settings);
        txv_saveSetting = findViewById(R.id.txv_saveSettings_settings);
        sw_dailyNotification = findViewById(R.id.sw_dailyNotification_settings);
        rdb_darkTheme = findViewById(R.id.rdb_darkTheme_settings);
        rdb_grayTheme = findViewById(R.id.rdb_grayTheme_settings);

        TextView txv_description=findViewById(R.id.txv_descriptionDailyNotification_settings);
        txv_description.setPaintFlags(txv_description.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        TextView txv=findViewById(R.id.textViewDailyNotification);
        txv.setPaintFlags(txv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE_CALENDAR) {
            //is user accepted the permissions of calender?
            if (Reminder.isPermissionAllowed()) {
                //has user a active calender?
                sw_reminder.setChecked(true);
                if (Reminder.getCalenderIdList().size() == 0) {
                    Toast.makeText(SettingsActivity.this, getString(R.string.errorNoCalender_settings), Toast.LENGTH_LONG).show();
                    sw_reminder.setChecked(false);
                }
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onPause() {
        //when user change activity (all activities of user phone), this method will called
        //and the appWidget on the homeScreen needs to be updated
        updateWidgets();
        super.onPause();
    }

    private void updateWidgets() {
        //get all appWidgetId to update them
        ComponentName name = new ComponentName(SettingsActivity.this, MainWidgetAppWidgetProvider.class);
        int[] appWidgetIds = AppWidgetManager.getInstance(SettingsActivity.this).getAppWidgetIds(name);
        for (int appWidgetId : appWidgetIds) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(SettingsActivity.this);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lsv_todoList_mainWidget);
        }
        //update mainWidget
        Intent intent = new Intent(this, MainWidgetAppWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        sendBroadcast(intent);
    }

    @Override
    public void onBackPressed() {
        //call onCreate method in activity is required to apply theme settings (reCreate menuActivity to apply theme)
        startActivity(new Intent(this,MenuActivity.class));
        finish();
    }
}
