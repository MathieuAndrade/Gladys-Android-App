package com.gladysinc.gladys.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Gladys project app
 * http://gladysproject.com
 * Created by Mathieu Andrade on 08/03/2018.
 */

public class User {

    @SerializedName("firstname")
    private String firstname;

    @SerializedName("lastname")
    private String lastname;

    @SerializedName("id")
    private Long userId;

    public User(){}

    public User(String firstname, String lastname, Long userId){
        this.firstname = firstname;
        this.lastname = lastname;
        this.userId = userId;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public Long getUserId() {
        return userId;
    }

}
