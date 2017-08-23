package com.codurance.oauthPlayground.infrastructure;

import com.codurance.oauthPlayground.User;
import com.google.api.client.auth.oauth2.Credential;
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
        if (token == null) {
            return true;
        }

        Credential credential = googleAuth.loadCredentials(token);

        return (credential == null) || (credential.getExpiresInSeconds() <= 0);
    }

    public String getRedirectUrl() {
         return googleAuth.getNewAuthorizationUrl(googleAuth.callbackUrl());
    }

    public User authenticate(String code) throws IOException {
        GoogleTokenResponse googleResponse = googleAuth.getTokenResponse(googleAuth.callbackUrl(), code);

        User user = getGoogleUser(googleResponse);

        if (user.getEmail().endsWith(CODURANCE_EMAIL)) {
            googleAuth.createAndStoreCredentials(googleResponse, user);
        }

        return user;
    }

    private User getGoogleUser(GoogleTokenResponse googleResponse) throws IOException {
        String userInfoResponse = fetchGoogleUser(googleResponse);

        Gson gson = new Gson();

        return gson.fromJson(userInfoResponse, User.class);
    }

    private String fetchGoogleUser(GoogleTokenResponse googleResponse) throws IOException {
        String userInfo = TOKEN_INFO_URL + googleResponse.getAccessToken();
        URL url = new URL(userInfo);
        URLConnection conn = url.openConnection();
        return new BufferedReader(new InputStreamReader(conn.getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}
