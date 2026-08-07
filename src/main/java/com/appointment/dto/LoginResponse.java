package com.appointment.dto;

public class LoginResponse {
    private String token;

    //DEFAULT CONSTRUCTOR
    public LoginResponse() {
    }
    //CONSTRUCTOR
    public LoginResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
