package com.codurance.oauthPlayground;

import com.codurance.oauthPlayground.api.Routes;
import com.codurance.oauthPlayground.infrastructure.Authenticator;
import com.codurance.oauthPlayground.infrastructure.GoogleOAuthClient;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Authenticator authenticator = buildAuthenticator();

        MustacheTemplateEngine templateEngine = new MustacheTemplateEngine();

        AuthenticationFilter filter = new AuthenticationFilter(authenticator);

        Routes routes = new Routes();

        routes.init(authenticator, templateEngine, filter);
    }

    private static Authenticator buildAuthenticator() throws IOException {
        GoogleOAuthClient googleAuth = GoogleOAuthClient.buildGoogleOauthClient();

        return new Authenticator(googleAuth);
    }
}
