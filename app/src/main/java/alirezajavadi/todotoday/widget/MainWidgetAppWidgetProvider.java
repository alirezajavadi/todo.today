package alirezajavadi.todotoday.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import alirezajavadi.todotoday.DataBase;
import alirezajavadi.todotoday.Prefs;
import alirezajavadi.todotoday.Reminder;
import alirezajavadi.todotoday.activity.MenuActivity;
import alirezajavadi.todotoday.R;

public class MainWidgetAppWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_CLICK_LIST_VIEW = "actionClickListView";
    public static final String EXTRA_ITEM_CLICKED = "itemClicked";
    public static final String EXTRA_ITEM_POSITION_IN_DATABASE = "itemPositionInDatabase";
    public static final String EXTRA_ITEM_IS_DONE = "itemIsDone";
    public static final String EXTRA_ITEM_REMINDER_ID = "itemIsDone";


    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        //every widget in homeScreen
        for (int widgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.main_widget_app_widget_provider);

            //open new day activity
            Intent intentNewDay = new Intent(context, MenuActivity.class);
            PendingIntent pendingIntentNewDay = PendingIntent.getActivity(context, 0, intentNewDay, 0);
            remoteViews.setOnClickPendingIntent(R.id.img_openMenu_mainWidget, pendingIntentNewDay);

            //adapter list view (something like that)
            Intent serviceIntent = new Intent(context, MainWidgetWidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

            //onClick listener for listViews Item (handle events in "onReceive()" )
            //set adapter to listView
            Intent clickIntent = new Intent(context, MainWidgetAppWidgetProvider.class);
            clickIntent.setAction(ACTION_CLICK_LIST_VIEW);
            PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, 0);
            remoteViews.setRemoteAdapter(R.id.lsv_todoList_mainWidget, serviceIntent);
            remoteViews.setEmptyView(R.id.lsv_todoList_mainWidget, R.id.txv_emptyView_mainWidget);
            remoteViews.setPendingIntentTemplate(R.id.lsv_todoList_mainWidget, clickPendingIntent);

            //change widget theme
            themeSetting(remoteViews, context);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
            appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.lsv_todoList_mainWidget);
        }


    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_CLICK_LIST_VIEW.equals(intent.getAction())) {
            DataBase dataBase = new DataBase(context);
            Prefs.initial(context);
            int clickedItem = intent.getIntExtra(EXTRA_ITEM_CLICKED, 0);
            int databaseId = intent.getIntExtra(EXTRA_ITEM_POSITION_IN_DATABASE, 0);


            if (clickedItem == R.id.img_checkBox_itemListTodoMainWidget) {
                //update database if user clicked on img_checkBox_itemListTodoMainWidget and change his is Done
                int isDone = intent.getIntExtra(EXTRA_ITEM_IS_DONE, 0);
                int result;
                if (isDone == 0)
                    result = dataBase.updateIsDone(databaseId, 1);
                else
                    result = dataBase.updateIsDone(databaseId, 0);

                //check update database is success or not
                if (result == 0)
                    Toast.makeText(context, context.getString(R.string.toastAddUnSuccess), Toast.LENGTH_SHORT).show();
            } else {
                long reminderId = intent.getLongExtra(EXTRA_ITEM_REMINDER_ID, 0);
                //update database if user clicked on img_deleteItem_itemListTodoMainWidget and delete a record
                int result = dataBase.deleteRecord(databaseId);
                //check delete record in database is success or not
                if (result != 0) {
                    //delete reminder from calender if currentRecord successfully deleted from database
                    if (Prefs.read(Prefs.IS_ENABLE_REMINDER,false))
                        Reminder.deleteReminder(reminderId, context);
                } else {
                    Toast.makeText(context, context.getString(R.string.toastAddUnSuccess), Toast.LENGTH_SHORT).show();
                }
            }

            //update widget
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lsv_todoList_mainWidget);
        }

        super.onReceive(context, intent);
    }


    private void themeSetting(RemoteViews remoteViews, Context context) {
        Prefs.initial(context);
        boolean themeIsGray = Prefs.read(Prefs.THEME_IS_GRAY, true);

        int backgroundMain;
        int backgroundHeader;
        int backgroundFooter;
        int checkboxImageRec;
        int textColor;
        int headerShadow;
        int footerShadow;

        if (themeIsGray) {
            backgroundMain = R.drawable.shape_gray_bg_main_widget;
            backgroundHeader = R.drawable.shape_gray_bg_header_widget;
            backgroundFooter = R.drawable.selector_gray_bg_footer_widget;
            textColor = ContextCompat.getColor(context, R.color.gray_textColorHigh);
            headerShadow = R.drawable.shape_gray_header_shadow_widget;
            footerShadow = R.drawable.shape_gray_footer_shadow_widget;
            checkboxImageRec = R.drawable.ic_menu_widget;
        } else {
            backgroundMain = R.drawable.shape_dark_bg_main_widget;
            backgroundHeader = R.drawable.shape_dark_bg_header_widget;
            backgroundFooter = R.drawable.selector_dark_bg_footer_widget;
            textColor = ContextCompat.getColor(context, R.color.dark_textColorHigh);
            headerShadow = R.drawable.shape_dark_header_shadow_widget;
            footerShadow = R.drawable.shape_dark_footer_shadow_widget;
            checkboxImageRec = R.drawable.ic_dark_menu_widget;
        }

        //main background
        remoteViews.setInt(R.id.rll_containerMainWidget_mainWidget, "setBackgroundResource", backgroundMain);
        //header widget
        remoteViews.setInt(R.id.lnl_containerHeaderWidget_mainWidget, "setBackgroundResource", backgroundHeader);
        //text in header
        remoteViews.setTextColor(R.id.txv_startFrom_mainWidget, textColor);
        remoteViews.setTextColor(R.id.txv_endTo_mainWidget, textColor);
        remoteViews.setTextColor(R.id.txv_taskTitle_mainWidget, textColor);
        //textView "List is empty" (EmptyView)
        remoteViews.setTextColor(R.id.txv_emptyView_mainWidget, textColor);
        //footer widget
        remoteViews.setInt(R.id.img_openMenu_mainWidget, "setBackgroundResource", backgroundFooter);
        remoteViews.setImageViewResource(R.id.img_openMenu_mainWidget, checkboxImageRec);
        //header and footer shadow
        remoteViews.setInt(R.id.frl_footerShadow_mainWidget, "setBackgroundResource", footerShadow);
        remoteViews.setInt(R.id.frl_headerShadow_mainWidget, "setBackgroundResource", headerShadow);

    }
}

