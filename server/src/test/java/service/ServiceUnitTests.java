package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requestandresults.*;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceUnitTests {

    GameDAO memoryGameDAO = new MemoryGameDAO();
    UserDAO memoryUserDAO = new MemoryUserDAO();
    AuthDAO memoryAuthDAO = new MemoryAuthDAO();
    service.userService userService = new userService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
    GameService gameService = new GameService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);

    @BeforeEach
    public void clear() throws DataAccessException{
        userService.clear();
        gameService.clear();
    }

    @Test
    public void registerValid() throws  DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResult registerResult = userService.register(registerRequest);
        assertEquals("username", registerResult.username());
    }

    @Test
    public void registerInvalid() {
        RegisterRequest invalidRequest = new RegisterRequest(null, null, null);
        assertThrows(Exception.class, () -> userService.register(invalidRequest));
    }

    @Test
    public void loginValid() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("username", "password");

        assertNotNull(userService.login(loginRequest).username());
    }

    @Test
    public void loginInvalid() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("username", "wrongpassword");
        assertThrows(Exception.class, () -> userService.login(loginRequest));
    }


    @Test
    public void logoutValid() throws DataAccessException {

        service.userService userService = new userService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
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
    public void logoutInvalid() {
        LogoutRequest invalidLogoutRequest = new LogoutRequest(null);
        assertThrows(DataAccessException.class, () -> userService.logout(invalidLogoutRequest));
    }

    @Test
    public void createGameValid() throws DataAccessException {
        service.userService userService = new userService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
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
    public void createGameInvalid() {
        GameService gameService = new GameService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
        CreateGameRequest invalidRequest = new CreateGameRequest(null, null);
        assertThrows(DataAccessException.class, () -> gameService.createGame(invalidRequest));
    }

    @Test
    public void joinGameValid() throws DataAccessException {
        service.userService userService = new userService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
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
    public void joinGameInvalid() throws DataAccessException {
        service.userService userService = new userService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
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
    public void listGameValid() throws DataAccessException {
        service.userService userService = new userService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
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
    public void listGameInvalid() {

        ListGamesRequest listGamesRequest = new ListGamesRequest(null);

        assertThrows(DataAccessException.class, () -> gameService.listGames(listGamesRequest));
    }

    @Test
    public void clearValid() throws DataAccessException {
        service.userService userService = new userService(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
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
