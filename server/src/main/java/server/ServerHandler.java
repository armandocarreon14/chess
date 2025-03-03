package server;

import RequestsAndResults.*;
import com.google.gson.Gson;
import dataaccess.*;
import passoff.exception.ResponseParseException;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class ServerHandler {

    private final UserService userService;

    public ServerHandler(UserService userService) {
        this.userService = userService;
    }

    public Object RegisterHandler(Request request, Response response) {
        try {
            RegisterRequest registerRequest = new Gson().fromJson(request.body(), RegisterRequest.class);
            RegisterResult registerResult = userService.register(registerRequest);

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

    ///example here
    public Object clearHandler(Request request, Response response) {
        try {
            userService.clear();
            response.status(200);
            return "{}";
        } catch (DataAccessException e) {
            response.status(500);
            String json = new Gson().toJson(Map.of("message", e.getMessage()));         //error example
            response.body(json);
            return json;
        }
    }


    public Object LoginHandler(Request request, Response response) {
        try {
            LoginRequest loginRequest = new Gson().fromJson(request.body(), LoginRequest.class);
            LoginResult loginResult = userService.login(loginRequest);

            response.status(200);
            return new Gson().toJson(loginResult);

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

    public Object LogoutHandler(Request request, Response response) {
        try {
            LogoutRequest logoutRequest = new Gson().fromJson(request.body(), LogoutRequest.class);

            // Ensure logoutRequest is not null before accessing its properties
            if (logoutRequest == null || logoutRequest.authToken() == null) {
                response.status(401);
                String json = new Gson().toJson(Map.of("message", "Error: unauthorized"));
                response.body(json);
                return json;
            }

            userService.logout(logoutRequest);
            response.status(200);
            return "{}"; // Success response

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


