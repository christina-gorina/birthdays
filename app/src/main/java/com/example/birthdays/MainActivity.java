package com.example.birthdays;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.lang.reflect.Array;

public class MainActivity extends AppCompatActivity {
    ViewPager pager;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private int[] tabIcons = {
            R.drawable.ic_cake_black_24dp,
            R.drawable.ic_people_black_24dp
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pager = findViewById(R.id.pager);
        MainFragmentPagerAdapter adapter = new MainFragmentPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BirthdaysFragment(), "ДР");
        adapter.addFragment(new ImportantDatesFragment(), "Важные даты");
        pager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);


    }

    public void onNotificationButtonClick(View button) {
        Toast.makeText(getApplicationContext(), Boolean.toString(((ToggleButton) button).isChecked()), Toast.LENGTH_SHORT).show();
    }

    public void onCongratulateButtonClick(View view) {
        Toast.makeText(getApplicationContext(), "Congratulate", Toast.LENGTH_SHORT).show();
    }

    public void addImportantDate(View view) {
        Intent intent = new Intent(this, AddImportantDatePage.class);
        startActivity(intent);
    }


}











