/*
 *  Classname: ScanBookActivity
 *  Version: 1.0
 *  Date: 06/11/2020
 *  Copyright notice:
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *         http://www.apache.org/licenses/LICENSE-2.0
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.example.bookit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * This Activity is used to perform scanning of barcodes and looking up the information of
 * a book using the ISBN via the Google Books API.
 */
public class ScanBookActivity extends AppCompatActivity implements AsyncResponse {

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    //This class provides methods to play DTMF tones
    private ToneGenerator toneGen1;
    private TextView barcodeText;
    private String barcodeData;

    private static final String TAG = ScanBookActivity.class.getSimpleName();
    private EditText eBookIsbn; // have the option to manually enter isbn if book cannot be scanned
    private TextView mAuthorText;
    private TextView mTitleText;


    private TextView testTextView;

    FetchBook fetchBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_book);

        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        surfaceView = findViewById(R.id.surface_view);
        barcodeText = findViewById(R.id.barcode_text);

        eBookIsbn = findViewById(R.id.isbnEditText);
        mAuthorText = findViewById(R.id.authorTextView);
        mTitleText = findViewById(R.id.bookTitleTextView);

        //testTextView = getParent().findViewById(R.id.scan_text_view);

    }


    public void searchBooks(View view) {
        String queryString = barcodeText.getText().toString();

        // Hide the keyboard when the button is pushed.
//        InputMethodManager inputManager = (InputMethodManager)
//                getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
//                InputMethodManager.HIDE_NOT_ALWAYS);

        // Check the status of the network connection.
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If the network is active and the search field is not empty, start a FetchBook AsyncTask.
        if (networkInfo != null && networkInfo.isConnected() && queryString.length() != 0) {
            fetchBook = new FetchBook();
            fetchBook.delegate = this;
            fetchBook.execute(queryString);

        }
        // Otherwise update the TextView to tell the user there is no connection or no search term.
        else {
            if (queryString.length() == 0) {
                mAuthorText.setText("");
                mTitleText.setText("No search term");
            } else {
                mAuthorText.setText("");
                mTitleText.setText("No Network");
            }
        }
        Log.d("TITLE", "Hello " + mTitleText.getText().toString());

    }


    /**
     * This override the implemented method from AsyncResponse
     * Handles the results on the UI thread. Gets the information from
     * the JSON and updates the Views.
     *
     * @param output Result from the doInBackground method containing the raw JSON response,
     *               or null if it failed.
     * @param isbn   isbn number of the book scanned (more precisely, the barcode value)
     */
    @Override
    public void processFinish(String output, String isbn) {
        // Here you will receive the result fired from async class
        // of onPostExecute(result) method.
        try {
            // Convert the response into a JSON object.
            JSONObject jsonObject = new JSONObject(output);
            // Get the JSONArray of book items.
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            // Initialize iterator and results fields.
            int i = 0;
            String title = null;
            String authors = null;

            /*
             * Look for results in the items array, exiting when both the title and author
             * are found or when all items have been checked.
             */

            while (i < itemsArray.length() || (authors == null && title == null)) {
                // Get the current item information.
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                // Try to get the author and title from the current item,
                // catch if either field is empty and move on.
                try {
                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Move to the next item.
                i++;
            }

            // If both are found, display the result.
            if (title != null && authors != null) {
                mTitleText.setText(title);
                mAuthorText.setText(authors);
                //mBookInput.setText("");

                Intent intent = new Intent();
                intent.putExtra("title", title);
                intent.putExtra("authors", authors);
                intent.putExtra("isbn",isbn);
                setResult(RESULT_OK, intent);
                finish();

            } else {
                // If none are found, update the UI to show failed results.
                mTitleText.setText("No Results");
                mAuthorText.setText("");
            }

        } catch (Exception e) {
            // If onPostExecute does not receive a proper JSON string,
            // Check if the book is in Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference books = db.collection("books");

            db.collection("books")
                    .whereEqualTo("isbn", isbn)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("DATA", "NO1");
                                    Log.d("DATA", document.getId() + " => " + document.getData());
                                    mTitleText.setText(document.getString("book_title"));
                                    mAuthorText.setText(document.getString("author"));
                                    Intent intent = new Intent();
                                    intent.putExtra("title", document.getString("book_title"));
                                    intent.putExtra("authors", document.getString("author"));
                                    intent.putExtra("isbn",document.getString("isbn"));
                                    setResult(RESULT_OK, intent);
                                    finish();
                                    //mBookInput.setText("");
                                    break;
                                }

                                // If onPostExecute does not receive a proper JSON string and book is not in Firestore,
                                // update the UI to show failed results.
                                if (task.getResult().isEmpty()) {
                                    mTitleText.setText("No Results");
                                    mAuthorText.setText("");
                                    e.printStackTrace();
                                }
                            } else {
                                Log.d("DATA", "Error getting documents: ", task.getException());
                                // If onPostExecute does not receive a proper JSON string and book is not in Firestore,
                                // update the UI to show failed results.
                                mTitleText.setText("No Results");
                                mAuthorText.setText("");
                                e.printStackTrace();
                            }
                        }
                    });

        }
    }

    /**
     * Initializes the Barcode and Camera
     */
    private void initialiseDetectorsAndSources() {

        //Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScanBookActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScanBookActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {


                    barcodeText.post(new Runnable() {

                        @Override
                        public void run() {

                            if (barcodes.valueAt(0).email != null) {
                                barcodeText.removeCallbacks(null);
                                barcodeData = barcodes.valueAt(0).email.address;
                                barcodeText.setText(barcodeData);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                            } else {

                                barcodeData = barcodes.valueAt(0).displayValue;
                                barcodeText.setText(barcodeData);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);

                            }
                        }
                    });

                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        getSupportActionBar().hide();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().hide();
        initialiseDetectorsAndSources();
    }
}