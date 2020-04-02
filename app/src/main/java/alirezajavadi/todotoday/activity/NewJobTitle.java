package alirezajavadi.todotoday.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import alirezajavadi.todotoday.DataBase;
import alirezajavadi.todotoday.R;

public class NewJobTitle extends AppCompatActivity {
    private TextView txv_addNewJobTitle;
    private EditText edt_newJobTitle;

    private List<String> jobTitleList;

    private DataBase dataBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_job_title);
        init();

        //get all job titles to check that new jobTitle (user will enter) is not a duplicate
        jobTitleList=dataBase.getAllJobTitle();

        //add to database and show Toast
        txv_addNewJobTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newJobTitle = edt_newJobTitle.getText().toString();
                //if edt_jobTitle is empty
                if (newJobTitle.length() == 0) {
                    Toast.makeText(NewJobTitle.this, ":|", Toast.LENGTH_SHORT).show();
                    return;
                }
                //if the "new job title" is entered, already in the database
                for (int i = 0; i < jobTitleList.size(); i++)
                    if (jobTitleList.get(i).equals(newJobTitle)) {
                        Toast.makeText(NewJobTitle.this, getString(R.string.errorInsertedNewJobTitle_menu), Toast.LENGTH_SHORT).show();
                        return;
                    }

                //add new jobTitle to database
                int result = dataBase.addNewJobTitle(edt_newJobTitle.getText().toString());
                if (result == 1) {
                    Toast.makeText(NewJobTitle.this, getString(R.string.toastAddSuccess_menu), Toast.LENGTH_SHORT).show();
                    //add new jobTitle in jobTitleList (it will show in spinner)
                    jobTitleList.add(jobTitleList.size(), edt_newJobTitle.getText().toString());
                } else
                    Toast.makeText(NewJobTitle.this, getString(R.string.toastAddUnSuccess_menu), Toast.LENGTH_SHORT).show();


                //close the current activity if user click on txv_addNewJobTitle (return back to menu)
                onBackPressed();
            }
        });


    }

    private void init() {
        txv_addNewJobTitle = findViewById(R.id.txv_addNewJobTitle_newJobTitle);
        edt_newJobTitle = findViewById(R.id.edt_newJobTitle_newJobTitle);

        dataBase = new DataBase(NewJobTitle.this);
    }
}
