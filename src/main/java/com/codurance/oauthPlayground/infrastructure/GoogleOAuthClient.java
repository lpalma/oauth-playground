package com.codurance.oauthPlayground.infrastructure;

import com.codurance.oauthPlayground.User;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;

import java.io.IOException;

import static java.util.Collections.singleton;

public class GoogleOAuthClient {

    public static final String EMAIL = "email";
    private GoogleAuthorizationCodeFlow googleAuth;
    private OAuthConfig oAuthConfig;

    public GoogleOAuthClient(GoogleAuthorizationCodeFlow googleAuth, OAuthConfig oAuthConfig) {
        this.googleAuth = googleAuth;
        this.oAuthConfig = oAuthConfig;
    }

    public static GoogleOAuthClient buildGoogleOauthClient() throws IOException {
        OAuthConfig oAuthConfig = new OAuthConfig().build();

        String cliendId = oAuthConfig.getCliendId();
        String clientSecret = oAuthConfig.getClientSecret();

        HttpTransport transport = new ApacheHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        GoogleAuthorizationCodeFlow googleAuth = new GoogleAuthorizationCodeFlow
                .Builder(transport, jsonFactory, cliendId, clientSecret, singleton(EMAIL))
                .setDataStoreFactory(MemoryDataStoreFactory.getDefaultInstance())
                .build();

        return new GoogleOAuthClient(googleAuth, oAuthConfig);
    }

    public GoogleTokenResponse getTokenResponse(String callbackUrl, String code) throws IOException {
        return googleAuth.newTokenRequest(code)
                .setRedirectUri(callbackUrl)
                .execute();
    }

    public String getNewAuthorizationUrl(String callbackUrl) {
        return googleAuth.newAuthorizationUrl()
                .setRedirectUri(callbackUrl)
                .setScopes(singleton(EMAIL))
                .build();
    }

    public Credential loadCredentials(String userId) throws IOException {
        return googleAuth.loadCredential(userId);
    }

    public Credential createAndStoreCredentials(GoogleTokenResponse googleResponse, User user) throws IOException {
        return googleAuth.createAndStoreCredential(googleResponse, user.getUser_id());
    }

    public String callbackUrl() {
        return oAuthConfig.getCallbackUrl();
    }
}
