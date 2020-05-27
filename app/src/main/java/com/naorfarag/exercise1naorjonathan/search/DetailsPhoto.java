package com.naorfarag.exercise1naorjonathan.search;

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
import com.google.firebase.firestore.SetOptions;
import com.kc.unsplash.models.Photo;
import com.naorfarag.exercise1naorjonathan.R;
import com.naorfarag.exercise1naorjonathan.utility.OnSwipeTouchListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsPhoto extends Fragment {
    private Photo photo;
    private ImageView photoImageView;
    private TextView photoDetails;
    private TextView comment;
    private Button favoriteButt;

    private FirebaseFirestore db;


    public DetailsPhoto(Photo photo) {
        this.photo = photo;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details_photo2, container, false);
        db = FirebaseFirestore.getInstance();
        photoImageView = view.findViewById(R.id.searchImage);
        photoDetails = view.findViewById(R.id.imageDetails);
        comment = view.findViewById(R.id.comment);
        favoriteButt = view.findViewById(R.id.butt);

        updatePhotoDetails();
        favoriteButtListener();
        swipeListener(view);

        return view;
    }

    private void updatePhotoDetails() {
        photoDetails.setText(String.format("%s", "Likes:" +  photo.getLikes()));

        comment.setText(String.format("Dimensions: %d x %d\nUploaded at: %s"
                , photo.getHeight(), photo.getWidth(),photo.getCreatedAt()));

        Picasso.get().load(photo.getUrls().getSmall()).into(photoImageView);
    }

    private void favoriteButtListener() {
        favoriteButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPictureToFavorites();
                Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void swipeListener(View view) {
        // Inflate the layout for this fragment
        view.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                Toast.makeText(getActivity(), "swiped", Toast.LENGTH_SHORT).show();
                Go_back_fragment();
            }
        });
    }

    private void Go_back_fragment() {
        SearchActivity searchActivity = (SearchActivity) getActivity();
        Objects.requireNonNull(searchActivity).GoBack();
    }

    public void addPictureToFavorites(){
        Map<String, Object> hashMap = new HashMap<>();
        Map<String, Object> update = new HashMap<>();
        update.put(photo.getId(), photo.getUrls().getSmall());
        hashMap.put("favoriteImages", update);

        db.collection("users").document(SearchActivity.email)
                .set(hashMap, SetOptions.merge()).addOnCompleteListener(task -> {
           if(!task.isSuccessful()){
               Log.i("ADD", " FAILED");
               return;
           }
           Log.i("ADD", "SUCCESS");
       });
    }

    /*private void uploadFile() {
        Glide.with(this)
                .asBitmap()
                .load(photo.getUrls().getSmall())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (photo.getUrls().getSmall() != null) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();
                            UploadTask uploadTask = storage.getReference().child("photo").child(UUID.randomUUID().toString()).putBytes(data);
                            uploadTask.addOnFailureListener(exception -> {
                            }).addOnSuccessListener(taskSnapshot -> { });
                        }
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {}});
    }
    */
}
