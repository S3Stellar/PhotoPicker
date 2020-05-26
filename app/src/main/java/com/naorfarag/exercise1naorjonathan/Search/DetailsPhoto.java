package com.naorfarag.exercise1naorjonathan.Search;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naorfarag.exercise1naorjonathan.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsPhoto extends Fragment {

    public DetailsPhoto() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details_photo2, container, false);
        return view;
    }
}
