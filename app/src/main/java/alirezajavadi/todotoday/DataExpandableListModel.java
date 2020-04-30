package alirezajavadi.todotoday;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataExpandableListModel {

    public static HashMap<String, List<String>> getData(Context context) {
        HashMap<String, List<String>> expandableListDetail = new HashMap<>();
        Resources resources = context.getResources();


        List<String> howAddMyDateInChartList = new ArrayList<>();
        howAddMyDateInChartList.add(resources.getString(R.string.howAddMyDateInChart_exvText_help));

        List<String> reminderNotWorkList = new ArrayList<>();
        reminderNotWorkList.add(resources.getString(R.string.reminderNotWork_exvText_help));

        List<String> dailyNotificationNotWorkList = new ArrayList<>();
        dailyNotificationNotWorkList.add(resources.getString(R.string.dailyNotificationNotWork_exvText_help));

        List<String> chartIsNotDisplayList = new ArrayList<>();
        chartIsNotDisplayList.add(resources.getString(R.string.chartIsNotDisplay_exvText_help));


        expandableListDetail.put(resources.getString(R.string.howAddMyDateInChart_exvTitle_help), howAddMyDateInChartList);
        expandableListDetail.put(resources.getString(R.string.reminderNotWork_exvTitle_help), reminderNotWorkList);
        expandableListDetail.put(resources.getString(R.string.dailyNotificationNotWork_exvTitle_help), dailyNotificationNotWorkList);
        expandableListDetail.put(resources.getString(R.string.chartIsNotDisplay_exvTitle_help), chartIsNotDisplayList);

        return expandableListDetail;
    }
}
