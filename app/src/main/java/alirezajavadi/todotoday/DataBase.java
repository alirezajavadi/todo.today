package alirezajavadi.todotoday;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import alirezajavadi.todotoday.model.Todo;

public class DataBase extends SQLiteOpenHelper {
    private static final String DATA_BASE_NAME = "todoDatabase";

    //table main
    private final String TB_NAME_MAIN = "tableMain";
    private final String KEY_MAIN_ID = "mainId";
    private final String KEY_MAIN_START_FROM = "mainStartFrom";
    private final String KEY_MAIN_END_TO = "mainEndTo";
    private final String KEY_MAIN_IS_DONE = "mainIsDone";
    private final String KEY_MAIN_DATE = "mainDate";
    private final String KEY_MAIN_TASK_TITLE = "mainTaskTitle";
    private final String KEY_MAIN_IS_DISPLAY_IN_LIST = "mainIsDisplayInList"; //that mean is: show in charts or not (if displayInList is true -> it will not apply to the chart)
    private final String KEY_MAIN_REMINDER_ID = "mainReminderId";

    //table taskTitle
    private final String TB_NAME_TASK_TITLE = "tableTaskTitle";
    private final String KEY_TASK_TITLE_ID = "taskTitleId";
    private final String KEY_TASK_TITLE_TASK_TITLE = "taskTitleTaskTitle";

    public DataBase(@Nullable Context context) {
        super(context, DATA_BASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TB_MAIN = "CREATE TABLE " + TB_NAME_MAIN + " ( " +
                KEY_MAIN_ID + " INTEGER PRIMARY KEY , " +
                KEY_MAIN_START_FROM + " TEXT ," +
                KEY_MAIN_END_TO + " TEXT , " +
                KEY_MAIN_IS_DONE + " INTEGER , " +
                KEY_MAIN_DATE + " TEXT , " +
                KEY_MAIN_TASK_TITLE + " TEXT , " +
                KEY_MAIN_REMINDER_ID + " INTEGER , " +
                KEY_MAIN_IS_DISPLAY_IN_LIST + " INTEGER )";
        db.execSQL(CREATE_TB_MAIN);

        String CREATE_TB_TASK_TITLE = "CREATE TABLE " + TB_NAME_TASK_TITLE + " ( " +
                KEY_TASK_TITLE_ID + " INTEGER PRIMARY KEY , " +
                KEY_TASK_TITLE_TASK_TITLE + " TEXT )";

        db.execSQL(CREATE_TB_TASK_TITLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //insert new taskTitle in DB
    public int addNewTaskTitle(String taskTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TASK_TITLE_TASK_TITLE, taskTitle);
        if (db.insert(TB_NAME_TASK_TITLE, null, values) == -1) {
            db.close();
            return -1;
        }
        db.close();
        return 1;
    }

    public List<String> getAllTaskTitles() {
        List<String> taskTitleList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TB_NAME_TASK_TITLE;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor == null || cursor.getCount() == 0)
            return taskTitleList;

        cursor.moveToFirst();
        do {
            taskTitleList.add(cursor.getString(cursor.getColumnIndex(KEY_TASK_TITLE_TASK_TITLE)));
        } while (cursor.moveToNext());
        db.close();
        cursor.close();
        return taskTitleList;

    }

    //insert new taskTodo in DB - displayInList is true(default)
    public int addNewTaskTodo(Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_MAIN_START_FROM, todo.getStartFrom());
        values.put(KEY_MAIN_END_TO, todo.getEndTo());
        values.put(KEY_MAIN_TASK_TITLE, todo.getTaskTitle());
        values.put(KEY_MAIN_IS_DONE, todo.getIsDone());
        values.put(KEY_MAIN_IS_DISPLAY_IN_LIST, 1);//true == 1
        values.put(KEY_MAIN_DATE, todo.getDate());
        values.put(KEY_MAIN_REMINDER_ID, todo.getReminderId());

        long result = db.insert(TB_NAME_MAIN, null, values);
        if (result == -1) {
            db.close();
            return -1;
        }
        db.close();
        return 1;
    }

    //get Today to do list : returns "to do list" everywhere isDisplayInList is true
    public List<Todo> getTodayTodoList() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Todo> todoList = new ArrayList<>();

        String query = "SELECT " +
                KEY_MAIN_TASK_TITLE + " , " +
                KEY_MAIN_DATE + " , " +
                KEY_MAIN_IS_DONE + " , " +
                KEY_MAIN_ID + " , " +
                KEY_MAIN_REMINDER_ID + " , " +
                KEY_MAIN_END_TO + " , " +
                KEY_MAIN_START_FROM +
                " FROM " + TB_NAME_MAIN +
                " WHERE " + KEY_MAIN_IS_DISPLAY_IN_LIST + " = 1 " +
                " ORDER BY  " + KEY_MAIN_START_FROM + " ;";

        Cursor cursor = db.rawQuery(query, null);


        if (cursor == null || cursor.getCount() == 0)
            return todoList;

        cursor.moveToFirst();
        do {
            Todo todo = new Todo();
            todo.setDate(cursor.getString(cursor.getColumnIndex(KEY_MAIN_DATE)));
            todo.setTaskTitle(cursor.getString(cursor.getColumnIndex(KEY_MAIN_TASK_TITLE)));
            todo.setStartFrom(cursor.getString(cursor.getColumnIndex(KEY_MAIN_START_FROM)));
            todo.setEndTo(cursor.getString(cursor.getColumnIndex(KEY_MAIN_END_TO)));
            todo.setIsDone(cursor.getInt(cursor.getColumnIndex(KEY_MAIN_IS_DONE)));
            todo.setDatabaseId(cursor.getInt(cursor.getColumnIndex(KEY_MAIN_ID)));
            todo.setIsDisplayInList(1);
            todo.setReminderId(cursor.getInt(cursor.getColumnIndex(KEY_MAIN_REMINDER_ID)));
            todoList.add(todo);
        } while (cursor.moveToNext());
        db.close();
        cursor.close();
        return todoList;
    }

    //update isDone (change it to 1). that mean is: user click on "newDay" then last taskTodo is done and start new day(newTaskTodo)
    //update isDisplayInList (change it to 0).  that mean is: user click on "newDay" then last taskTodo is done and start new day(newTaskTodo) with new list of to do work
    public void updateForNewDay(String lastDayDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_MAIN_IS_DISPLAY_IN_LIST, 0);//0 == false

        db.update(TB_NAME_MAIN, values, KEY_MAIN_DATE + "=?", new String[]{lastDayDate});
        db.close();
    }


    //update isDone (when user clicked on img_checkBox_itemListTodoMainWidget)
    public int updateIsDone(int id, int isDone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_MAIN_IS_DONE, isDone);

        int result = db.update(TB_NAME_MAIN, values, KEY_MAIN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }

    //delete one record from database (if user clicked on img_deleteItem_itemListTodoMainWidget)
    public int deleteRecord(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TB_NAME_MAIN, KEY_MAIN_ID + "=?", new String[]{id + ""});
        db.close();
        return result;
    }

    //get data everywhere that "isDisplayInList" is equal to 1  and date is between "startFrom" & "endTo" (show in chart)
    public List<Todo> getAllData(String startFrom, String endTo) {
        List<Todo> todoList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " +
                KEY_MAIN_TASK_TITLE + " , " +
                KEY_MAIN_DATE + " , " +
                KEY_MAIN_IS_DONE + " , " +
                KEY_MAIN_ID + " , " +
                KEY_MAIN_END_TO + " , " +
                KEY_MAIN_START_FROM +
                " FROM " + TB_NAME_MAIN +
                " WHERE (" + KEY_MAIN_IS_DISPLAY_IN_LIST + " = 0 ) AND (" + KEY_MAIN_DATE + " BETWEEN \"" + startFrom + "\" and \"" + endTo + "\" ) ;";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor == null || cursor.getCount() == 0)
            return todoList;

        cursor.moveToFirst();
        do {
            Todo todo = new Todo();
            todo.setDate(cursor.getString(cursor.getColumnIndex(KEY_MAIN_DATE)));
            todo.setTaskTitle(cursor.getString(cursor.getColumnIndex(KEY_MAIN_TASK_TITLE)));
            todo.setStartFrom(cursor.getString(cursor.getColumnIndex(KEY_MAIN_START_FROM)));
            todo.setEndTo(cursor.getString(cursor.getColumnIndex(KEY_MAIN_END_TO)));
            todo.setIsDone(cursor.getInt(cursor.getColumnIndex(KEY_MAIN_IS_DONE)));
            todo.setDatabaseId(cursor.getInt(cursor.getColumnIndex(KEY_MAIN_ID)));
            todo.setIsDisplayInList(0);
            todoList.add(todo);
        } while (cursor.moveToNext());
        db.close();
        cursor.close();
        return todoList;

    }


}
