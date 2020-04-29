package alirezajavadi.todotoday.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import alirezajavadi.todotoday.Prefs;
import alirezajavadi.todotoday.R;

public class AboutUsActivity extends AppCompatActivity {

    //todo add aboutUs
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set theme to activity
        Prefs.initial(getApplicationContext());
        if (Prefs.read(Prefs.THEME_IS_GRAY,true))
            this.setTheme(R.style.GrayTheme);
        else
            this.setTheme(R.style.DarkTheme);
        setContentView(R.layout.activity_about_us);
    }
}
