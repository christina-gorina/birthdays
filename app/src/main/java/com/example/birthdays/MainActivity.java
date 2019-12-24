package com.example.birthdays;

import android.content.Intent;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

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

    public void addImportantDate(View view) {
        Intent intent = new Intent(this, AddImportantDatePage.class);
        startActivity(intent);
    }
}











