package server;

import com.google.gson.Gson;
import service.GameService;
import RequestsAndResults.CreateGameRequest;
import RequestsAndResults.CreateGameResult;
import dataaccess.DataAccessException;

import spark.Request;
import spark.Response;

import java.util.Map;

public class GameHandler {

    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object createGameHandler(Request request, Response response) {
        try {
            String authToken = request.headers("Authorization");
            String gameName = request.queryParams("gameName");

            // Ensure authToken and gameName are not null or empty
            if (authToken == null || authToken.isEmpty() || gameName == null || gameName.isEmpty()) {
                response.status(400);
                String json = new Gson().toJson(Map.of("message", "Error: bad request"));
                response.body(json);
                return json;
            }

            // Create the game using the GameService
            CreateGameRequest createGameRequest = new CreateGameRequest(authToken, gameName);
            CreateGameResult createGameResult = gameService.createGame(createGameRequest);

            // Return the success response with the gameID
            response.status(200);
            return new Gson().toJson(createGameResult); // Response with gameID

        } catch (DataAccessException e) {
            // Handle unauthorized access
            if (e.getMessage().contains("unauthorized")) {
                response.status(401);
                String json = new Gson().toJson(Map.of("message", "Error: unauthorized"));
                response.body(json);
                return json;
            }

            // Handle other DataAccessExceptions
            response.status(500);
            String json = new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
            response.body(json);
            return json;

        } catch (Exception e) {
            // Handle unexpected errors
            response.status(500);
            String json = new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
            response.body(json);
            return json;
        }
    }
}
