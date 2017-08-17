import com.google.gson.Gson;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.oauth.client.Google2Client;
import org.pac4j.sparkjava.CallbackRoute;
import org.pac4j.sparkjava.DefaultHttpActionAdapter;
import org.pac4j.sparkjava.SecurityFilter;
import org.pac4j.sparkjava.SparkWebContext;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        Google2Client googleClient = new Google2Client("", "");

        googleClient.setScope(Google2Client.Google2Scope.PROFILE);

        Clients client = new Clients("http://localhost:4567/callback", googleClient);

        Config config = new Config(client);

        config.setHttpActionAdapter(new DefaultHttpActionAdapter());

        before("/login", new SecurityFilter(config, "Google2Client"));

        get("/", (req, res) -> "hello world!");

        Gson gson = new Gson();

//        CallbackRoute callback = new CallbackRoute(config, null, true);
//
        get("/callback", ((request, response) -> request.body()));
//        post("/callback", callback);
    }
}
