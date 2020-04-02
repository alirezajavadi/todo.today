package alirezajavadi.todotoday.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;

import alirezajavadi.todotoday.DataBase;
import alirezajavadi.todotoday.Prefs;
import alirezajavadi.todotoday.R;
import alirezajavadi.todotoday.CurrentDate;

public class NewDayActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "NewDayActivity";

    private TextView txv_selectTodayDate;
    private TextView txv_addNewDay;

    private DataBase dataBase;

    private String lastDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_day);
        init();

        //get last day date
        lastDay = Prefs.read(Prefs.TODAY_DATE, CurrentDate.getCurrentDate());

        //
        txv_addNewDay.setOnClickListener(this);
        txv_selectTodayDate.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.txv_selectTodayDate_newDay:
                clickSelectNewDay();
                break;
            case R.id.txv_addNewDay_newDay:
                clickAddTxvNewDay();
                break;
        }
    }


    //handle txv_selectTodayDate_newDay click, show datePicker to select new date
    private void clickSelectNewDay() {


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
        }, CurrentDate.getYear(), CurrentDate.getMonth(), CurrentDate.getDay());
        datePickerDialog.setThemeDark(true);
        datePickerDialog.show(NewDayActivity.this.getFragmentManager(), "datePickerDialog");
    }


    //handle txv_addNewDays click ,update DB and sharedPrefs
    private void clickAddTxvNewDay() {
        String date = txv_selectTodayDate.getText().toString();
        //dose user select date or not
        if (date.equals(getString(R.string.tapHere))) {
            Toast.makeText(NewDayActivity.this, getString(R.string.errorSelectDateNewDay_menu), Toast.LENGTH_SHORT).show();
            return;
        }

        //update DB , sharedPrefs and show message
        dataBase.updateForNewDay(lastDay);
        Toast.makeText(NewDayActivity.this, getString(R.string.toastAddNewDaySuccessDialogNewDay_menu), Toast.LENGTH_SHORT).show();
        Prefs.write(Prefs.TODAY_DATE, date);

        //close the current activity if user click on txv_addNewDay_newDay (return back to menu)
        onBackPressed();
    }


    private void init() {
        CurrentDate.initial();
        Prefs.initial(NewDayActivity.this);


        txv_selectTodayDate = findViewById(R.id.txv_selectTodayDate_newDay);
        txv_addNewDay = findViewById(R.id.txv_addNewDay_newDay);

        dataBase = new DataBase(NewDayActivity.this);

    }
}
