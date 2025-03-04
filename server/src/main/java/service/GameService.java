package service;

import RequestsAndResults.*;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class GameService {

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;

    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        return null;
    }
}
