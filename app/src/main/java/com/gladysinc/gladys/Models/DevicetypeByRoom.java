package com.gladysinc.gladys.Models;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Table;

import java.util.ArrayList;
import java.util.List;

@Table
public class DevicetypeByRoom {

    @SerializedName("name")
    private String roomName;

    @SerializedName("house")
    private Integer house;

    @SerializedName("id")
    private Long rommId;

    @SerializedName("deviceTypes")
    private List<Devicetype> deviceTypes = new ArrayList<Devicetype>();

    public  DevicetypeByRoom() {}

    public  DevicetypeByRoom(String roomName, Integer house, Long roomId, List<Devicetype> deviceType2) {
        super();
        this.roomName = roomName;
        this.house = house;
        this.rommId = roomId;
        this.deviceTypes = deviceType2;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Integer getHouse() {
        return house;
    }

    public void setHouse(Integer house) {
        this.house = house;
    }

    public Long getRommId() {
        return rommId;
    }

    public void setRommId(Long rommId) {
        this.rommId = rommId;
    }

    public List<Devicetype> getDeviceTypes() {
        return deviceTypes;
    }

    public void setDeviceTypes(List<Devicetype> deviceTypes) {
        this.deviceTypes = deviceTypes;
    }
}

