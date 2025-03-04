package service;

import RequestsAndResults.*;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;

import java.util.Random;

public class GameService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public GameService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        // Validate authToken
        if (createGameRequest.authToken() == null || createGameRequest.authToken().isEmpty()) {
            throw new DataAccessException(401, "Error: unauthorized");
        }

        // Check if the auth token is valid
        if (authDAO.getAuth(createGameRequest.authToken()) == null) {
            throw new DataAccessException(401, "Error: unauthorized");
        }

        // Ensure gameName is not null or empty
        if (createGameRequest.gameName() == null || createGameRequest.gameName().isEmpty()) {
            throw new DataAccessException(400, "Error: bad request");
        }

        try {
            // Simulating game creation (you can implement a real game creation logic)
            int gameID = new Random().nextInt(10000);  // Example gameID generation logic

            return new CreateGameResult(gameID);
        } catch (Exception e) {
            throw new DataAccessException(500, "Error: " + e.getMessage());
        }
    }
}
