package alirezajavadi.todotoday.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.util.List;

import alirezajavadi.todotoday.DataBase;
import alirezajavadi.todotoday.Prefs;
import alirezajavadi.todotoday.R;
import alirezajavadi.todotoday.widget.MainWidgetAppWidgetProvider;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MenuActivity";

    private TextView txv_newDay;
    private TextView txv_newJobTodo;
    private TextView txv_newJobTitle;
    private TextView txv_help;
    private ImageView img_close;


    private int[] appWidgetIds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        init();


        //set onclick listener
        txv_help.setOnClickListener(this);
        txv_newDay.setOnClickListener(this);
        txv_newJobTitle.setOnClickListener(this);
        txv_newJobTodo.setOnClickListener(this);
        img_close.setOnClickListener(this);

        //get all appWidgetId to update them
        ComponentName name = new ComponentName(MenuActivity.this, MainWidgetAppWidgetProvider.class);
        appWidgetIds = AppWidgetManager.getInstance(MenuActivity.this).getAppWidgetIds(name);

    }

    private void init() {
        txv_newDay = findViewById(R.id.txv_newDay_menu);
        txv_help = findViewById(R.id.txv_help_menu);
        txv_newJobTitle = findViewById(R.id.txv_newJobTitle_menu);
        txv_newJobTodo = findViewById(R.id.txv_newJobTodo_menu);
        img_close = findViewById(R.id.img_closeMenu_menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txv_newDay_menu:
                startActivity(new Intent(MenuActivity.this, NewDayActivity.class));
                break;

            case R.id.txv_help_menu:
                // TODO add activity help
                break;

            case R.id.txv_newJobTitle_menu:
                startActivity(new Intent(MenuActivity.this, NewJobTitle.class));
                break;

            case R.id.txv_newJobTodo_menu:
                startActivity(new Intent(MenuActivity.this, NewJobTodo.class));
                break;

            case R.id.img_closeMenu_menu:
                onBackPressed();
                break;

        }
    }


    @Override
    public void onBackPressed() {
        //when the user pressed back button, this method will called
        //and the appWidget on the homeScreen needs to be updated
        updateWidgets();
        super.onBackPressed();
    }

    @Override
    protected void onUserLeaveHint() {
        //when the user leave the app (with pressed home button or incoming call or ...), this method will called
        //and the appWidget on the homeScreen needs to be updated
        updateWidgets();
        super.onUserLeaveHint();
    }

    private void updateWidgets() {
        for (int appWidgetId : appWidgetIds) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(MenuActivity.this);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lsv_todoList_mainWidget);
        }
    }
}
