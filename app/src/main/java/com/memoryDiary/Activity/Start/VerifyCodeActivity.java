package com.memoryDiary.Activity.Start;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.memoryDiary.Activity.Main.MainActivity;
import com.memoryDiary.Entity.User;
import com.memoryDiary.R;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class VerifyCodeActivity extends AppCompatActivity implements Validator.ValidationListener{

    @NotEmpty
    private EditText userCode;
    private Button btnSignIn;
    private String phoneNumber, userName, mVerificationId;

    private Validator validator;
    private static boolean valIsDone;
    private FirebaseAuth fbAuth;
    private DatabaseReference fbData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);
        //get parameters from PhoneResignationActivity
        Bundle extras = getIntent().getExtras();
        this.userName = extras.getString("userName");
        this.phoneNumber = extras.getString("phoneNumber");
        sendVerificationCode();

        initFields();
        initValidator();
        initFireBase();
    }

    private void initFields(){
        this.userCode = findViewById(R.id.user_code);
        this.btnSignIn = findViewById(R.id.btn_sign_in);

        //if the automatic sms detection did not work, user can also enter the code manually
        //so adding a click listener to the button
        this.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnBtn();
            }
        });
    }

    private void initValidator(){
        this.validator = new Validator(this);
        this.validator.setValidationListener(this);
    }

    /**
     * Gets fireBase instances & references.
     */
    private void initFireBase(){
        this.fbAuth = FirebaseAuth.getInstance();
        this.fbData = FirebaseDatabase.getInstance().getReference();
    }

    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();
            //sometime the code is not detected automatically
            //in this case the code will be null, so user has to manually enter it
            if (code != null) {
                userCode.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyCodeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };

    /**
     * the method is sending verification code to user
     */
    private void sendVerificationCode(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+972" + this.phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        this.fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyCodeActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful, save user & start main activity
                            final FirebaseUser fbUser = task.getResult().getUser();
                            final User user = new User(userName, fbUser.getUid(), "+972"+phoneNumber);
                            fbData.child("Users")
                                    .child(fbUser.getUid())
                                    .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(),"Succeeded Log-in",Toast.LENGTH_SHORT).show();
                                    mainActivity();
                                }
                            });
                        } else {
                            //verification unsuccessful.. display an error message
                            String message = "Somthing is wrong, we will fix it soon...";
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid Code..";
                            }
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }

    private void clickOnBtn(){
        this.validator.validate();
        if(valIsDone){
            String code = this.userCode.getText().toString().trim();
            verifyVerificationCode(code);
        }

    }



    @Override
    public void onValidationSucceeded() {
        valIsDone = true;
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        valIsDone = false;
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

    private void mainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}
