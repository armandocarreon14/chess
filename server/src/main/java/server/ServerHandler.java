package server;

import RequestsAndResults.*;
import com.google.gson.Gson;
import dataaccess.*;
import passoff.exception.ResponseParseException;
import service.UserService;
import spark.Request;
import spark.Response;

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
            if (e.getErrorCode() == 403) {
                response.status(403);
                return new Gson().toJson(new DataAccessException(403, "Error: already taken"));
            }

            response.status(400);
            return new Gson().toJson(new DataAccessException(400, "Error: bad request"));

        } catch (Exception e) {

            response.status(500);
            return new Gson().toJson(new DataAccessException(500, "Error: " + e.getMessage()));
        }
    }

    public Object clearHandler(Request request, Response response) {
        try {
            userService.clear();
            return new Gson().toJson(new DataAccessException(200, ""));
        } catch (DataAccessException e) {
            response.status(500);
            return new Gson().toJson(new DataAccessException(500, "Error: " + e.getMessage()));
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
            return new Gson().toJson(new DataAccessException(401, "Error: unauthorized"));

        } catch (Exception e) {
            response.status(500);
            return new Gson().toJson(new DataAccessException(500, "Error: " + e.getMessage()));
        }
    }



}


