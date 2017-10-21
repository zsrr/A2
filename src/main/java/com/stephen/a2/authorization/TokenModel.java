package com.stephen.a2.authorization;

import java.io.Serializable;

public class TokenModel {
    private Serializable userId;
    private String token;

    public TokenModel(Serializable userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public Serializable getUserId() {
        return userId;
    }

    public void setUserId(Serializable userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
