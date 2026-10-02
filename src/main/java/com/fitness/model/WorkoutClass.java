package com.fitness.model;

public class WorkoutClass {

    private long id;
    private String name;
    private long coachId;
    private String date;
    private String time;
    private String room;
    private int capacity;
    private String status;

    public WorkoutClass() {
    }

    public WorkoutClass(long id,
                        String name,
                        long coachId,
                        String date,
                        String time,
                        String room,
                        int capacity,
                        String status) {

        this.id = id;
        this.name = name;
        this.coachId = coachId;
        this.date = date;
        this.time = time;
        this.room = room;
        this.capacity = capacity;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCoachId() {
        return coachId;
    }

    public void setCoachId(long coachId) {
        this.coachId = coachId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}