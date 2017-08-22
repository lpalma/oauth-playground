package com.codurance.oauthPlayground;

import com.codurance.oauthPlayground.infrastructure.Authenticator;
import spark.Filter;
import spark.Request;
import spark.Response;

import java.io.IOException;

public class AuthenticationFilter implements Filter {

    private Authenticator authenticator;

    public AuthenticationFilter(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public void handle(Request request, Response response) throws Exception {
        if (this.isNotCallbackUrl(request) && this.isNotAuthenticated(request)) {
            request.session().invalidate();
            String url = authenticator.getRedirectUrl();
            response.redirect(url);
        }
    }

    private boolean isNotAuthenticated(Request request) throws IOException {

        return authenticator.isNotAuthenticated(request.session().attribute("token"));
    }

    private boolean isNotCallbackUrl(Request request) {

        return !request.url().contains("/callback");
    }
}
