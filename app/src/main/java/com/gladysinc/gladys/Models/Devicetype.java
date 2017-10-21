package com.gladysinc.gladys.Models;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Table;

@Table
public class Devicetype {

    @SerializedName("name")
    private String devicetypeName;

    @SerializedName("id")
    private Long devicetypeId;

    @SerializedName("type")
    private String type;

    @SerializedName("category")
    private String category;

    @SerializedName("tag")
    private String tag;

    @SerializedName("unit")
    private String unit;

    @SerializedName("min")
    private Integer min;

    @SerializedName("max")
    private Integer max;

    @SerializedName("display")
    private Long display;

    @SerializedName("sensor")
    private Long sensor;

    @SerializedName("lastChanged")
    private String lastChanged;

    @SerializedName("lastValue")
    private Float lastValue;

    private Long roomId;
    private String roomName;

    public Devicetype() {}

    public Devicetype(String devicetypeName, Long devicetypeId, String type
            , String category, String tag, String unit, Integer min, Integer max
            , Long display, Long sensor, String lastChanged, Float lastValue
            , Long roomId, String roomName) {
        super();
        this.devicetypeName = devicetypeName;
        this.devicetypeId = devicetypeId;
        this.type = type;
        this.category = category;
        this.tag = tag;
        this.unit = unit;
        this.min = min;
        this.max = max;
        this.display = display;
        this.sensor = sensor;
        this.lastChanged = lastChanged;
        this.lastValue = lastValue;
        this.roomId = roomId;
        this.roomName = roomName;
    }

    public String getDevicetypeName() {
        return devicetypeName;
    }

    public void setDevicetypeName(String devicetypeName) {
        this.devicetypeName = devicetypeName;
    }

    public Long getDevicetypeId() {
        return devicetypeId;
    }

    public void setDevicetypeId(Long devicetypeId) {
        this.devicetypeId = devicetypeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Long getDisplay() {
        return display;
    }

    public void setDisplay(Long display) {
        this.display = display;
    }

    public Long getSensor() {
        return sensor;
    }

    public void setSensor(Long sensor) {
        this.sensor = sensor;
    }

    public String getLastChanged() {
        return lastChanged;
    }

    public void setLastChanged(String lastChanged) {
        this.lastChanged = lastChanged;
    }

    public Float getLastValue() {
        return lastValue;
    }

    public void setLastValue(Float lastValue) {
        this.lastValue = lastValue;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}

