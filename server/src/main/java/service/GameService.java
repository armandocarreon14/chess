package service;

import RequestsAndResults.*;
import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;

import java.util.Random;

public class GameService {

    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        if (createGameRequest.authToken() == null || createGameRequest.authToken().isEmpty()) {
            throw new DataAccessException(401, "Error: unauthorized");
        }

        if (authDAO.getAuth(createGameRequest.authToken()) == null) {
            throw new DataAccessException(401, "Error: unauthorized");
        }

        if (createGameRequest.gameName() == null || createGameRequest.gameName().isEmpty()) {
            throw new DataAccessException(400, "Error: bad request");
        }

        try {
            int gameID = new Random().nextInt(10000);

            return new CreateGameResult(gameID);
        } catch (Exception e) {
            throw new DataAccessException(500, "Error: " + e.getMessage());
        }
    }

    public void joinGame(JoinGameRequest joinGameRequest) throws DataAccessException{
        if (joinGameRequest.playerColor() == null || joinGameRequest.authToken() == null || joinGameRequest.gameID() <= 0) {
            throw new DataAccessException(400, "Error: bad request");
        }


        var authData = authDAO.getAuth(joinGameRequest.authToken());
        if (authData == null) {
            throw new DataAccessException(401, "Error: unauthorized");
        }

        String username = authData.username();
        GameData gameData = gameDAO.getGame(joinGameRequest.gameID());
        if (gameData == null) {
            throw new DataAccessException(400, "Error: no such gameID");
        }

        GameData updatedGameData;

        if (joinGameRequest.playerColor().equals("WHITE") && gameData.whiteUsername() == null) {
            updatedGameData = new GameData(gameData.gameID(), username,
                    gameData.blackUsername(), gameData.gameName(), gameData.game());
        }

        else if (joinGameRequest.playerColor().equals("BLACK") && gameData.blackUsername() == null) {
            updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(),
                    username, gameData.gameName(), gameData.game());
        }
        else {
            throw new DataAccessException(403, "Error: color taken");
        }
        gameDAO.updateGame(updatedGameData);
    }




}