package com.naorfarag.exercise1naorjonathan.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.kc.unsplash.Unsplash;
import com.kc.unsplash.api.Orientation;
import com.kc.unsplash.models.Photo;
import com.kc.unsplash.models.SearchResults;
import com.naorfarag.exercise1naorjonathan.R;
import com.naorfarag.exercise1naorjonathan.fragments.ContainerPhotoFragment;
import com.naorfarag.exercise1naorjonathan.loginRegister.LoginActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import hari.bounceview.BounceView;


public class SearchPhoto extends Fragment implements PhotoRecyclerAdapter.OnImageClickListener {
    private static final int PAGE_SIZE = 10;
    private static int pageNumber;
    private String searchContent;
    private EditText searchBar;
    private RecyclerView recyclerView;
    private Button uploadButt;

    private PhotoRecyclerAdapter adapter;
    private List<Photo> photoList = new ArrayList<>();
    private static String lastSearchContent;
    private boolean isKeyboardOpen = false;
    private View view;
    private TextWatcher textWatcher;

    public SearchPhoto() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_search_photo2, container, false);
            uploadButt = view.findViewById(R.id.uploadButt);
            searchBar = view.findViewById(R.id.searchBar);
            recyclerView = view.findViewById(R.id.recycleView);
            BounceView.addAnimTo(uploadButt);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            adapter = new PhotoRecyclerAdapter(this);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        uploadButtListener();
        searchBarTextListener();
        scrollListener();
    }

    private void uploadButtListener() {
        uploadButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContainerPhotoFragment containerPhotoFragment = new ContainerPhotoFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer,containerPhotoFragment, getString(R.string.container_frag))
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        searchBar.removeTextChangedListener(textWatcher);
    }

    void logOut() {
        new SweetAlertDialog(Objects.requireNonNull(getContext()), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Sign out?")
                .setConfirmText("YES")
                .setConfirmClickListener(sDialog -> {
                    FirebaseAuth.getInstance().signOut();
                    sDialog.dismissWithAnimation();
                    Objects.requireNonNull(getActivity()).finish();
                    startActivity(new Intent(getContext(), LoginActivity.class));
                })
                .setCancelButton("NO", SweetAlertDialog::dismissWithAnimation)
                .show();
    }

    private void searchBarTextListener() {
        searchBar.setOnClickListener(v -> isKeyboardOpen = true);
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                isKeyboardOpen = true;
                pageNumber = 1;
                searchContent = s.toString();
                searchPhoto(searchContent, pageNumber);
            }
        };
        searchBar.addTextChangedListener(textWatcher);
    }

    private void scrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    searchPhoto(lastSearchContent, ++pageNumber);
                }
            }
        });
    }

    private void searchPhoto(String searchBy, int pageNumber){
        Unsplash unsplash = new Unsplash(getString(R.string.client_id));
        Log.i("SEARCH", "SEARCH="+searchBy);
        unsplash.searchPhotos(searchBy, pageNumber, PAGE_SIZE, Orientation.PORTRAIT.getOrientation(), new Unsplash.OnSearchCompleteListener() {
            @Override
            public void onComplete(SearchResults results) {
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
        dismissKeyboard();
        if(!isKeyboardOpen) {
            DetailsPhoto detailsPhotoFragment = new DetailsPhoto(photoList.get(position));
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, detailsPhotoFragment, getString(R.string.search_photo))
                    .addToBackStack(null)
                    .commit();
        }
        isKeyboardOpen = false;
    }

    private void sweetAlertDialog(String title, String description){
        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(String.format("%s",title + " " + description))
                .show();
    }

    public void dismissKeyboard(){
        InputMethodManager imm =(InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
    }

    @Override
    public void onDestroyView() {
        if (view.getParent() != null) {
            ((ViewGroup)view.getParent()).removeView(view);
        }
        super.onDestroyView();
    }
}
