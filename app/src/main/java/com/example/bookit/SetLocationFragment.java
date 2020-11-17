package com.example.bookit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SetLocationFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private MapView mapView;
    private Button setLocationButton;
    private double latitude;
    private double longitude;
    private String bookID;

    public SetLocationFragment() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_dropoff_location, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        //get the ids of all xml elements
        mapView = view.findViewById(R.id.mapView);
        setLocationButton = view.findViewById(R.id.setLocationButton);
        assert getArguments() != null;
        bookID = getArguments().getString("bookID");

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync((OnMapReadyCallback) this);

        //on click listener for set location button
        setLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                DocumentReference docRef = db.collection("books").document(bookID);
                docRef.update("latitude", String.valueOf(latitude)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d("UPDATE_DATA", "update latitude was successful");
                        } else {
                            Log.d("UPDATE_DATA", "update latitude failed with ", task.getException());
                        }
                    }
                });

                docRef.update("longitude", String.valueOf(longitude)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d("UPDATE_DATA", "update longitude was successful");
                        } else {
                            Log.d("UPDATE_DATA", "update longitude failed with ", task.getException());
                        }
                    }
                });

                getActivity().finish();
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setScrollGesturesEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);

        // Add marker in Edmonton and move the camera
        LatLng edmonton = new LatLng(53.5461, -113.4938);
        map.addMarker(new MarkerOptions().position(edmonton).title("Marker in Edmonton"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(edmonton, 10f));
        Toast.makeText(getContext(), "Tap anywhere to place the marker at the drop off location!", Toast.LENGTH_SHORT).show();

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.clear();
                map.addMarker(new MarkerOptions().position(latLng).title("Exchange location"));
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                latitude = latLng.latitude;
                longitude = latLng.longitude;
            }
        });

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}

