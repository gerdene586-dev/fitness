package com.fitness.model;

public class Attendance {

    private long id;
    private long bookingId;
    private String date;
    private String status;

    public Attendance() {
    }

    public Attendance(long id,
                      long bookingId,
                      String date,
                      String status) {

        this.id = id;
        this.bookingId = bookingId;
        this.date = date;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBookingId() {
        return bookingId;
    }

    public void setBookingId(long bookingId) {
        this.bookingId = bookingId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}