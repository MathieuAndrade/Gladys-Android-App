package com.gladysinc.gladys.Models;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Table;

@Table
public class Alarm {

    @SerializedName("id")
    private Long alarm_id;

    private String name;
    private String datetime;
    private String time;
    private Integer dayofweek;
    private String cronrule;
    private String active;

    public Alarm () {}

    public Alarm (Long alarm_id, String name, String datetime, String time, Integer dayofweek, String cronrule, String active){
        this.alarm_id = alarm_id;
        this.name = name;
        this.datetime = datetime;
        this.time = time;
        this.dayofweek = dayofweek;
        this.cronrule = cronrule;
        this.active = active;
    }

    public Long getAlarm_id() {
        return alarm_id;
    }

    public void setAlarm_id(Long alarm_id) {
        this.alarm_id = alarm_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getDayofweek() {
        return dayofweek;
    }

    public void setDayofweek(Integer dayofweek) {
        this.dayofweek = dayofweek;
    }

    public String getCronrule() {
        return cronrule;
    }

    public void setCronrule(String cronrule) {
        this.cronrule = cronrule;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}
