package alirezajavadi.todotoday.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import alirezajavadi.todotoday.Prefs;
import alirezajavadi.todotoday.R;
import alirezajavadi.todotoday.Reminder;

import static alirezajavadi.todotoday.Reminder.PERMISSION_REQUEST_CODE_CALENDAR;

public class SettingsActivity extends AppCompatActivity {
    private Switch sw_reminder;
    private TextView txv_saveSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();

        //request calender permissions
        sw_reminder.setChecked(Prefs.read(Prefs.IS_ENABLE_REMINDER, false));
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


        //save settings in sharedPrefs and close activity
        txv_saveSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save reminder in sharedPrefs
                Prefs.write(Prefs.IS_ENABLE_REMINDER, sw_reminder.isChecked());

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
}
