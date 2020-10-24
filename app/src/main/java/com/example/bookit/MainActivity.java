package com.example.bookit;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    // initialize variables to track which tab is selected in each mode, and which mode is active
    // (borrower or lender). Default is borrower.
    boolean borrower = true;
    int currentId;
    int borrowerId;
    int lenderId = R.id.MyBooksFragment;

    // declare variables for nav views
    BottomNavigationView botNavViewBorrower;
    BottomNavigationView botNavViewLender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
}