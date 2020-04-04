package alirezajavadi.todotoday.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.core.content.ContextCompat;

import java.util.List;

import alirezajavadi.todotoday.DataBase;
import alirezajavadi.todotoday.R;
import alirezajavadi.todotoday.model.Todo;

import static alirezajavadi.todotoday.widget.MainWidgetAppWidgetProvider.EXTRA_ITEM_CLICKED;
import static alirezajavadi.todotoday.widget.MainWidgetAppWidgetProvider.EXTRA_ITEM_IS_DONE;
import static alirezajavadi.todotoday.widget.MainWidgetAppWidgetProvider.EXTRA_ITEM_POSITION_IN_DATABASE;

public class MainWidgetWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MainWidgetItemFactory(getApplicationContext(), intent);
    }

    static class MainWidgetItemFactory implements RemoteViewsFactory {
        private static final String TAG = "MainWidgetItemFactory";
        private Context context;
        private int appWidgetId;
        private List<Todo> todoList;
        DataBase dataBase;

        public MainWidgetItemFactory(Context context, Intent intent) {
            this.context = context;
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            //connect to dataBase
            dataBase = new DataBase(context);

        }

        @Override
        public void onDataSetChanged() {
            //when ever user insert new data, it must be called
            todoList = dataBase.getTodayTodoList();
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public int getCount() {
            if (todoList == null)
                return 0;
            return todoList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            //bind data to list view
            Todo todo = todoList.get(position);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.view_item_list_todo_main_widget);
            views.setTextViewText(R.id.txv_taskTitle_itemListTodoMainWidget, todo.getTaskTitle());
            views.setTextViewText(R.id.txv_startFrom_itemListTodoMainWidget, todo.getStartFrom());
            views.setTextViewText(R.id.txv_endTo_itemListTodoMainWidget, todo.getEndTo());
            int imageRecId;
            if (todo.getIsDone() == 1) {
                //draw the line on the all textViews and change its color
                views.setInt(R.id.txv_taskTitle_itemListTodoMainWidget, "setPaintFlags", Paint.STRIKE_THRU_TEXT_FLAG);
                views.setInt(R.id.txv_startFrom_itemListTodoMainWidget, "setPaintFlags", Paint.STRIKE_THRU_TEXT_FLAG);
                views.setInt(R.id.txv_endTo_itemListTodoMainWidget, "setPaintFlags", Paint.STRIKE_THRU_TEXT_FLAG);
                views.setTextColor(R.id.txv_taskTitle_itemListTodoMainWidget, ContextCompat.getColor(context, R.color.doneTask_itemListTodoMainWidget));
                views.setTextColor(R.id.txv_startFrom_itemListTodoMainWidget, ContextCompat.getColor(context, R.color.doneTask_itemListTodoMainWidget));
                views.setTextColor(R.id.txv_endTo_itemListTodoMainWidget, ContextCompat.getColor(context, R.color.doneTask_itemListTodoMainWidget));

                //set img_checkBox_itemListTodoMainWidget resource
                imageRecId = R.drawable.ic_checkbox_checked;
            } else {
                //clear the line on the all textViews and change its color
                views.setInt(R.id.txv_taskTitle_itemListTodoMainWidget, "setPaintFlags", Paint.ANTI_ALIAS_FLAG);
                views.setInt(R.id.txv_startFrom_itemListTodoMainWidget, "setPaintFlags", Paint.ANTI_ALIAS_FLAG);
                views.setInt(R.id.txv_endTo_itemListTodoMainWidget, "setPaintFlags", Paint.ANTI_ALIAS_FLAG);
                views.setTextColor(R.id.txv_taskTitle_itemListTodoMainWidget, ContextCompat.getColor(context, android.R.color.black));
                views.setTextColor(R.id.txv_startFrom_itemListTodoMainWidget, ContextCompat.getColor(context, android.R.color.black));
                views.setTextColor(R.id.txv_endTo_itemListTodoMainWidget, ContextCompat.getColor(context, android.R.color.black));

                //set img_checkBox_itemListTodoMainWidget resource
                imageRecId = R.drawable.ic_checkbox_not_checked;
            }
            views.setImageViewResource(R.id.img_checkBox_itemListTodoMainWidget, imageRecId);


            //if user click on img_checkBox_itemListTodoMainWidget
            Intent fillIntentCheckBox = new Intent();
            fillIntentCheckBox.putExtra(EXTRA_ITEM_CLICKED, R.id.img_checkBox_itemListTodoMainWidget);
            fillIntentCheckBox.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            fillIntentCheckBox.putExtra(EXTRA_ITEM_POSITION_IN_DATABASE, todo.getIdDatabase());
            fillIntentCheckBox.putExtra(EXTRA_ITEM_IS_DONE, todo.getIsDone());
            views.setOnClickFillInIntent(R.id.img_checkBox_itemListTodoMainWidget, fillIntentCheckBox);

            //if user click on img_deleteItem_itemListTodoMainWidget
            Intent fillIntentDelete = new Intent();
            fillIntentDelete.putExtra(EXTRA_ITEM_CLICKED, R.id.img_deleteItem_itemListTodoMainWidget);
            fillIntentDelete.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            fillIntentDelete.putExtra(EXTRA_ITEM_POSITION_IN_DATABASE, todo.getIdDatabase());
            views.setOnClickFillInIntent(R.id.img_deleteItem_itemListTodoMainWidget, fillIntentDelete);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
