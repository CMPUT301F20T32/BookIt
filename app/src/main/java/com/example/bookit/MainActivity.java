/*
 *  Classname: MainActivity
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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    // initialize variables to track which tab is selected in each mode, and which mode is active
    // (borrower or lender). Default is lender.
    boolean borrower = false;
    int currentId = R.id.MyBooksFragment;
    int borrowerId = R.id.BorrowedFragment;
    int lenderId = R.id.MyBooksFragment;
    int flag = 0;
    // declare variables for nav views
    BottomNavigationView botNavViewBorrower;
    BottomNavigationView botNavViewLender;
    public BookPathViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (flag==0){
//        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//        flag = 1;
//        startActivity(intent);
//        finish();
//        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewModel = new ViewModelProvider(this).get(BookPathViewModel.class);
        // assign value to navViews and set listeners so that whenever an item is selected, the
        // fragment with the id of the item selected is opened.
        // Whenever any any selection is made, set currentId to that id. CurrentId Will always hold
        // id of last "tab" selected.

        botNavViewBorrower = findViewById(R.id.navigationViewBorrower);
        botNavViewBorrower.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
                currentId = item.getItemId();
                navController.navigate(item.getItemId());
                return true;
            }
        });
        botNavViewLender = findViewById(R.id.navigationViewLender);
        botNavViewLender.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
                currentId = item.getItemId();
                navController.navigate(item.getItemId());
                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // if profile button is clicked, open ProfileActivity
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.notifications_tab) {
            Intent intent = new Intent(this, NotificationActivity.class);
            startActivity(intent);
            return true;
        }
        // else if other button is clicked, change mode from borrower to lender or vice versa, then
        // save the id of the opposite mode to the last accessed "tab". Switch the visibility of
        // each navBar to correspond with switching modes. Restore currentId to the saved value for
        // the other mode, and switch to whichever fragment was active by switching to saved value
        // (i.e. lenderId or borrowerId).
        else if (id == R.id.menu_toggle) {
            borrower = !borrower;
            if (borrower) {
                lenderId = currentId;
                item.setTitle("borrower");
                botNavViewLender.setVisibility(View.INVISIBLE);
                botNavViewBorrower.setVisibility(View.VISIBLE);
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
                navController.navigate(borrowerId);
                currentId = borrowerId;
            } else {
                borrowerId = currentId;
                item.setTitle("lender");
                botNavViewLender.setVisibility(View.VISIBLE);
                botNavViewBorrower.setVisibility(View.INVISIBLE);
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
                navController.navigate(lenderId);
                currentId = lenderId;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            ImageView imageView = findViewById(R.id.imageView4);
            viewModel.selectItem(currentPhotoPath);
            Bitmap myBitmap = resizeBitmap(BitmapFactory.decodeFile(currentPhotoPath), 300);
            imageView.setImageBitmap(myBitmap);
        }
    }

    public void attemptTakePhoto(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    String currentPhotoPath;
    static final int REQUEST_CAMERA_PERMISSION = 3;
    static final int REQUEST_TAKE_PHOTO = 2;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Bitmap resizeBitmap(Bitmap myBitmap, float newSize) {
        float ratio = Math.min((float) newSize / myBitmap.getWidth(), (float) newSize / myBitmap.getHeight());
        int width = Math.round((float) ratio * myBitmap.getWidth());
        int height = Math.round((float) ratio * myBitmap.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(myBitmap, width, height, true);
        return newBitmap;
    }
}