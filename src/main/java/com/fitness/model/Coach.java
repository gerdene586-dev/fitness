package com.fitness.model;

public class Coach {

    private long id;
    private String name;
    private String phone;
    private String specialization;
    private String status;

    public Coach() {
    }

    public Coach(long id,
                  String name,
                  String phone,
                  String specialization,
                  String status) {

        this.id = id;
        this.name = name;
        this.phone = phone;
        this.specialization = specialization;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}