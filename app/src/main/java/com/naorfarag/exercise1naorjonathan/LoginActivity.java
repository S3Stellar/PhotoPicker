package com.naorfarag.exercise1naorjonathan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button signUpButt;
    private Button loginButt;
    private EditText email;
    private EditText pass;
    private FirebaseUser currentUser;
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
        loginButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidInput()) {
                    mAuth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Intent intent = new Intent(ctx, DetailsActivity.class);
                            intent.putExtra("email", email.getText().toString());
                            intent.putExtra("byReg", false);
                            startActivity(intent);
                            loginButt.setEnabled(false);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ctx, "Wrong credentials", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

    }

    private boolean isValidInput() {
        return email.getText() != null && pass.getText() != null && !pass.getText().toString().isEmpty() && !email.getText().toString().isEmpty();
    }

    private void setSignUpListener() {
        signUpButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, RegActivity.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();

        // Go to details activity
        if (currentUser != null) {
            new Intent(this, DetailsActivity.class);
        }
    }
}
