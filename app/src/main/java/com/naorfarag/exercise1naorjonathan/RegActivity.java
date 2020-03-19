package com.naorfarag.exercise1naorjonathan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage firebaseStorage;
    private StorageReference mStorageRef;


    private Bitmap bitmap;
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
        firebaseStorage = FirebaseStorage.getInstance();
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

                String emailText = email.getText().toString();
                String password = pass.getText().toString();
                String phoneText = phone.getText().toString();
                int ageText = 0;

                if (emailText.isEmpty()) {
                    email.setError("Enter correct email");
                    return;
                }

                if (password.length() < 5) {
                    pass.setError("Minimum 5 chars long");
                    return;
                }

                if (phoneText.isEmpty()) {
                    phone.setError("Enter correct phone number");
                    return;
                }

                try {
                    ageText = Integer.parseInt(age.getText().toString());
                } catch (Exception e) {
                    age.setError("Type correct age");
                    return;
                }

                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                User newUser = new User(emailText, password, phoneText, ageText);
                intent.putExtra("NewUser", newUser);
                intent.putExtra("byReg", true);


                // Save to storage
                if (selectedImageUri != null) {
                    uploadFile();
                    intent.putExtra("uri", selectedImageUri);
                }

                // Save to FireStore
                db.collection("users").document(emailText).set(newUser);

                // Save to authentication
                createAccount(emailText, password);

                // Go Details Activity
                startActivity(intent);
                finish();
            }
        });
    }
    public static boolean isValidEmail(String emailStr) {
        final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches() && emailStr.trim().length() != 0;
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
                            FirebaseUser user = mAuth.getCurrentUser();
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
                bitmap = MediaStore
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

  /*  @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            Bitmap bitmap;
            if (data != null)
                selectedImage = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            bitmap = BitmapFactory.decodeFile(picturePath);

            if (bitmap != null) {
                userImage = findViewById(R.id.profileImage);
                userImage.setImageBitmap(bitmap);
            }
        }
    }*/

      /*  public void pickImage() {

          Intent intent = new Intent();
          intent.setType("image/*");
          intent.setAction(Intent.ACTION_GET_CONTENT);
          startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                  11);

      }*/

}
