package com.naorfarag.exercise1naorjonathan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class RegActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference mStorageRef;


    private Uri selectedImageUri;
    private EditText email;
    private EditText pass;
    private EditText phone;
    private EditText age;
    private ImageView userImage;
    private Button confirmButt;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("images");

        email = findViewById(R.id.emailTextReg);
        pass = findViewById(R.id.passTextReg);
        phone = findViewById(R.id.phoneTextReg);
        age = findViewById(R.id.ageTextReg);
        userImage = findViewById(R.id.userImageReg);
        confirmButt = findViewById(R.id.confirm);

        confirmListener();
        imageChooseListener();
    }

    private void confirmListener() {
        confirmButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validEmail, validPass, validPhone, validAge;
                String emailText = email.getText().toString();
                String password = pass.getText().toString();
                String phoneText = phone.getText().toString();
                int ageText = 0;

                if (!(validEmail = Validation.isValidEmail(emailText))) {
                    email.setError(getString(R.string.not_valid_email_format));
                }

                if (!(validPass = Validation.isValidPassword(password))) {
                    pass.setError(getString(R.string.not_vailid_password_format));
                }

                if (!(validPhone = Validation.isValidPhone(phoneText))) {
                    phone.setError(getString(R.string.not_valid_phone_format));
                }
                if (!(validAge = Validation.isValidAge(age.getText().toString()))) {
                    age.setError(getString(R.string.not_valid_age));
                } else {
                    ageText = Integer.parseInt(age.getText().toString());
                }
                if (validEmail && validPass && validPhone && validAge) {
                    Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                    User newUser = new User(emailText, password, phoneText, ageText);
                    intent.putExtra(getString(R.string.intent_newUser), newUser);
                    intent.putExtra(getString(R.string.intent_byReg), true);

                    // Save to storage
                    if (selectedImageUri != null) {
                        uploadFile();
                        intent.putExtra(getString(R.string.intent_uri), selectedImageUri);
                    }

                    // Save to FireStore
                    db.collection("users").document(emailText).set(newUser);

                    // Save to authentication
                    createAccount(emailText, password);

                    // Go Details Activity
                    startActivity(intent);
                    finish();
                }

            }
        });
    }


    private void uploadFile() {

        mStorageRef.child(email.getText().toString()).putFile(selectedImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d("TAG", "uploadFile(): failed to upload the file to storage.");
                    }
                });
    }


    private void imageChooseListener() {
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    public void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void selectImage() {
        // Select Image method

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), 11);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == 11
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            selectedImageUri = data.getData();


            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                selectedImageUri);

                userImage.setImageBitmap(bitmap);
                //((ImageView) findViewById(R.id.profileImage)).setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
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
