package client;

import chess.*;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import requestandresults.*;
import server.Server;
import ui.ServerFacade;
import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    static String authToken;

    @BeforeAll
    public static void init() throws Exception {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var serverURL = "http://localhost:" + port;
        facade = new ServerFacade(serverURL);
    }

    @BeforeEach
    public void resetDatabase() throws Exception {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void registerValid() throws Exception {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        RegisterResult result = facade.register(request);
        assert result.username().equals("username");
    }

    @Test
    public void registerInvalid()  {
        RegisterRequest request = new RegisterRequest(null, "password", "email");
        assertThrows(ResponseException.class, () -> facade.register(request));
    }

    @Test
    public void loginValid() throws Exception {
        RegisterRequest regRequest = new RegisterRequest("username", "password", "email");
        facade.register(regRequest);
        LoginRequest request = new LoginRequest("username", "password");
        LoginResult result = facade.login(request);
        assertNotNull(result);
        assertNotNull(result.authToken());
        authToken = result.authToken();
    }

    @Test
    public void loginInvalid() {
        LoginRequest request = new LoginRequest("username", "password");
        assertThrows(ResponseException.class, () -> facade.login(request));
    }

    @Test
    public void createGameValid() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResult registerResult = facade.register(registerRequest);
        String auth = registerResult.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest("gameName", auth);
        assertNotNull(createGameRequest);
    }

    @Test
    public void createGameInvalid() throws Exception {
        CreateGameRequest createGameRequest = new CreateGameRequest("gameName", null);
        assertThrows(ResponseException.class, () -> facade.createGame(createGameRequest));
    }

    @Test
    public void listGamesValid() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResult registerResult = facade.register(registerRequest);
        authToken = null;

        CreateGameRequest createGameRequest = new CreateGameRequest("gameName", authToken);
        facade.createGame(createGameRequest);

        ListGamesResult listGamesResult = facade.listGames();
        assertNotNull(listGamesResult);
    }

    @Test
    public void listGamesInvalid() {
        assertThrows(ResponseException.class, () -> facade.listGames());
    }

    @Test
    public void joinGameValid() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        facade.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("username", "password");
        LoginResult loginResult = facade.login(loginRequest);
        String auth = loginResult.authToken();

        CreateGameRequest createGameRequest = new CreateGameRequest("gameName", auth);
        facade.createGame(createGameRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest(auth, ChessGame.TeamColor.WHITE, 1);
        int gameID = joinGameRequest.gameID();
        ChessGame.TeamColor teamColor = joinGameRequest.playerColor();

        assertThrows(ResponseException.class, () -> facade.join(gameID, teamColor));
    }

    @Test
    public void joinGameInvalid() throws Exception {
        if (authToken == null) {
            loginValid();
        }

        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, ChessGame.TeamColor.BLACK, -1);
        int gameID = joinGameRequest.gameID();
        ChessGame.TeamColor teamColor = joinGameRequest.playerColor();
        assertThrows(ResponseException.class, () -> facade.join(gameID, teamColor));
    }

    @Test
    public void clearValid() throws Exception {
        facade.clear();
    }

    @Test
    public void logoutValid() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("username", "password");
        LoginResult result = facade.login(loginRequest);
        authToken = result.authToken();
        assertDoesNotThrow(() -> facade.logout());
    }

    @Test
    public void logoutInvalid() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        facade.register(registerRequest);
        facade.logout();
        assertThrows(ResponseException.class, () -> facade.logout());
    }

}
