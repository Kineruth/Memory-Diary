package com.memoryDiary.Activity.Start;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.memoryDiary.Entity.Diary;
import com.memoryDiary.Entity.User;
import com.memoryDiary.R;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.List;

public class RegistrationActivity extends AppCompatActivity implements Validator.ValidationListener{
    @NotEmpty()
    @Email()
    private EditText email;
    @NotEmpty()
    @Password(message = "Minimum 6 Characters")
    private EditText password;
    @NotEmpty
    @Pattern(message = "Input Must Contain Only Letters", regex = "[a-zA-Z][a-zA-Z ]+")
    private EditText fullname;
    private Validator validator;
    private static boolean valIsDone;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
        initFields();
        initValidator();
    }

    /**
     * Initialization the connection of the fields in xml file to their activities.
     */
    private void initFields(){
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        fullname = findViewById(R.id.etFullName);
        findViewById(R.id.btnRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnBtnRegister();
            }
        });
    }

    private void initValidator(){
        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    /**
     * When the button 'Register' was clicked.
     */
    private void clickOnBtnRegister() {
        validator.validate();
        if (valIsDone) {
            final String name = fullname.getText().toString();
            String mail = email.getText().toString();
            String pass = password.getText().toString();
            mAuth.createUserWithEmailAndPassword(mail, pass)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            final User user = new User(name, mAuth.getCurrentUser().getUid());
                            mData.child("Users")
                                    .child(mAuth.getCurrentUser().getUid())
                                    .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mData.child("Diary")
                                            .child(mAuth.getCurrentUser().getUid())
                                            .setValue(new Diary(user))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    startLoginActivity();
                                                }
                                            });
                                }
                            });
                        }
                    });
        }
    }

    /**
     * Connects to login activity.
     * An intent - basically a message to say you did or want something to happen.
     */
    private void startLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Called when all the 'Rules' added to this Validator are valid.
     */
    @Override
    public void onValidationSucceeded() {
        valIsDone = true;
    }

    /**
     * Called if any of the Rules fail.
     * @param errors the errors.
     */
    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        valIsDone = false;
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
}
