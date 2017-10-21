package com.gladysinc.gladys.Models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class TchatResponse {

    @SerializedName("text")
    @Expose
    private String text;

    public TchatResponse() {
    }

    public TchatResponse(String text) {
        super();
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
