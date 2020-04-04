package alirezajavadi.todotoday.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import alirezajavadi.todotoday.DataBase;
import alirezajavadi.todotoday.R;

public class NewTaskTitle extends AppCompatActivity {
    private TextView txv_addNewTaskTitle;
    private EditText edt_newTaskTitle;

    private List<String> TaskTitleList;

    private DataBase dataBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task_title);
        init();

        //get all Task titles to check that new TaskTitle (user will enter) is not a duplicate
        TaskTitleList =dataBase.getAllTaskTitles();

        //add to database and show Toast
        txv_addNewTaskTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTaskTitle = edt_newTaskTitle.getText().toString();
                //if edt_taskTitle is empty
                if (newTaskTitle.length() == 0) {
                    Toast.makeText(NewTaskTitle.this, ":|", Toast.LENGTH_SHORT).show();
                    return;
                }
                //if the "new task title" is entered, already in the database
                for (int i = 0; i < TaskTitleList.size(); i++)
                    if (TaskTitleList.get(i).equals(newTaskTitle)) {
                        Toast.makeText(NewTaskTitle.this, getString(R.string.errorExistTitle_newTaskTitle), Toast.LENGTH_SHORT).show();
                        return;
                    }

                //add new taskTitle to database
                int result = dataBase.addNewTaskTitle(edt_newTaskTitle.getText().toString());
                if (result == 1) {
                    Toast.makeText(NewTaskTitle.this, getString(R.string.toastAddSuccess), Toast.LENGTH_SHORT).show();
                    //add new taskTitle in taskTitleList (it will show in spinner)
                    TaskTitleList.add(TaskTitleList.size(), edt_newTaskTitle.getText().toString());
                } else
                    Toast.makeText(NewTaskTitle.this, getString(R.string.toastAddUnSuccess), Toast.LENGTH_SHORT).show();


                //close the current activity if user click on txv_addNewTaskTitle (return back to menu)
                onBackPressed();
            }
        });


    }

    private void init() {
        txv_addNewTaskTitle = findViewById(R.id.txv_addNewTaskTitle_newTaskTitle);
        edt_newTaskTitle = findViewById(R.id.edt_newTaskTitle_newTaskTitle);

        dataBase = new DataBase(NewTaskTitle.this);
    }
}
