package com.fitness.model;

public class ClassBooking {

    private long id;
    private long memberId;
    private long classId;
    private String bookingDate;
    private String status;

    public ClassBooking() {
    }

    public ClassBooking(long id,
                        long memberId,
                        long classId,
                        String bookingDate,
                        String status) {

        this.id = id;
        this.memberId = memberId;
        this.classId = classId;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public long getClassId() {
        return classId;
    }

    public void setClassId(long classId) {
        this.classId = classId;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}