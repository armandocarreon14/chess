package client;

import chess.ChessGame;
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
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        var serverURL = "http://localhost:8080";
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
    public void registerInvalid() throws Exception {
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
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResult registerResult = facade.register(registerRequest);
        String auth = registerResult.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest("gameName", auth);
        assertThrows(ResponseException.class, () -> facade.createGame(createGameRequest));
    }



    @Test
    public void listGamesValid() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResult registerResult = facade.register(registerRequest);
        authToken = registerResult.authToken();

        CreateGameRequest createGameRequest = new CreateGameRequest("gameName", authToken);
        facade.createGame(createGameRequest);

        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        assertNotNull(facade.listGames(listGamesRequest));
    }

    @Test
    public void listGamesInvalid() {
        ListGamesRequest request = new ListGamesRequest("invalidAuthToken");
        assertThrows(ResponseException.class, () -> facade.listGames(request));
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
        facade.join(auth, ChessGame.TeamColor.WHITE, 1);
        assertThrows(ResponseException.class, () -> facade.join(auth, ChessGame.TeamColor.WHITE, 1));
    }



    @Test
    public void joinGameInvalid() throws Exception {
        if (authToken == null) {
            loginValid();
        }
        assertThrows(ResponseException.class, () -> facade.join(authToken, ChessGame.TeamColor.BLACK, -1));
    }

    @Test
    public void clearValid() throws Exception {
        facade.clear();
    }

    @Test
    public void logoutValid() throws Exception {
        RegisterRequest regRequest = new RegisterRequest("username", "password", "email");
        facade.register(regRequest);
        LoginRequest request = new LoginRequest("username", "password");
        LoginResult result = facade.login(request);
        authToken = result.authToken();
        assertDoesNotThrow(() -> facade.logout(authToken));
    }


    @Test
    public void logoutInvalid() throws Exception {
        RegisterRequest regRequest = new RegisterRequest("username", "password", "email");
        facade.register(regRequest);
        LoginRequest request = new LoginRequest("username", "password");
        LoginResult result = facade.login(request);
        authToken = result.authToken();
        assertThrows(ResponseException.class, () -> facade.logout(authToken));
    }

}
