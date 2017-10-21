package com.gladysinc.gladys.Models;

import com.orm.SugarRecord;

public class Event extends SugarRecord {

    private String name;
    private String code;
    private String datetime;
    private Integer user;

    public Event(){}

    public Event(String name, String code, String datetime, Integer user){
        this.name = name;
        this.code = code;
        this.datetime = datetime;
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode(){
        return  code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }
}

