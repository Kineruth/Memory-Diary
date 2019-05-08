package com.memoryDiary.Activity.Main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.memoryDiary.Adapter.MainPagerAdapter;
import com.memoryDiary.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int colorBlue = ContextCompat.getColor(this, R.color.lightBlue);
//        final int colorTurqiz = ContextCompat.getColor(this, R.color.babyBlue);

        final View background = findViewById(R.id.app_main_view_background);
        ViewPager vPager = (ViewPager)findViewById(R.id.app_main_view_pager);
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());
        vPager.setAdapter(adapter);

        vPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                if(i == 0){ //sliding right
                    background.setBackgroundColor(colorBlue);
                    background.setAlpha(v);
                }
//                else if(i == 1){ //sliding left
//                    background.setBackgroundColor(colorTurqiz);
//                    background.setAlpha(1-v);
//                }
            }

            @Override
            public void onPageSelected(int i) {}

            @Override
            public void onPageScrollStateChanged(int i) {}
        });
    }
}
