package server;

import requestandresults.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.DataAccessException;

import spark.Request;
import spark.Response;

import java.util.Map;

public class GameHandler {

    private final service.gameService gameService;

    public GameHandler(service.gameService gameService) {
        this.gameService = gameService;
    }

    public Object createGameHandler(Request request, Response response) {
        try {
            JsonObject jsonObject = new Gson().fromJson(request.body(), JsonObject.class);
            jsonObject.addProperty("authToken", request.headers("authorization"));

            CreateGameRequest createRequest = new Gson().fromJson(jsonObject, CreateGameRequest.class);
            CreateGameResult createResponse = gameService.createGame(createRequest);

            return new Gson().toJson(createResponse);

        } catch (DataAccessException e) {
            response.status(e.getErrorCode());
            return new Gson().toJson(Map.of("message", e.getMessage()));

        }
    }

    public Object joinGameHandler(Request req, Response res) throws DataAccessException {
        JsonObject jsonObject = new Gson().fromJson(req.body(), JsonObject.class);
        jsonObject.addProperty("authToken", req.headers("authorization"));
        JoinGameRequest joinRequest = new Gson().fromJson(jsonObject, JoinGameRequest.class);

        try {
            gameService.joinGame(joinRequest);
            return "{}";

        } catch (DataAccessException e) {
            int statusCode = e.getErrorCode();
            res.status(statusCode);
            JsonObject error = new JsonObject();
            error.addProperty("message", e.getMessage());
            return new Gson().toJson(error);
        }
    }

    public Object listHandler(Request request, Response response) {
        try {

            String authToken = request.headers("authorization");
            ListGamesResult listResponse = gameService.listGames(new ListGamesRequest(authToken));

            return new Gson().toJson(listResponse);

        } catch (DataAccessException e) {
            response.status(e.getErrorCode());
            return new Gson().toJson(Map.of("message", e.getMessage()));

        }
    }

}
