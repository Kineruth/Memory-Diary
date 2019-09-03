package com.memoryDiary.Activity.Start;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.memoryDiary.R;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.List;


public class PhoneResignationActivity extends AppCompatActivity implements Validator.ValidationListener {
    @NotEmpty
    private EditText edUserName;
    @NotEmpty
    @Pattern(regex = "^\\d{10}$")
    private EditText edPhoneNumber;
    private FloatingActionButton fabDone; //id: phone_resig_fabDone
    private String phoneNumber, userName;

    private FirebaseAuth fbAuth;
    private DatabaseReference fbData;
    private Validator validator;
    private static boolean valIsDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_resignation);
        initFields();
        initValidator();
        initFireBase();
    }

    /**
     * Initialization the connection of the fields in xml file to their activities.
     */
    private void initFields(){
        this.edUserName = findViewById(R.id.userName);
        this.edPhoneNumber = findViewById(R.id.phoneNumber);
        this.fabDone = findViewById(R.id.phone_resig_fabDone);

        this.fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnDone();
            }
        });
    }

    /**
     * Gets fireBase instances & references.
     */
    private void initFireBase(){
        this.fbAuth = FirebaseAuth.getInstance();
        this.fbData = FirebaseDatabase.getInstance().getReference();
    }

    private void initValidator(){
        this.validator = new Validator(this);
        this.validator.setValidationListener(this);
    }


    private void clickOnDone(){
        this.validator.validate();
        if(this.valIsDone){
            this.phoneNumber = this.edPhoneNumber.getText().toString().trim();
//            this.phoneNumber = this.edPhoneNumber.getText().toString().trim().substring(1,10);
            this.userName = this.edUserName.getText().toString();
//            if(fbAuth.getCurrentUser() != null)
            verifyCodeActivity();
        }
        else{
            Toast.makeText(this, "Enter A Valid Phone Number", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Validation succeeded.
     */
    @Override
    public void onValidationSucceeded() {
        this.valIsDone = true;
    }

    /**
     * Validation failed - sends errors messages.
     * @param errors all the errors to be shown.
     */
    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        this.valIsDone = false;
        for(ValidationError error: errors){
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if(view instanceof EditText){
                ((EditText)view).setError(message);
            }
            else{
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void verifyCodeActivity() {
        Intent intent = new Intent(this,VerifyCodeActivity.class);
        Bundle extras = new Bundle();
        extras.putString("edUserName",this.userName);
        extras.putString("phoneNumber",this.phoneNumber);
        intent.putExtras(extras);
        finish();
        startActivity(intent);
    }
}
