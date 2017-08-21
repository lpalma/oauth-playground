import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.gson.Gson;
import spark.Filter;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static spark.Spark.*;

public class Main {

    public static final String CALLBACK_URL = "http://localhost:4567/callback";
    public static final String TOKEN_INFO_URL = "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=";

    public static void main(String[] args) throws IOException {
        MustacheTemplateEngine templateEngine = new MustacheTemplateEngine();

        HttpTransport transport = new ApacheHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        String cliendId = "";
        String clientSecret = "";
        Collection<String> scopes = singletonList("email");
        GoogleAuthorizationCodeFlow googleAuth = new GoogleAuthorizationCodeFlow
                .Builder(transport, jsonFactory, cliendId, clientSecret, scopes)
                .setDataStoreFactory(MemoryDataStoreFactory.getDefaultInstance())
                .build();

        before("/*", new Filter() {

            @Override
            public void handle(Request request, Response response) throws Exception {
                if(request.url().contains("/callback")) {
                    return;
                }

                if(request.session().attribute("token") != null) {
                    if(googleAuth.loadCredential(request.session().attribute("token")) == null) {
                        request.session().invalidate();
                        response.redirect("/");
                    }
                } else {
                    String url = googleAuth.newAuthorizationUrl()
                            .setRedirectUri(CALLBACK_URL)
                            .setScopes(scopes)
                            .build();

                    response.redirect(url);
                }
            }
        });

        get("/", (req, res) -> new ModelAndView(new HashMap<>(), "itworks.mustache"), templateEngine);

        get("/playground/protected", ((request, response) -> "I'm protected"));


        get("/callback", ((request, response) -> {
            String code = request.queryParams("code");
            User user = authenticate(googleAuth, code);

            if (googleAuth.loadCredential(user.getUser_id()) != null) {
                request.session().attribute("token", user.getUser_id());
            }

            response.redirect("/");

            return null;
        }));
    }

    private static User authenticate(GoogleAuthorizationCodeFlow googleAuth, String code) throws IOException {
        GoogleTokenResponse googleResponse = googleAuth.newTokenRequest(code)
                .setRedirectUri(CALLBACK_URL)
                .execute();

        String userInfo = TOKEN_INFO_URL + googleResponse.getAccessToken();
        URL url = new URL(userInfo);
        URLConnection conn = url.openConnection();
        String userInfoResponse = new BufferedReader(new InputStreamReader(conn.getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));

        Gson gson = new Gson();

        User user = gson.fromJson(userInfoResponse, User.class);

        if (user.getEmail().endsWith("@codurance.com")) {
            googleAuth.createAndStoreCredential(googleResponse, user.getUser_id());
        }

        return user;
    }
}
