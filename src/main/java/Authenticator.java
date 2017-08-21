import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.stream.Collectors;

import static java.util.Collections.singleton;

public class Authenticator {

    public static final String TOKEN_INFO_URL = "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=";
    private String callbackUrl;
    private GoogleAuthorizationCodeFlow googleAuth;

    public Authenticator(GoogleAuthorizationCodeFlow googleAuth, String callbackUrl) {
        this.googleAuth = googleAuth;
        this.callbackUrl = callbackUrl;
    }

    public boolean isNotAuthenticated(String token) throws IOException {
        return token == null || (googleAuth.loadCredential(token) == null);
    }

    public String getRedirectUrl() {
         return googleAuth.newAuthorizationUrl()
                .setRedirectUri(callbackUrl)
                .setScopes(singleton("email"))
                .build();
    }

    public User authenticate(String code) throws IOException {
        GoogleTokenResponse googleResponse = googleAuth.newTokenRequest(code)
                .setRedirectUri(callbackUrl)
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

    public Credential loadCredentials(User user) throws IOException {
        return googleAuth.loadCredential(user.getUser_id());
    }
}
