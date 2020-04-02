package alirezajavadi.todotoday.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.List;

import alirezajavadi.todotoday.CurrentDate;
import alirezajavadi.todotoday.DataBase;
import alirezajavadi.todotoday.Prefs;
import alirezajavadi.todotoday.R;
import alirezajavadi.todotoday.model.Todo;


public class NewJobTodo extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "NewJobTodo";

    private TextView txv_startFrom;
    private TextView txv_endTo;
    private TextView txv_addNewJobTodo;
    private Spinner spn_selectJobTitle;

    private DataBase dataBase;

    private int startFromM = -1;
    private int startFromH = -1;
    private boolean isTxvEndToClicked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_job_todo);
        init();

        //get jobTitles from database for spinner
        List<String> jobTitleList = dataBase.getAllJobTitle();
        jobTitleList.add(0, getString(R.string.selectJobTitle_menuNewJobTodo));//helps the user to choose the jobTitle

        //init spinnerAdapter with received jobTitles from database and set that to spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.view_spinner_text_view, jobTitleList);
        spinnerAdapter.setDropDownViewResource(R.layout.view_spinner_drop_down);
        spn_selectJobTitle.setAdapter(spinnerAdapter);

        //
        txv_startFrom.setOnClickListener(this);
        txv_endTo.setOnClickListener(this);
        txv_addNewJobTodo.setOnClickListener(this);


    }

    private void init() {
        CurrentDate.initial();
        dataBase = new DataBase(NewJobTodo.this);
        txv_startFrom = findViewById(R.id.txv_startFrom_newJobTodo);
        txv_endTo = findViewById(R.id.txv_endTo_newJobTodo);
        txv_addNewJobTodo = findViewById(R.id.txv_addNewJobTodo_newJobTodo);
        spn_selectJobTitle = findViewById(R.id.spn_selectJobTitle_newJobTodo);
        //get currentDay from sharedPrefs and set text to textView "message"
        ((TextView) findViewById(R.id.txv_message_newJobTodo)).setText(getString(R.string.messageNewJobTodo_menu, Prefs.read(Prefs.TODAY_DATE, CurrentDate.getCurrentDate())));

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.txv_startFrom_newJobTodo:
                clickTxvStartFrom();
                break;
            case R.id.txv_endTo_newJobTodo:
                clickTxvEndTo();
                break;
            case R.id.txv_addNewJobTodo_newJobTodo:
                clickAddTxvNewJobTodo();
                break;
        }
    }

    private void clickAddTxvNewJobTodo() {
        //is everything imported ?
        if (!isTxvEndToClicked || startFromH == -1 || startFromM == -1 || spn_selectJobTitle.getSelectedItem().toString().equals(getString(R.string.selectJobTitle_menuNewJobTodo))) {
            Toast.makeText(NewJobTodo.this, getString(R.string.errorEmptyCellsNewJobTodo_menu), Toast.LENGTH_SHORT).show();
            return;
        }

        //if everything is imported
        Todo todo = new Todo();
        todo.setIsDone(0);
        todo.setEndTo(txv_endTo.getText().toString());
        todo.setStartFrom(txv_startFrom.getText().toString());
        todo.setJobTitle(spn_selectJobTitle.getSelectedItem().toString());
        todo.setDate(Prefs.read(Prefs.TODAY_DATE, CurrentDate.getCurrentDate()));
        //add to database
        int result = dataBase.addNewJobTodo(todo);
        if (result == 1)
            Toast.makeText(NewJobTodo.this, getString(R.string.toastAddSuccess_menu), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(NewJobTodo.this, getString(R.string.toastAddUnSuccess_menu), Toast.LENGTH_SHORT).show();

        //close the current activity if user click on txv_addNewJobTodo (return back to menu)
        onBackPressed();

    }

    private void clickTxvEndTo() {
        //txv_startFrom is Clicked first orc not
        if (startFromH == -1 && startFromM == -1) {
            Toast.makeText(NewJobTodo.this, getString(R.string.errorSelectStartFromFirstNewJobTodo_menu), Toast.LENGTH_SHORT).show();
            return;
        }
        //show time picker
        TimePickerDialog timePickerDialog = new TimePickerDialog(NewJobTodo.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (hourOfDay == startFromH && minute <= startFromM || hourOfDay < startFromH) {
                    Toast.makeText(NewJobTodo.this, getString(R.string.errorSelectPassedTimeNewJobTodo_menu), Toast.LENGTH_SHORT).show();
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

    private void clickTxvStartFrom() {
        //show time Picker
        TimePickerDialog timePickerDialog = new TimePickerDialog(NewJobTodo.this, new TimePickerDialog.OnTimeSetListener() {
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
}
