package com.example.bookit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * LocationActivity starts the SetLocationFragment or the GetLocationFragment depending on the argument passed to it
 *
 * @author Alisha Crasta
 * @version 1.0
 * @since 1.0
 */
public class LocationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        String value = getIntent().getExtras().getString("bookID");
        Bundle bundle = new Bundle();
        bundle.putString("bookID", value);

        /*
         * type 1 = set drop off location (owner)
         * type 2 = get drop off location
         */
        int type = getIntent().getExtras().getInt("type");

        //open the fragment according to the type value
        if (type == 1) {
            SetLocationFragment fragment = new SetLocationFragment();
            fragment.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.location_container, fragment);
            transaction.commit();

        }

        else if (type == 2){
            GetLocationFragment fragment = new GetLocationFragment();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("books").document(value);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        //pass the latitude and longitude values of the exchange location as arguments
                        double latitude = Double.valueOf(document.get("latitude").toString());
                        double longitude = Double.valueOf(document.get("longitude").toString());
                        bundle.putDouble("latitude", latitude);
                        bundle.putDouble("longitude", longitude);
                        fragment.setArguments(bundle);

                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.add(R.id.location_container, fragment);
                        transaction.commit();
                    }}});

        }

    }
}
