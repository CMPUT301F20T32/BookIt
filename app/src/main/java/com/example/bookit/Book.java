package com.example.bookit;

import java.util.ArrayList;

public class Book {
    private String bookTitle;
    private String author;
    private String ISBN;
    private String status;
    private String description;
    private String borrower = "N/A";
    private String owner;
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


    public Book(String bookTitle, String author, String ISBN, String status, String description, String borrower) {
        this.bookTitle = bookTitle;
        this.author = author;
        this.ISBN = ISBN;
        this.status = status;
        this.description = description;
        this.borrower = borrower;
    }

    public Book(String bookTitle, String requester, String owner) {
        this.bookTitle = bookTitle;
        this.requester = requester;
        this.owner = owner;
    }

    /**
     * Sets the bookTitle. This is the name of the Book
     *
     * @param bookTitle Book name
     */
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    /**
     * Sets the Author name for the Book
     *
     * @param author Author name
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Sets the ISBN of the Book
     *
     * @param ISBN ISBN of the Book
     */
    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    /**
     * Sets the status of the Book
     *
     * @param status status of the Book
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the description of the Book
     *
     * @param description description of the Book
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the borrower of the Book (if any)
     *
     * @param borrower The name/userid of the borrower
     */
    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    /**
     * Sets the owner of the Book
     *
     * @param owner the owner of the Book
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Returns the name of the Book
     *
     * @return the name of the Book
     */
    public String getBookTitle() {
        return bookTitle;
    }

    /**
     * Returns the name of the Author
     *
     * @return the name of the Author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Returns the ISBN of the Book
     *
     * @return the ISBN of the Book
     */
    public String getISBN() {
        return ISBN;
    }

    /**
     * Returns the Status of the Book
     *
     * @return the status of the Book
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns the description of the Book
     *
     * @return the description of the Book
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the borrower name (if any)
     *
     * @return the borrower name (if any)
     */
    public String getBorrower() {
        return borrower;
    }

    public String getRequester() {
        return requester;
    }

    /**
     * Returns the owner name
     *
     * @return the owner name
     */
    public String getOwner() {
        return owner;
    }

}
