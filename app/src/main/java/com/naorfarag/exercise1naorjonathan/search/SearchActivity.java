
package com.naorfarag.exercise1naorjonathan.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.naorfarag.exercise1naorjonathan.loginRegister.LoginActivity;
import com.naorfarag.exercise1naorjonathan.R;

import java.util.Objects;

public class SearchActivity extends AppCompatActivity {
    private DetailsPhoto detailsPhoto;
    private SearchPhoto searchPhoto;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    public static String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_container);
        email = getIntent().getStringExtra(getString(R.string.intent_email));

        searchPhoto = new SearchPhoto();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer, searchPhoto,"PHOTO");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        fragmentManager.addOnBackStackChangedListener(() -> Log.i("TAG", "onBackStackChanged: " + fragmentManager.getBackStackEntryCount()));

    }
    public void GoBack() {
       fragmentManager.popBackStack();
       fragmentManager.beginTransaction().replace(R.id.fragmentContainer, Objects.requireNonNull(fragmentManager.findFragmentByTag("PHOTO")), "PHOTO").commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, LoginActivity.class));
    }
}
