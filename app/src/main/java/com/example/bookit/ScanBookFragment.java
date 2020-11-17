/*
 *  Classname: ScanBookFragment
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

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;


/**
 * This fragment is used to open the {@link ScanBookActivity} and display the results
 * of scanning a barcode obtained from the {@link ScanBookActivity}
 */
public class ScanBookFragment extends Fragment {
    private TextView titleTextView;
    private TextView authorTextView;


    public ScanBookFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan_book, container, false);
    }

    /**
     * Sets an onClickListener on the scan_book_image_view ImageView
     * Opens the {@link ScanBookActivity}
     *
     * @param view               The View returned by onCreateView(android.view.LayoutInflater,
     *                           android.view.ViewGroup, android.os.Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here. This value may be null
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView scanBook = view.findViewById(R.id.scan_book_image_view);
        titleTextView = view.findViewById(R.id.book_title_text_view);
        authorTextView = view.findViewById(R.id.book_author_text_view);

        scanBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the ScanBookActivity
                Intent intent = new Intent(getActivity(), ScanBookActivity.class);
                startActivityForResult(intent, 1);

            }
        });
    }

    /**
     * This is called when {@link ScanBookActivity} is finished.
     * It displays the results received from {@link ScanBookActivity}
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                titleTextView.setText(data.getStringExtra("title"));
                authorTextView.setText(data.getStringExtra("authors"));
            } else {
                titleTextView.setText("No results");
                authorTextView.setText("No results");
            }
        }
    }

}