/*
 *  Classname: ProfileActivity
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


import android.app.FragmentTransaction;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
/**
 * ProfileActivity refers to the view My Profile functionality of the application.
 * This activity is opened when the user clicks on the profile icon.
 * This activity navigates to  myProfileFragment.
 *
 * @author Alisha Crasta
 * @version 1.0
 * @since 1.0
 */

    public class ProfileActivity extends AppCompatActivity {
    /**
     * This method is used on the creation of this activity.
     * It navigates to myProfileFragment.
     *
     * @param savedInstanceState refers to the cached state of the UI.
     */
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_profile);

            Bundle bundle = new Bundle();
            if(getIntent().hasExtra("user")){
                bundle.putString("user",getIntent().getExtras().getString("user"));
            }

            MyProfileFragment myProfileFragment = new MyProfileFragment();
            myProfileFragment.setArguments(bundle);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, myProfileFragment); // give your fragment container id in first parameter
            transaction.commit();
        }
    }
