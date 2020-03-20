package com.naorfarag.exercise1naorjonathan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private Button signUpButt;
    private Button loginButt;
    private EditText email;
    private EditText pass;
    private Context ctx;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ctx = this;

        email = findViewById(R.id.emailTextLogin);
        pass = findViewById(R.id.passTextLogin);
        signUpButt = findViewById(R.id.signButt);
        loginButt = findViewById(R.id.logButt);
        mAuth = FirebaseAuth.getInstance();

        setSignUpListener();
        setLoginListener();
    }

    private void setLoginListener() {
        loginButt.setOnClickListener(v -> {
            loginButt.setEnabled(false);
            boolean validEmail, validPassword;
            validEmail = Validation.isValidEmail(email.getText().toString());
            validPassword = Validation.isValidPassword(pass.getText().toString());
            if (validEmail && validPassword) {
                mAuth.signInWithEmailAndPassword(email.getText().toString().toLowerCase(), pass.getText().toString()).addOnSuccessListener(authResult -> {
                    Intent intent = new Intent(ctx, DetailsActivity.class);
                    intent.putExtra(getString(R.string.intent_email), email.getText().toString().toLowerCase());
                    intent.putExtra(getString(R.string.intent_byReg), false);
                    finish();
                    startActivity(intent);
                }).addOnFailureListener(e -> {
                    Toast.makeText(ctx, R.string.wrong_credentials, Toast.LENGTH_SHORT).show();
                    loginButt.setEnabled(true);
                });

            } else {
                if (!validEmail)
                    email.setError(getString(R.string.not_valid_email_format));
                if (!validPassword)
                    pass.setError(getString(R.string.not_valid_password_format));
                loginButt.setEnabled(true);
            }
        });

    }


    private void setSignUpListener() {
        signUpButt.setOnClickListener(v -> {
            signUpButt.setEnabled(false);
            startActivity(new Intent(this, RegActivity.class));
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        signUpButt.setEnabled(true);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Go to details activity
        if (currentUser != null) {
            Log.d("TAG", "onStart: Starting DetailsAct");
            finish();
            startActivity(new Intent(this, DetailsActivity.class).putExtra("email", currentUser.getEmail()).
                    putExtra(getString(R.string.intent_byReg), false));
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        signUpButt.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        signUpButt.setEnabled(true);
    }

    /**
     * Hide the keyboard with pressing on the screen
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (imm != null && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        return super.onTouchEvent(event);

    }
}
