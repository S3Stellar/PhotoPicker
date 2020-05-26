package com.naorfarag.exercise1naorjonathan.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kc.unsplash.Unsplash;
import com.kc.unsplash.api.Orientation;
import com.kc.unsplash.models.Photo;
import com.kc.unsplash.models.SearchResults;
import com.naorfarag.exercise1naorjonathan.R;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class SearchPhoto extends Fragment implements PhotoRecyclerAdapter.OnImageClickListener {
    private static final int PAGE_SIZE = 10;
    private static int pageNumber;
    private String searchContent;
    private EditText searchBar;
    private RecyclerView recyclerView;
    private PhotoRecyclerAdapter adapter;
    private List<Photo> photoList = new ArrayList<>();
    private static String lastSearchContent;

    public SearchPhoto() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_photo2, container, false);

        recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new PhotoRecyclerAdapter(this);
        recyclerView.setAdapter(adapter);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                        Log.i("SCROLL", "PAGE NUM = " + pageNumber);
                        searchPhoto(lastSearchContent, ++pageNumber);
                }
            }
        });

        searchBar = view.findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               // pageNumber = 0;
            }

            @Override
            public void afterTextChanged(Editable s) {
                pageNumber = 0;
                searchContent = s.toString();
                searchPhoto(searchContent, pageNumber);
            }
        });

        return view;
    }
    private void searchPhoto(String searchBy, int pageNumber){
        Unsplash unsplash = new Unsplash(getString(R.string.client_id));
        Log.i("SEARCH", "SEARCH="+searchBy);
        unsplash.searchPhotos(searchBy, pageNumber, PAGE_SIZE, Orientation.PORTRAIT.getOrientation(), new Unsplash.OnSearchCompleteListener() {
            @Override
            public void onComplete(SearchResults results) {
                Log.i("SEARCH", "IN SEARCH");
                Log.i("SEARCH", "PAGE NUM = " + pageNumber);
               // SearchPhoto.pageNumber++;
                if(results != null) {
                    if(!searchBy.equalsIgnoreCase(lastSearchContent)) {
                        photoList.clear();
                        lastSearchContent = searchBy;
                    }

                    photoList.addAll(results.getResults());
                    adapter.setPhotos(photoList);
                }
            }

            @Override
            public void onError(String error) {
                sweetAlertDialog("Unsplash Error", error);
            }
        });
    }

    @Override
    public void onImageClick(int position) {
        DetailsPhoto detailsPhotoFragment = new DetailsPhoto(photoList.get(position));
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, detailsPhotoFragment,"PHOTO")
                .addToBackStack(null)
                .commit();
    }

    private void sweetAlertDialog(String title, String description){
        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(String.format("%s",title + " " + description))
                .show();
    }
}