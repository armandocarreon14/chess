package server;

import requestandresults.*;
import com.google.gson.Gson;
import dataaccess.*;
import spark.Request;
import spark.Response;

import java.util.Map;

public class UserHandler {

    private final service.userService UserService;

    public UserHandler(service.userService userService) {
        this.UserService = userService;
    }

    public Object registerHandler(Request request, Response response) {
        try {
            RegisterRequest registerRequest = new Gson().fromJson(request.body(), RegisterRequest.class);
            RegisterResult registerResult = UserService.register(registerRequest);

            response.status(200);
            return new Gson().toJson(registerResult);

        } catch (DataAccessException e) {
            response.status(e.getErrorCode());
            String json = new Gson().toJson(Map.of("message", e.getMessage()));
            response.body(json);
            return json;

        } catch (Exception e) {
            response.status(500);
            String json = new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
            response.body(json);
            return json;
        }
    }


    public Object clearHandler(Request request, Response response) {
        try {
            UserService.clear();
            response.status(200);
            return "{}";
        } catch (DataAccessException e) {
            response.status(500);
            String json = new Gson().toJson(Map.of("message", e.getMessage()));
            response.body(json);
            return json;
        }
    }


    public Object loginHandler(Request request, Response response) {
        try {
            LoginRequest loginRequest = new Gson().fromJson(request.body(), LoginRequest.class);
            LoginResult loginResult = UserService.login(loginRequest);

            return new Gson().toJson(loginResult);

        } catch (DataAccessException e) {
            response.status(401);
            String json = new Gson().toJson(Map.of("message", "Error: " +e.getMessage()));
            response.body(json);
            return json;

        } catch (Exception e) {
            response.status(500);
            String json = new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
            response.body(json);
            return json;
        }
    }

    public Object logoutHandler(Request request, Response response) {
        try {
            String authToken = request.headers("Authorization");

            if (authToken == null || authToken.isEmpty()) {
                response.status(401);
                String json = new Gson().toJson(Map.of("message", "Error: unauthorized"));
                response.body(json);
                return json;
            }

            UserService.logout(new LogoutRequest(authToken));
            return "{}";

        } catch (DataAccessException e) {
            response.status(401);
            String json = new Gson().toJson(Map.of("message", "Error: unauthorized"));
            response.body(json);
            return json;

        } catch (Exception e) {
            response.status(500);
            String json = new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
            response.body(json);
            return json;
        }
    }






}


