package com.codurance.oauthPlayground.infrastructure;

import com.codurance.oauthPlayground.User;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.stream.Collectors;

public class Authenticator {

    public static final String TOKEN_INFO_URL = "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=";
    public static final String CODURANCE_EMAIL = "@codurance.com";
    public GoogleOAuthClient googleAuth;

    public Authenticator(GoogleOAuthClient client) {
        this.googleAuth = client;
    }

    public boolean isNotAuthenticated(String token) throws IOException {
        return token == null || (googleAuth.loadCredentials(token) == null);
    }

    public String getRedirectUrl() {
         return googleAuth.getNewAuthorizationUrl(googleAuth.callbackUrl());
    }

    public User authenticate(String code) throws IOException {
        GoogleTokenResponse googleResponse = googleAuth.getTokenResponse(googleAuth.callbackUrl(), code);

        String userInfo = TOKEN_INFO_URL + googleResponse.getAccessToken();
        URL url = new URL(userInfo);
        URLConnection conn = url.openConnection();
        String userInfoResponse = new BufferedReader(new InputStreamReader(conn.getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));

        Gson gson = new Gson();

        User user = gson.fromJson(userInfoResponse, User.class);

        if (user.getEmail().endsWith(CODURANCE_EMAIL)) {
            googleAuth.createAndStoreCredentials(googleResponse, user);
        }

        return user;
    }
}
