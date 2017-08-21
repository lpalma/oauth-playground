import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.gson.Gson;
import spark.Filter;
import spark.Request;
import spark.Response;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Properties;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

class AuthenticationFilter implements Filter {

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
