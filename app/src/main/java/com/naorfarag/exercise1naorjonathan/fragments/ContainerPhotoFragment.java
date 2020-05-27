package com.naorfarag.exercise1naorjonathan.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.naorfarag.exercise1naorjonathan.R;

public class ContainerPhotoFragment extends Fragment implements FragmentUploadListener {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private MapFragment mapFragment;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_container_fragmeny_layout, container, false);
        addFragments();
        return view;
    }
    public void addFragments(){
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        UploadFragment uploadFragment = new UploadFragment(this);
        mapFragment = new MapFragment();
        fragmentTransaction.add(R.id.uploadFragment, uploadFragment).add(R.id.mapFragment, mapFragment);
        fragmentTransaction.commit();

    }


    @Override
    public void onUploadPerformed(Bundle bundle) {
        mapFragment.setArguments(bundle);
    }
}
