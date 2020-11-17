/*
 *  Interface: AsyncResponse
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

/**
 * This interface is used to get the response from {@link FetchBook} and
 * pass on the data to {@link ScanBookActivity}
 */
public interface AsyncResponse {

    /**
     * This should be called after the AsyncTAsk is finished
     *
     * @param output Result from the doInBackground method containing the raw JSON response,
     *               or null if it failed.
     * @param isbn   isbn number of the book scanned (more precisely, the barcode value)
     */
    public void processFinish(String output, String isbn);
}
