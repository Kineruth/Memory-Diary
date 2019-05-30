package com.memoryDiary.Activity.Main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.widget.Toolbar;

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

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;
//    private final int CAMERA_REQUEST_CODE = 1;
//    public SurfaceHolder cameraSurfaceHolder;

    public MainActivity() {}


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager vpPager = findViewById(R.id.vpPager);

        //Returns the FragmentManager for interacting with fragments associated with this activity
        vpPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        initFields();
        initFireBase();
        initUser();
    }

    /**
     * Initialization the connection of the fields in xml file to their activities.
     */
    private void initFields(){
        mToolbar = this.findViewById(R.id.fragment_memory_toolbar);
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         *
         * @param position a specific fragment.
         * @return the fragment to display for a particular page.
         */
        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0: return CameraFragment.newInstance();
                case 1: return MemoryFragment.newInstance();
                default: return null;
            }
        }

        /**
         *
         * @return  total number of pages.
         */
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        /**
         * WILL BE DELETED IN THE FUTURE AS WE WILL PROGRESS
         * @param position
         * @return the page title for the top indicator.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch(position){
                case 0: return "Camera";
                case 1: return "Diary";
                default: return null;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode){
//            case CAMERA_REQUEST_CODE:{
//                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    cameraSurfaceHolder.addCallback(this);
//                    cameraSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//                }else{
//                    Toast.makeText(this, "Please provide permission to camera.", Toast.LENGTH_LONG).show();
//                }
//                break;
//            }
//
//        }
    }
}
