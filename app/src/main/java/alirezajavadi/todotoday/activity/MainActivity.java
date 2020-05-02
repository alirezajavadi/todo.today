package alirezajavadi.todotoday.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import alirezajavadi.todotoday.CurrentDate;
import alirezajavadi.todotoday.Prefs;
import alirezajavadi.todotoday.R;
import alirezajavadi.todotoday.adapter.SliderViewPagerAdapter;

public class MainActivity extends AppCompatActivity {
    private TextView txv_next;
    private ViewPager vpg_containerSlider;
    private int[] pages;
    private View[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set theme to activity
        Prefs.initial(getApplicationContext());
        if (Prefs.read(Prefs.THEME_IS_GRAY, true))
            this.setTheme(R.style.GrayTheme);
        else
            this.setTheme(R.style.DarkTheme);
        setContentView(R.layout.activity_main);
        init();

        //settings for first run
        firstRun();

        //txv (next/ok) event
        txv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txv_next.getText().equals(getString(R.string.nextButton_main)))
                    //next
                    vpg_containerSlider.setCurrentItem(vpg_containerSlider.getCurrentItem() + 1, true);
                else
                    //ok
                    onBackPressed();
            }
        });

        //set adapter to viewPager
        vpg_containerSlider.setAdapter(new SliderViewPagerAdapter(MainActivity.this, pages));

    }

    private void init() {
        txv_next = findViewById(R.id.txv_next_main);
        vpg_containerSlider = findViewById(R.id.vpg_containerSlider_main);
        Prefs.initial(MainActivity.this);
        CurrentDate.initial();

        dots = new View[]{
                findViewById(R.id.view_dot1_main),
                findViewById(R.id.view_dot2_main),
                findViewById(R.id.view_dot3_main),
//                findViewById(R.id.view_dot4_main)
        };


    }

    private void firstRun() {
        if (Prefs.read(Prefs.IS_FIRST_RUN, true)) {

            //save today's date to sharedPrefs (display it by default in the charts)
            Prefs.write(Prefs.FIRST_RUN_DATE, CurrentDate.getCurrentDate());

            //
            pages = new int[]{
                    R.layout.view_intro_slider1,
                    R.layout.view_intro_slider2,
                    //   R.layout.view_intro_slider3,
                    R.layout.view_intro_slider4
            };


            //button text in first run
            txv_next.setText(getString(R.string.nextButton_main));

            vpg_containerSlider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    //when user changes the pages, the dots need to be changed
                    for (int i = 0; i < pages.length; i++) {
                        if (i == position)
                            dots[i].setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.shape_dot_dark));
                        else
                            dots[i].setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.shape_dot_light));
                    }

                    //when user changes the pages, the button text need to be changed
                    if (position == pages.length - 1)
                        txv_next.setText(getString(R.string.intimateOk));
                    else
                        txv_next.setText(getString(R.string.nextButton_main));

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            //next run, is not the first run :|
            Prefs.write(Prefs.IS_FIRST_RUN, false);
            return;
        }

        //if not the first run, just this layout will be show
        pages = new int[]{
                R.layout.view_intro_slider1,
        };

        //if not the first run, hide all dots
        for (View dot : dots)
            dot.setVisibility(View.GONE);
    }
}
