package com.memoryDiary.Activity.Main;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
//import android.support.v7.widget.Toolbar;
import android.widget.Toolbar;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.memoryDiary.Entity.User;
import com.memoryDiary.Fragment.CameraFragment;
import com.memoryDiary.Fragment.MemoryFragment;
import com.memoryDiary.Holder.UserDataHolder;
import com.memoryDiary.R;

public class MainActivity extends FragmentActivity {

    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;

    public MainActivity() {}


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager pager = (ViewPager)findViewById(R.id.app_view_pager);
        //Return the FragmentManager for interacting with fragments associated with this activity
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        initFields();
        initFireBase();
        initUser();
//        initFragments();
    }

    /**
     * Initialization the connection of the fields in xml file to their activities.
     */
    private void initFields(){
        mToolbar = this.findViewById(R.id.app_memory_toolbar);
        setActionBar(mToolbar);
    }

    /**
     * Gets the firebase instances & references.
     */
    private void initFireBase(){
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }


    private void initUser() {
        mData.child("Users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userTemp = dataSnapshot.getValue(User.class);
                UserDataHolder.getUserDataHolder().getUser().setAll(userTemp);
            }

            /**
             *  when an error occurs.
             * @param databaseError the errors.
             */
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void initFragments(){
//        LIGHT_BLUE = ContextCompat.getColor(this, R.color.lightBlue);
////        final int colorTurqiz = ContextCompat.getColor(this, R.color.babyBlue);
//        mRoot = (RelativeLayout) findViewById(R.id.app_view_pager);
//        mMemoryView = MemoryFragment.newInstance();
//
//        View left = findViewById(R.id.app_right_layout);
////        left.setTranslationX(-getScreenSize().x);
//
//        final View background = findViewById(R.id.app_main_view_background);
//        ViewPager vPager = (ViewPager)findViewById(R.id.app_main_view_pager);
//        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());
//        vPager.setAdapter(adapter);
//
//        vPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int i, float v, int i1) {
//                if(i == 0){ //sliding right
//                    background.setBackgroundColor(LIGHT_BLUE);
//                    background.setAlpha(v);
//                }
////                else if(i == 1){ //sliding left
////                    background.setBackgroundColor(colorTurqiz);
////                    background.setAlpha(1-v);
////                }
//            }
//
//            @Override
//            public void onPageSelected(int i) {}
//
//            @Override
//            public void onPageScrollStateChanged(int i) {}
//        });
    }

    //******************************INCLUDED**************************************************
    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos){
                case 0: return CameraFragment.newInstance();
                case 1: return MemoryFragment.newInstance();
                default: return CameraFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
