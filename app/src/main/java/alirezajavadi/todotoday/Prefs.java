package alirezajavadi.todotoday;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    public static final String IS_FIRST_RUN = "firstRun";
    //today date
    public static final String TODAY_DATE = "todayDate";

    //
    public static final String FIRST_RUN_DATE = "firstRunDate";

    //
    public static final String DEFAULT_START_DATE_CHARTS = "defaultStartDateCharts";

    //
    public static final String IS_ENABLE_REMINDER = "isEnableReminder";

    //
    public static final String IS_ENABLE_DAILY_NOTIFICATION="isEnableDailyNotification";

    //
    public static final String THEME_IS_GRAY="themeIsGray";


    private static SharedPreferences mSharedPref;


    private Prefs() {

    }

    public static void initial(Context context) {
        if (mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }

    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.apply();
    }

    public static Integer read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void write(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putInt(key, value).apply();
    }
}
