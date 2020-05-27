package com.naorfarag.exercise1naorjonathan.fragments;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.naorfarag.exercise1naorjonathan.R;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.UUID;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MapFragment extends Fragment {
    private MapView mMapView;
    private GoogleMap googleMaps;
    private Button uploadButt;
    private Location userLocation;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.map_fragment_layout, container, false);
        mMapView = view.findViewById(R.id.mapView);
        uploadButt = view.findViewById(R.id.map_uploadButt);

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        if (getActivity() != null) {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }

        // Set google maps
        mMapView.getMapAsync(mMap -> {
            googleMaps = mMap;
            googleMaps.setOnMapLoadedCallback(() -> {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(new LatLng(28.580903, 77.317408)); //Taking Point A (First LatLng)
                builder.include(new LatLng(28.583911, 77.319116)); //Taking Point B (Second LatLng)
                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
                googleMaps.moveCamera(cu);
                googleMaps.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
            });
        });

        uploadButt.setOnClickListener(v -> {
            getLocation();
            Bundle bundle = getArguments();
            Bitmap bitmap = Objects.requireNonNull(bundle).
                    getParcelable(FragmentUploadListener.UPLOAD_KEY);
            if (bitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] byteArrayData = baos.toByteArray();
                UploadTask uploadTask = firebaseStorage.getReference()
                        .child("photo")
                        .child(UUID.randomUUID()
                                .toString()).
                                putBytes(byteArrayData);
                uploadTask.addOnFailureListener(exception -> {
                }).addOnSuccessListener(taskSnapshot -> { });
            }
        });
        return view;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    private void accessClientLocation() {
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(getContext());
        ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION);
        client.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                userLocation = location;
                LatLng latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                googleMaps.addMarker(new MarkerOptions().position(latLng).title("Your location"));
                googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));
            }
        });

    }

    private void getLocation() {
        requestPermission();
        accessClientLocation();
    }
}
