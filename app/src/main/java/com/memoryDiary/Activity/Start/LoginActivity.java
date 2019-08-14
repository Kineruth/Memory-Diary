package com.memoryDiary.Activity.Start;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.memoryDiary.Activity.Main.MainActivity;
import com.memoryDiary.R;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;


public class LoginActivity extends AppCompatActivity implements Validator.ValidationListener{
    @NotEmpty
    @Email()
    private EditText email;
    @NotEmpty()
    @Password(message = "Minimum 6 Characters")
    private EditText password;
    private Validator validator;
    private static boolean valIsDone;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        initFields();
        initValidator();
    }

    /**
     * Initialization the connection of the fields in xml file to their activities.
     */
    private void initFields(){
        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             * @param v the view that has been clicked.
             */
            @Override
            public void onClick(View v) {
                clickOnBtnLogin();
            }
        });
        findViewById(R.id.tvRegister).setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             * @param v the view that has been clicked.
             */
            @Override
            public void onClick(View v) {
                clickOntvRegister();
            }
        });
        findViewById(R.id.tvResetPassword).setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             * @param v the view that has been clicked.
             */
            @Override
            public void onClick(View v) {
                clickOntvResetPassword();
            }
        });
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
    }

    private void initValidator(){
        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    /**
     * When the button 'Login' was clicked.
     */
    private void clickOnBtnLogin() {
        validator.validate();
        if(valIsDone) {
            String mail = email.getText().toString();
            String pass = password.getText().toString();
            mAuth.signInWithEmailAndPassword(mail, pass)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            cameraFragment();
                        }
                    });
        }
    }

    private void cameraFragment() {
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    /**
     * When clicked on the 'Register' button.
     */
    private void clickOntvRegister() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        clearError();
    }

    /**
     * When clicked on the 'Reset Password' button.
     */
    private void clickOntvResetPassword() {
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        startActivity(intent);
        clearError();
    }

    /**
     * Sets the error message to null - so here is no error.
     */
    private void clearError() {
        email.setError(null);
        password.setError(null);
    }

    /**
     * Validation succeeded.
     */
    @Override
    public void onValidationSucceeded() {
        valIsDone = true;
    }
    /**
     * Validation failed - sends errors messages.
     * @param errors all the errors to be shown.
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
