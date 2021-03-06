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

/**
 * This class represents a Book Object.
 * A Book has a title, author, ISBN, status (available, requested, borrowed, accepted),
 * description, borrower, owner, and requester.
 */
public class Book {
    private String bookTitle;
    private String author;
    private String ISBN;
    private String status;
    private String description;
    private String borrower;
    private String owner;
    private String requester;
    private String bookID;
    private String imageLink;

    /*public Book(String bookTitle, String author, String ISBN) {
        this.bookTitle = bookTitle;
        this.author = author;
        this.ISBN = ISBN;
    }*/

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

    public Book(String bookTitle, String author, String ISBN, String status, String borrower, String bookID, String imageLink) {
        this.bookTitle = bookTitle;
        this.author = author;
        this.ISBN = ISBN;
        this.status = status;
        this.borrower = borrower;
        this.bookID = bookID;
        this.imageLink = imageLink;
    }

    /*
    public Book(String bookTitle, String author, String ISBN, String status, String description, String borrower) {
        this.bookTitle = bookTitle;
        this.author = author;
        this.ISBN = ISBN;
        this.status = status;
        this.description = description;
        this.borrower = borrower;
    }
    */

    public Book(String bookID, String requester, String owner, String imageLink) {
        this.bookID = bookID;
        this.requester = requester;
        this.owner = owner;
        this.imageLink = imageLink;
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

    /**
     * Sets the requester of the Book
     *
     * @param requester the name of the requester
     */
    public void setRequester(String requester) {
        this.requester = requester;
    }

    /**
     * Sets the bookID of the Book
     *
     * @param bookID the name of the requester
     */
    public void setBookID(String bookID) {
        this.bookID = bookID;
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

    /**
     * Returns the requester of the book
     *
     * @return the requester of the book
     */
    public String getRequester() {
        return requester;
    }

    /**
     * Returns the owner of the Book
     *
     * @return the owner of the Book
     */
    public String getOwner() {
        return owner;
    }

    public String getBookID() {return bookID; }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
