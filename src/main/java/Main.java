import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import spark.ModelAndView;

import spark.template.mustache.MustacheTemplateEngine;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;

import static java.util.Collections.singleton;
import static spark.Spark.*;

public class Main {

    public static void main(String[] args) throws IOException {

        Properties properties = new Properties();

        properties.load(new FileInputStream("config.properties"));

        String callbackUrl = properties.getProperty("CALLBACK_URL");

        HttpTransport transport = new ApacheHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        String cliendId = properties.getProperty("CLIENT_ID");
        String clientSecret = properties.getProperty("CLIENT_SECRET");
        GoogleAuthorizationCodeFlow googleAuth = new GoogleAuthorizationCodeFlow
                .Builder(transport, jsonFactory, cliendId, clientSecret, singleton("email"))
                .setDataStoreFactory(MemoryDataStoreFactory.getDefaultInstance())
                .build();

        Authenticator authenticator = new Authenticator(googleAuth, callbackUrl);

        MustacheTemplateEngine templateEngine = new MustacheTemplateEngine();
        AuthenticationFilter filter = new AuthenticationFilter(authenticator);
        before("/*", filter);

        get("/", (req, res) -> new ModelAndView(new HashMap<>(), "itworks.mustache"), templateEngine);

        get("/playground/protected", ((request, response) -> "I'm protected"));

        get("/callback", ((request, response) -> {
            String code = request.queryParams("code");
            User user = authenticator.authenticate(code);

            if (authenticator.loadCredentials(user) != null) {
                request.session().attribute("token", user.getUser_id());
            }

            response.redirect("/");

            return null;
        }));
    }
}
