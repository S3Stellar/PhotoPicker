package com.naorfarag.exercise1naorjonathan.search;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kc.unsplash.models.Location;
import com.kc.unsplash.models.Photo;
import com.naorfarag.exercise1naorjonathan.R;
import com.naorfarag.exercise1naorjonathan.utility.OnSwipeTouchListener;
import com.squareup.picasso.Picasso;

import java.net.URI;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsPhoto extends Fragment {
    private Photo photo;
    private ImageView photoImageView;
    private TextView photoDetails;
    private TextView comment;
    private Button addComment;

    private StorageReference mStorageRef;
    private FirebaseFirestore db;

    public DetailsPhoto(){

    }

    public DetailsPhoto(Photo photo) {
        this.photo = photo;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details_photo2, container, false);
        photoImageView = view.findViewById(R.id.searchImage);
        photoDetails = view.findViewById(R.id.imageDetails);
        comment = view.findViewById(R.id.comment);
        addComment = view.findViewById(R.id.butt);
        photoDetails.setText(String.format("%s", "Likes:" +  photo.getLikes()));

        comment.setText(String.format("Dimensions: %d x %d\nUploaded at: %s"
                , photo.getHeight(), photo.getWidth(),photo.getCreatedAt()));

        Log.i("ID", "MY ID=" + photo.getId());
        Picasso.get().load(photo.getUrls().getSmall()).into(photoImageView);
//        photoDetails.setText(photo.getDownloads());
//        comment.setText(photo.describeContents());

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db = FirebaseFirestore.getInstance();
                mStorageRef = FirebaseStorage.getInstance().getReference(getString(R.string.user_images));

                Log.i("URLS", "URL: = " + photo.getUrls().getSmall());
                mStorageRef.child("djfarag@gmail.com").putFile(Uri.parse(photo.getUrls().getSmall()))
                        .addOnCompleteListener(taskSnapshot -> {
                            Log.i("ADD","File added successfully");
                        })
                        .addOnFailureListener(exception -> Log.d("TAG", "uploadFile(): failed to upload the file to storage."));

                Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        // Inflate the layout for this fragment
        view.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                Toast.makeText(getActivity(), "swiped", Toast.LENGTH_SHORT).show();
                Go_back_fragment();
            }
        });

        return view;
    }

    private void Go_back_fragment() {
        SearchActivity searchActivity = (SearchActivity) getActivity();
        searchActivity.GoBack();
    }

}
