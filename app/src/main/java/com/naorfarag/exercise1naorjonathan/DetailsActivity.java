package com.naorfarag.exercise1naorjonathan;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.MessageFormat;

public class DetailsActivity extends AppCompatActivity {

    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("images");
    private FirebaseFirestore db;

    private Uri uri;
    private ImageView userImage;
    private TextView email;
    private TextView phone;
    private TextView age;
    private boolean byReg = false;

    private User user;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        db = FirebaseFirestore.getInstance();

        userImage = findViewById(R.id.profileImage);
        email = findViewById(R.id.emailText);
        phone = findViewById(R.id.phoneText);
        age = findViewById(R.id.ageText);
        byReg = getIntent().getBooleanExtra(getString(R.string.intent_byReg), false);
        if (byReg) {
            user = (User) getIntent().getSerializableExtra(getString(R.string.intent_newUser));
            uri = getIntent().getParcelableExtra(getString(R.string.intent_uri));
        }
        loadDetails();
    }

    private void loadDetails() {
        if (byReg) {
            loadFromIntentObject();
        } else {
            loadFromDatabase();

        }
    }
    // TODO: 19/03/2020  FIX Load image from database when doing login
    private void loadFromDatabase() {
        String mail = getIntent().getStringExtra("email");

        if (mail != null) {
            email.setText(mail);
            db.collection("users").document(mail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            email.setText(user.getEmail());
                            phone.setText(user.getPhone());
                            age.setText(MessageFormat.format("{0} ", user.getAge()));
                        }
                    }
                }
            });

            mStorageRef.child(email.getText().toString()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.i("TAG", "before uri if ");
                    if (uri != null) {
                        userImage.setImageURI(uri);
                        Log.i("TAG", "onSuccess: " + uri.toString());
                    }
                    else {
                        userImage.setBackgroundResource(R.drawable.cyclops);
                    }
                }
            });
        }
    }

    private void loadFromIntentObject() {
        if (uri != null)
            userImage.setImageURI(uri);
        else
            userImage.setBackgroundResource(R.drawable.cyclops);
        email.setText(user.getEmail());
        phone.setText(user.getPhone());
        age.setText(MessageFormat.format("{0} ", user.getAge()));
    }
}
