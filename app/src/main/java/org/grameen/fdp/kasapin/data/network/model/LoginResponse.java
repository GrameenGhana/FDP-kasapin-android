package org.grameen.fdp.kasapin.data.network.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("token")
    private String token;

    /**
     * No args constructor for use in serialization
     */
    public LoginResponse() {
    }

    /**
     * @param token token value obtained from the server
     */
    public LoginResponse(String token) {
        super();
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LoginResponse withToken(String token) {
        this.token = token;
        return this;
    }
}