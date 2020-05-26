package com.naorfarag.exercise1naorjonathan.Search;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kc.unsplash.Unsplash;
import com.kc.unsplash.api.Order;
import com.kc.unsplash.api.Orientation;
import com.kc.unsplash.models.Photo;
import com.kc.unsplash.models.SearchResults;
import com.naorfarag.exercise1naorjonathan.R;

import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 */
public class SearchPhoto extends Fragment {

    private EditText searchBar;
    private RecyclerView recyclerView;
    private PhotoRecyclerAdapter adapter;

    public SearchPhoto() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_photo2, container, false);

        recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new PhotoRecyclerAdapter();
        recyclerView.setAdapter(adapter);

        searchBar = view.findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                searchPhoto(s.toString());
            }
        });

        return view;
    }
    private void searchPhoto(String searchBy){
        Unsplash unsplash = new Unsplash("9xebvc9XEro15Zr2OyE9H1J80O-YS-B7l6RhSiZbzkc");

        unsplash.searchPhotos(searchBy,0,10, Orientation.PORTRAIT.getOrientation(), new Unsplash.OnSearchCompleteListener() {

            @Override
            public void onComplete(SearchResults results) {
                Log.d("Photos", "Total Results Found " + results.getTotal());
                List<Photo> photos = results.getResults();
                adapter.setPhotos(photos);
            }

            @Override
            public void onError(String error) {
                Log.d("Unsplash", error);
            }
        });
    }
}
