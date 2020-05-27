package com.naorfarag.exercise1naorjonathan.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.naorfarag.exercise1naorjonathan.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class UploadFragment extends Fragment {
    private static final int PICK_IMAGE_GALLERY = 1;
    private static final int PICK_IMAGE_CAMERA = 0;
    private Uri imageUri;
    private ImageView pictureFromCamera;
    private String currentPhotoPath;
    private String pathToFile;
    private Button picButton;
    private Bitmap bitmap;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private FragmentUploadListener uploadListener;

    public UploadFragment(FragmentUploadListener uploadListener){
        this.uploadListener = uploadListener;
    }

    public UploadFragment(){

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.upload_fragment_layout, container, false);
        pictureFromCamera = view.findViewById(R.id.pictureImageView);
        picButton = view.findViewById(R.id.pictureButton);

        picButton.setOnClickListener(v -> selectImage(getActivity()));
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case PICK_IMAGE_GALLERY:
                    imageUri = data.getData();
                    Picasso.get().load(imageUri).into(pictureFromCamera);

                    Glide.with(this)
                            .asBitmap()
                            .load(imageUri)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    if (imageUri != null) {
                                        bitmap = resource;
                                        uploadAndSendPhoto();
                                    }
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            });

                    break;
                case PICK_IMAGE_CAMERA:
                    bitmap  = (Bitmap) data.getExtras().get("data");
                    pictureFromCamera.setImageBitmap(bitmap);
                    uploadAndSendPhoto();
                    break;
            }
        }
    }

    private void uploadAndSendPhoto() {
        if(bitmap!=null && uploadListener != null){
            Bundle bundle = new Bundle();
            bundle.putParcelable(FragmentUploadListener.UPLOAD_KEY,
                    bitmap);
            uploadListener.onUploadPerformed(bundle);
        }
    }


    private void selectImage(Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), PICK_IMAGE_CAMERA);
            } else if (options[item].equals("Choose from Gallery")) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE_GALLERY);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
