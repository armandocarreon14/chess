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

        /// QUESTION 1: game ID doesn't seem to work
        String gameName = createGameRequest.gameName();
        int gameID = new Random().nextInt(10000); ;
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(gameID, null, null, gameName, game);
        gameDAO.createGame(gameData);
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

        /// QUESTION 2: JOIN STEAL TEAM COLOR TEST (403 Already taken) seems like it's not working (taking a color that's taken)
        ChessGame.TeamColor teamColor = joinGameRequest.playerColor();

        if (teamColor == ChessGame.TeamColor.WHITE && gameData.whiteUsername() != null) {
            throw new DataAccessException(403, "Error already taken");
        }

        else if (teamColor == ChessGame.TeamColor.BLACK && gameData.blackUsername() != null) {
            throw new DataAccessException(403, "Error already taken");
        }

        /// QUESTION 3: JOIN CREATED GAME TEST
        GameData updatedGameData;
        ChessGame chessGame = gameData.game();
        //if black, put all the data including the white username
        if(teamColor == ChessGame.TeamColor.BLACK){
            updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), authData.username(), gameData.gameName(), chessGame);
        }

        else {
            updatedGameData = new GameData(gameData.gameID(), gameData.blackUsername(), authData.username(), gameData.gameName(), chessGame);
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

        /// Q4: GAMES LIST
        var gamesList = gameDAO.listGames();
        //return new ListGamesResult(gamesList);
        return null;
    }


}