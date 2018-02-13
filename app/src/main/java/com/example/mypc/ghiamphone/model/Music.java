package com.example.mypc.ghiamphone.model;

import java.util.Date;

/**
 * Created by MyPC on 13/02/2018.
 */

public class Music {
    private String name;
    private String path;
    private String status;
    private Date date;
    private String phoneNumber;

    public Music(String name, String path, String status, Date date,String phoneNumber) {
        this.name = name;
        this.path = path;
        this.status = status;
        this.date = date;
        this.phoneNumber = phoneNumber;
    }
    public Music(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
