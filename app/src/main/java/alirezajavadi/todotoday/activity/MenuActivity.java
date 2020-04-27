package alirezajavadi.todotoday.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import alirezajavadi.todotoday.CurrentDate;
import alirezajavadi.todotoday.Prefs;
import alirezajavadi.todotoday.R;
import alirezajavadi.todotoday.widget.MainWidgetAppWidgetProvider;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout lnl_newDay;
    private LinearLayout lnl_newTask;
    private LinearLayout lnl_newTaskTitle;
    private LinearLayout lnl_charts;
    private TextView txv_help;
    private TextView txv_settings;
    private ImageView img_more;
    private View view_backgroundMore;
    private LinearLayout lnl_containerMore;
    private boolean moreContainerIsOpen = false;
    private FrameLayout frl_containerMore;
    private TextView txv_aboutus;


    private int[] appWidgetIds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        init();


        //set onclick listener
        txv_help.setOnClickListener(this);
        lnl_newDay.setOnClickListener(this);
        lnl_newTaskTitle.setOnClickListener(this);
        lnl_newTask.setOnClickListener(this);
        lnl_charts.setOnClickListener(this);
        view_backgroundMore.setOnClickListener(this);
        txv_settings.setOnClickListener(this);
        frl_containerMore.setOnClickListener(this);
        txv_aboutus.setOnClickListener(this);

        view_backgroundMore.setClickable(false);

        //get all appWidgetId to update them
        ComponentName name = new ComponentName(MenuActivity.this, MainWidgetAppWidgetProvider.class);
        appWidgetIds = AppWidgetManager.getInstance(MenuActivity.this).getAppWidgetIds(name);

    }


    private void init() {
        Prefs.initial(MenuActivity.this);
        CurrentDate.initial();
        lnl_newDay = findViewById(R.id.lnl_newDay_menu);
        txv_help = findViewById(R.id.txv_help_menu);
        lnl_newTaskTitle = findViewById(R.id.lnl_newTaskTitle_menu);
        lnl_newTask = findViewById(R.id.lnl_newTask_menu);
        txv_settings = findViewById(R.id.txv_settings_menu);
        lnl_charts = findViewById(R.id.lnl_charts_menu);
        img_more = findViewById(R.id.img_moreMenu_menu);
        lnl_containerMore = findViewById(R.id.lnl_containerMore_menu);
        view_backgroundMore = findViewById(R.id.view_backgroundMoreMenu_menu);
        frl_containerMore = findViewById(R.id.frl_containerMore_menu);
        txv_aboutus = findViewById(R.id.txv_aboutUs_menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lnl_newDay_menu:
                startActivity(new Intent(MenuActivity.this, NewDayActivity.class));
                break;

            case R.id.txv_help_menu:
                openCloseMore();
                startActivity(new Intent(this,HelpActivity.class));
                break;

            case R.id.lnl_newTaskTitle_menu:
                startActivity(new Intent(MenuActivity.this, NewTaskTitleActivity.class));
                break;

            case R.id.lnl_newTask_menu:
                startActivity(new Intent(MenuActivity.this, NewTaskTodoActivity.class));
                break;

            case R.id.lnl_charts_menu:
                startActivity(new Intent(MenuActivity.this, ChartsActivity.class));
                break;

            case R.id.txv_settings_menu:
                openCloseMore();
                startActivity(new Intent(MenuActivity.this, SettingsActivity.class));
                break;
            case R.id.frl_containerMore_menu:
            case R.id.view_backgroundMoreMenu_menu:
                openCloseMore();
                break;

            case R.id.txv_aboutUs_menu:
                openCloseMore();
                startActivity(new Intent(this, AboutUsActivity.class));
                break;

        }
    }

    private void openCloseMore() {
        //change container params with anim (not work in api 19)
        TransitionSet transitionSet = new TransitionSet().addTransition(new ChangeBounds());
        TransitionManager.beginDelayedTransition(lnl_containerMore, transitionSet);


        //get container params, (we need last params)
        final ViewGroup.LayoutParams params = lnl_containerMore.getLayoutParams();

        if (moreContainerIsOpen) {
            //show container background (fade out)
            view_backgroundMore.animate().alpha(0).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    frl_containerMore.setClickable(false);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    frl_containerMore.setClickable(true);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            view_backgroundMore.setClickable(false);


            //change container height
            params.height = 0;
            lnl_containerMore.setLayoutParams(params);

            //rotate img more(t0 +)
            img_more.animate().rotation(0);
        } else {
            //hide container background (fade in)
            view_backgroundMore.animate().alpha(0.4f).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    frl_containerMore.setClickable(false);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    frl_containerMore.setClickable(true);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            view_backgroundMore.setClickable(true);

            //change container height
            params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            lnl_containerMore.setLayoutParams(params);

            //rotate img more (tp *)
            img_more.animate().rotation(225);
        }

        moreContainerIsOpen = !moreContainerIsOpen;


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
