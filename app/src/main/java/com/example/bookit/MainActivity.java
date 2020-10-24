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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BottomNavigationView botNavView = findViewById(R.id.navigationView);
        botNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.HomeScreenFragment) {
                    NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
                    navController.navigate(item.getItemId());
                    return true;
                }
                else if (item.getItemId() == R.id.AddBookFragment) {
                    NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
                    navController.navigate(item.getItemId());
                    return true;
                }
                else if (item.getItemId() == R.id.SearchFragment) {
                    NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
                    navController.navigate(item.getItemId());
                    return true;
                }   else if (item.getItemId() == R.id.MyRequestsFragment) {
                    NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
                    navController.navigate(item.getItemId());
                    return true;
                }
                return false;
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}