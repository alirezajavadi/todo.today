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


public class NewTaskTodo extends AppCompatActivity implements View.OnClickListener {

    private TextView txv_startFrom;
    private TextView txv_endTo;
    private TextView txv_addNewTaskTodo;
    private Spinner spn_selectTaskTitle;

    private DataBase dataBase;

    private int startFromM = -1;
    private int startFromH = -1;
    private boolean isTxvEndToClicked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task_todo);
        init();

        //get taskTitles from database for spinner
        List<String> taskTitleList = dataBase.getAllTaskTitles();
        taskTitleList.add(0, getString(R.string.selectTaskTitle_newTaskTodo));//helps the user to choose the taskTitle

        //init spinnerAdapter with received taskTitles from database and set that to spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.view_spinner_text_view, taskTitleList);
        spinnerAdapter.setDropDownViewResource(R.layout.view_spinner_drop_down);
        spn_selectTaskTitle.setAdapter(spinnerAdapter);

        //
        txv_startFrom.setOnClickListener(this);
        txv_endTo.setOnClickListener(this);
        txv_addNewTaskTodo.setOnClickListener(this);


    }

    private void init() {
        CurrentDate.initial();
        dataBase = new DataBase(NewTaskTodo.this);
        txv_startFrom = findViewById(R.id.txv_startFrom_newTaskTodo);
        txv_endTo = findViewById(R.id.txv_endTo_newTaskTodo);
        txv_addNewTaskTodo = findViewById(R.id.txv_addNewTaskTodo_newTaskTodo);
        spn_selectTaskTitle = findViewById(R.id.spn_selectTaskTitle_newTaskTodo);
        //get currentDay from sharedPrefs and set text to textView "message"
        ((TextView) findViewById(R.id.txv_message_newTaskTodo)).setText(getString(R.string.message_newTaskTodo, Prefs.read(Prefs.TODAY_DATE, CurrentDate.getCurrentDate())));

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.txv_startFrom_newTaskTodo:
                clickTxvStartFrom();
                break;
            case R.id.txv_endTo_newTaskTodo:
                clickTxvEndTo();
                break;
            case R.id.txv_addNewTaskTodo_newTaskTodo:
                clickAddTxvNewTaskTodo();
                break;
        }
    }

    private void clickAddTxvNewTaskTodo() {
        //is everything imported ?
        if (!isTxvEndToClicked || startFromH == -1 || startFromM == -1 || spn_selectTaskTitle.getSelectedItem().toString().equals(getString(R.string.selectTaskTitle_newTaskTodo))) {
            Toast.makeText(NewTaskTodo.this, getString(R.string.errorEmptyCells_newTaskTodo), Toast.LENGTH_SHORT).show();
            return;
        }

        //if everything is imported
        Todo todo = new Todo();
        todo.setIsDone(0);
        todo.setEndTo(txv_endTo.getText().toString());
        todo.setStartFrom(txv_startFrom.getText().toString());
        todo.setTaskTitle(spn_selectTaskTitle.getSelectedItem().toString());
        todo.setDate(Prefs.read(Prefs.TODAY_DATE, CurrentDate.getCurrentDate()));
        //add to database
        int result = dataBase.addNewTaskTodo(todo);
        if (result == 1)
            Toast.makeText(NewTaskTodo.this, getString(R.string.toastAddSuccess), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(NewTaskTodo.this, getString(R.string.toastAddUnSuccess), Toast.LENGTH_SHORT).show();

        //close the current activity if user click on txv_addNewTaskTodo (return back to menu)
        onBackPressed();

    }

    private void clickTxvEndTo() {
        //txv_startFrom is Clicked first orc not
        if (startFromH == -1 && startFromM == -1) {
            Toast.makeText(NewTaskTodo.this, getString(R.string.errorSelectStartFromFirst_newTaskTodo), Toast.LENGTH_SHORT).show();
            return;
        }
        //show time picker
        TimePickerDialog timePickerDialog = new TimePickerDialog(NewTaskTodo.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (hourOfDay == startFromH && minute <= startFromM || hourOfDay < startFromH) {
                    Toast.makeText(NewTaskTodo.this, getString(R.string.errorSelectPastTime_newTaskTodo), Toast.LENGTH_SHORT).show();
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(NewTaskTodo.this, new TimePickerDialog.OnTimeSetListener() {
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
