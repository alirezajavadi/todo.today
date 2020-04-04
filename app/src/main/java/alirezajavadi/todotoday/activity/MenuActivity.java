package alirezajavadi.todotoday.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import alirezajavadi.todotoday.CurrentDate;
import alirezajavadi.todotoday.Prefs;
import alirezajavadi.todotoday.R;
import alirezajavadi.todotoday.widget.MainWidgetAppWidgetProvider;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txv_newDay;
    private TextView txv_newTaskTodo;
    private TextView txv_newTaskTitle;
    private TextView txv_charts;
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
        txv_newTaskTitle.setOnClickListener(this);
        txv_newTaskTodo.setOnClickListener(this);
        txv_charts.setOnClickListener(this);
        img_close.setOnClickListener(this);

        //get all appWidgetId to update them
        ComponentName name = new ComponentName(MenuActivity.this, MainWidgetAppWidgetProvider.class);
        appWidgetIds = AppWidgetManager.getInstance(MenuActivity.this).getAppWidgetIds(name);

    }



    private void init() {
        Prefs.initial(MenuActivity.this);
        CurrentDate.initial();
        txv_newDay = findViewById(R.id.txv_newDay_menu);
        txv_help = findViewById(R.id.txv_help_menu);
        txv_newTaskTitle = findViewById(R.id.txv_newTaskTitle_menu);
        txv_newTaskTodo = findViewById(R.id.txv_newTaskTodo_menu);
        txv_charts = findViewById(R.id.txv_charts_menu);
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

            case R.id.txv_newTaskTitle_menu:
                startActivity(new Intent(MenuActivity.this, NewTaskTitle.class));
                break;

            case R.id.txv_newTaskTodo_menu:
                startActivity(new Intent(MenuActivity.this, NewTaskTodo.class));
                break;

            case R.id.txv_charts_menu:
                startActivity(new Intent(MenuActivity.this, ChartsActivity.class));
                break;

            case R.id.img_closeMenu_menu:
                onBackPressed();
                break;

        }
    }


    @Override
    protected void onPause() {
        //when user change activity (all activities of user phone), this method will called
        //and the appWidget on the homeScreen needs to be updated
        updateWidgets();
        super.onPause();
    }

    private void updateWidgets() {
        for (int appWidgetId : appWidgetIds) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(MenuActivity.this);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lsv_todoList_mainWidget);
        }
    }
}
