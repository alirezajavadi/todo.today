package alirezajavadi.todotoday;

import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

public class CurrentDate {
    private static PersianCalendar calendar;

    private CurrentDate() {

    }

    public static void initial() {
        if (calendar == null)
            calendar = new PersianCalendar();
    }


    public static String getCurrentDate() {

        String month;
        String day;
        if ((getMonth() + 1) < 9)
            month = "0" + (getMonth() + 1);
        else
            month = String.valueOf(getMonth() + 1);

        if (getDay() <= 9)
            day = "0" + (getDay());
        else
            day = String.valueOf(getDay());


        return getYear() + "/" + month + "/" + day;
    }

    public static int getDay() {
        return calendar.getPersianDay();
    }

    public static int getMonth() {
        return calendar.getPersianMonth();
    }

    public static int getYear() {
        return calendar.getPersianYear();
    }
}
