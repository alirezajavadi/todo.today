package alirezajavadi.todotoday.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.List;

import alirezajavadi.todotoday.CurrentDate;
import alirezajavadi.todotoday.DataBase;
import alirezajavadi.todotoday.Prefs;
import alirezajavadi.todotoday.R;
import alirezajavadi.todotoday.model.Todo;
import alirezajavadi.todotoday.model.TodoChart;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

public class ChartsActivity extends AppCompatActivity {
    private static final String TAG = "ChartsActivity";
    private TextView txv_showDialogSelectDate;
    private TextView txv_detailChartsDate;

    private DataBase dataBase;

    private List<Todo> taskList;//all data

    private List<TodoChart> finalTaskDataDone;//all doneTasks data
    private List<TodoChart> finalTaskDataUndone;//all undoneTasks data

    //dialog select date
    private Dialog dialog_selectDate;
    private View view_rootViewSelectDate;
    private TextView txv_selectDateFrom;
    private TextView txv_selectDateTo;
    private TextView txv_okSelectDate;
    int startDateYear = 0;
    int startDateMonth = 0;
    int startDateDay = 0;


    //chart
    private ColumnChartData doneChartData;
    private ColumnChartView chartViewDone;
    private List<AxisValue> axisXValueListDone;
    private Axis axisXDoneChart;
    private List<Column> columnListDoneChart;

    private ColumnChartData undoneChartData;
    private ColumnChartView chartViewUndone;
    private List<AxisValue> axisXValueListUndone;
    private Axis axisXUndoneChart;
    private List<Column> columnListUndoneChart;


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
        int taskListSize=getTasksData(startFrom, endTo);

        if (taskListSize==0)
            showHideCharts(false);
        else {
            showHideCharts(true);
            //charts
            doneChart();
            undoneChart();
        }


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


        finalTaskDataDone = new ArrayList<>();
        finalTaskDataUndone = new ArrayList<>();

        //
        initCharts();
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
                int taskListSize=getTasksData(txv_selectDateFrom.getText().toString(), txv_selectDateTo.getText().toString());

                if (taskListSize==0)
                    showHideCharts(false);
                else {
                    showHideCharts(true);
                    //update charts with new data
                    doneChart();
                    undoneChart();
                }

                //update detailSelectDate
                txv_detailChartsDate.setText(getString(R.string.detailChartsDate_charts, txv_selectDateFrom.getText(), txv_selectDateTo.getText()));

                //
                dialog_selectDate.dismiss();
            }
        });


    }

    //get data from database to show in the charts
    private int getTasksData(String startFrom, String endTo) {
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

        //To check if the list is empty
        return taskList.size();
    }


    private void initCharts() {
        //axis Y setting
        Axis axisY = new Axis();
        axisY.setHasLines(true);
        axisY.setName(getString(R.string.descriptionYAxisChart_charts));
        axisY.setLineColor(ContextCompat.getColor(ChartsActivity.this, android.R.color.black));
        axisY.setTextColor(ContextCompat.getColor(ChartsActivity.this, R.color.textColorHigh));
        axisY.setTextSize(10);

        //axisX doneChart Setting
        axisXDoneChart = new Axis();
        axisXDoneChart.setName(getString(R.string.descriptionXAxisChart_charts));
        axisXDoneChart.setLineColor(ContextCompat.getColor(ChartsActivity.this, android.R.color.black));
        axisXDoneChart.setTextColor(ContextCompat.getColor(ChartsActivity.this, R.color.textColorHigh));
        axisXDoneChart.setHasTiltedLabels(false);
        axisXDoneChart.setAutoGenerated(false);
        axisXDoneChart.setTextSize(10);

        //doneChart init
        chartViewDone = findViewById(R.id.chart_doneTask_charts);
        chartViewDone.setZoomEnabled(true);
        chartViewDone.setZoomType(ZoomType.HORIZONTAL);
        doneChartData = new ColumnChartData();
        doneChartData.setAxisXBottom(axisXDoneChart);
        doneChartData.setAxisYLeft(axisY);
        columnListDoneChart = new ArrayList<>();

        axisXValueListDone = new ArrayList<>();

        //axisX undoneChart setting
        axisXUndoneChart = new Axis();
        axisXUndoneChart.setName(getString(R.string.descriptionXAxisChart_charts));
        axisXUndoneChart.setLineColor(ContextCompat.getColor(ChartsActivity.this, android.R.color.black));
        axisXUndoneChart.setTextColor(ContextCompat.getColor(ChartsActivity.this, R.color.textColorHigh));
        axisXUndoneChart.setHasTiltedLabels(false);
        axisXUndoneChart.setAutoGenerated(false);
        axisXUndoneChart.setTextSize(10);


        //undoneChart init
        chartViewUndone = findViewById(R.id.chart_undoneTask_charts);
        chartViewUndone.setZoomEnabled(true);
        chartViewUndone.setZoomType(ZoomType.HORIZONTAL);
        undoneChartData = new ColumnChartData();
        undoneChartData.setAxisYLeft(axisY);
        undoneChartData.setAxisXBottom(axisXUndoneChart);
        columnListUndoneChart=new ArrayList<>();

        axisXValueListUndone = new ArrayList<>();


    }

    private void doneChart() {
        //set label to axisX(task Title)
        axisXValueListDone.clear();
        for (int i = 0; i < finalTaskDataDone.size(); i++) {
            AxisValue axisValue = new AxisValue(i);
            axisValue.setLabel(finalTaskDataDone.get(i).getTaskTitle());
            axisXValueListDone.add(axisValue);
        }
        axisXDoneChart.setValues(axisXValueListDone);

        //set value , animation
        columnListDoneChart.clear();
        for (TodoChart todoChart : finalTaskDataDone) {
            List<SubcolumnValue> subColumnValueList = new ArrayList<>();
            SubcolumnValue subcolumnValue = new SubcolumnValue();
            subcolumnValue.setColor( ContextCompat.getColor(ChartsActivity.this, R.color.bgHeader_mainWidget));
            subcolumnValue.setTarget(todoChart.getHour() + todoChart.getMinute());
            subcolumnValue.setLabel(todoChart.getHour() + todoChart.getMinute()+"");
            subColumnValueList.add(subcolumnValue);

            Column column = new Column(subColumnValueList);
            column.setHasLabels(true);
            column.setHasLabelsOnlyForSelected(false);
            columnListDoneChart.add(column);
        }
        //
        doneChartData.setColumns(columnListDoneChart);

        //
        chartViewDone.startDataAnimation(1000L);

        //
        chartViewDone.setColumnChartData(doneChartData);
    }

    private void undoneChart() {
        //set label to axisX(task Title)
        axisXValueListUndone.clear();
        for (int i = 0; i < finalTaskDataUndone.size(); i++) {
            AxisValue axisValue = new AxisValue(i);
            axisValue.setLabel(finalTaskDataUndone.get(i).getTaskTitle());
            axisXValueListUndone.add(axisValue);
        }
        axisXUndoneChart.setValues(axisXValueListUndone);


        //set value , animation
        columnListUndoneChart.clear();
        for (TodoChart todoChart : finalTaskDataUndone) {

            List<SubcolumnValue> subColumnValueList = new ArrayList<>();
            SubcolumnValue subcolumnValue = new SubcolumnValue();
            subcolumnValue.setColor( ContextCompat.getColor(ChartsActivity.this, R.color.bgHeader_mainWidget));
            subcolumnValue.setTarget(todoChart.getHour() + todoChart.getMinute());
            subcolumnValue.setLabel(todoChart.getHour() + todoChart.getMinute()+"");
            subColumnValueList.add(subcolumnValue);

            Column column = new Column(subColumnValueList);
            column.setHasLabels(true);
            column.setHasLabelsOnlyForSelected(false);
            columnListUndoneChart.add(column);
        }
        //
        undoneChartData.setColumns(columnListUndoneChart);

        //
        chartViewUndone.startDataAnimation(1000L);

        //
        chartViewUndone.setColumnChartData(undoneChartData);

    }


    //hide/show charts and show\hide textView "list is empty"
    //false for hide charts and true for show charts
    private void showHideCharts(boolean showIt) {
        if (showIt){
            findViewById(R.id.txv_descriptionDoneChart_charts).setVisibility(View.VISIBLE);
            findViewById(R.id.chart_doneTask_charts).setVisibility(View.VISIBLE);
            findViewById(R.id.view_B_charts).setVisibility(View.VISIBLE);
            findViewById(R.id.txv_descriptionUndoneChart_charts).setVisibility(View.VISIBLE);
            findViewById(R.id.chart_undoneTask_charts).setVisibility(View.VISIBLE);
            findViewById(R.id.txv_listIsEmpty_charts).setVisibility(View.GONE);
        }else {
            findViewById(R.id.txv_descriptionDoneChart_charts).setVisibility(View.GONE);
            findViewById(R.id.chart_doneTask_charts).setVisibility(View.GONE);
            findViewById(R.id.view_B_charts).setVisibility(View.GONE);
            findViewById(R.id.txv_descriptionUndoneChart_charts).setVisibility(View.GONE);
            findViewById(R.id.chart_undoneTask_charts).setVisibility(View.GONE);
            findViewById(R.id.txv_listIsEmpty_charts).setVisibility(View.VISIBLE);
        }

    }

}
