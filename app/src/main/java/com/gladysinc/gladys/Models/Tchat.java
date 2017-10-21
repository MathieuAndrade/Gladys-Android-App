package com.gladysinc.gladys.Models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tchat {

    @SerializedName("response")
    @Expose
    private TchatResponse response;


    public Tchat() {
    }

    public Tchat(TchatResponse response) {
        super();
        this.response = response;
    }

    public TchatResponse getResponse() {
        return response;
    }

    public void setResponse(TchatResponse response) {
        this.response = response;
    }


}
