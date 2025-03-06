package service;

import RequestsAndResults.*;
import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;

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

        int gameID = new Random().nextInt(10000);
        String gameName = createGameRequest.gameName();
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(gameID, null, null, gameName, game);
        gameDAO.createGame(gameData);
        return new CreateGameResult(gameID);
    }

    public void joinGame(JoinGameRequest joinGameRequest) throws DataAccessException {
        if (joinGameRequest.playerColor() == null ) {
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

        ChessGame.TeamColor teamColor = joinGameRequest.playerColor();

        if (teamColor == ChessGame.TeamColor.WHITE && gameData.whiteUsername() != null) {
            throw new DataAccessException(403, "Error already taken");
        } else if (teamColor == ChessGame.TeamColor.BLACK && gameData.blackUsername() != null) {
            throw new DataAccessException(403, "Error already taken");
        }


        GameData updatedGameData;
        ChessGame chessGame = gameData.game();
        if (teamColor == ChessGame.TeamColor.BLACK) {
            updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), authData.username(), gameData.gameName(), chessGame);
        } else {
            updatedGameData = new GameData(gameData.gameID(), authData.username(), gameData.blackUsername(), gameData.gameName(), chessGame);
        }


        GameData game = updatedGameData;
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

        Collection<GameData> gameList = gameDAO.listGames();
        return new ListGamesResult(gameList);
    }

    public void clear() throws DataAccessException {
        authDAO.clearAllAuth();
        userDAO.clear();
        gameDAO.clear();


    }
}