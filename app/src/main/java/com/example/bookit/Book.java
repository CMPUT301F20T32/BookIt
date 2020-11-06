/*
 *  Classname: Book
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

public class Book {
    private String bookTitle;
    private String author;
    private String ISBN;
    private String status;
    private String borrower = "N/A";

    public Book(String bookTitle, String author, String ISBN, String status) {
        this.bookTitle = bookTitle;
        this.author = author;
        this.ISBN = ISBN;
        this.status = status;
    }

    public Book(String bookTitle, String author, String ISBN, String status, String borrower) {
        this.bookTitle = bookTitle;
        this.author = author;
        this.ISBN = ISBN;
        this.status = status;
        this.borrower = borrower;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getAuthor() {
        return author;
    }

    public String getISBN() {
        return ISBN;
    }

    public String getStatus() {
        return status;
    }

    public String getBorrower() {
        return borrower;
    }


}
