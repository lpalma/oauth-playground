package com.codurance.oauthPlayground.api;

import com.codurance.oauthPlayground.AuthenticationFilter;
import com.codurance.oauthPlayground.User;
import com.codurance.oauthPlayground.infrastructure.Authenticator;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;

import static spark.Spark.before;
import static spark.Spark.get;

public class Routes {
    public void init(Authenticator authenticator, MustacheTemplateEngine templateEngine, AuthenticationFilter filter) {
        before("/*", filter);

        get("/", (req, res) -> new ModelAndView(new HashMap<>(), "itworks.mustache"), templateEngine);

        get("/playground/protected", ((request, response) -> "I'm protected"));

        get("/callback", ((request, response) -> {
            String code = request.queryParams("code");
            User user = authenticator.authenticate(code);

            request.session().attribute("token", user.getUserId());

            response.redirect("/");

            return null;
        }));
    }
}
