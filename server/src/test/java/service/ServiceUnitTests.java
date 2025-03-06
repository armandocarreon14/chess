package service;

import RequestsAndResults.*;
import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceUnitTests {

    GameDAO memoryGameDAO = new MemoryGameDAO();
    UserDAO memoryUserDAO = new MemoryUserDAO();
    AuthDAO memoryAuthDAO = new MemoryAuthDAO();
    UserService userService = new UserService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
    GameService gameService = new GameService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);

    @BeforeEach
    public void clear() throws DataAccessException{
        userService.clear();
        gameService.clear();
    }

    @Test
    public void Register_Valid() throws  DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResult registerResult = userService.register(registerRequest);
        assertEquals("username", registerResult.username());
    }

    @Test
    public void Register_Invalid() {
        RegisterRequest invalidRequest = new RegisterRequest(null, null, null);
        assertThrows(Exception.class, () -> userService.register(invalidRequest));
    }

    @Test
    public void Login_valid () throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("username", "password");

        assertNotNull(userService.login(loginRequest).username());
    }

    @Test
    public void Login_Invalid() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("username", "wrongpassword");
        assertThrows(Exception.class, () -> userService.login(loginRequest));
    }


    @Test
    public void logout_Valid() throws DataAccessException {

        UserService userService = new UserService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email@example.com");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("username", "password");
        LoginResult loginResult = userService.login(loginRequest);
        String authToken = loginResult.authToken();
        assertNotNull(authToken);

        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        userService.logout(logoutRequest);

        AuthData authData = memoryAuthDAO.getAuth(authToken);
        assertNull(authData);//verify token is null
    }


    @Test
    public void logout_Invalid() {
        LogoutRequest invalidLogoutRequest = new LogoutRequest(null);
        assertThrows(DataAccessException.class, () -> userService.logout(invalidLogoutRequest));
    }

    @Test
    public void createGame_Valid() throws DataAccessException {
        UserService userService = new UserService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("username", "password");
        LoginResult loginResult = userService.login(loginRequest);
        String authToken = loginResult.authToken();

        GameService gameService = new GameService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        CreateGameRequest createGameRequest = new CreateGameRequest("gameName", authToken);
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);

        assert(createGameResult.gameID() >= 0);//Make sure there is a valid ID
    }

    @Test
    public void createGame_Invalid() {
        GameService gameService = new GameService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        CreateGameRequest invalidRequest = new CreateGameRequest(null, null);
        assertThrows(DataAccessException.class, () -> gameService.createGame(invalidRequest));
    }

    @Test
    public void joinGame_Valid() throws DataAccessException {
        UserService userService = new UserService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("username", "password");
        LoginResult loginResult = userService.login(loginRequest);
        String authToken = loginResult.authToken();
        GameService gameService = new GameService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);

        CreateGameRequest createGameRequest = new CreateGameRequest("gameName", authToken);
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        int gameID = createGameResult.gameID();

        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, ChessGame.TeamColor.WHITE, gameID);
        gameService.joinGame(joinGameRequest);

        GameData gameData = memoryGameDAO.getGame(gameID);
        assertEquals("username", gameData.whiteUsername());

    }

    @Test
    public void joinGame_Invalid() throws DataAccessException {
        UserService userService = new UserService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("username", "password");
        LoginResult loginResult = userService.login(loginRequest);
        String authToken = loginResult.authToken();
        GameService gameService = new GameService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);

        CreateGameRequest createGameRequest = new CreateGameRequest("gameName", authToken);
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        int gameID = createGameResult.gameID();

        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, ChessGame.TeamColor.WHITE, gameID);
        gameService.joinGame(joinGameRequest);

        JoinGameRequest newJoinRequest =  new JoinGameRequest(authToken, ChessGame.TeamColor.WHITE, gameID); //Joining with a taken color
        assertThrows(DataAccessException.class, () -> gameService.joinGame(newJoinRequest));

    }

    @Test
    public void listGame_Valid() throws DataAccessException {
        UserService userService = new UserService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("username", "password");
        LoginResult loginResult = userService.login(loginRequest);

        String authToken = loginResult.authToken();
        GameService gameService = new GameService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);

        CreateGameRequest createGameRequest = new CreateGameRequest("gameName", authToken);
        gameService.createGame(createGameRequest);

        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        ListGamesResult listGamesResult = gameService.listGames(listGamesRequest);

        assertNotNull(listGamesResult.games());
    }

    @Test
    public void listGame_Invalid() {

        ListGamesRequest listGamesRequest = new ListGamesRequest(null);

        assertThrows(DataAccessException.class, () -> gameService.listGames(listGamesRequest));
    }

    @Test
    public void Clear () throws DataAccessException {
        UserService userService = new UserService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("username", "password");
        LoginResult loginResult = userService.login(loginRequest);

        String authToken = loginResult.authToken();
        GameService gameService = new GameService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);

        CreateGameRequest createGameRequest = new CreateGameRequest("gameName", authToken);
        gameService.createGame(createGameRequest);

        userService.clear();

        assertNull(memoryUserDAO.getUser("username"));
        assertNull(memoryAuthDAO.getAuth(authToken));
    }


}
