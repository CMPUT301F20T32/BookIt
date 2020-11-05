package com.example.bookit;

import java.util.ArrayList;

public class Book {
    private String bookTitle;
    private String author;
    private String ISBN;
    private String status;
    private String borrower = "N/A";

    private String requester;

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

    public Book(String bookTitle, String requester){
        this.bookTitle = bookTitle;
        this.requester = requester;
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

    public void setRequester(String requester) {
        this.requester = requester;
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

    public String getRequester() {
        return requester;
    }


}
