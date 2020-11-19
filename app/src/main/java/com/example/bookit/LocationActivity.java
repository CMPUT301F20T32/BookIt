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

public class LocationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        String bookID = getIntent().getExtras().getString("bookID");
        Bundle bundle = new Bundle();
        bundle.putString("bookID", bookID);

        /*
         * type 1 = set drop off location (owner)
         * type 2 = get drop off location (borrower)
         */
        int type = getIntent().getExtras().getInt("type");

        if (type == 1) {
            SetLocationFragment fragment = new SetLocationFragment();
            fragment.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.common_container, fragment);
            transaction.commit();

        }

        else if (type == 2){
            GetLocationFragment fragment = new GetLocationFragment();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("books").document(bookID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Double latitude = Double.valueOf(document.get("latitude").toString());
                        Double longitude = Double.valueOf(document.get("longitude").toString());
                        bundle.putDouble("latitude", latitude);
                        bundle.putDouble("longitude", longitude);
                        fragment.setArguments(bundle);

                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.add(R.id.common_container, fragment);
                        transaction.commit();
                    }}});

        }

    }
}
