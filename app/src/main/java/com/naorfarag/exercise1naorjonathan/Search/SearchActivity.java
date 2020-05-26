
package com.naorfarag.exercise1naorjonathan.Search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.naorfarag.exercise1naorjonathan.R;

public class SearchActivity extends AppCompatActivity {
    private DetailsPhoto detailsPhoto;
    private SearchPhoto searchPhoto;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_container);
        detailsPhoto = new DetailsPhoto();
        searchPhoto = new SearchPhoto();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer, searchPhoto);
        fragmentTransaction.commit();

    }
}
