package alirezajavadi.todotoday.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

import alirezajavadi.todotoday.CurrentDate;
import alirezajavadi.todotoday.DataBase;
import alirezajavadi.todotoday.Prefs;
import alirezajavadi.todotoday.R;
import alirezajavadi.todotoday.model.Todo;
import alirezajavadi.todotoday.model.TodoChart;

public class ChartsActivity extends AppCompatActivity {
    private static final String TAG = "ChartsActivity";
    private TextView txv_showDialogSelectDate;
    private TextView txv_detailChartsDate;
    private BarChart chart_doneTask;
    private BarChart chart_undoneTask;
    private DataBase dataBase;

    private List<Todo> taskList;
    //    private List<TodoChart> doneTaskList_todoChart;
//    private List<TodoChart> undoneTaskList_todoChart;
    private List<String> taskTitleList;
//    private List<Float> doneTaskHourList;
//    private List<Float> doneTaskMinuteList;
//    private List<Float> undoneTaskHourList;
//    private List<Float> undoneTaskMinuteList;

    //new
    private List<TodoChart> finalTaskDataDone;
    private List<TodoChart> finalTaskDataUndone;

    //dialog select date
    private Dialog dialog_selectDate;
    private View view_rootViewSelectDate;
    private TextView txv_selectDateFrom;
    private TextView txv_selectDateTo;
    private TextView txv_okSelectDate;
    int startDateYear = 0;
    int startDateMonth = 0;
    int startDateDay = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);
        init();

        //handle charts with "defaultStartDate(startDate)" and "todayDate(endDate)"
        //if user dose not add a default date, firstRun's date is used
        String firstRunDate = Prefs.read(Prefs.FIRST_RUN_DATE, CurrentDate.getCurrentDate());
        String startFrom = Prefs.read(Prefs.DEFAULT_START_DATE_CHARTS, firstRunDate);
        String endTo = CurrentDate.getCurrentDate();
        txv_detailChartsDate.setText(getString(R.string.detailChartsDate_charts, startFrom, endTo));
        getTasksData(startFrom, endTo);
        initDoneChart();
        initUndoneChart();


        //handle everything about selectData dialog (like onClick, getAllData from database, dismissDialog and ...)
        initDialogSelectDate();

        //show dialogSelectDate
        txv_showDialogSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_selectDate.show();
            }
        });
    }


    private void init() {
        Prefs.initial(ChartsActivity.this);
        CurrentDate.initial();
        dataBase = new DataBase(ChartsActivity.this);
        txv_detailChartsDate = findViewById(R.id.txv_detailChartsDate_charts);
        txv_showDialogSelectDate = findViewById(R.id.txv_showDialogSelectDate_charts);
        chart_doneTask = findViewById(R.id.barChart_doneTask_charts);
        chart_undoneTask = findViewById(R.id.barChart_undoneTask_charts);

        //new
        finalTaskDataDone = new ArrayList<>();
        finalTaskDataUndone = new ArrayList<>();
        taskTitleList = new ArrayList<>();
    }

    //handle everything about selectData dialog
    // (like onClick, getAllData from database, initial chart with new data and ...)
    private void initDialogSelectDate() {
        //inflate view_dialog_select_date_charts as rootView dialog selectDate
        view_rootViewSelectDate = getLayoutInflater().inflate(R.layout.view_dialog_select_date_charts, null, false);
        txv_selectDateFrom = view_rootViewSelectDate.findViewById(R.id.txv_selectDateFrom_dialogSelectDate);
        txv_selectDateTo = view_rootViewSelectDate.findViewById(R.id.txv_selectDateTo_dialogSelectDate);
        txv_okSelectDate = view_rootViewSelectDate.findViewById(R.id.txv_okSelectedDate_dialogSelectDate);

        //dialogs settings
        dialog_selectDate = new Dialog(ChartsActivity.this);
        dialog_selectDate.setContentView(view_rootViewSelectDate);
        if (dialog_selectDate.getWindow() != null)
            dialog_selectDate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_selectDate.setCancelable(true);
        dialog_selectDate.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                txv_selectDateFrom.setText(getString(R.string.tapHere));
                txv_selectDateTo.setText(getString(R.string.tapHere));
            }
        });

        //
        txv_selectDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show datePicker to select date startFrom
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        //to check the date in txv_selectDateTo
                        startDateYear = year;
                        startDateDay = dayOfMonth;
                        startDateMonth = monthOfYear;

                        //convert "1" to "01" , "2" to "02" , ... , "9" to "09"
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

                        //
                        txv_selectDateFrom.setText(date);
                    }
                }, CurrentDate.getYear(), CurrentDate.getMonth(), CurrentDate.getDay());
                datePickerDialog.setThemeDark(true);
                datePickerDialog.show(getFragmentManager(), "tag");
            }
        });


        //
        txv_selectDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txv_selectDateFrom.getText().equals(getString(R.string.tapHere))) {
                    //if user's first click is txv_selectDateTo
                    Toast.makeText(ChartsActivity.this, getString(R.string.errorSelectStartDateFirst_charts), Toast.LENGTH_SHORT).show();
                    return;
                }

                //show datePicker to select date endTo
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        //checks that date (selected data in txv_selectDateFrom) is not past
                        if (year < startDateYear) {
                            Toast.makeText(ChartsActivity.this, getString(R.string.errorSelectPastDate_charts), Toast.LENGTH_SHORT).show();
                            return;
                        } else if (year == startDateYear)
                            if (monthOfYear < startDateMonth) {
                                Toast.makeText(ChartsActivity.this, getString(R.string.errorSelectPastDate_charts), Toast.LENGTH_SHORT).show();
                                return;
                            } else if (monthOfYear == startDateMonth)
                                if (dayOfMonth < startDateDay) {
                                    Toast.makeText(ChartsActivity.this, getString(R.string.errorSelectPastDate_charts), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                        //convert "1" to "01" , "2" to "02" , ... , "9" to "09"
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
                        //
                        txv_selectDateTo.setText(date);
                    }
                }, CurrentDate.getYear(), CurrentDate.getMonth(), CurrentDate.getDay());
                datePickerDialog.setThemeDark(true);
                datePickerDialog.show(getFragmentManager(), "tag1");
            }
        });

        //onclick txv_okSelectDate and initial charts with new data
        txv_okSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //has user selected the dates?
                if (txv_selectDateTo.getText().equals(getString(R.string.tapHere)) || txv_selectDateFrom.getText().equals(getString(R.string.tapHere))) {
                    Toast.makeText(ChartsActivity.this, getString(R.string.errorFirstSelectDate_charts), Toast.LENGTH_SHORT).show();
                    return;
                }

                //get new date between "startDate" and "endDate"
                getTasksData(txv_selectDateFrom.getText().toString(), txv_selectDateTo.getText().toString());

                //initial charts with new data
                initDoneChart();
                initUndoneChart();

                //update detailSelectDate
                txv_detailChartsDate.setText(getString(R.string.detailChartsDate_charts, txv_selectDateFrom.getText(), txv_selectDateTo.getText()));

                //
                dialog_selectDate.dismiss();
            }
        });


    }

    //get data from database to show in the charts
    private void getTasksData(String startFrom, String endTo) {
        //
        taskList = dataBase.getAllData(startFrom, endTo);
        //
        float hourStartFrom;
        float hourEndTo;
        float hour;
        float finalHour;

        float minuteStartFrom;
        float minuteEndTo;
        float minute;
        float finalMinute;

        String[] stringsStartFrom;
        String[] stringsEndTo;

        finalTaskDataUndone.clear();
        finalTaskDataDone.clear();

        //search in taskList and separate it based on the titles
        for (Todo task : taskList) {
            TodoChart todoChart = new TodoChart();

            //
            stringsStartFrom = task.getStartFrom().split(":"); //convert "10:34" to {"10","34"}
            stringsEndTo = task.getEndTo().split(":"); //convert "10:34" to {"10","34"}

            //calculate hour and minute
            hourStartFrom = Float.parseFloat(stringsStartFrom[0]);//convert string to float (hourStart)
            hourEndTo = Float.parseFloat(stringsEndTo[0]);//convert string to float

            minuteStartFrom = Float.parseFloat(stringsStartFrom[1]);
            minuteEndTo = Float.parseFloat(stringsEndTo[1]);

            if (minuteEndTo >= minuteStartFrom)
                hour = hourEndTo - hourStartFrom;
            else
                hour = hourEndTo - hourStartFrom - 1;

            if (minuteStartFrom > minuteEndTo)
                minute = (60 - minuteStartFrom) + minuteEndTo;
            else
                minute = (minuteStartFrom - minuteEndTo) * -1;

            //separate all date based on the isDone
            if (task.getIsDone() == 1) {
                int finalI = 0;
                boolean isAlreadyInList = false;//fuck this name
                for (int i = 0; i < finalTaskDataDone.size(); i++)
                    //if the title is already in the list
                    if (task.getTaskTitle().equals(finalTaskDataDone.get(i).getTaskTitle())) {
                        isAlreadyInList = true;
                        finalI = i;
                        break;
                    }

                if (isAlreadyInList) {
                    //calculate minute
                    minute += finalTaskDataDone.get(finalI).getMinute();

                    //if minute more than 60 ---> hour = minute / 60   and    minute = minute % 60
                    hour += Math.round(minute) / 60;
                    finalHour = finalTaskDataDone.get(finalI).getHour() + hour;
                    minute %= 60;
                    finalMinute = minute / 60;// that mean is: minute = minute * 1 / 60 (minute per natural number)

                    //set new time
                    finalTaskDataDone.get(finalI).setMinute(finalMinute);
                    finalTaskDataDone.get(finalI).setHour(finalHour);
                } else {
                    todoChart.setHour(hour);
                    todoChart.setMinute(minute);
                    todoChart.setTaskTitle(task.getTaskTitle());
                    finalTaskDataDone.add(todoChart);
                }

            } else {
                boolean isAlreadyInList = false;//too
                int finalI = 0;
                for (int i = 0; i < finalTaskDataUndone.size(); i++)
                    if (task.getTaskTitle().equals(finalTaskDataUndone.get(i).getTaskTitle())) {
                        isAlreadyInList = true;
                        finalI = i;
                        break;
                    }


                if (isAlreadyInList) {
                    //calculate minute
                    minute += finalTaskDataUndone.get(finalI).getMinute();

                    //if minute more than 60 ---> hour = minute / 60   and    minute = minute % 60
                    hour += Math.round(minute) / 60;
                    finalHour = finalTaskDataUndone.get(finalI).getHour() + hour;
                    minute %= 60;
                    finalMinute = minute / 60;// that mean is: minute = minute * 1 / 60 (minute per natural number)

                    //set new time
                    finalTaskDataUndone.get(finalI).setMinute(finalMinute);
                    finalTaskDataUndone.get(finalI).setHour(finalHour);
                } else {
                    todoChart.setHour(hour);
                    todoChart.setMinute(minute);
                    todoChart.setTaskTitle(task.getTaskTitle());
                    finalTaskDataUndone.add(todoChart);
                }


            }

        }

    }

    private void initUndoneChart() {
        chart_undoneTask.setDrawBarShadow(false);
        chart_undoneTask.setDrawValueAboveBar(true);
        chart_undoneTask.getDescription().setEnabled(false);
        chart_undoneTask.setDrawGridBackground(false);
        chart_undoneTask.getLegend().setEnabled(false);
        chart_undoneTask.animateXY(1000, 1000);

        // scaling can now only be done on x- and y-axis separately
        ///todo check this in real device
        chart_undoneTask.setPinchZoom(true);

        taskTitleList.clear();
        for (TodoChart todoChart : finalTaskDataUndone)
            taskTitleList.add(todoChart.getTaskTitle());

        XAxis xAxis = chart_undoneTask.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(finalTaskDataUndone.size());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(taskTitleList));


        YAxis leftAxis = chart_undoneTask.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = chart_undoneTask.getAxisRight();
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);


        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < finalTaskDataUndone.size(); i++) {
            values.add(new BarEntry(i, finalTaskDataUndone.get(i).getHour() + finalTaskDataUndone.get(i).getMinute()));
        }


        if (chart_undoneTask.getData() != null && chart_undoneTask.getData().getDataSetCount() > 0) {
            chart_undoneTask.invalidate();
            chart_undoneTask.clear();
        }

        BarDataSet barDataSet = new BarDataSet(values, "");
        barDataSet.setColor(ContextCompat.getColor(ChartsActivity.this, R.color.bgHeader_mainWidget));


        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet);


        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);

        chart_undoneTask.setData(data);

    }

    private void initDoneChart() {
        chart_doneTask.setDrawBarShadow(false);
        chart_doneTask.setDrawValueAboveBar(true);
        chart_doneTask.getDescription().setEnabled(false);
        chart_doneTask.setDrawGridBackground(false);
        chart_doneTask.getLegend().setEnabled(false);
        chart_doneTask.animateXY(1000, 1000);

        // scaling can now only be done on x- and y-axis separately
        //todo check this in real device
        chart_doneTask.setPinchZoom(true);

        taskTitleList.clear();
        for (TodoChart todoChart : finalTaskDataDone)
            taskTitleList.add(todoChart.getTaskTitle());

        XAxis xAxis = chart_doneTask.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(finalTaskDataDone.size());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(taskTitleList));


        YAxis leftAxis = chart_doneTask.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = chart_doneTask.getAxisRight();
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);


        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < finalTaskDataDone.size(); i++) {
            values.add(new BarEntry(i, finalTaskDataDone.get(i).getHour() + finalTaskDataDone.get(i).getMinute()));
        }


        if (chart_doneTask.getData() != null && chart_doneTask.getData().getDataSetCount() > 0) {
            chart_doneTask.invalidate();
            chart_doneTask.clear();
        }

        BarDataSet barDataSet = new BarDataSet(values, "");
        barDataSet.setColor(ContextCompat.getColor(ChartsActivity.this, R.color.bgHeader_mainWidget));

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet);


        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);

        chart_doneTask.setData(data);


    }

}
