package com.codurance.oauthPlayground;

import com.google.gson.annotations.SerializedName;

public class User {

    private static final String CODURANCE_EMAIL = "@codurance.com";

    @SerializedName("user_id")
    private String userId;

    private String email;

    public boolean isFromCodurance() {
        return getEmail().endsWith(CODURANCE_EMAIL);
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}
