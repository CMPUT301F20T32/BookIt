package com.example.bookit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BookTest {

    private Book mockBook1() {
        return new Book("Harry Potter and the Goblet of Fire", "J.K Rowling",
                "123456789", "available",
                "When Harry is chosen as a fourth participant of the inter-school " +
                        "Triwizard Tournament, he is unwittingly pulled into a dark conspiracy " +
                        "that endangers his life.", "Ron");
    }

    @Test
    public void testGetBookTitle() {
        Book book = mockBook1();
        assertEquals("Harry Potter and the Goblet of Fir", book.getBookTitle());
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
    public void testGetDescription() {
        Book book = mockBook1();
        assertEquals("When Harry is chosen as a fourth participant of the inter-school " +
                "Triwizard Tournament, he is unwittingly pulled into a dark conspiracy " +
                "that endangers his life.", book.getDescription());
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
    public void testSetDescription() {
        Book book = mockBook1();
        book.setDescription("I am your father");
        assertEquals("I am your father", book.getDescription());
    }

    @Test
    public void testSetBorrower() {
        Book book = mockBook1();
        book.setBorrower("Luke");
        assertEquals("Luke", book.getBorrower());
    }
}
