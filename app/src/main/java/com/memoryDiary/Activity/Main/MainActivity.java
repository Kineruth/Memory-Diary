package com.memoryDiary.Activity.Main;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.memoryDiary.Activity.Memory.AddMemoryActivity;
import com.memoryDiary.Entity.User;
import com.memoryDiary.Fragment.CameraFragment;
import com.memoryDiary.Fragment.MemoryFragment;
import com.memoryDiary.Fragment.TagsFragment;
import com.memoryDiary.Holder.UserDataHolder;
import com.memoryDiary.R;

import io.ktor.client.engine.android.Android;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

//    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    private Long lastTimestamp = (long)-1;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final int mID = 001;
    private NotificationCompat.Builder notification_builder;
    private NotificationManager mNotificationManager;
    private int photoCounter = 0;


    public MainActivity() {}


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager vpPager = findViewById(R.id.vpPager);
        //Returns the FragmentManager for interacting with fragments associated with this activity
        vpPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        sendNotification();
        initFields();
        initFireBase();
        initUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Initialization the connection of the fields in xml file to their activities.
     */
    private void initFields(){
//        mToolbar = this.findViewById(R.id.fragment_memory_toolbar);
//        setActionBar(mToolbar);
    }

    /**
     * Gets the firebase instances & references.
     */
    private void initFireBase(){
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }


    private void initUser() {
        UserDataHolder.getUserDataHolder().getUser().setUid(mAuth.getUid());
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;
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
                case 1: return MemoryFragment.Companion.newInstance();
                case 2: return TagsFragment.newInstance();
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
                case 2: return "Tags";
                default: return null;
            }
        }
    }

    private Long readLastDateFromMediaStore(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, "date_added DESC");
        //PhotoHolder media = null;
        Long dateAdded =(long)-1;
        if (cursor.moveToNext()) {
            dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED));
        }
        cursor.close();
        return dateAdded;
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                (Activity) context,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            showDialog("External storage", context, Manifest.permission.READ_EXTERNAL_STORAGE);
                        } else {
                            ActivityCompat
                                    .requestPermissions(
                                            (Activity) context,
                                            new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                        return false;
                    } else {
                        return true;
                    }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context, final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void sendNotification(){
        getContentResolver().registerContentObserver(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true,
                new ContentObserver(new Handler()) {
                    @Override
                    public void onChange(boolean selfChange) {
                        Log.d("MainActivity","External Media has been changed");
                        super.onChange(selfChange);
                        if (checkPermissionREAD_EXTERNAL_STORAGE(MainActivity.this)) {
                            Long timestamp = readLastDateFromMediaStore(MainActivity.this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                            // comapare with your stored last value
                            if (timestamp > lastTimestamp) {
                                Log.d("MainActivity","Need to create notification");
                                lastTimestamp = timestamp;
                                photoCounter++;

                                if(photoCounter == 15){
                                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "notify_001");
                                    Intent addMemoIntent = new Intent(getApplicationContext(), AddMemoryActivity.class);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, addMemoIntent, 0);


                                    mBuilder.setContentIntent(pendingIntent);
                                    mBuilder.setSmallIcon(R.drawable.ic_notifications_white_24dp);
                                    mBuilder.setContentTitle("Got something special today?");
                                    mBuilder.setContentText("Lets document it in your journal!");
                                    mBuilder.setPriority(Notification.PRIORITY_MAX);
                                    mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                                    mBuilder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE);
//                                    mBuilder.setDefaults(Notification.DEFAULT_ALL);
                                    mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                    // === Removed some obsoletes
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                        String channelId = "3000";
                                        NotificationChannel channel = new NotificationChannel(channelId,"Gallery Notification",NotificationManager.IMPORTANCE_DEFAULT);
                                        channel.enableLights(true);
                                        channel.setLightColor(Color.BLUE);
                                        mNotificationManager.createNotificationChannel(channel);
                                        mBuilder.setChannelId(channelId);
                                    }
                                    mNotificationManager.notify(0, mBuilder.build());

                                    photoCounter = 0;
                                }
                            }


                        }

                    }
                }
        );
    }
}
