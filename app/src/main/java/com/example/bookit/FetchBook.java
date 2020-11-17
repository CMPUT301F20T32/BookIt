/*
 *  Classname: FetchBook
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

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * AsyncTask implementation that opens a network connection and
 * query's the Book Service API.
 */
public class FetchBook extends AsyncTask<String, Void, String> {

    // Variables for the search input field, and results TextViews
    private TextView mBookInput;
    private TextView mTitleText;
    private TextView mAuthorText;

    private String isbn;
    public AsyncResponse delegate = null;

    // Class name for Log tag
    private static final String LOG_TAG = FetchBook.class.getSimpleName();

    // Constructor providing a reference to the views in MainActivity
    public FetchBook(TextView titleText, TextView authorText, TextView bookInput) {
        this.mTitleText = titleText;
        this.mAuthorText = authorText;
        this.mBookInput = bookInput;
    }


    /**
     * Makes the Books API call off of the UI thread.
     *
     * @param params String array containing the search data.
     * @return Returns the JSON string from the Books API or
     * null if the connection failed.
     */
    @Override
    protected String doInBackground(String... params) {

        // Get the search string
        String queryString = params[0];
        isbn = queryString;


        // Set up variables for the try block that need to be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        // Attempt to query the Books API.
        try {
            // Base URI for the Books API.
            final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + queryString;

            /*
             * final String QUERY_PARAM = "q"; // Parameter for the search string.
             * final String MAX_RESULTS = "maxResults"; // Parameter that limits search results.
             *  final String PRINT_TYPE = "printType"; // Parameter to filter by print type.
             * Build up your query URI, limiting results to 10 items and printed books.
             * Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
             * .appendQueryParameter(QUERY_PARAM, queryString)
             * .build();
             */

            Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                    .build();

            URL requestURL = new URL(builtURI.toString());
            Log.d("HELLO", builtURI.toString());

            // Open the network connection.
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Get the InputStream.
            InputStream inputStream = urlConnection.getInputStream();

            // Read the response string into a StringBuilder.
            StringBuilder builder = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                /*
                 * Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                 * but it does make debugging a *lot* easier if you print out the completed buffer for debugging.
                 */
                builder.append(line + "\n");
            }

            if (builder.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            bookJSONString = builder.toString();

            // Catch errors.
        } catch (IOException e) {
            e.printStackTrace();

            // Close the connections.
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        // Return the raw response.
        return bookJSONString;
    }

    /**
     * Handles the results on the UI thread. Gets the information from
     * the JSON and updates the Views.
     * Calls the processFinish method in the {@link AsyncResponse}
     *
     * @param s Result from the doInBackground method containing the raw JSON response,
     *          or null if it failed.
     */
    @Override
    protected void onPostExecute(String s) {
        delegate.processFinish(s, isbn);
    }
}