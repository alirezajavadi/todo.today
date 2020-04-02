package alirezajavadi.todotoday.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.util.List;

import alirezajavadi.todotoday.DataBase;
import alirezajavadi.todotoday.Prefs;
import alirezajavadi.todotoday.R;
import alirezajavadi.todotoday.model.Todo;
import alirezajavadi.todotoday.widget.MainWidgetAppWidgetProvider;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MenuActivity";


    //main
    private TextView txv_newDay;
    private TextView txv_newJobTodo;
    private TextView txv_newJobTitle;
    private TextView txv_help;
    private ImageView img_close;

    private DataBase dataBase;

    private PersianCalendar calendar;

    private int[] appWidgetIds;


    //newJobTitle dialog
    private View rootViewNewJobTitle;
    private EditText edt_jobTitle;
    private TextView txv_addNewJobTitle;

    private Dialog dialogNewJobTitle;

    //newJobTodo dialog
    private View rootViewNewJobTodo;
    private TextView txv_startFrom;
    private TextView txv_endTo;
    private TextView txv_addNewJobTodo;
    private Spinner spn_jobTitle;

    private List<String> jobTitleList;
    private ArrayAdapter<String> spinnerAdapter;

    private Dialog dialogNewJobTodo;

    private int startFromM;
    private int startFromH;
    private boolean isTxvEndToClicked;

    //newDay dialog
    private View rootViewNewDay;
    private TextView txv_addNewDay;
    private TextView txv_selectTodayDate;

    private Dialog dialogNewDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        init();


        //set onclick listener
        txv_help.setOnClickListener(this);
        txv_newDay.setOnClickListener(this);
        txv_newJobTitle.setOnClickListener(this);
        txv_newJobTodo.setOnClickListener(this);
        img_close.setOnClickListener(this);

        //get all appWidgetId
        ComponentName name = new ComponentName(MenuActivity.this, MainWidgetAppWidgetProvider.class);
        appWidgetIds = AppWidgetManager.getInstance(MenuActivity.this).getAppWidgetIds(name);


        //handel everything about newJobTitle
        newJobTitle();

        //handle everything about newJobTodo
        newJobTodo();

        //handle everything about newDay
        newDay();


    }

    private void init() {
        //main init
        txv_newDay = findViewById(R.id.txv_newDay_menu);
        txv_help = findViewById(R.id.txv_help_menu);
        txv_newJobTitle = findViewById(R.id.txv_newJobTitle_menu);
        txv_newJobTodo = findViewById(R.id.txv_newJobTodo_menu);
        img_close = findViewById(R.id.img_closeMenu_menu);

        dataBase = new DataBase(MenuActivity.this);
        Prefs.init(MenuActivity.this);
        calendar = new PersianCalendar();

        //newTitleJob init
        rootViewNewJobTitle = getLayoutInflater().inflate(R.layout.view_dialog_new_job_title, null, false);
        edt_jobTitle = rootViewNewJobTitle.findViewById(R.id.edt_newJobTitle_dialogNewJobTitle);
        txv_addNewJobTitle = rootViewNewJobTitle.findViewById(R.id.txv_addNewJobTitle_dialogNewJobTitle);

        dialogNewJobTitle = new Dialog(this);
        dialogNewJobTitle.setContentView(rootViewNewJobTitle);
        dialogNewJobTitle.setCancelable(true);

        //newJobTodo init
        rootViewNewJobTodo = getLayoutInflater().inflate(R.layout.view_dialog_new_job_todo, null, false);
        txv_startFrom = rootViewNewJobTodo.findViewById(R.id.txv_startFrom_dialogNewJobTodo);
        txv_endTo = rootViewNewJobTodo.findViewById(R.id.txv_endTo_dialogNewJobTodo);
        txv_addNewJobTodo = rootViewNewJobTodo.findViewById(R.id.txv_addNewJobTodo_dialogNewJobTodo);
        spn_jobTitle = rootViewNewJobTodo.findViewById(R.id.spn_selectJobTitle_dialogNewJobTodo);
        ((TextView) rootViewNewJobTodo.findViewById(R.id.txv_message_dialogNewJobTodo)).setText(getString(R.string.messageNewJobTodo_menu, Prefs.read(Prefs.TODAY_DATE, getCurrentShamsidate())));

        jobTitleList = dataBase.getAllJobTitle();
        jobTitleList.add(0, getString(R.string.selectJobTitle_menuNewJobTodo));

        spinnerAdapter = new ArrayAdapter<>(this, R.layout.view_spinner_text_view, jobTitleList);
        spinnerAdapter.setDropDownViewResource(R.layout.view_spinner_drop_down);
        spn_jobTitle.setAdapter(spinnerAdapter);

        dialogNewJobTodo = new Dialog(this);
        dialogNewJobTodo.setContentView(rootViewNewJobTodo);
        dialogNewJobTodo.setCancelable(true);

        //newDay init
        rootViewNewDay = getLayoutInflater().inflate(R.layout.view_dialog_new_day, null, false);
        txv_addNewDay = rootViewNewDay.findViewById(R.id.txv_addNewDay_dialogNewDay);
        txv_selectTodayDate = rootViewNewDay.findViewById(R.id.txv_selectTodayDate_dialogNewDay);

        dialogNewDay = new Dialog(MenuActivity.this);
        dialogNewDay.setContentView(rootViewNewDay);
        dialogNewDay.setCancelable(true);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txv_newDay_menu:
                dialogNewDay.show();
                break;

            case R.id.txv_help_menu:
                // TODO add activity help
                break;

            case R.id.txv_newJobTitle_menu:
                //show dialog for insert jobTitle
                dialogNewJobTitle.show();
                break;

            case R.id.txv_newJobTodo_menu:
                startFromM = -1;
                startFromH = -1;
                isTxvEndToClicked = false;
                //update textView txv_message_dialogNewJobTodo
                ((TextView) rootViewNewJobTodo.findViewById(R.id.txv_message_dialogNewJobTodo)).setText(getString(R.string.messageNewJobTodo_menu, Prefs.read(Prefs.TODAY_DATE, getCurrentShamsidate())));
                //show Dialog for insert newJobTodo
                dialogNewJobTodo.show();

                //if data changed in dialog newJobTitle
                spinnerAdapter.notifyDataSetChanged();
                break;

            case R.id.img_closeMenu_menu:
                onBackPressed();
                break;

        }
    }

    private void newJobTitle() {
        //if user return again to dialog
        dialogNewJobTitle.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                edt_jobTitle.setText("");
            }
        });

        //add to database and show Toast
        txv_addNewJobTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newJobTitle = edt_jobTitle.getText().toString();
                //if edt_jobTitle is empty
                if (newJobTitle.length() == 0) {
                    Toast.makeText(MenuActivity.this, ":|", Toast.LENGTH_SHORT).show();
                    return;
                }
                //if the "new job title" is entered, already in the database
                for (int i = 0; i < jobTitleList.size(); i++)
                    if (jobTitleList.get(i).equals(newJobTitle)) {
                        Toast.makeText(MenuActivity.this, getString(R.string.errorInsertedNewJobTitle_menu), Toast.LENGTH_SHORT).show();
                        return;
                    }

                //add new jobTitle to database
                int result = dataBase.addNewJobTitle(edt_jobTitle.getText().toString());
                if (result == 1) {
                    Toast.makeText(MenuActivity.this, getString(R.string.toastAddSuccess_menu), Toast.LENGTH_SHORT).show();
                    //add new jobTitle in jobTitleList (it will show in spinner)
                    jobTitleList.add(jobTitleList.size(), edt_jobTitle.getText().toString());
                } else
                    Toast.makeText(MenuActivity.this, getString(R.string.toastAddUnSuccess_menu), Toast.LENGTH_SHORT).show();
                dialogNewJobTitle.dismiss();
            }
        });

    }

    private void newJobTodo() {

        //if user return again to dialog
        dialogNewJobTodo.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                txv_startFrom.setText(getString(R.string.tapHere));
                txv_endTo.setText(getString(R.string.tapHere));
                spn_jobTitle.setSelection(0);
            }
        });


        //handle onClick txv_startFrom
        txv_startFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show time Picker
                TimePickerDialog timePickerDialog = new TimePickerDialog(MenuActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String minuteString = String.valueOf(minute);
                        if (minute < 10)
                            minuteString = "0" + minuteString;

                        String hourString = String.valueOf(hourOfDay);
                        if (hourOfDay < 10)
                            hourString = "0" + hourString;

                        txv_startFrom.setText(hourString + ":" + minuteString);
                        startFromH = hourOfDay;
                        startFromM = minute;

                    }
                }, 0, 0, true);
                timePickerDialog.show();
            }
        });


        //handle onclick txv_endTo
        txv_endTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check txv_startFrom Clicked first or not
                if (startFromH == -1 && startFromM == -1) {
                    Toast.makeText(MenuActivity.this, getString(R.string.errorSelectStartFromFirstNewJobTodo_menu), Toast.LENGTH_SHORT).show();
                    return;
                }
                //show time picker
                TimePickerDialog timePickerDialog = new TimePickerDialog(MenuActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay == startFromH && minute <= startFromM || hourOfDay < startFromH) {
                            Toast.makeText(MenuActivity.this, getString(R.string.errorSelectPassedTimeNewJobTodo_menu), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String minuteString = String.valueOf(minute);
                        if (minute < 10)
                            minuteString = "0" + minuteString;

                        String hourString = String.valueOf(hourOfDay);
                        if (hourOfDay < 10)
                            hourString = "0" + hourString;
                        txv_endTo.setText(hourString + ":" + minuteString);

                    }
                }, startFromH, startFromM, true);
                timePickerDialog.show();

                isTxvEndToClicked = true;

            }
        });

        //handle onClickListener txv_addNewJobTodo
        txv_addNewJobTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is everything imported ?
                if (!isTxvEndToClicked || startFromH == -1 || startFromM == -1 || spn_jobTitle.getSelectedItem().toString().equals(getString(R.string.selectJobTitle_menuNewJobTodo))) {
                    Toast.makeText(MenuActivity.this, getString(R.string.errorEmptyCellsNewJobTodo_menu), Toast.LENGTH_SHORT).show();
                    return;
                }

                //if everything is imported
                Todo todo = new Todo();
                todo.setIsDone(0);
                todo.setEndTo(txv_endTo.getText().toString());
                todo.setStartFrom(txv_startFrom.getText().toString());
                todo.setJobTitle(spn_jobTitle.getSelectedItem().toString());
                todo.setDate(Prefs.read(Prefs.TODAY_DATE, getCurrentShamsidate()));

                //add to dateBase
                int result = dataBase.addNewJobTodo(todo);
                if (result == 1)
                    Toast.makeText(MenuActivity.this, getString(R.string.toastAddSuccess_menu), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MenuActivity.this, getString(R.string.toastAddUnSuccess_menu), Toast.LENGTH_SHORT).show();

                dialogNewJobTodo.dismiss();


            }
        });


    }

    private void newDay() {

        final String lastDay = Prefs.read(Prefs.TODAY_DATE, getCurrentShamsidate());
        //save new day date in sharedPref and update DB
        txv_addNewDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = txv_selectTodayDate.getText().toString();
                //dose user select date or not
                if (date.equals(getString(R.string.tapHere))) {
                    Toast.makeText(MenuActivity.this, getString(R.string.errorSelectDateNewDay_menu), Toast.LENGTH_SHORT).show();
                    return;
                }

                //update DB
                Log.i(TAG, "onClick: " + lastDay);
                dataBase.updateForNewDay(lastDay);
                Toast.makeText(MenuActivity.this, getString(R.string.toastAddNewDaySuccessDialogNewDay_menu), Toast.LENGTH_SHORT).show();
                Prefs.write(Prefs.TODAY_DATE, date);

                dialogNewDay.dismiss();
            }
        });

        //if user return to dialog newDay
        dialogNewDay.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                txv_selectTodayDate.setText(getString(R.string.tapHere));
            }
        });

        //show datePicker to select new date
        txv_selectTodayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //show date picker
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
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

                        //save in sharedPref
                        Prefs.write(Prefs.TODAY_DATE, date);

                        txv_selectTodayDate.setText(date);

                    }
                }, calendar.getPersianYear(), calendar.getPersianMonth(), calendar.getPersianDay());
                datePickerDialog.setThemeDark(true);
                datePickerDialog.show(MenuActivity.this.getFragmentManager(), "datePickerDialog");


            }
        });

    }

    private String getCurrentShamsidate() {
        String month;
        String day;
        if ((calendar.getPersianMonth() + 1) < 9)
            month = "0" +(calendar.getPersianMonth() + 1);
        else
            month = String.valueOf((calendar.getPersianMonth() + 1));

        if (calendar.getPersianDay() <= 9)
            day = "0" + (calendar.getPersianDay());
        else
            day = String.valueOf(calendar.getPersianDay());


        return calendar.getPersianYear() + "/" + month + "/" + day;
    }

    @Override
    public void onBackPressed() {
        updateWidgets();
        super.onBackPressed();
    }

    @Override
    protected void onUserLeaveHint() {
        updateWidgets();
        super.onUserLeaveHint();
    }

    private void updateWidgets() {
        for (int appWidgetId : appWidgetIds) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(MenuActivity.this);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lsv_todoList_mainWidget);
        }
    }
}
