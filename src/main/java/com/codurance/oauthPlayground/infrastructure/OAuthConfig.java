package com.codurance.oauthPlayground.infrastructure;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

class OAuthConfig {
    private String cliendId;
    private String clientSecret;
    private String callbackUrl;

    public String getCliendId() {
        return cliendId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public OAuthConfig build() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("config.properties"));

        cliendId = properties.getProperty("CLIENT_ID");
        clientSecret = properties.getProperty("CLIENT_SECRET");
        callbackUrl = properties.getProperty("CALLBACK_URL");

        return this;
    }
}
