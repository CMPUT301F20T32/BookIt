package com.example.bookit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BookTest {

    private Book mockBook1() {
        return new Book("Harry Potter and the Goblet of Fire", "J.K Rowling",
                "123456789", "available",
                "Ron", "987654321", "");
    }

    @Test
    public void testGetBookTitle() {
        Book book = mockBook1();
        assertEquals("Harry Potter and the Goblet of Fire", book.getBookTitle());
    }

    @Test
    public void testGetAuthor() {
        Book book = mockBook1();
        assertEquals("J.K Rowling", book.getAuthor());
    }

    @Test
    public void testGetISBN() {
        Book book = mockBook1();
        assertEquals("123456789", book.getISBN());
    }

    @Test
    public void testGetStatus() {
        Book book = mockBook1();
        assertEquals("available", book.getStatus());
    }

    @Test
    public void testGetBookID() {
        Book book = mockBook1();
        assertEquals("987654321", book.getBookID());
    }

    @Test
    public void testGetBorrower() {
        Book book = mockBook1();
        assertEquals("Ron", book.getBorrower());
    }

    @Test
    public void testSetBookTitle() {
        Book book = mockBook1();
        book.setBookTitle("Lord of the Rings");
        assertEquals("Lord of the Rings", book.getBookTitle());
    }

    @Test
    public void testSetAuthor() {
        Book book = mockBook1();
        book.setAuthor("Darth Vader");
        assertEquals("Darth Vader", book.getAuthor());
    }

    @Test
    public void testSetISBN() {
        Book book = mockBook1();
        book.setISBN("111111111");
        assertEquals("111111111", book.getISBN());
    }

    @Test
    public void testSetStatus() {
        Book book = mockBook1();
        book.setStatus("requested");
        assertEquals("requested", book.getStatus());
    }

    @Test
    public void testSetBookID() {
        Book book = mockBook1();
        book.setBookID("4567838765");
        assertEquals("4567838765", book.getBookID());
    }

    @Test
    public void testSetBorrower() {
        Book book = mockBook1();
        book.setBorrower("Luke");
        assertEquals("Luke", book.getBorrower());
    }
}
