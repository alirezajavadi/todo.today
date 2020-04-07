package alirezajavadi.todotoday.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import alirezajavadi.todotoday.CurrentDate;
import alirezajavadi.todotoday.Prefs;
import alirezajavadi.todotoday.R;
import saman.zamani.persiandate.PersianDate;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        Prefs.initial(MainActivity.this);
        CurrentDate.initial();
        //settings for first run
        firstRun();
    }

    private void firstRun() {
        if (Prefs.read(Prefs.IS_FIRST_RUN, true)) {

            //set onClick to txv_okFirstRun and close to app because user should work with appWidget
            findViewById(R.id.txv_okFirstRun_menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

//todo remove this comments in the end
//            //hide app icon
//            PackageManager p = getPackageManager();
//            ComponentName componentName = new ComponentName(this, MainActivity.class); // activity which is first time open in manifest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
//            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

            //save today's date to sharedPrefs (display it by default in the charts)
            Prefs.write(Prefs.FIRST_RUN_DATE, CurrentDate.getCurrentDate());

            //next run, is not the first run :|
            Prefs.write(Prefs.IS_FIRST_RUN, false);

        }
    }
}
