package alirezajavadi.todotoday.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import alirezajavadi.todotoday.Prefs;
import alirezajavadi.todotoday.R;

public class AboutUsActivity extends AppCompatActivity {

    private TextView txv_ok;
    private TextView txv_openLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set theme to activity
        Prefs.initial(getApplicationContext());
        if (Prefs.read(Prefs.THEME_IS_GRAY, true))
            this.setTheme(R.style.GrayTheme);
        else
            this.setTheme(R.style.DarkTheme);
        setContentView(R.layout.activity_about_us);
        init();

        txv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        txv_openLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.github.com/alirezajavadi/todo.today")));
            }
        });
    }

    private void init() {
        txv_ok = findViewById(R.id.txv_ok_about);
        txv_openLink = findViewById(R.id.txv_openLink_about);
        txv_openLink.setPaintFlags(txv_openLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

    }
}
