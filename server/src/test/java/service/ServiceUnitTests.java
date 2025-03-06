package service;

import requestandresults.*;
import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceUnitTests {

    GameDAO memoryGameDAO = new MemoryGameDAO();
    UserDAO memoryUserDAO = new MemoryUserDAO();
    AuthDAO memoryAuthDAO = new MemoryAuthDAO();
    service.userService userService = new userService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
    service.gameService gameService = new gameService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);

    @BeforeEach
    public void clear() throws DataAccessException{
        userService.clear();
        gameService.clear();
    }

    @Test
    public void registerValid() throws  DataAccessException {
        registerRequest registerRequest = new registerRequest("username", "password", "email");
        registerResult registerResult = userService.register(registerRequest);
        assertEquals("username", registerResult.username());
    }

    @Test
    public void registerInvalid() {
        registerRequest invalidRequest = new registerRequest(null, null, null);
        assertThrows(Exception.class, () -> userService.register(invalidRequest));
    }

    @Test
    public void loginValid() throws DataAccessException {
        registerRequest registerRequest = new registerRequest("username", "password", "email");
        userService.register(registerRequest);
        loginRequest loginRequest = new loginRequest("username", "password");

        assertNotNull(userService.login(loginRequest).username());
    }

    @Test
    public void loginInvalid() throws DataAccessException {
        registerRequest registerRequest = new registerRequest("username", "password", "email");
        userService.register(registerRequest);
        loginRequest loginRequest = new loginRequest("username", "wrongpassword");
        assertThrows(Exception.class, () -> userService.login(loginRequest));
    }


    @Test
    public void logout_Valid() throws DataAccessException {

        service.userService userService = new userService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        registerRequest registerRequest = new registerRequest("username", "password", "email@example.com");
        userService.register(registerRequest);

        loginRequest loginRequest = new loginRequest("username", "password");
        loginResult loginResult = userService.login(loginRequest);
        String authToken = loginResult.authToken();
        assertNotNull(authToken);

        logoutRequest logoutRequest = new logoutRequest(authToken);
        userService.logout(logoutRequest);

        AuthData authData = memoryAuthDAO.getAuth(authToken);
        assertNull(authData);//verify token is null
    }


    @Test
    public void logout_Invalid() {
        logoutRequest invalidLogoutRequest = new logoutRequest(null);
        assertThrows(DataAccessException.class, () -> userService.logout(invalidLogoutRequest));
    }

    @Test
    public void createGame_Valid() throws DataAccessException {
        service.userService userService = new userService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        registerRequest registerRequest = new registerRequest("username", "password", "email");
        userService.register(registerRequest);

        loginRequest loginRequest = new loginRequest("username", "password");
        loginResult loginResult = userService.login(loginRequest);
        String authToken = loginResult.authToken();

        service.gameService gameService = new gameService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        createGameRequest createGameRequest = new createGameRequest("gameName", authToken);
        createGameResult createGameResult = gameService.createGame(createGameRequest);

        assert(createGameResult.gameID() >= 0);//Make sure there is a valid ID
    }

    @Test
    public void createGame_Invalid() {
        service.gameService gameService = new gameService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        createGameRequest invalidRequest = new createGameRequest(null, null);
        assertThrows(DataAccessException.class, () -> gameService.createGame(invalidRequest));
    }

    @Test
    public void joinGame_Valid() throws DataAccessException {
        service.userService userService = new userService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        registerRequest registerRequest = new registerRequest("username", "password", "email");
        userService.register(registerRequest);

        loginRequest loginRequest = new loginRequest("username", "password");
        loginResult loginResult = userService.login(loginRequest);
        String authToken = loginResult.authToken();
        service.gameService gameService = new gameService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);

        createGameRequest createGameRequest = new createGameRequest("gameName", authToken);
        createGameResult createGameResult = gameService.createGame(createGameRequest);
        int gameID = createGameResult.gameID();

        joinGameRequest joinGameRequest = new joinGameRequest(authToken, ChessGame.TeamColor.WHITE, gameID);
        gameService.joinGame(joinGameRequest);

        GameData gameData = memoryGameDAO.getGame(gameID);
        assertEquals("username", gameData.whiteUsername());

    }

    @Test
    public void joinGame_Invalid() throws DataAccessException {
        service.userService userService = new userService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        registerRequest registerRequest = new registerRequest("username", "password", "email");
        userService.register(registerRequest);

        loginRequest loginRequest = new loginRequest("username", "password");
        loginResult loginResult = userService.login(loginRequest);
        String authToken = loginResult.authToken();
        service.gameService gameService = new gameService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);

        createGameRequest createGameRequest = new createGameRequest("gameName", authToken);
        createGameResult createGameResult = gameService.createGame(createGameRequest);
        int gameID = createGameResult.gameID();

        joinGameRequest joinGameRequest = new joinGameRequest(authToken, ChessGame.TeamColor.WHITE, gameID);
        gameService.joinGame(joinGameRequest);

        requestandresults.joinGameRequest newJoinRequest =  new joinGameRequest(authToken, ChessGame.TeamColor.WHITE, gameID); //Joining with a taken color
        assertThrows(DataAccessException.class, () -> gameService.joinGame(newJoinRequest));

    }

    @Test
    public void listGame_Valid() throws DataAccessException {
        service.userService userService = new userService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        registerRequest registerRequest = new registerRequest("username", "password", "email");
        userService.register(registerRequest);

        loginRequest loginRequest = new loginRequest("username", "password");
        loginResult loginResult = userService.login(loginRequest);

        String authToken = loginResult.authToken();
        service.gameService gameService = new gameService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);

        createGameRequest createGameRequest = new createGameRequest("gameName", authToken);
        gameService.createGame(createGameRequest);

        listGamesRequest listGamesRequest = new listGamesRequest(authToken);
        listGamesResult listGamesResult = gameService.listGames(listGamesRequest);

        assertNotNull(listGamesResult.games());
    }

    @Test
    public void listGame_Invalid() {

        listGamesRequest listGamesRequest = new listGamesRequest(null);

        assertThrows(DataAccessException.class, () -> gameService.listGames(listGamesRequest));
    }

    @Test
    public void clearValid() throws DataAccessException {
        service.userService userService = new userService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        registerRequest registerRequest = new registerRequest("username", "password", "email");
        userService.register(registerRequest);

        loginRequest loginRequest = new loginRequest("username", "password");
        loginResult loginResult = userService.login(loginRequest);

        String authToken = loginResult.authToken();
        service.gameService gameService = new gameService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);

        createGameRequest createGameRequest = new createGameRequest("gameName", authToken);
        gameService.createGame(createGameRequest);

        userService.clear();

        assertNull(memoryUserDAO.getUser("username"));
        assertNull(memoryAuthDAO.getAuth(authToken));
    }


}
