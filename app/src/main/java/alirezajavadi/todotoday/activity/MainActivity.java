package alirezajavadi.todotoday.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.util.ArrayList;
import java.util.List;

import alirezajavadi.todotoday.DataBase;
import alirezajavadi.todotoday.Prefs;
import alirezajavadi.todotoday.R;
import alirezajavadi.todotoday.model.Todo;
import alirezajavadi.todotoday.model.TodoChart;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private float MAX_HEIGHT_CONTAINER_SELECT_DATE;
    private float MIN_HEIGHT_CONTAINER_SELECT_DATE;
    private boolean isOpenContainer = false;


    int startDateYear = 0;
    int startDateMonth = 0;
    int startDateDay = 0;

    private List<Todo> jobList;
    private List<TodoChart> doneJobList_todoChart;
    private List<TodoChart> undoneJobList_todoChart;
    private List<String> jobTitleList;
    private List<Integer> doneJobHourList;
    private List<Float> doneJobMinuteList;
    private List<Integer> undoneJobHourList;
    private List<Float> undoneJobMinuteList;

    private TextView txv_openContainerSelectDate;
    private TextView txv_okSelectedDate;
    private CardView cdv_containerSelectDateClosed;
    private CardView cdv_containerSelectDate;
    private Animation anim_closeContainer;
    private Animation anim_openContainer;

    private TextView txv_selectDateFrom;
    private TextView txv_selectDateTo;

    private DataBase dataBase;

    private BarChart chartDone;
    private BarChart chartUnDone;

    private TextView txv_descriptionDoneJobChart;
    private TextView txv_descriptionUndoneJobChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        //set Toolbar
        setSupportActionBar((Toolbar) findViewById(R.id.tlb_toolbar_main));

        Log.i(TAG, "onCreate: ");
        init();
        //app setting if it is first run
        settingFirstRun();

        //init container select date and every about that
        initContainer();

        getDataJobs("0", "0");
        initDoneChart();
        initUnDoneChart();
    }


    //initial
    private void init() {
        Prefs.initial(MainActivity.this);


        //get pixel
        //todo change this dpToPx method
        MAX_HEIGHT_CONTAINER_SELECT_DATE = dpToPx(141f);
        MIN_HEIGHT_CONTAINER_SELECT_DATE = dpToPx(36f);

        //select date view and anim
        txv_openContainerSelectDate = findViewById(R.id.txv_openContainerSelectDate_main);
        txv_okSelectedDate = findViewById(R.id.txv_okSelectedDate_main);
        cdv_containerSelectDateClosed = findViewById(R.id.cdv_containerSelectDateClosed_main);
        cdv_containerSelectDate = findViewById(R.id.cdv_containerSelectDate_main);
        initAnimationContainer();

        //
        txv_selectDateFrom = findViewById(R.id.txv_selectDateFrom_main);
        txv_selectDateTo = findViewById(R.id.txv_selectDateTo_main);
        chartDone = findViewById(R.id.barChart_doneJob_main);
        chartUnDone = findViewById(R.id.barChart_undoneJob_main);

        //
        dataBase = new DataBase(MainActivity.this);

        //

        txv_descriptionDoneJobChart = findViewById(R.id.txv_descriptionDoneJobChart_main);
        txv_descriptionUndoneJobChart = findViewById(R.id.txv_descriptionUndoneJobChart_main);
        doneJobList_todoChart = new ArrayList<>();
        undoneJobList_todoChart = new ArrayList<>();
        doneJobHourList = new ArrayList<>();
        undoneJobHourList = new ArrayList<>();
        doneJobMinuteList = new ArrayList<>();
        undoneJobHourList = new ArrayList<>();
        undoneJobMinuteList = new ArrayList<>();


    }

    private void initUnDoneChart() {
        chartUnDone.setDrawBarShadow(false);
        chartUnDone.setDrawValueAboveBar(true);
        chartUnDone.getDescription().setEnabled(false);
        chartUnDone.setDrawGridBackground(false);
        chartUnDone.getLegend().setEnabled(false);
        chartUnDone.animateXY(1000, 1000);

        // scaling can now only be done on x- and y-axis separately
        //todo check this in real device
        chartUnDone.setPinchZoom(true);


        XAxis xAxis = chartUnDone.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(jobTitleList.size());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(jobTitleList));


        YAxis leftAxis = chartUnDone.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = chartUnDone.getAxisRight();
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);


        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < jobTitleList.size(); i++) {
            values.add(new BarEntry(i, (float) undoneJobHourList.get(i) + undoneJobMinuteList.get(i)));
        }


        if (chartUnDone.getData() != null && chartUnDone.getData().getDataSetCount() > 0) {
            Log.i(TAG, "initDoneChart: ");
            chartUnDone.invalidate();
            chartUnDone.clear();
        }

        BarDataSet barDataSet = new BarDataSet(values, "");
        barDataSet.setColor(ContextCompat.getColor(MainActivity.this, R.color.bgHeader_mainWidget));


        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet);


        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);

        chartUnDone.setData(data);

    }

    //get data from database to show in the charts
    private void getDataJobs(String startFrom, String endTo) {


        //get all jobTitle
        jobTitleList = dataBase.getAllJobTitle();

        //get all date between date "start" and "end"
        jobList = dataBase.getAllData(startFrom, endTo);

        //get all "to do" object from list and separate it to "done job" and "undone job";
        doneJobList_todoChart.clear();
        for (int i = 0; i < jobList.size(); i++) {
            TodoChart todoChart = new TodoChart();

            todoChart.setJobTitle(jobList.get(i).getJobTitle());

            String[] stringsStartFrom = jobList.get(i).getStartFrom().split(":"); //convert "10:34" to {"10","34"} and save it in static array
            String[] stringsEndTo = jobList.get(i).getEndTo().split(":"); //convert "10:34" to {"10","34"} and save it in static array

            //calculate hour
            int hourStartFrom = Integer.parseInt(stringsStartFrom[0]);//convert string to int
            int hourEndTo = Integer.parseInt(stringsEndTo[0]);//convert string to int

            todoChart.setHour(hourEndTo - hourStartFrom);

            //calculate minute
            int minuteStartFrom = Integer.parseInt(stringsStartFrom[1]);
            int minuteEndTo = Integer.parseInt(stringsEndTo[1]);
            int finalMin = minuteEndTo - minuteStartFrom;
            finalMin = finalMin >= 0 ? finalMin : finalMin * -1;//for example: 10(minuteEndTo) - 40(minuteStartFrom) = -30(finalMin) ---->must multiplied in -1
            todoChart.setMinute(finalMin);
            //separate to "done job" and "undone job"
            if (jobList.get(i).getIsDone() == 1) {
                doneJobList_todoChart.add(todoChart);

            } else {
                undoneJobList_todoChart.add(todoChart);
            }

        }

        //add zero in all list Hour and Minute
        for (int i = 0; i < jobTitleList.size(); i++) {
            if (doneJobMinuteList.size() < jobTitleList.size()) {
                doneJobHourList.add(i, 0);
                doneJobMinuteList.add(i, 0f);
                undoneJobHourList.add(i, 0);
                undoneJobMinuteList.add(i, 0f);
            } else {
                doneJobHourList.set(i, 0);
                doneJobMinuteList.set(i, 0f);
                undoneJobHourList.set(i, 0);
                undoneJobMinuteList.set(i, 0f);
            }
        }

        int hour ;
        float minute ;
        int lastHour ;
        float lastMinute ;
        //search in lists and separate it based on the jobTitles
        for (int i = 0; i < jobTitleList.size(); i++) {
            String jobTitle = jobTitleList.get(i);
            //search in done jobs
            for (TodoChart todoChart : doneJobList_todoChart) {
                if (jobTitle.equals(todoChart.getJobTitle())) {
                    hour = todoChart.getHour();
                    minute = (float) todoChart.getMinute();
                    lastHour = doneJobHourList.get(i);
                    lastMinute = doneJobMinuteList.get(i);

                    doneJobHourList.set(i, lastHour + hour);
                    doneJobMinuteList.set(i, lastMinute + minute);
                }

            }

            //search in undone jobs
            for (TodoChart todoChart : undoneJobList_todoChart) {
                if (jobTitle.equals(todoChart.getJobTitle())) {
                    hour = todoChart.getHour();
                    minute = todoChart.getMinute();
                    lastHour = undoneJobHourList.get(i);
                    lastMinute = undoneJobMinuteList.get(i);

                    undoneJobHourList.set(i, lastHour + hour);
                    undoneJobMinuteList.set(i, lastMinute + minute);

                }
            }
        }

        //if minute more than 60 ---> hour = minute / 60   and    minute = minute % 60
        for (int i = 0; i < jobTitleList.size(); i++) {
            //calculate done job time
            hour = Math.round(doneJobMinuteList.get(i)) / 60;
            hour += doneJobHourList.get(i);

            minute = doneJobMinuteList.get(i) % 60;

            minute /= 60;// that mean is: minute = minute * 1 / 60 (minute per natural number)

            doneJobHourList.set(i, hour);
            doneJobMinuteList.set(i, minute);

            //calculate undone job time
            hour = Math.round(undoneJobMinuteList.get(i)) / 60;
            hour += undoneJobHourList.get(i);
            minute = undoneJobMinuteList.get(i) % 60;
            minute /= 60;// that mean is: minute = minute * 1 / 60 (minute per natural number)

            undoneJobHourList.set(i, hour);
            undoneJobMinuteList.set(i, minute);
        }

        for (int i = 0; i < doneJobMinuteList.size(); i++)
            Log.i(TAG, "getDataJobs: " + i);
    }

    //init container select date and every about that
    private void initContainer() {
        //open select date
        txv_openContainerSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                openAndCloseContainer();
            }
        });

        //close container select date and show charts with new data
        txv_okSelectedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txv_selectDateTo.getText().equals(getString(R.string.tapHere)) || txv_selectDateFrom.getText().equals(getString(R.string.tapHere))) {
                    Toast.makeText(MainActivity.this, getString(R.string.errorFirstSelectDate_main), Toast.LENGTH_SHORT).show();
                    return;
                }
                //close container select date
                openAndCloseContainer();
                //get new date between "startDate" and "endDate"
                getDataJobs(txv_selectDateFrom.getText().toString(), txv_selectDateTo.getText().toString());
                //initial charts with new data
                initDoneChart();
                initUnDoneChart();
                //update charts and containers description
                String newDescription = "\n" + "بین تاریخ " + txv_selectDateFrom.getText().toString() + " و " + txv_selectDateTo.getText().toString();
                txv_descriptionDoneJobChart.setText(getString(R.string.descriptionDoneChart_main) + newDescription);
                txv_descriptionUndoneJobChart.setText(getString(R.string.descriptionUnDoneChart_main) + newDescription);
                findViewById(R.id.txv_descriptionSelectDate_main).setVisibility(View.GONE);

            }
        });

        final PersianCalendar calendar = new PersianCalendar();

        //show date picker
        txv_selectDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        startDateYear = year;
                        startDateDay = dayOfMonth;
                        startDateMonth = monthOfYear;
                        String month;
                        String day;
                        if (monthOfYear < 9)
                            month = "0" + (monthOfYear + 1);
                        else
                            month = String.valueOf(monthOfYear + 1);

                        if (dayOfMonth <= 9)
                            day = "0" + (dayOfMonth);
                        else
                            day = String.valueOf(dayOfMonth);

                        String date = year + "/" + month + "/" + day;

                        txv_selectDateFrom.setText(date);
                    }
                }, calendar.getPersianYear(), calendar.getPersianMonth(), calendar.getPersianDay());
                datePickerDialog.setThemeDark(true);
                datePickerDialog.show(getFragmentManager(), "tag");
            }
        });

        txv_selectDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txv_selectDateFrom.getText().equals(getString(R.string.tapHere))) {
                    //if user first click on txv_selectDateTo
                    Toast.makeText(MainActivity.this, getString(R.string.errorSelectStartDateFirst_main), Toast.LENGTH_SHORT).show();
                    return;
                }
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        //if user select past date
                        if (year < startDateYear) {
                            Toast.makeText(MainActivity.this, getString(R.string.errorSelectPastDate_main), Toast.LENGTH_SHORT).show();
                            return;
                        } else if (year == startDateYear)
                            if (monthOfYear < startDateMonth) {
                                Toast.makeText(MainActivity.this, getString(R.string.errorSelectPastDate_main), Toast.LENGTH_SHORT).show();
                                return;
                            } else if (monthOfYear == startDateMonth)
                                if (dayOfMonth < startDateDay) {
                                    Toast.makeText(MainActivity.this, getString(R.string.errorSelectPastDate_main), Toast.LENGTH_SHORT).show();
                                    return;
                                }


                        String month;
                        String day;
                        if (monthOfYear < 9)
                            month = "0" + (monthOfYear + 1);
                        else
                            month = String.valueOf(monthOfYear + 1);


                        if (dayOfMonth <= 9)
                            day = "0" + dayOfMonth;
                        else
                            day = String.valueOf(dayOfMonth);

                        String date = year + "/" + month + "/" + day;
                        txv_selectDateTo.setText(date);
                    }
                }, calendar.getPersianYear(), calendar.getPersianMonth(), calendar.getPersianDay());
                datePickerDialog.setThemeDark(true);
                datePickerDialog.show(getFragmentManager(), "tag1");
            }
        });


    }

    private void initDoneChart() {
        chartDone.setDrawBarShadow(false);
        chartDone.setDrawValueAboveBar(true);
        chartDone.getDescription().setEnabled(false);
        chartDone.setDrawGridBackground(false);
        chartDone.getLegend().setEnabled(false);
        chartDone.animateXY(1000, 1000);

        // scaling can now only be done on x- and y-axis separately
        //todo check this in real device
        chartDone.setPinchZoom(true);


        XAxis xAxis = chartDone.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(jobTitleList.size());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(jobTitleList));


        YAxis leftAxis = chartDone.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = chartDone.getAxisRight();
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);


        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < jobTitleList.size(); i++) {
            values.add(new BarEntry(i, (float) doneJobHourList.get(i) + doneJobMinuteList.get(i)));
        }


        if (chartDone.getData() != null && chartDone.getData().getDataSetCount() > 0) {
            Log.i(TAG, "initDoneChart: ");
            chartDone.invalidate();
            chartDone.clear();
        }

        BarDataSet barDataSet = new BarDataSet(values, "");
        barDataSet.setColor(ContextCompat.getColor(MainActivity.this, R.color.bgHeader_mainWidget));

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet);


        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);

        chartDone.setData(data);


    }

    private void settingFirstRun() {
        if (Prefs.read(Prefs.IS_FIRST_RUN, true)) {
            //app setting if it is first run
            ((TextView) findViewById(R.id.txv_descriptionSelectDate_main)).setText(getString(R.string.descriptionFirstRun_main));
            ((TextView) findViewById(R.id.txv_descriptionSelectDate_main)).setTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_red_light));
            Prefs.write(Prefs.IS_FIRST_RUN, false);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //if user click on items, this method will be call
        //handle click
        switch (item.getItemId()) {
            case R.id.itemMenu_help_menuMain:
                //todo start activity help
                return true;

            case R.id.itemMenu_aboutUs_menuMain:
                //todo start activity about us
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAndCloseContainer() {
        final View v = cdv_containerSelectDate;

        //check container is open or not
        if (isOpenContainer) {
            //change height of container
            ValueAnimator va = ValueAnimator.ofInt((int) MAX_HEIGHT_CONTAINER_SELECT_DATE, (int) MIN_HEIGHT_CONTAINER_SELECT_DATE);
            va.setDuration(400);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    v.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                    v.requestLayout();
                }
            });
            va.start();
            //show views
            cdv_containerSelectDateClosed.setVisibility(View.VISIBLE);
            txv_openContainerSelectDate.setVisibility(View.VISIBLE);
            //start animation (fade views)
            cdv_containerSelectDateClosed.startAnimation(anim_closeContainer);
        } else {
            ValueAnimator va = ValueAnimator.ofInt((int) MIN_HEIGHT_CONTAINER_SELECT_DATE, (int) MAX_HEIGHT_CONTAINER_SELECT_DATE);
            va.setDuration(400);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    v.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                    v.requestLayout();
                }
            });
            va.start();
            //start animation (show faded views)
            cdv_containerSelectDateClosed.startAnimation(anim_openContainer);
        }
        isOpenContainer = !isOpenContainer;
    }


    //convert db to pixel
    private float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private void initAnimationContainer() {
        anim_closeContainer = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_open_container_select_date);
        anim_openContainer = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_close_container_select_date);

        anim_openContainer.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //gone view
                cdv_containerSelectDateClosed.setVisibility(View.GONE);
                txv_openContainerSelectDate.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        anim_closeContainer.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //refresh view
                txv_selectDateFrom.setText(getString(R.string.tapHere));
                txv_selectDateTo.setText(getString(R.string.tapHere));
                startDateDay = 0;
                startDateMonth = 0;
                startDateYear = 0;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}
