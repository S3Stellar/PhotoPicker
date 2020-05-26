
package com.naorfarag.exercise1naorjonathan.Search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.naorfarag.exercise1naorjonathan.LoginRegister.LoginActivity;
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
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.i("TAG", "onBackStackChanged: " + fragmentManager.getBackStackEntryCount());
            }
        });

    }
    public void GoBack() {
        Fragment f;
        getSupportFragmentManager().popBackStack();
        int num = getSupportFragmentManager().getBackStackEntryCount();
        Log.i("TAG", "GoBack: " + num);
        f = getSupportFragmentManager().findFragmentByTag((num - 1) + "");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, f, (getSupportFragmentManager().getBackStackEntryCount() - 1) + "").commit();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, LoginActivity.class));
    }
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        final View view =  inflater.inflate(R.layout.fragment_second, container, false);
//
//        view.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
//            @Override
//            public void onSwipeLeft() {
//                super.onSwipeLeft();
//                Go_back_fragment();
//            }
//        });
//        return view;
//    }
//
//    private void Go_back_fragment() {
//        MainActivity mainActivity = (MainActivity) getActivity();
//        mainActivity.GoBack();
//    }

}
