package com.example.bookit;

import org.junit.Test;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

public class NotificationTest {
    private Notification mockNotification() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String date = timestamp.toString();
        return new Notification("This notification is for testing purpose!", date);
    }

    @Test
    public void testGetBookTitle() {
        Notification notification = mockNotification();
        assertEquals("This notification is for testing purpose!", notification.getText());
    }
}
