package alirezajavadi.todotoday.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import alirezajavadi.todotoday.DataExpandableListModel;
import alirezajavadi.todotoday.Prefs;
import alirezajavadi.todotoday.R;
import alirezajavadi.todotoday.adapter.ExpandableListViewAdapter;

public class HelpActivity extends AppCompatActivity {

    private ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;

    private TextView txv_ok;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set theme to activity
        Prefs.initial(getApplicationContext());
        if (Prefs.read(Prefs.THEME_IS_GRAY, true))
            this.setTheme(R.style.GrayTheme);
        else
            this.setTheme(R.style.DarkTheme);
        setContentView(R.layout.activity_help);
        init();

        //expandable setting
        HashMap<String, List<String>> expandableListDetail = DataExpandableListModel.getData(HelpActivity.this);
        List<String> expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        expandableListAdapter = new ExpandableListViewAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        //add main description help to header of expandableListView
        expandableListView.addHeaderView(getLayoutInflater().inflate(R.layout.view_header_expandable_list,null,false));


        txv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void init() {
        expandableListView = findViewById(R.id.exv_expandableHelp_help);
        txv_ok=findViewById(R.id.txv_ok_help);
    }
}
