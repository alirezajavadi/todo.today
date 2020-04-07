package alirezajavadi.todotoday;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class Reminder {
    private static final String TAG = "ReminderHelper";
    public static final int PERMISSION_REQUEST_CODE_CALENDAR = 101;
    private static Activity activity;

    private Reminder() {

    }

    public static void initial(Activity activity) {
        if (Reminder.activity == null)
            Reminder.activity = activity;
    }

    public static boolean isPermissionAllowed() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestCalendarReadWritePermission(Activity activity) {
        List<String> permissionList = new ArrayList<String>();

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.WRITE_CALENDAR);

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.READ_CALENDAR);


        if (permissionList.size() > 0) {
            String[] permissionArray = new String[permissionList.size()];
            for (int i = 0; i < permissionList.size(); i++) {
                permissionArray[i] = permissionList.get(i);
            }
            ActivityCompat.requestPermissions(activity, permissionArray, PERMISSION_REQUEST_CODE_CALENDAR);
        }
    }


    public static long MakeNewReminder(String title, long startTime, long endTime) {

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)
            return 0;

        List<String> calendarIdList = getCalenderIdList();
        if (calendarIdList.size() == 0)
            return 0;

        int calendarId = Integer.parseInt(calendarIdList.get(0));

        int selectedReminderValue = 0;


        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startTime);
        values.put(CalendarContract.Events.DTEND, endTime);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, title);//description
        values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        values.put(CalendarContract.Events.STATUS, CalendarContract.Events.STATUS_CONFIRMED);
        values.put(CalendarContract.Events.HAS_ALARM, true);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());//Get current timezone

        ContentResolver cr = activity.getContentResolver();
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());
        ContentValues reminders = new ContentValues();
        reminders.put(CalendarContract.Reminders.EVENT_ID, eventID);
        reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        reminders.put(CalendarContract.Reminders.MINUTES, selectedReminderValue);
        cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders);
        return eventID;

    }

    public static List<String> getCalenderIdList() {
        String[] projection = {"_id"};
        Uri calendars = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = activity.getContentResolver();

        Cursor managedCursor = contentResolver.query(calendars, projection, CalendarContract.Calendars.VISIBLE + " = 1", null, CalendarContract.Calendars._ID + " ASC");

        List<String> calenderIdList = new ArrayList<>();

        if (managedCursor == null || !managedCursor.moveToFirst())
            return calenderIdList;

        String calID;

        do {
            calID = managedCursor.getString(managedCursor.getColumnIndex(projection[0]));
            calenderIdList.add(calID);
        } while (managedCursor.moveToNext());
        managedCursor.close();
        return calenderIdList;
    }

    public static void deleteReminder(long reminderId, Context context) {
        Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Long.parseLong(String.valueOf(reminderId)));
        context.getContentResolver().delete(deleteUri, null, null);
    }

}
