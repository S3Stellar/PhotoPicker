package com.naorfarag.exercise1naorjonathan.Search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.kc.unsplash.models.Photo;
import com.naorfarag.exercise1naorjonathan.R;
import com.naorfarag.exercise1naorjonathan.utility.OnSwipeTouchListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsPhoto extends Fragment {
    private Photo photo;
    private ImageView photoImageView;
    private TextView photoDetails;
    private TextView comment;
    private Button addComment;

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

        Picasso.get().load(photo.getUrls().getSmall()).into(photoImageView);
//        photoDetails.setText(photo.getDownloads());
//        comment.setText(photo.describeContents());
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
