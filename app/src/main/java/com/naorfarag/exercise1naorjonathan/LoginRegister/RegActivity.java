package com.naorfarag.exercise1naorjonathan.LoginRegister;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.naorfarag.exercise1naorjonathan.R;
import com.naorfarag.exercise1naorjonathan.Search.SearchActivity;
import com.naorfarag.exercise1naorjonathan.utility.Validation;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class RegActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference mStorageRef;

    private SweetAlertDialog pDialog;
    private Uri selectedImageUri;
    private EditText email;
    private EditText pass;
    private EditText phone;
    private EditText age;
    private ImageView userImage;
    private Button confirmButt;
    private Intent intent;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference(getString(R.string.user_images));

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
        confirmButt.setOnClickListener(v -> {
            confirmButt.setEnabled(false);
            boolean validEmail, validPass, validPhone, validAge;
            String emailText = email.getText().toString().toLowerCase();
            String password = pass.getText().toString();
            String phoneText = phone.getText().toString();
            int ageText = 0;

            if (!(validEmail = Validation.isValidEmail(emailText))) {
                email.setError(getString(R.string.not_valid_email_format));
            }

            if (!(validPass = Validation.isValidPassword(password))) {
                pass.setError(getString(R.string.not_valid_password_format));
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
                showLoadingDialog();

                User newUser = new User(emailText, password, phoneText, ageText);

                intent = new Intent(this, SearchActivity.class);
                intent.putExtra(getString(R.string.intent_newUser), newUser);
                intent.putExtra(getString(R.string.intent_byReg), true);

                // Save to storage
                if (selectedImageUri != null) {
                    uploadFile();
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    intent.putExtra(getString(R.string.intent_uri), selectedImageUri);
                }

                // Save to FireStore
                db.collection("users").document(emailText).set(newUser);

                // Save to authentication
                createAccount(emailText, password);
            } else {
                confirmButt.setEnabled(true);
            }

        });
    }


    private void uploadFile() {
        if (selectedImageUri != null) {
            mStorageRef.child(email.getText().toString().toLowerCase()).putFile(selectedImageUri)
                    .addOnCompleteListener(taskSnapshot -> {
                        hidePDialog();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        finish();
                        startActivity(intent);
                    })
                    .addOnFailureListener(exception -> Log.d("TAG", "uploadFile(): failed to upload the file to storage."));
        } else {
            hidePDialog();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            finish();
            startActivity(intent);
        }
    }


    private void imageChooseListener() {
        userImage.setOnClickListener(v -> selectImage());
    }

    public void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "createUserWithEmail:success");
                        // Go Details Activity
                        uploadFile();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.d("TAG", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegActivity.this, "Email already in use.",
                                Toast.LENGTH_SHORT).show();
                        hidePDialog();
                        confirmButt.setEnabled(true);
                    }
                });
    }


    private void selectImage() {
        // Select Image method
        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            // Defining Implicit Intent to mobile gallery
            Intent imageIntent = new Intent();
            imageIntent.setType("image/*");
            imageIntent.setAction(Intent.ACTION_GET_CONTENT);
            imageIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            startActivityForResult(Intent.createChooser(imageIntent, "Select Image from here..."), 11);
        }
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            selectedImageUri = data.getData();
            Bitmap bitmap;
            try {
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    userImage.setImageBitmap(bitmap);
                } else {
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), selectedImageUri);
                    bitmap = ImageDecoder.decodeBitmap(source);
                    userImage.setImageBitmap(bitmap);
                }

            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                (dialog, which) -> ActivityCompat.requestPermissions((Activity) context,
                        new String[]{permission},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE));
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do your stuff
            } else {
                Toast.makeText(this, "GET_ACCOUNTS Denied",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions,
                    grantResults);
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


    private void showLoadingDialog() {
        hidePDialog();
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        btnParams.leftMargin = 5;
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading...");
        pDialog.setCancelable(false);
        pDialog.showCancelButton(true);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelText("Cancel");
        pDialog.setCancelClickListener(sDialog -> hidePDialog());
        pDialog.show();
        pDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setHeight(85);
        pDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setLayoutParams(btnParams);
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismissWithAnimation();
            pDialog = null;
        }
    }
}
