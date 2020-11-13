package com.example.bookit;

import android.content.Context;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class ScanBookActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    //This class provides methods to play DTMF tones
    private ToneGenerator toneGen1;
    private TextView barcodeText;
    private String barcodeData;

    private static final String TAG = ScanBookActivity.class.getSimpleName();
    private EditText eBookIsbn;
    private TextView mAuthorText;
    private TextView mTitleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_book);

//        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
//        surfaceView = findViewById(R.id.surface_view);
//        barcodeText = findViewById(R.id.barcode_text);

        eBookIsbn = findViewById(R.id.isbnEditText);
        mAuthorText = findViewById(R.id.authorTextView);
        mTitleText = findViewById(R.id.bookTitleTextView);

    }

    public void searchBooks(View view) {
        String queryString = eBookIsbn.getText().toString();
        // Hide the keyboard when the button is pushed.
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        // Check the status of the network connection.
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If the network is active and the search field is not empty, start a FetchBook AsyncTask.
        if (networkInfo != null && networkInfo.isConnected() && queryString.length()!=0) {
            new FetchBook(mTitleText, mAuthorText, eBookIsbn).execute(queryString);
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

    }



















//    private void initialiseDetectorsAndSources() {
//
//        //Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
//
//        barcodeDetector = new BarcodeDetector.Builder(this)
//                .setBarcodeFormats(Barcode.ALL_FORMATS)
//                .build();
//
//        cameraSource = new CameraSource.Builder(this, barcodeDetector)
//                .setRequestedPreviewSize(1920, 1080)
//                .setAutoFocusEnabled(true) //you should add this feature
//                .build();
//
//        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                try {
//                    if (ActivityCompat.checkSelfPermission(ScanBookActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                        cameraSource.start(surfaceView.getHolder());
//                    } else {
//                        ActivityCompat.requestPermissions(ScanBookActivity.this, new
//                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
//                    }
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//                cameraSource.stop();
//            }
//        });
//
//
//        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
//            @Override
//            public void release() {
//                // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void receiveDetections(Detector.Detections<Barcode> detections) {
//                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
//                if (barcodes.size() != 0) {
//
//
//                    barcodeText.post(new Runnable() {
//
//                        @Override
//                        public void run() {
//
//                            if (barcodes.valueAt(0).email != null) {
//                                barcodeText.removeCallbacks(null);
//                                barcodeData = barcodes.valueAt(0).email.address;
//                                barcodeText.setText(barcodeData);
//                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
//                            } else {
//
//                                barcodeData = barcodes.valueAt(0).displayValue;
//                                barcodeText.setText(barcodeData);
//                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
//
//                            }
//                        }
//                    });
//
//                }
//            }
//        });
//    }
//
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        getSupportActionBar().hide();
//        cameraSource.release();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        getSupportActionBar().hide();
//        initialiseDetectorsAndSources();
//    }
}