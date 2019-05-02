package com.memoryDiary.Activity.Start;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.memoryDiary.Fragment.CameraFragment;
import com.memoryDiary.R;

public class WelcomeActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    /**
     * In case the activity needs to be recreated - the saved state can be passed back to onCreate
     * so there is no lost of this prior information.
     * @param savedInstanceState a bundle of a saved state of the application.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initFields();
        initFireBase();
    }

    /**
     * Initialization the connection of the fields in xml file to their activities.
     */
    private void initFields(){
        findViewById(R.id.tapAnywhereToConBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnConBTN();
            }
        });
    }

    /**
     * Gets the firebase instances & references.
     */
    private void initFireBase(){
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    /**
     * When the button 'Agree' was clicked.
     */
    private void clickOnConBTN() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Makes the activity visible to the user,
     * as the app prepares for the activity to enter the foreground and become interactive.
     * onStart follows onCreate.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser != null) {
            startActivity(new Intent(WelcomeActivity.this, CameraFragment.class));
            finish();
        }
    }
}
