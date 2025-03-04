package service;

import RequestsAndResults.*;
import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;

import java.util.List;

import java.util.Collection;
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
        if (createGameRequest.authToken() == null) {
            throw new DataAccessException(401, "Error: unauthorized");
        }

        if (authDAO.getAuth(createGameRequest.authToken()) == null) {
            throw new DataAccessException(401, "Error: unauthorized");
        }

        /// QUESTION 1
        int gameID = new Random().nextInt(10000);  //delete this
        return new CreateGameResult(gameID);
    }

    public void joinGame(JoinGameRequest joinGameRequest) throws DataAccessException{
        if (joinGameRequest.playerColor() == null || joinGameRequest.authToken() == null || joinGameRequest.gameID() <= 0) {
            throw new DataAccessException(400, "Error: bad request");
        }

        var authData = authDAO.getAuth(joinGameRequest.authToken());
        if (authData == null) {
            throw new DataAccessException(401, "Error: unauthorized");
        }

        GameData gameData = gameDAO.getGame(joinGameRequest.gameID());
        if (gameData == null) {
            throw new DataAccessException(400, "Error: bad requestD");
        }

//        ChessGame.TeamColor teamColor = joinGameRequest.playerColor();
//
//        if (teamColor == ChessGame.TeamColor.WHITE && gameData.whiteUsername() != null) {
//            throw new DataAccessException(403, "Error already taken");
//        }
//
//        else if (teamColor == ChessGame.TeamColor.BLACK && gameData.blackUsername() != null) {
//            throw new DataAccessException(403, "Error already taken");
//        }

        GameData game = gameDAO.getGame(joinGameRequest.gameID());
//        GameData updatedGameData;
        gameDAO.updateGame(game);
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        if (listGamesRequest.authToken() == null) {
            throw new DataAccessException(400, "Error: invalid request");
        }
        var authData = authDAO.getAuth(listGamesRequest.authToken());
        if (authData == null) {
            throw new DataAccessException(401, "Error: unauthorized");
        }
        var data = gameDAO.listGames();
        return new ListGamesResult((List<GameData>) data);
    }


}