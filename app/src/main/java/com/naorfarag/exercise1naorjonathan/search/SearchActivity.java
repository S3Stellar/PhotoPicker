
package com.naorfarag.exercise1naorjonathan.search;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.naorfarag.exercise1naorjonathan.R;

import java.util.Objects;

public class SearchActivity extends AppCompatActivity {
    private SearchPhoto searchPhoto;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    public static String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_container);
        email = getIntent().getStringExtra(getString(R.string.intent_email));

        Log.i("MAIL", email);
        searchPhoto = new SearchPhoto();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer, searchPhoto,getString(R.string.search_photo));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        fragmentManager.addOnBackStackChangedListener(() -> Log.i("TAG", "onBackStackChanged: " + fragmentManager.getBackStackEntryCount()));

    }
    public void GoBack() {
       fragmentManager.popBackStack();
       fragmentManager.beginTransaction().replace(R.id.fragmentContainer, Objects.
               requireNonNull(fragmentManager.findFragmentByTag(getString(R.string.search_photo))), getString(R.string.search_photo)).commit();
    }

    @Override
    public void onBackPressed() {
        Fragment containerFragment = fragmentManager.findFragmentByTag(getString(R.string.container_frag));
        if (containerFragment != null && containerFragment.isVisible()) {
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer,
                    Objects.requireNonNull(fragmentManager.findFragmentByTag(getString(R.string.search_photo))), getString(R.string.search_photo)).commit();
        }else{
            searchPhoto.logOut();
        }
    }
}
