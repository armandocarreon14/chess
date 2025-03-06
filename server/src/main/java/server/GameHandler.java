package server;

import RequestsAndResults.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import service.GameService;
import dataaccess.DataAccessException;

import spark.Request;
import spark.Response;

import java.util.Map;

public class GameHandler {

    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object CreateGameHandler(Request req, Response res) {
        try {
            JsonObject jsonObject = new Gson().fromJson(req.body(), JsonObject.class);
            jsonObject.addProperty("authToken", req.headers("authorization"));

            CreateGameRequest createRequest = new Gson().fromJson(jsonObject, CreateGameRequest.class);
            CreateGameResult createResponse = gameService.createGame(createRequest);

            res.status(200);
            return new Gson().toJson(createResponse);

        } catch (DataAccessException e) {
            res.status(e.getErrorCode());
            return new Gson().toJson(Map.of("message", e.getMessage()));

        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public Object joinGameHandler(Request req, Response res) throws DataAccessException {
        JsonObject jsonObject = new Gson().fromJson(req.body(), JsonObject.class);
        jsonObject.addProperty("authToken", req.headers("authorization"));
        JoinGameRequest joinRequest = new Gson().fromJson(jsonObject, JoinGameRequest.class);

        try {
            gameService.joinGame(joinRequest);

            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            int statusCode = e.getErrorCode();
            res.status(statusCode);
            JsonObject error = new JsonObject();
            error.addProperty("message", e.getMessage());
            return new Gson().toJson(error);
        } catch (Exception e) {
            res.status(500);
            JsonObject error = new JsonObject();
            error.addProperty("message", "Error: " + e.getMessage());
            return new Gson().toJson(error);
        }
    }

    public Object listHandler(Request request, Response response) {
        try {

            String authToken = request.headers("authorization");
            ListGamesRequest listRequest = new ListGamesRequest(authToken);
            ListGamesResult listResponse = gameService.listGames(listRequest);

            response.status(200);
            return new Gson().toJson(listResponse);

        } catch (DataAccessException e) {
            response.status(e.getErrorCode());
            return new Gson().toJson(Map.of("message", e.getMessage()));

        } catch (Exception e) {
            response.status(500);
            return new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

}
