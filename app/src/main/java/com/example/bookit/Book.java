package com.example.bookit;

public class Book {


    private String imageLink;
    private String bookTitle;
    private String author;
    private String ISBN;
    private String status;
    private String borrower = "N/A";

    public Book(String bookTitle, String author, String ISBN, String status, String imageLink) {
        this.bookTitle = bookTitle;
        this.author = author;
        this.ISBN = ISBN;
        this.status = status;
        this.imageLink = imageLink;
    }

    public Book(String bookTitle, String author, String ISBN, String status, String borrower, String imageLink) {
        this.bookTitle = bookTitle;
        this.author = author;
        this.ISBN = ISBN;
        this.status = status;
        this.borrower = borrower;
        this.imageLink = imageLink;
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

    public void setImageLink(String imageLink) { this.imageLink = imageLink; }

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

    public String getImageLink() { return imageLink; }


}
