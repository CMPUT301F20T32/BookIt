package com.example.bookit;

import java.util.Date;

public class Notification implements Comparable<Notification>{

    private String userName;
    private String status;
    private String text;
    private String datetime;

    public Notification(String text) {
        this.text = text;
    }

    public Notification(String text, String datetime) {
        this.text = text;
        this.datetime = datetime;
    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDatetime() { return datetime;
    }
    public void setDatetime(String datetime) { this.datetime = datetime;
    }

    @Override
    public int compareTo(Notification o) {
        return getDatetime().compareTo(o.getDatetime());
    }
}
