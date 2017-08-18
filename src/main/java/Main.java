import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.oauth.client.Google2Client;
import org.pac4j.oauth.credentials.OAuth20Credentials;
import org.pac4j.sparkjava.DefaultHttpActionAdapter;
import org.pac4j.sparkjava.SecurityFilter;
import org.pac4j.sparkjava.SparkWebContext;

import static spark.Spark.before;
import static spark.Spark.get;

public class Main {
    public static void main(String[] args) {
        Google2Client googleClient = new Google2Client("", "");

        googleClient.setScope(Google2Client.Google2Scope.EMAIL_AND_PROFILE);

        Clients client = new Clients("http://localhost:4567/callback", googleClient);

        Config config = new Config(client);

        config.setHttpActionAdapter(new DefaultHttpActionAdapter());

        before("/login", new SecurityFilter(config, "Google2Client", "domain"));

        get("/", (req, res) -> "hello world!");

        get("/callback", ((request, response) -> {
            SparkWebContext context = new SparkWebContext(request, response);

            OAuth20Credentials credentials = googleClient.getCredentials(context);

            return googleClient.getUserProfile(credentials, context).getAttributes();
        }));
    }
}
